package com.jepongdevxyz.vpn.tunnel;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.ProxyInfo;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import androidx.preference.PreferenceManager;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.service.SocksDNSService;
import com.jepongdevxyz.vpn.config.PasswordCache;
import com.jepongdevxyz.vpn.config.Settings;
import com.jepongdevxyz.vpn.logger.SkStatus;
import com.jepongdevxyz.vpn.tunnel.vpn.TunnelState;
import com.jepongdevxyz.vpn.tunnel.vpn.TunnelVpnManager;
import com.jepongdevxyz.vpn.tunnel.vpn.TunnelVpnService;
import com.jepongdevxyz.vpn.tunnel.vpn.TunnelVpnSettings;
import com.jepongdevxyz.vpn.tunnel.UDPListener;
import com.jepongdevxyz.vpn.tunnel.UDPTunnel;
import com.jepongdevxyz.vpn.tunnel.Pinger;
import com.jepongdevxyz.vpn.tunnel.vpn.VpnUtils;
import com.jepongdevxyz.vpn.tunnel.vpn.V2Listener;
import com.jepongdevxyz.vpn.tunnel.V2Tunnel;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.ConnectionMonitor;
import com.trilead.ssh2.DebugLogger;
import com.trilead.ssh2.DynamicPortForwarder;
import com.trilead.ssh2.InteractiveCallback;
import com.trilead.ssh2.KnownHosts;
import com.trilead.ssh2.ProxyData;
import com.trilead.ssh2.ServerHostKeyVerifier;
import com.trilead.ssh2.transport.TransportManager;

public class TunnelManagerThread
        implements Runnable, ConnectionMonitor, InteractiveCallback,
        ServerHostKeyVerifier, DebugLogger {
    private static final String TAG = TunnelManagerThread.class.getSimpleName();
    private OnStopCliente mListener;
    private Context mContext;
    private Handler mHandler;
    private Settings mConfig;
    private Pinger pinger;
    private boolean mRunning = false, mStopping = false, mStarting = false;
    private CountDownLatch mTunnelThreadStopSignal;
    private static UDPListener udpListener;
    private SharedPreferences preferencias;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback callback;
    private boolean pandaspeed = false;
    private UDPTunnel udpTunnel;
    private boolean v2rayrunning = false;
    private V2Tunnel v2Tunnel;
    private static V2Listener v2Listener;
    public interface OnStopCliente {
        void onStop();
    }
    

    public TunnelManagerThread(Handler handler, Context context) {
        mContext = context;
        mHandler = handler;
       

        mConfig = new Settings(context);
        preferencias = mConfig.getPrefsPrivate();
        normalpanda();
        udpListener = new UDPListener() {
            @Override
            public void onConnecting() {
                SkStatus.updateStateString(SkStatus.SSH_CONECTANDO, "Conectando");
            }
            @Override
            public void onConnected() {
                try {
                    
                    SkStatus.updateStateString(SkStatus.SSH_CONECTADO, "SSH Connection Established");
                    SkStatus.logInfo("<font color='#00adbe'><strong>" + mContext.getString(R.string.state_connected) + "</strong></font>"); //conectado log del v2ray

                    mConnected = true;
                    startTunnelVpnService();
                } catch (Exception e)  {

                }
                
            }
            @Override
            public void onNetworkLost() {
            }
            @Override
            public void onAuthFailed() {
                //stopAll();
                
            }
            @Override
            public void onReconnecting() {
              //  stopAll();
            }
            @Override
            public void onConnectionLost() {
                // stopAll();
            }
            @Override
            public void onError() {
                stopAll();
            }
            @Override
            public void onDisconnected() {

            }
        };
        v2Listener = new V2Listener() {
            @Override
            public boolean onProtect(int socket) {
                return false;
            }

            @Override
            public Service getService() {
                return null;
            }

            @Override
            public void startService() {
                SkStatus.updateStateString(SkStatus.SSH_CONECTANDO, "Conectando");
            }

            @Override
            public void stopService() {

            }
            @Override
            public void onConnected() {
                try {
                    SkStatus.updateStateString(SkStatus.SSH_CONECTADO, "SSH Connection Established");
                   // SkStatus.logInfo("<font color='#00adbe'><strong>V2ray Conectado</strong></font>");
                    mConnected = false;
                   
                      //  startTunnelVpnService();
                      //  V2Tunnel.checkConnectionAfterDelay(context);
                        SkStatus.logInfo("<font color='#00adbe'><strong>V2ray conectado</strong></font>");
                        startTunnelVpnService();
                    
                } catch (Exception ignored)  {

                }

            }

            @Override
            public void onError() {
                Intent stopTunnel = new Intent(SocksDNSService.TUNNEL_SSH_STOP_SERVICE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(stopTunnel);

            }
        };
    }
    public void setOnStopClienteListener(OnStopCliente listener) {
        mListener = listener;
    }
    
    @Override
    public void run() {
        mStarting = true;
        mTunnelThreadStopSignal = new CountDownLatch(1);
        SkStatus.logInfo("<strong>" + mContext.getString(R.string.starting_service_ssh) + "</strong>");
        int tries = 0;
        while (!mStopping) {
            try {
                if (!TunnelUtils.isNetworkOnline(mContext)) {
                    SkStatus.updateStateString(SkStatus.SSH_AGUARDANDO_REDE, mContext.getString(R.string.state_nonetwork));
                    

                    SkStatus.logInfo(R.string.state_nonetwork);

                    try {
                        Thread.sleep(9000);
                    } catch (InterruptedException e2) {
                        stopAll();
                       
                        break;
                    }
                } else {
                    if (tries > 0)
                        SkStatus.logInfo(mContext.getString(R.string.state_reconnecting));

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e2) {
                        stopAll();
                        break;
                    }
                    

                    if(isudpmode()){
                        if (udpTunnel == null) {
                            udpTunnel = new UDPTunnel(mContext);
                            udpTunnel.iniciarUdp();
                            pandaspeed = true;
                        }
                    } else if (isv2raymode()) {
                        if (v2Tunnel == null) {
                            v2Tunnel = new V2Tunnel(mContext);
                            V2Tunnel.StartV2ray(mContext.getApplicationContext(), "Default", mConfig.getPrivString(Settings.V2RAY_JSON), null);
                            v2rayrunning = true;
                        }
                    } else{
                        startClienteSSH();
                    }
                    break;
                }
            } catch (Exception e) {
               
                SkStatus.logError("<strong>" + mContext.getString(R.string.state_disconnected) + "</strong>");
                closeSSH();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                    stopAll();
                    break;
                }
            }

            tries++;
        }
        
        mStarting = false;

        if (!mStopping) {
            try {
                mTunnelThreadStopSignal.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        if (mListener != null) {
            mListener.onStop();
        }
    }

    public static V2Listener getV2rayServicesListener() {
        return v2Listener;
    }
    public void stopAll() {
        if (mStopping) return;
        SkStatus.updateStateString(SkStatus.SSH_PARANDO, mContext.getString(R.string.stopping_service_ssh));
        SkStatus.logInfo("<strong>" + mContext.getString(R.string.stopping_service_ssh) + "</strong>");
        new Thread(new Runnable() {
            @Override
            public void run() {
                mStopping = true;
                if (mTunnelThreadStopSignal != null)
                    mTunnelThreadStopSignal.countDown();
                
                if (isudpmode()) {
                    if (pandaspeed) {
                        udpTunnel.detenerUdp();
                        udpTunnel = null;
                        pandaspeed = false;
                        connectivityManager.unregisterNetworkCallback(callback);
                    }
                    if (mConnected) {
                        stopForwarder();
                    }
                    mRunning = false;
                    mStarting = false;
                    mReconnecting = false;
                } else if (isv2raymode()) {
                    if (v2rayrunning) {
                        V2Tunnel.StopV2ray(mContext.getApplicationContext());
                        v2Tunnel = null;
                        v2rayrunning= false;
                        connectivityManager.unregisterNetworkCallback(callback);
                    }
                    if (mConnected) {
                        stopForwarder();
                    }
                    mRunning = false;
                    mStarting = false;
                    mReconnecting = false;
                } else {
                    closeSSH();
                    mRunning = false;
                    mStarting = false;
                    mReconnecting = false;
                }
            }
        }).start();
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SkStatus.updateStateString(SkStatus.SSH_DESCONECTADO, mContext.getString(R.string.state_disconnected));
              
                
            }
        }, 500);
    }

  public void pandxd() {
    {
      mStarting = true;
      mTunnelThreadStopSignal = new CountDownLatch(1);
      SkStatus.logInfo(
          "<strong>" + mContext.getString(R.string.starting_service_ssh) + "</strong>");
      int tries = 0;
      while (!mStopping) {
        try {
          if (!TunnelUtils.isNetworkOnline(mContext)) {
            if (isudpmode()) {
              if (pandaspeed) {
                udpTunnel.detenerUdp();
                udpTunnel = null;
                pandaspeed = false;
                connectivityManager.unregisterNetworkCallback(callback);
              }
              if (mConnected) {
                stopForwarder();
              }
              mRunning = false;
              mStarting = false;
              mReconnecting = false;
            } 
                        else {
              closeSSH();
              mRunning = false;
              mStarting = false;
              mReconnecting = false;
            }
            SkStatus.updateStateString(
                SkStatus.SSH_AGUARDANDO_REDE, mContext.getString(R.string.state_nonetwork));
            SkStatus.logInfo(R.string.state_nonetwork);
            try {
              Thread.sleep(9000);
            } catch (InterruptedException e2) {
              reconnectSSH();
              break;
            }
          } else {
            if (tries > 0) SkStatus.logInfo(mContext.getString(R.string.state_reconnecting));
            try {
              Thread.sleep(500);
            } catch (InterruptedException e2) {
              stopAll();
              break;
            }
            if (isudpmode()) {
              if (udpTunnel == null) {
                udpTunnel = new UDPTunnel(mContext);
                udpTunnel.iniciarUdp();
                pandaspeed = true;
              }
            } else {
              startClienteSSH();
            }
            break;
          }
        } catch (Exception e) {
          SkStatus.logError(
              "<strong>" + mContext.getString(R.string.state_disconnected) + "</strong>");
          closeSSH();
          try {
            Thread.sleep(500);
          } catch (InterruptedException e2) {
            stopAll();
            break;
          }
        }
        tries++;
      }
      mStarting = false;
      if (!mStopping) {
        try {
          mTunnelThreadStopSignal.await();
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
      if (mListener != null) {
        mListener.onStop();
      }
    }
    mStarting = false;
    if (!mStopping) {
      try {
        mTunnelThreadStopSignal.await();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    if (mListener != null) {
      mListener.onStop();
    }
  }

  /** Forwarder */
  protected void startForwarder(int portaLocal) throws Exception {
    if (!mConnected) {
      throw new Exception();
    }
    startForwarderSocks(portaLocal);
    startTunnelVpnService();
        
        	/* If connected, 1s delay before pinger started */

		String PING = mConfig.setPinger();	

		if (mConfig.setAutoPing()){

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}

			if (!PING.equals(""))
			{
				pinger = new Pinger(mConnection, PING);
				pinger.start();
			}

		}

        
    }
   
    protected void stopForwarder() {
        stopTunnelVpnService();
        stopForwarderSocks();
    }
    /**
     * Cliente SSH
     */
    private final static int AUTH_TRIES = 1;
    private final static int RECONNECT_TRIES = 5;
    private Connection mConnection;
    private boolean mConnected = false;
    protected void startClienteSSH() throws Exception {
        mStopping = false;
        mRunning = true;
        String servidor = mConfig.getPrivString(Settings.SERVIDOR_KEY);
        int porta = Integer.parseInt(mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY));
        String usuario = mConfig.getPrivString(Settings.USUARIO_KEY);
        String _senha = mConfig.getPrivString(Settings.SENHA_KEY);
        String senha = _senha.isEmpty() ? PasswordCache.getAuthPassword(null, false) : _senha;
        String keyPath = mConfig.getSSHKeypath();
        int portaLocal = Integer.parseInt(mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
        try {
          
            conectar(servidor, porta);
            for (int i = 0; i < AUTH_TRIES; i++) {
                if (mStopping) {
                    return;
                }
                try {
                    autenticar(usuario, senha, keyPath);
                    break;
                } catch (IOException e) {
                    if (i + 1 >= AUTH_TRIES) {
                        throw new IOException("Autenticación fallida");
                    } else {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e2) {
                            return;
                        } } } }
            SkStatus.updateStateString(SkStatus.SSH_CONECTADO, "SSH Connection Establecida");
            SkStatus.logInfo("<strong><html><font color='#33964f'>" + mContext.getString(R.string.state_connected) + "</font></html></strong>");
            

            startForwarder(portaLocal);
               if (mConfig.getSSHPinger() > 0) {
			 startPinger(mConfig.getSSHPinger());
			 }

                
            
            
            
        } catch (Exception e) {
            mConnected = false;
          
            throw e;
        }
    }
    
    public synchronized void closeSSH() {
        stopForwarder();
       stopPinger();
       
        if (mConnection != null) {
            SkStatus.logDebug("Stopping SSH");
            mConnection.close();
        }
    }
   
    protected void conectar(String servidor, int porta) throws Exception {
        if (!mStarting) {
            throw new Exception();
        }
       
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        // aqui deve conectar
        try {
            mConnection = new Connection(servidor, porta);
            

         /*   if (mConfig.getModoDebug() && !prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
                // Desativado, pois estava enchendo o Logger
                //mConnection.enableDebugging(true, this);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Debug mode enabled",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
*/
            // delay sleep
            if (mConfig.getIsDisabledDelaySSH()) {
                
                mConnection.setTCPNoDelay(true);
                SkStatus.logInfo("<font color='#33964f'><strong>Sin retrasos TCP</strong></font>");
            }
           
            
            	if (mConfig.ssh_compression()){
				mConnection.setCompression(true);
				SkStatus.logInfo("<strong>Comprension SSH activo</strong>");
			}
            
            

            // proxy
            addProxy(prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false), prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT),
                    (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true) ? mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY) : null), mConfig.getPrivString(Settings.CUSTOM_SNI),
                    mConnection);
            // monitora a conexão
            mConnection.addConnectionMonitor(this);
          

            if (Build.VERSION.SDK_INT >= 23) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                ProxyInfo proxy = cm.getDefaultProxy();
                if (proxy != null) {
                    //SkStatus.logInfo("<strong>Welcome to the world of freenet</strong>");
                }
            }
           
            SkStatus.updateStateString(SkStatus.SSH_CONECTANDO, mContext.getString(R.string.state_connecting));
            SkStatus.logInfo(R.string.state_connecting);
            mConnection.connect(this, 10 * 1000, 20 * 1000);
            mConnected = true;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String cause = e.getCause().toString();
            if (useProxy && cause.contains("Key exchange was not finished")) {
                SkStatus.logError("Proxy: Connection Lost");
            } else {
                SkStatus.logError("SSH: " + cause);
            }
            throw new Exception(e);
        }
    }
    
    /**
     * Autenticação
     */
    

    private static final String AUTH_PUBLICKEY = "publickey",
            AUTH_PASSWORD = "password", AUTH_KEYBOARDINTERACTIVE = "keyboard-interactive";
    
    protected void autenticar(String usuario, String senha, String keyPath) throws IOException {
        if (!mConnected) {
            throw new IOException();
        }
        SkStatus.updateStateString(SkStatus.SSH_AUTENTICANDO, mContext.getString(R.string.state_auth));
        try {
            if (mConnection.isAuthMethodAvailable(usuario,
                    AUTH_PASSWORD)) {
                SkStatus.logInfo("Verificando");

                if (mConnection.authenticateWithPassword(usuario,
                        senha)) {
                  //  SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_auth_success) + "</strong>");
                }
            }
        } catch (IllegalStateException e) {
            Log.e(TAG,
                    "La conexión desapareció mientras intentábamos autenticar",
                    e);
        } catch (Exception e) {
            Log.e(TAG, "Problem during handleAuthentication()", e);
        }
       
        try {
            if (mConnection.isAuthMethodAvailable(usuario,
                    AUTH_PUBLICKEY) && keyPath != null && !keyPath.isEmpty()) {
                File f = new File(keyPath);
                if (f.exists()) {
                    if (senha.equals("")) senha = null;

                    SkStatus.logInfo("Authenticating com public key");

                    if (mConnection.authenticateWithPublicKey(usuario, f,
                            senha)) {
                        SkStatus.logInfo("<strong>" + mContext.getString(R.string.state_auth_success) + "</strong>");
                    }
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Host does not support 'Public key' authentication.");
        }
      
        if (!mConnection.isAuthenticationComplete()) {
            SkStatus.logInfo("<font color='red'>El usuario o la contraseña son incorrectos o caducaron</font>");
            throw new IOException("No fue posible autenticarse con los datos proporcionados");}}

    // XXX: Is it right?
    @Override
    public String[] replyToChallenge(String name, String instruction,
                                     int numPrompts, String[] prompt, boolean[] echo) throws Exception {
        String[] responses = new String[numPrompts];
        for (int i = 0; i < numPrompts; i++) {
            // request response from user for each prompt
            if (prompt[i].toLowerCase().contains("password"))
                responses[i] = mConfig.getPrivString(Settings.SENHA_KEY);
        } return responses;}
    /**
     * ServerHostKeyVerifier
     * Fingerprint
     */

    @Override
    public boolean verifyServerHostKey(String hostname, int port,
                                       String serverHostKeyAlgorithm, byte[] serverHostKey)
            throws Exception {

        String fingerPrint = KnownHosts.createHexFingerprint(
                serverHostKeyAlgorithm, serverHostKey);
       

        SkStatus.logInfo("Finger Print: " + fingerPrint);


        return true;
    }
    
    /**
     * Proxy
     */
   
    private boolean useProxy = false;

  protected void addProxy(boolean isProteger, int mTunnelType, String mCustomPayload, String mCustomSNI, Connection conn) throws Exception {

		if (mTunnelType != 0) {
			useProxy = true;

			switch (mTunnelType) {
				case Settings.bTUNNEL_TYPE_SSH_DIRECT:
					if (mCustomPayload != null) {
						try {
							ProxyData proxyData = new HttpProxyCustom(mConfig.getPrivString(Settings.SERVIDOR_KEY), Integer.parseInt(mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY)),
									null, null, mCustomPayload, true, mContext);

							conn.setProxyData(proxyData);

							if (!mCustomPayload.isEmpty() && !isProteger)
								SkStatus.logInfo("Payload : " + mCustomPayload);

						} catch(Exception e) {
							throw new Exception(mContext.getString(R.string.error_proxy_invalid));
						}
					}
					else {
						useProxy = false;
					}
					break;

				case Settings.bTUNNEL_TYPE_SSH_PROXY:
					String customPayload = mCustomPayload;

					if (customPayload != null && customPayload.isEmpty()) {
						customPayload = null;
					}

					String servidor = mConfig.getPrivString(Settings.PROXY_IP_KEY);
					int porta = Integer.parseInt(mConfig.getPrivString(Settings.PROXY_PORTA_KEY));

					try {
						ProxyData proxyData = new HttpProxyCustom(servidor, porta,
								null, null, customPayload, false, mContext);

						if (!isProteger)
							SkStatus.logInfo(String.format("Connected to  %s:%d", servidor, porta));
						conn.setProxyData(proxyData);

						if (customPayload != null && !customPayload.isEmpty() && !isProteger)
							SkStatus.logInfo("Payload : "+ customPayload);

					} catch(Exception e) {
						SkStatus.logError(R.string.error_proxy_invalid);

						throw new Exception(mContext.getString(R.string.error_proxy_invalid));
					}
					break;

				case Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL:
					String customSNI = mCustomSNI;
					if (customSNI != null && customSNI.isEmpty()) {
						customPayload = null;
					}

					String sshServer = mConfig.getPrivString(Settings.SERVIDOR_KEY);
					int sshPort = Integer.parseInt(mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY));

					try {
						if (customSNI != null && !customSNI.isEmpty() && !isProteger)
							SkStatus.logInfo("PeerHost : " + customSNI);

						ProxyData sslTypeData = new SSLTunnelProxy(sshServer, sshPort, customSNI);
						conn.setProxyData(sslTypeData);

					}catch(Exception e) {
						SkStatus.logInfo(e.getMessage());
					}
					break;

				case Settings.bTUNNEL_TYPE_PAY_SSL:
					String customSNI2 = mCustomSNI;
					if (customSNI2 != null && customSNI2.isEmpty()) {
						customSNI2 = null;
					}
					String customPayload2 = mCustomPayload;

					if (customPayload2 != null && customPayload2.isEmpty()) {
						customPayload2= null;
					}

					String sshServer2 = mConfig.getPrivString(Settings.SERVIDOR_KEY);
					int sshPort2 = Integer.parseInt(mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY));

					try{

						if (customSNI2 != null && !customSNI2.isEmpty() && !isProteger)
							SkStatus.logInfo("PeerHost : " + customSNI2);
						if (customPayload2 != null && !customPayload2.isEmpty() && !isProteger)
							SkStatus.logInfo("Payload : " + customPayload2);


						SSLProxy sslTun = new SSLProxy(sshServer2, sshPort2, customSNI2,customPayload2);
						conn.setProxyData(sslTun);

					} catch(Exception e) {
						SkStatus.logInfo(e.getMessage());
					}

					break;

				case Settings.bTUNNEL_TYPE_SLOWDNS:
					String servidor2 = mConfig.getPrivString(Settings.SERVIDOR_KEY);
					int porta2 = Integer.parseInt(mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY));
					if (mCustomPayload != null) {
						try {
							ProxyData proxyData = new HttpProxyCustom(servidor2, porta2,
									null, null, mCustomPayload, false, mContext);
							conn.setProxyData(proxyData);

							if (!mCustomPayload.isEmpty() && !isProteger)
								SkStatus.logInfo("Payload :" + mCustomPayload);

						} catch(Exception e) {
							throw new Exception(mContext.getString(R.string.error_proxy_invalid));
						}
					}
					else {
						useProxy = false;
					}
					break;

				case Settings.bTUNNEL_TYPE_SSL_RP:


					String customSNI3 = mCustomSNI;
					if (customSNI3 != null && customSNI3.isEmpty()) {
						customSNI3 = null;
					}
					String customPayload3 = mCustomPayload;

					if (customPayload3 != null && customPayload3.isEmpty()) {
						customPayload3= null;
					}

					String sshServer3 = mConfig.getPrivString(Settings.SERVIDOR_KEY);
					int sshPort3 = Integer.parseInt(mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY));
					String servidor3 = mConfig.getPrivString("proxyRemoto");
					int porta3 = Integer.parseInt(mConfig.getPrivString("proxyRemotoPorta"));

					try{
						SSLRemoteProxy sslTun = new SSLRemoteProxy(sshServer3, sshPort3, customSNI3,customPayload3);
						conn.setProxyData(sslTun);

						if (!isProteger)
							SkStatus.logInfo(String.format("Connected to %s:%d", servidor3, porta3));
						conn.setProxyData(sslTun);
						if (customPayload3 != null && !customPayload3.isEmpty() && !isProteger)
							SkStatus.logInfo("Payload : " + customPayload3);
						if (customSNI3 != null && !customSNI3.isEmpty() && !isProteger)
							SkStatus.logInfo("PeerHost : " + customSNI3);



					} catch(Exception e) {
						SkStatus.logInfo(e.getMessage());
					}
					break;

				default: useProxy = false;
			}
		}
	}



  /** Socks5 Forwarder */
  private DynamicPortForwarder dpf;

  private synchronized void startForwarderSocks(int portaLocal) throws Exception {
    if (!mConnected) {
      throw new Exception();
    }
    try {
      int nThreads = mConfig.getMaximoThreadsSocks();
      if (nThreads > 0) {
        dpf = mConnection.createDynamicPortForwarder(portaLocal, nThreads);
        SkStatus.logDebug("socks local number threads: " + Integer.toString(nThreads));
      } else {
        dpf = mConnection.createDynamicPortForwarder(portaLocal);
      }
    } catch (Exception e) {
      SkStatus.logError("Socks Local: " + e.getCause().toString());
      throw new Exception();
    }
  }

  private synchronized void stopForwarderSocks() {
    if (dpf != null) {
      try {
        dpf.close();
      } catch (IOException e) {
      }
      dpf = null;
    }
  }
    
    	/*Pinger
	 */

	private Thread thPing;
	private long lastPingLatency = -1;

	private void startPinger(final int timePing) throws Exception {
		if (!mConnected) {
			throw new Exception();
		}
		SkStatus.logInfo("starting pinger");

		thPing = new Thread() {
			@Override
			public void run() {
				while (mConnected) {
					try {
						makePinger();
					} catch(InterruptedException e) {
						break;
					}
				}
				SkStatus.logDebug("pinger stopped");
			}

			private synchronized void makePinger() throws InterruptedException {
				try {
					if (mConnection != null) {
						long ping = mConnection.ping();
						if (lastPingLatency < 0) {
							lastPingLatency = ping;
						}
					}
					else throw new InterruptedException();
				} catch(Exception e) {
					Log.e(TAG, "ping error", e);
				}

				if (timePing == 0)
					return;

				if (timePing > 0)
					sleep(timePing*1000);
				else {
					SkStatus.logError("ping invalid");
					throw new InterruptedException();
				}
			}
		};

		// inicia
		thPing.start();
	}

	private synchronized void stopPinger() {
		if (thPing != null && thPing.isAlive()) {
			SkStatus.logInfo("stopping pinger");
			thPing.interrupt();
			thPing = null;
		}
	}



  /** Connection Monitor */
  @Override
  public void connectionLost(Throwable reason) {
    if (mStarting || mStopping || mReconnecting || isudpmode()) {
      return;
      
    }
    SkStatus.logError("<strong>" + mContext.getString(R.string.log_conection_lost) + "</strong>");
    SkStatus.logError("SSH: " + (reason.getMessage().toString()));
    reconnectSSH();
  }

  public boolean mReconnecting = false;

  public void reconnectSSH() {
    if (mStarting || mStopping || mReconnecting) {
      return;
    }
    mReconnecting = true;
    closeSSH();
    SkStatus.updateStateString(SkStatus.SSH_RECONECTANDO, "Reconectando..");
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      mReconnecting = false;
      return;
    }
    SkStatus.clearLog();
    for (int i = 0; i < RECONNECT_TRIES; i++) {
      if (mStopping) {
        mReconnecting = false;
        return;
      }
      int sleepTime = 5;
      if (!TunnelUtils.isNetworkOnline(mContext)) {
        SkStatus.updateStateString(SkStatus.SSH_AGUARDANDO_REDE, "Esperando red ..");
        SkStatus.logInfo(R.string.state_nonetwork);
      } else {
        sleepTime = 3;
        mStarting = true;
        SkStatus.updateStateString(SkStatus.SSH_RECONECTANDO, "Reconectando..");
        SkStatus.logInfo(
            "<strong>" + mContext.getString(R.string.state_reconnecting) + "</strong>");
        try {
          startClienteSSH();
          mStarting = false;
          mReconnecting = false;
          return;
        } catch (Exception e) {
          SkStatus.logInfo(
              "<strong><html><font color=\"red\">"
                  + mContext.getString(R.string.state_disconnected)
                  + "</font></html></strong>");
        }
        mStarting = false;
      }
      try {
        Thread.sleep(sleepTime * 1000);
        i--;
      } catch (InterruptedException e2) {
        mReconnecting = false;
        return;
      }
    }
    mReconnecting = false;
    stopAll();
  }
    
    /*@Override
	public void onReceiveInfo(int id, String msg) {
		if (id == SERVER_BANNER) {
			SkStatus.logInfo("<strong>" + mContext.getString(R.string.log_server_banner) + "</strong> " + "<br><br><b>Server Rules And Regulations</b><br><br>\u274cNO SPAMMING!\u274c<br>\u274cNO TORRENT!\u274c<br>\u274cNO CARDING!\u274c<br>\u274cNO HACKING!\u274c<br>\u274cNO ILLEGAL ACTIVITIES!\u274c<br><br>SERVER OWNER: <b><font color=#0062AF>ROMEL P. BROSAS</font></b><br>");
		}
	}*/

  @Override
  public void onReceiveInfo(int id, String msg) {
    if (id == SERVER_BANNER) {
      SkStatus.logInfo(
          "<strong>"
              + mContext.getString(R.string.log_server_banner)
              + msg
             + "</strong> ");
           //   + "<br>Propietario: <strong><font color=#33964f>"
          //    + mContext.getString(R.string.app_name)
          //    + "</font></strong>");
    }
  }

 
  /** Debug Logger */
  @Override
  public void log(int level, String className, String message) {
    SkStatus.logDebug(String.format("%s: %s", className, message));
   
  }

  /** Vpn Tunnel */
  String serverAddr;

  protected void startTunnelVpnService() throws IOException {
		if (!mConnected) {
			throw new IOException();
		}
		SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences prefs = mConfig.getPrefsPrivate();

		// Broadcast
		IntentFilter broadcastFilter = new IntentFilter(TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST);
		broadcastFilter.addAction(TunnelVpnService.TUNNEL_VPN_START_BROADCAST);
		// Inicia Broadcast
		LocalBroadcastManager.getInstance(mContext).registerReceiver(m_vpnTunnelBroadcastReceiver, broadcastFilter);

		String m_socksServerAddress = String.format("127.0.0.1:%s", mConfig.getPrivString(Settings.PORTA_LOCAL_KEY));
		boolean m_dnsForward = mConfig.getVpnDnsForward();
		boolean m_autoPing = mConfig.setAutoPing();
		boolean m_udpForward = mConfig.getVpnUdpForward();
		String m_udpResolver = mConfig.getVpnUdpForward() ? mConfig.getVpnUdpResolver() : null;

		String NmN = mConfig.getVpnUdpResolver().toString();
		String getUdpLastString = mPref.getString(Settings.UDPRESOLVER_KEY, "");

		String servidorIP = mConfig.getPrivString(Settings.SERVIDOR_KEY);
String auto = mPref.getString(Settings.PINGER, "");
		String arr = mConfig.getPrivString(Settings.PROXY_IP_KEY);

		if (prefs.getInt(Settings.TUNNELTYPE_KEY,
				Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY) {
			try {
				servidorIP = arr;
			} catch (Exception e) {
				SkStatus.logWarning(R.string.error_proxy_invalid);

				throw new IOException(mContext.getString(R.string.error_proxy_invalid));
			}
		}

		try {
			InetAddress servidorAddr = TransportManager.createInetAddress(servidorIP);
			serverAddr = servidorIP = servidorAddr.getHostAddress();
		} catch (Exception e) {
			throw new IOException(mContext.getString(R.string.error_server_ip_invalid));
		}

		String[] m_excludeIps = { servidorIP };

		String[] m_dnsResolvers = null;
		if (m_dnsForward) {
			if (mConfig.getVpnDnsResolver2().isEmpty()) {
				m_dnsResolvers = new String[] { mConfig.getVpnDnsResolver1() };
			} else {
				m_dnsResolvers = new String[] { mConfig.getVpnDnsResolver1(), mConfig.getVpnDnsResolver2() };
			}
		} else {
			List<String> lista = VpnUtils.getNetworkDnsServer(mContext);
			m_dnsResolvers = new String[] { lista.get(0) };
		}

		/*if (!m_udpForward && !m_dnsForward && !m_autoPing) {
		          VpnStatus.logWarning(
		                  "UDPGW &amp; DNS &amp; AutoPing " + mContext.getText(R.string.disabled));
		      } else if (!m_udpForward && !m_dnsForward) {
		          VpnStatus.logWarning("UDPGW &amp; DNS " + mContext.getText(R.string.disabled));
		      } else if (!m_udpForward && !m_autoPing) {
		          VpnStatus.logWarning("UDPGW &amp; AutoPing " + mContext.getText(R.string.disabled));
		      } else if (!m_dnsForward && !m_udpForward) {
		          VpnStatus.logWarning("DNS &amp; UDPGW " + mContext.getText(R.string.disabled));
		      } else if (!m_dnsForward && !m_autoPing) {
		          VpnStatus.logWarning("DNS &amp; AutoPing " + mContext.getText(R.string.disabled));
		      } else if (!m_autoPing && !m_udpForward) {
		          VpnStatus.logWarning("UDPGW &amp; AutoPing " + mContext.getText(R.string.disabled));
		      } else if (!m_autoPing && !m_dnsForward) {
		          VpnStatus.logWarning("DNS &amp; AutoPing " + mContext.getText(R.string.disabled));
		      } else if (!m_udpForward) {
		          VpnStatus.logWarning("UDPGW " + mContext.getText(R.string.disabled));
		      } else if (!m_dnsForward) {
		          VpnStatus.logWarning("DNS " + mContext.getText(R.string.disabled));
		      } else if (!m_autoPing) {
		          VpnStatus.logWarning("AutoPing " + mContext.getText(R.string.disabled));
		      }
		*/

	

		if (m_dnsForward) {
			String primDns = mPref.getString(Settings.DNSRESOLVER_KEY1, "");
			String secDns = mPref.getString(Settings.DNSRESOLVER_KEY2, "");
			if (!primDns.isEmpty() || !secDns.isEmpty()) {
			//	VpnStatus.logWarning("DNS Enabled: Custom DNS");
				SkStatus.logWarning("Dns Resolver 1 : <strong><font color='#00acc2'>" + primDns + "</font></strong>");
			    SkStatus.logWarning("Dns Resolver 2 : <strong><font color='#00acc2'>" + secDns + "</font></strong>");
			} else {
			//	VpnStatus.logWarning("DNS Enabled: Default Google DNS");
			}
		}

        
        
        
        
        	if (m_udpForward) {
			SkStatus.logWarning("UDP GateWay : <strong><font color='#00acc2'>" + getUdpLastString + "</font></strong>");
		}  
        
        
        
        if (m_autoPing) {
			SkStatus.logWarning("Auto Ping URL : <strong><font color='#00acc2'>" + auto + "</font></strong>");
		}  
        
        
		if (isServiceVpnRunning()) {
			Log.d(TAG, "already running service");

			TunnelVpnManager tunnelManager = TunnelState.getTunnelState().getTunnelManager();

			if (tunnelManager != null) {
				tunnelManager.restartTunnel(m_socksServerAddress);
			}

			return;
		}

		Intent startTunnelVpn = new Intent(mContext, TunnelVpnService.class);
		startTunnelVpn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		TunnelVpnSettings settings = new TunnelVpnSettings(m_socksServerAddress, m_dnsForward, m_dnsResolvers,
				(m_dnsForward && m_udpResolver == null || !m_dnsForward && m_udpResolver != null), m_udpResolver,
				m_excludeIps, mConfig.getIsFilterApps(), mConfig.getIsFilterBypassMode(), mConfig.getFilterApps(),
				mConfig.getIsTetheringSubnet(), mConfig.getBypass());
		startTunnelVpn.putExtra(TunnelVpnManager.VPN_SETTINGS, settings);

		if (mContext.startService(startTunnelVpn) == null) {
			SkStatus.logWarning("Error al iniciar el servicio VPN del túnel");

			throw new IOException("El servicio VPN no pudo iniciarse");
		}

		TunnelState.getTunnelState().setStartingTunnelManager();
	}


  public static boolean
      isServiceVpnRunning() { 
     

    TunnelState tunnelState = TunnelState.getTunnelState();
    return tunnelState.getStartingTunnelManager() || tunnelState.getTunnelManager() != null;
  }

  public static Inet4Address getIPv4Addresses(InetAddress[] inetAddressArr) {
    int length = inetAddressArr.length;
    for (int i = 0; i < length; i += AUTH_TRIES) {
      InetAddress inetAddress = inetAddressArr[i];
      if (inetAddress instanceof Inet4Address) {
        return (Inet4Address) inetAddress;
       
      }
    }
    return null;
  }

  public static UDPListener getUDPListener() {
   
    return udpListener;
  }

  protected synchronized void stopTunnelVpnService() {
    if (!isServiceVpnRunning()) {
      return;
    }
    
    SkStatus.logInfo("stopping tunnel service");
    TunnelVpnManager currentTunnelManager = TunnelState.getTunnelState().getTunnelManager();
    if (currentTunnelManager != null) {
      currentTunnelManager.signalStopService();
      
    }
    // Parando Broadcast
    LocalBroadcastManager.getInstance(mContext)
       
        .unregisterReceiver(m_vpnTunnelBroadcastReceiver);
  }

  // Local BroadcastReceiver
  private BroadcastReceiver m_vpnTunnelBroadcastReceiver =
      new BroadcastReceiver() {
        @Override
        public synchronized void onReceive(Context context, Intent intent) {
          final String action = intent.getAction();
          if (TunnelVpnService.TUNNEL_VPN_START_BROADCAST.equals(action)) {
            boolean startSuccess =
                intent.getBooleanExtra(TunnelVpnService.TUNNEL_VPN_START_SUCCESS_EXTRA, true);
            if (!startSuccess) {
             
              stopAll();
            }
          } else if (TunnelVpnService.TUNNEL_VPN_DISCONNECT_BROADCAST.equals(action)) {
            stopAll();
          }
        }
      };

   private
  boolean isudpmode() {
    return preferencias.getInt(Settings.TUNNELTYPE_KEY, 0) == Settings.bTUNNEL_TYPE_UDP;
  }

  private boolean isv2raymode() {
    return preferencias.getInt(Settings.TUNNELTYPE_KEY, 0) == Settings.bTUNNEL_TYPE_V2RAY;
  }

  private
  void normalpanda() {
   
    connectivityManager =
        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkRequest request = new NetworkRequest.Builder().build();
    callback =
        new ConnectivityManager.NetworkCallback() {
          @Override
          public void onAvailable(Network network) {
           
          }

          @Override
          public void onLost(Network network) {
           if (pandaspeed) {
              if (isudpmode() || isv2raymode()) {
                pandxd();
              }
            }
          }
        };
    connectivityManager.registerNetworkCallback(request, callback);
  }
  
}
