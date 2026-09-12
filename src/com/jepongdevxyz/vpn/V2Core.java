package com.jepongdevxyz.vpn;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.jepongdevxyz.vpn.config.V2Config;
import com.jepongdevxyz.vpn.tunnel.vpn.V2Listener;
import com.jepongdevxyz.vpn.util.V2Utilities;

import org.json.JSONObject;

import libv2ray.Libv2ray;
import libv2ray.V2RayPoint;
import libv2ray.V2RayVPNServiceSupportsSet;

public final class V2Core {
    private static final String TAG = "V2Core";
    private volatile static V2Core INSTANCE;
    private V2Listener v2Listener = null;
    private boolean isLibV2rayCoreInitialized = false;
    public V2Configs.V2RAY_STATES V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_DISCONNECTED;
    private CountDownTimer countDownTimer;
    private int seconds, minutes, hours;
    private long totalDownload, totalUpload, uploadSpeed, downloadSpeed;
    private String SERVICE_DURATION = "00:00:00";
    private NotificationManager mNotificationManager = null;
    private final V2RayPoint v2RayPoint;
    private static final String CHANNEL_ID = "WakkoServiceChannel";

    private V2Core() {
        v2RayPoint = Libv2ray.newV2RayPoint(new V2RayVPNServiceSupportsSet() {
            @Override
            public long shutdown() {
                if (v2Listener == null) {
                    Log.e(TAG, "shutdown failed => can`t find initial service.");
                    return -1;
                }
                try {
                    v2Listener.stopService();
                    v2Listener = null;
                    Log.d(TAG, "shutdown exitoso");
                    return 0;
                } catch (Exception e) {
                    Log.e(TAG, "shutdown failed =>", e);
                    return -1;
                }
            }

            @Override
            public long prepare() {
                return 0;
            }

            @Override
            public boolean protect(long l) {
                if (v2Listener != null) {
                    return v2Listener.onProtect((int) l);
                }
                return true;
            }

            @Override
            public long onEmitStatus(long l, String s) {
                Log.d(TAG, "Estado V2Ray: " + s);
                return 0;
            }

            @Override
            public long setup(String s) {
                if (v2Listener != null) {
                    try {
                        v2Listener.startService();
                        Log.d(TAG, "startService invocado");
                        return 0;
                    } catch (Exception e) {
                        Log.e(TAG, "setup failed => ", e);
                        return -1;
                    }
                }
                Log.e(TAG, "v2Listener es nulo en setup");
                return -1;
            }
        }, Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1);
    }

    public static V2Core getInstance() {
        if (INSTANCE == null) {
            synchronized (V2Core.class) {
                if (INSTANCE == null) {
                    INSTANCE = new V2Core();
                }
            }
        }
        return INSTANCE;
    }

    private void makeDurationTimer(final Context context, final boolean enable_traffic_statics) {
        countDownTimer = new CountDownTimer(7200 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                seconds++;
                if (seconds == 59) {
                    minutes++;
                    seconds = 0;
                }
                if (minutes == 59) {
                    minutes = 0;
                    hours++;
                }
                if (hours == 23) {
                    hours = 0;
                }
                if (enable_traffic_statics) {
                    downloadSpeed = v2RayPoint.queryStats("block", "downlink") + v2RayPoint.queryStats("proxy", "downlink");
                    uploadSpeed = v2RayPoint.queryStats("block", "uplink") + v2RayPoint.queryStats("proxy", "uplink");
                    totalDownload += downloadSpeed;
                    totalUpload += uploadSpeed;
                }
                SERVICE_DURATION = V2Utilities.convertIntToTwoDigit(hours) + ":" + V2Utilities.convertIntToTwoDigit(minutes) + ":" + V2Utilities.convertIntToTwoDigit(seconds);
                Intent connection_info_intent = new Intent("V2RAY_CONNECTION_INFO");
                connection_info_intent.putExtra("STATE", V2RAY_STATE);
                connection_info_intent.putExtra("DURATION", SERVICE_DURATION);
                connection_info_intent.putExtra("UPLOAD_SPEED", V2Utilities.parseTraffic(uploadSpeed, false, true));
                connection_info_intent.putExtra("DOWNLOAD_SPEED", V2Utilities.parseTraffic(downloadSpeed, false, true));
                connection_info_intent.putExtra("UPLOAD_TRAFFIC", V2Utilities.parseTraffic(totalUpload, false, false));
                connection_info_intent.putExtra("DOWNLOAD_TRAFFIC", V2Utilities.parseTraffic(totalDownload, false, false));
                context.sendBroadcast(connection_info_intent);
            }

            public void onFinish() {
                countDownTimer.cancel();
                if (isV2rayCoreRunning()) {
                    makeDurationTimer(context, enable_traffic_statics);
                }
            }
        }.start();
    }

    public void setUpListener(Service targetService) {
        try {
            v2Listener = (V2Listener) targetService;
            if (!isLibV2rayCoreInitialized) {
                V2Utilities.copyAssets(targetService.getApplicationContext());
                Libv2ray.initV2Env(V2Utilities.getUserAssetsPath(targetService.getApplicationContext()));
                isLibV2rayCoreInitialized = true;
            }
            SERVICE_DURATION = "00:00:00";
            seconds = 0;
            minutes = 0;
            hours = 0;
            uploadSpeed = 0;
            downloadSpeed = 0;
            totalDownload = 0;
            totalUpload = 0;
            Log.d(TAG, "setUpListener inicializado desde " + v2Listener.getService().getClass().getSimpleName());
        } catch (Exception e) {
            Log.e(TAG, "setUpListener failed => ", e);
            isLibV2rayCoreInitialized = false;
        }
    }

    public boolean startCore(final V2Config v2Config) {
        if (!isLibV2rayCoreInitialized) {
            Log.e(TAG, "startCore failed => LibV2rayCore should be initialized before start.");
            return false;
        }
        if (isV2rayCoreRunning()) {
            stopCore();
        }
        try {
            if (v2Config.V2RAY_FULL_JSON_CONFIG == null) {
                throw new Exception("Configuración JSON nula");
            }
            Libv2ray.testConfig(v2Config.V2RAY_FULL_JSON_CONFIG);
            v2RayPoint.setConfigureFileContent(v2Config.V2RAY_FULL_JSON_CONFIG);
            v2RayPoint.setDomainName(v2Config.CONNECTED_V2RAY_SERVER_ADDRESS + ":" + v2Config.CONNECTED_V2RAY_SERVER_PORT);
            makeDurationTimer(v2Listener.getService().getApplicationContext(), v2Config.ENABLE_TRAFFIC_STATICS);
            V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_CONNECTING;
            v2RayPoint.runLoop(false);
            V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_CONNECTED;
            showNotification(v2Config);
            Log.d(TAG, "V2Ray core iniciado con éxito");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "startCore failed => ", e);
            sendDisconnectedBroadCast();
            return false;
        }
    }

    public void stopCore() {
        try {
            if (isV2rayCoreRunning()) {
                v2RayPoint.stopLoop();
                if (v2Listener != null) {
                    v2Listener.stopService();
                }
                Log.d(TAG, "V2Ray core detenido con éxito");
            } else {
                Log.d(TAG, "V2Ray core no está en ejecución");
            }
            sendDisconnectedBroadCast();
        } catch (Exception e) {
            Log.e(TAG, "stopCore failed =>", e);
        }
    }

    private void sendDisconnectedBroadCast() {
        V2RAY_STATE = V2Configs.V2RAY_STATES.V2RAY_DISCONNECTED;
        SERVICE_DURATION = "00:00:00";
        seconds = 0;
        minutes = 0;
        hours = 0;
        uploadSpeed = 0;
        downloadSpeed = 0;
        totalDownload = 0;
        totalUpload = 0;
        if (v2Listener != null) {
            Intent connection_info_intent = new Intent("V2RAY_CONNECTION_INFO");
            connection_info_intent.putExtra("STATE", V2RAY_STATE);
            connection_info_intent.putExtra("DURATION", SERVICE_DURATION);
            connection_info_intent.putExtra("UPLOAD_SPEED", V2Utilities.parseTraffic(0, false, true));
            connection_info_intent.putExtra("DOWNLOAD_SPEED", V2Utilities.parseTraffic(0, false, true));
            connection_info_intent.putExtra("UPLOAD_TRAFFIC", V2Utilities.parseTraffic(0, false, false));
            connection_info_intent.putExtra("DOWNLOAD_TRAFFIC", V2Utilities.parseTraffic(0, false, false));
            try {
                v2Listener.getService().getApplicationContext().sendBroadcast(connection_info_intent);
            } catch (Exception e) {
                Log.e(TAG, "Error al enviar broadcast", e);
            }
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private NotificationManager getNotificationManager() {
        if (mNotificationManager == null) {
            try {
                mNotificationManager = (NotificationManager) v2Listener.getService().getSystemService(Context.NOTIFICATION_SERVICE);
            } catch (Exception e) {
                Log.e(TAG, "Error al obtener NotificationManager", e);
                return null;
            }
        }
        return mNotificationManager;
    }

    private String createNotificationChannelID(final String application_name) {
        String notification_channel_id = CHANNEL_ID;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                notification_channel_id,
                "V2ray Service",
                NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.setLightColor(android.R.color.holo_blue_light);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_NONE);
            NotificationManager manager = getNotificationManager();
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        return notification_channel_id;
    }

    private int judgeForNotificationFlag() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT :
            PendingIntent.FLAG_UPDATE_CURRENT;
    }

    private void showNotification(final V2Config v2Config) {
        if (v2Listener == null) {
            Log.e(TAG, "v2Listener es nulo, no se puede mostrar notificación");
            return;
        }
        Intent launchIntent = new Intent(v2Listener.getService().getApplicationContext(), MainActivity.class);
        launchIntent.setAction("FROM_DISCONNECT_BTN");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent notificationContentPendingIntent = PendingIntent.getActivity(
            v2Listener.getService(),
            0,
            launchIntent,
            judgeForNotificationFlag()
        );

        String notificationChannelID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
            createNotificationChannelID(v2Config.APPLICATION_NAME) : "";

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(v2Listener.getService(), notificationChannelID)
            .setSmallIcon(R.drawable.v2)
            .setContentTitle("Conectado a V2ray")
            .setOngoing(true)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(notificationContentPendingIntent)
            .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);

        v2Listener.getService().startForeground(1, mBuilder.build());
        v2Listener.getService().stopForeground(true);
        Log.d(TAG, "Notificación en primer plano mostrada");
    }

    public boolean isV2rayCoreRunning() {
        return v2RayPoint != null && v2RayPoint.getIsRunning();
    }

    public Long getConnectedV2rayServerDelay() {
        try {
            return v2RayPoint.measureDelay();
        } catch (Exception e) {
            Log.e(TAG, "Error en getConnectedV2rayServerDelay", e);
            return -1L;
        }
    }

    public Long getV2rayServerDelay(final String config) {
        try {
            JSONObject config_json = new JSONObject(config);
            JSONObject new_routing_json = config_json.getJSONObject("routing");
            new_routing_json.remove("rules");
            config_json.remove("routing");
            config_json.put("routing", new_routing_json);
            return Libv2ray.measureOutboundDelay(config_json.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error en getV2rayServerDelay", e);
            return -1L;
        }
    }
}
