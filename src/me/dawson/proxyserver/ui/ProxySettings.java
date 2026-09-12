package me.dawson.proxyserver.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.tunnel.TunnelUtils;
import es.dmoral.toasty.Toasty;
import java.lang.reflect.Method;

public class ProxySettings extends AppCompatActivity implements ServiceConnection, CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "ProxySettings";
    protected static final String KEY_PREFS = "proxy_pref";
    protected static final String KEY_PROXY_ENABLE = "proxy_enable";
    protected static final String KEY_HOTSPOT_ENABLE = "hotspot_enable";
    public static final String PortDefault = "8080";
    private static final int PROXY_NOTIFICATION_ID = 20140701;
    private static final int HOTSPOT_NOTIFICATION_ID = 20140702;

    private IProxyControl proxyControl = null;
    private WifiManager wifiManager;

    private TextView tvInfo;
    private TextView txtip;
    private TextView text_port;
    private TextView tvHotspotInfo;
    private MaterialSwitch cbProxy;
    private MaterialSwitch cbHotspot;
    private Button btnCopyDetails;
    private AdView adsBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxy_settings);

        Toolbar mToolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        tvInfo = findViewById(R.id.tv_info);
        txtip = findViewById(R.id.ip_layout);
        txtip.setText(TunnelUtils.getLocalIpAddress());
        text_port = findViewById(R.id.textport);
        text_port.setText(PortDefault);

        cbProxy = findViewById(R.id.cb_proxy);
        cbProxy.setOnCheckedChangeListener(this);

        cbHotspot = findViewById(R.id.cb_hotspot);
        cbHotspot.setOnCheckedChangeListener(this);

        tvHotspotInfo = findViewById(R.id.tv_hotspot_info);

        btnCopyDetails = findViewById(R.id.btn_copy_details);
        btnCopyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyDetails();
            }
        });

        adsBannerView = findViewById(R.id.adView5);
        if (TunnelUtils.isNetworkOnline(this)) {
            adsBannerView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    if (adsBannerView != null) {
                        adsBannerView.setVisibility(View.VISIBLE);
                    }
                }
            });
            adsBannerView.loadAd(new AdRequest.Builder().build());
        }

        Intent intent = new Intent(this, ProxyService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

        updateHotspotStatus();
    }

    @Override
    public void onServiceConnected(ComponentName cn, IBinder binder) {
        proxyControl = IProxyControl.Stub.asInterface(binder);
        if (proxyControl != null) {
            updateProxy();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName cn) {
        proxyControl = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHotspotStatus();
    }

    @Override
    protected void onDestroy() {
        unbindService(this);
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences sp = getSharedPreferences(KEY_PREFS, MODE_PRIVATE);
        if (buttonView.getId() == R.id.cb_proxy) {
            sp.edit().putBoolean(KEY_PROXY_ENABLE, isChecked).apply();
            updateProxy();
        } else if (buttonView.getId() == R.id.cb_hotspot) {
            sp.edit().putBoolean(KEY_HOTSPOT_ENABLE, isChecked).apply();
            if (isChecked) {
                enableHotspot();
            } else {
                disableHotspot();
            }
        }
    }

    private void updateProxy() {
        if (proxyControl == null) {
            return;
        }

        boolean isRunning = false;
        try {
            isRunning = proxyControl.isRunning();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        boolean shouldRun = getSharedPreferences(KEY_PREFS, MODE_PRIVATE)
                .getBoolean(KEY_PROXY_ENABLE, false);
        if (shouldRun && !isRunning) {
            startProxy();
        } else if (!shouldRun && isRunning) {
            stopProxy();
        }

        try {
            isRunning = proxyControl.isRunning();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (isRunning) {
            tvInfo.setText(R.string.proxy_on);
            cbProxy.setChecked(true);
        } else {
            tvInfo.setText(R.string.proxy_off);
            cbProxy.setChecked(false);
        }
    }

    private void startProxy() {
        boolean started = false;
        try {
            started = proxyControl.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!started) {
            Toasty.error(this, "Error al iniciar el proxy").show();
            return;
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(getPackageName() + ".proxy");
            createNotificationChannel(manager, getPackageName() + ".proxy");
        }

        Intent intent = new Intent(this, ProxySettings.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        builder.setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.service_text))
                .setSmallIcon(R.drawable.v2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.v2))
                .setContentIntent(pendingIntent)
                .setOngoing(true);

        manager.notify(PROXY_NOTIFICATION_ID, builder.build());
        Toasty.success(this, getString(R.string.proxy_started)).show();
    }

    private void stopProxy() {
        boolean stopped = false;
        try {
            stopped = proxyControl.stop();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!stopped) {
            Toasty.error(this, "Error al detener el proxy").show();
            return;
        }

        tvInfo.setText(R.string.proxy_off);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(PROXY_NOTIFICATION_ID);
        Toasty.error(this, getString(R.string.proxy_stopped)).show();
    }

    private void enableHotspot() {
        boolean isProxyRunning = false;
        try {
            isProxyRunning = proxyControl != null && proxyControl.isRunning();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (!isProxyRunning) {
            tvHotspotInfo.setText("Estado: Error - Active el proxy primero");
            cbHotspot.setChecked(false);
            Toasty.error(this, "Por favor, active el proxy antes del punto de acceso").show();
            return;
        }

        openTetherSettings(true);
    }

    private void disableHotspot() {
        openTetherSettings(false);
    }

    private void openTetherSettings(boolean isEnabling) {
        Intent tetherSettings = new Intent();
        tetherSettings.setClassName("com.android.settings", "com.android.settings.TetherSettings");
        try {
            startActivity(tetherSettings);
            Toasty.info(this, isEnabling ? "Active el punto de acceso móvil en los ajustes" : "Desactive el punto de acceso móvil en los ajustes").show();
        } catch (Exception e) {
            e.printStackTrace();
            tvHotspotInfo.setText("Estado: Error al abrir ajustes");
            cbHotspot.setChecked(false);
            Toasty.error(this, "No se pudo abrir la configuración de tethering").show();
        }
    }

    private void updateHotspotStatus() {
        boolean isHotspotEnabled = false;
        String ssid = "Desconocido";
        String password = "Desconocido";

        try {
            Method getWifiApState = wifiManager.getClass().getMethod("isWifiApEnabled");
            isHotspotEnabled = (Boolean) getWifiApState.invoke(wifiManager);

            if (isHotspotEnabled && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Method getWifiApConfiguration = wifiManager.getClass().getMethod("getWifiApConfiguration");
                WifiConfiguration config = (WifiConfiguration) getWifiApConfiguration.invoke(wifiManager);
                if (config != null) {
                    ssid = config.SSID;
                    password = config.preSharedKey != null ? config.preSharedKey : "Sin contraseña";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isHotspotEnabled) {
            tvHotspotInfo.setText("Estado: Activado\nSSID: " + ssid + "\nContraseña: " + password);
            cbHotspot.setChecked(true);
            showHotspotNotification(ssid, password);
        } else {
            tvHotspotInfo.setText("Estado: Desactivado");
            cbHotspot.setChecked(false);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(HOTSPOT_NOTIFICATION_ID);
        }
    }

    private void showHotspotNotification(String ssid, String password) {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(getPackageName() + ".hotspot");
            createNotificationChannel(manager, getPackageName() + ".hotspot");
        }

        builder.setContentTitle("Punto de Acceso Móvil Activado")
                .setContentText("SSID: " + ssid + " | Contraseña: " + password + " | IP: " + TunnelUtils.getLocalIpAddress() + " | Puerto: " + PortDefault)
                .setSmallIcon(R.drawable.v2)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.v2))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setShowWhen(true)
                .setOngoing(true);

        manager.notify(HOTSPOT_NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel(NotificationManager manager, String channelId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Proxy Settings", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            manager.createNotificationChannel(channel);
        }
    }

    private void copyDetails() {
        String details = "IP: " + TunnelUtils.getLocalIpAddress() + "\nPuerto: " + PortDefault;
        boolean isHotspotEnabled = false;
        String ssid = "Desconocido";
        String password = "Desconocido";

        try {
            Method getWifiApState = wifiManager.getClass().getMethod("isWifiApEnabled");
            isHotspotEnabled = (Boolean) getWifiApState.invoke(wifiManager);

            if (isHotspotEnabled && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                Method getWifiApConfiguration = wifiManager.getClass().getMethod("getWifiApConfiguration");
                WifiConfiguration config = (WifiConfiguration) getWifiApConfiguration.invoke(wifiManager);
                if (config != null) {
                    ssid = config.SSID;
                    password = config.preSharedKey != null ? config.preSharedKey : "Sin contraseña";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isHotspotEnabled) {
            details += "\nSSID: " + ssid + "\nContraseña: " + password;
        } else {
            details += "\nSSID: Active el punto de acceso en los ajustes\nContraseña: Configure en los ajustes";
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Proxy Details", details);
        clipboard.setPrimaryClip(clip);
        Toasty.success(this, "Detalles copiados al portapapeles").show();
    }
}
