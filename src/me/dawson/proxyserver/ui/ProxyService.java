package me.dawson.proxyserver.ui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import me.dawson.proxyserver.core.ProxyServer;

public class ProxyService extends Service {
    public static final String TAG = "ProxyService";

    @Override
    public IBinder onBind(Intent binder) {
        return new IProxyControl.Stub() {
            @Override
            public boolean start() throws RemoteException {
                return doStart();
            }

            @Override
            public boolean stop() throws RemoteException {
                return doStop();
            }

            @Override
            public boolean isRunning() throws RemoteException {
                return ProxyServer.getInstance().isRunning();
            }

            @Override
            public int getPort() throws RemoteException {
                return ProxyServer.getInstance().getPort();
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "ProxyService created");
    }

    @Override
    public void onDestroy() {
        doStop();
        super.onDestroy();
        Log.d(TAG, "ProxyService destroyed");
    }

    private boolean doStart() {
        ProxyServer proxyServer = ProxyServer.getInstance();
        if (proxyServer.isRunning()) {
            Log.w(TAG, "ProxyServer already running");
            return false;
        }

        try {
            boolean started = proxyServer.start();
            if (started) {
                Log.i(TAG, "ProxyServer started on port " + proxyServer.getPort());
            } else {
                Log.e(TAG, "Failed to start ProxyServer");
            }
            return started;
        } catch (Exception e) {
            Log.e(TAG, "Error starting ProxyServer", e);
            return false;
        }
    }

    private boolean doStop() {
        ProxyServer proxyServer = ProxyServer.getInstance();
        if (!proxyServer.isRunning()) {
            Log.w(TAG, "ProxyServer not running");
            return false;
        }

        try {
            boolean stopped = proxyServer.stop();
            if (stopped) {
                Log.i(TAG, "ProxyServer stopped");
            } else {
                Log.e(TAG, "Failed to stop ProxyServer");
            }
            return stopped;
        } catch (Exception e) {
            Log.e(TAG, "Error stopping ProxyServer", e);
            return false;
        }
    }
}