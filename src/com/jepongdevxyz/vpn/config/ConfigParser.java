package com.jepongdevxyz.vpn.config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.jepongdevxyz.vpn.activities.BaseActivity;
import com.jepongdevxyz.vpn.util.VPNUtils;

import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;

import android.content.pm.PackageInfo;
import java.util.Calendar;

import android.content.pm.PackageManager;

import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.logger.SkStatus;
import com.jepongdevxyz.vpn.util.Cripto;
import com.jepongdevxyz.vpn.util.FileUtils;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;

import android.content.SharedPreferences;
import android.os.Build;
import com.kimchangyoun.rootbeerFresh.RootBeer;

import android.content.pm.InstallSourceInfo;


/**
* @author SlipkHunter
*/
public class ConfigParser extends BaseActivity
{
	private static final String TAG = ConfigParser.class.getSimpleName();
	public static final String CONVERTED_PROFILE = "converted Profile";
	
	public static final String FILE_EXTENSAO = "v2i";
	private static final String KEY_PASSWORD = "cinbdf665$4";
	private Settings mConfig;
	public static String GOOGLE_PLAY = "com.android.vending";
	
	private static final String
		SETTING_VERSION = "file.appVersionCode",
		SETTING_VALIDADE = "file.validade",
		SETTING_PROTEGER = "file.proteger",
		SETTING_AUTOR_MSG = "file.msg";
	
	
	public static boolean convertInputAndSave(InputStream input, Context mContext)
			throws IOException {
		Properties mConfigFile = new Properties();
		
		Settings settings = new Settings(mContext);
		SharedPreferences.Editor prefsEdit = settings.getPrefsPrivate()
			.edit();
		
		try {
			
			InputStream decodedInput = decodeInput(input);
			
			try {
				mConfigFile.loadFromXML(decodedInput);
			} catch(FileNotFoundException e) {
				throw new IOException("File Not Found");
			} catch(IOException e) {
				throw new Exception("Error Unknown", e);
			}

			// versão check
			int versionCode = Integer.parseInt(mConfigFile.getProperty(SETTING_VERSION));

			if (versionCode > getBuildId(mContext)) {
				throw new IOException(mContext.getString(R.string.alert_update_app));
			}

			// validade check
			String msg = mConfigFile.getProperty(SETTING_AUTOR_MSG);
            String autor = mConfigFile.getProperty("autorkey");
            
            
			boolean mIsProteger = mConfigFile.getProperty(SETTING_PROTEGER).equals("1") ? true : false;
			long mValidade = 0;
			
			try {
				mValidade = Long.parseLong(mConfigFile.getProperty(SETTING_VALIDADE));
			} catch(Exception e) {
				throw new IOException(mContext.getString(R.string.alert_update_app));
			}

			if (!mIsProteger || mValidade < 0) {
				mValidade = 0;
			}
			else if (mValidade > 0 && isValidadeExpirou(mValidade)){
				throw new IOException(mContext.getString(R.string.error_settings_expired));
			}
            
            
            boolean mBlocWifi = mConfigFile.getProperty("blockwifi").equals("1") ? true : false;
			
			// bloqueia root
			boolean isBloquearRoot = false;
			String _blockRoot = mConfigFile.getProperty("bloquearRoot");
			if (_blockRoot != null) {
				isBloquearRoot = _blockRoot.equals("1") ? true : false;
				if (isBloquearRoot) {
					if (isDeviceRooted(mContext)) {
						throw new IOException(mContext.getString(R.string.error_root_detected));
					}
				} 
			}
			
			//Only Data
			boolean  mOnlyDataMovil = false;
			String _only_data = mConfigFile.getProperty("onlydata");
			if (_only_data != null) {
				mOnlyDataMovil = _only_data.equals("1") ? true: false;
				/*if (mOnlyDataMovil) {
					if (WifiCheckConnected(mContext)) {
						throw new IOException(mContext.getString(R.string.block_only_data_movile));
					}
				}*/
				
			}
			
			//Only Instalation From Play Store
			boolean mOnlyPlayStore = false;
			String _only_play_store = mConfigFile.getProperty("onlyPlayStore");
			String packageName = mContext.getPackageName();
			String installerPackageName = "";
			if (Build.VERSION.SDK_INT >= 30) {
				PackageManager pm = mContext.getPackageManager();
				InstallSourceInfo info = pm.getInstallSourceInfo(packageName);
				
				if (info != null) {
					installerPackageName = info.getInstallingPackageName();
				}
			} else {
				PackageManager pm = mContext.getPackageManager();
				installerPackageName = pm.getInstallerPackageName(packageName);
				
			}
			if (_only_play_store != null) {
				mOnlyPlayStore = _only_play_store.equals("1") ? true: false;
				if (mOnlyPlayStore) {
					
					if (!"com.android.vending".equals(installerPackageName)) {
						throw new IOException(mContext.getString(R.string.only_play_store_instalation));
					}
					
				}
			}
			
			//Block Operator
			boolean mBlockOperador = false;
			String _block_operator = mConfigFile.getProperty("blockOperator");
			if (_block_operator != null) {
				mBlockOperador = _block_operator.equals("1") ? true : false;
				if(mBlockOperador) {
					TelephonyManager telephonyManager = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE));
					String MyPhoneOperator = telephonyManager.getSimOperatorName();
					String CheckOperator = mConfigFile.getProperty(Settings.CONFIG_OPERATOR_NAME);
					if (!CheckOperator.equals(telephonyManager.getSimOperatorName())) {
						throw new IOException(mContext.getString(R.string.operator_block_tittle));
					}
					
				}
			}
			
			//BlockHWID
			boolean mBlockHwid = false;
			String _block_hwid = mConfigFile.getProperty("blockHwid");
			if (_block_hwid != null) {
				mBlockHwid = _block_hwid.equals("1") ? true : false;
				if (mBlockHwid) {
					//String MyHWID = VPNUtils.getHWID();
					String CheckHWID = mConfigFile.getProperty(Settings.CONFIG_HWDI_STRING);
					if (!CheckHWID.equals(VPNUtils.getHWID())) {
						throw new IOException(mContext.getString(R.string.block_hwid_tittle));
					}
				}
			}
			
			boolean mLoginHwid = false;
			String _login_hwid = mConfigFile.getProperty("loginHwid");
			if (_login_hwid != null) {
				mLoginHwid = _login_hwid.equals("1") ? true : false;
				/**if (mLoginHwid) {
					
				}**/
			}
			
			boolean Pass = false;
			String _pass = mConfigFile.getProperty("cp");
			if (_pass != null) {
				Pass = _pass.equals("1") ? true : false;
					}
			

			

			try {
				String customSNI = mConfigFile.getProperty(Settings.CUSTOM_SNI);
				String mServidor = mConfigFile.getProperty(Settings.SERVIDOR_KEY);
				String mServidorPorta = mConfigFile.getProperty(Settings.SERVIDOR_PORTA_KEY);
				String mUsuario = mConfigFile.getProperty(Settings.USUARIO_KEY);
				String mSenha = mConfigFile.getProperty(Settings.SENHA_KEY);
                String mUDPGATESCCN = mConfigFile.getProperty(Settings.UDP_SERVER);
				String mUDPaGATESCCN = mConfigFile.getProperty(Settings.UDP_AUTH);
				String mUDPbGATESCCN = mConfigFile.getProperty(Settings.UDP_OBFS);
				String mUDPdGATESCCN = mConfigFile.getProperty(Settings.UDP_DOWN);
				String mUDPuGATESCCN = mConfigFile.getProperty(Settings.UDP_UP);
				String mv2rayGATESCCN = mConfigFile.getProperty(Settings.V2RAY_JSON);
                String mInputL = mConfigFile.getProperty(Settings.CONFIG_LINE_INPUT);
                String InputProxy = mConfigFile.getProperty(Settings.PROXY_LINE_INPUT);
				int mPortaLocal = Integer.parseInt(mConfigFile.getProperty(Settings.PORTA_LOCAL_KEY));
				int mTunnelType = Settings.bTUNNEL_TYPE_SSH_DIRECT;
						
				String chave = mConfigFile.getProperty(Settings.CHAVE_KEY);					
    			String nameserver = mConfigFile.getProperty(Settings.NAMESERVER_KEY);					
				String dns = mConfigFile.getProperty(Settings.DNS_KEY);
						
				String _tunnelType = mConfigFile.getProperty(Settings.TUNNELTYPE_KEY);
				if (!_tunnelType.isEmpty()) {
					/**
					* Mantêm compatibilidade
					*/
					if (_tunnelType.equals(Settings.TUNNEL_TYPE_SSH_PROXY)) {
						mTunnelType = Settings.bTUNNEL_TYPE_SSH_PROXY;
					} else if (_tunnelType.equals(Settings.bTUNNEL_TYPE_SSH_PROXY)) {
						mTunnelType = Settings.bTUNNEL_TYPE_SSH_PROXY;
					} else if (!_tunnelType.equals(Settings.TUNNEL_TYPE_SSH_DIRECT)) {
						mTunnelType = Integer.parseInt(_tunnelType);
					} else if (!_tunnelType.equals(Settings.bTUNNEL_TYPE_SSL_RP)) {
						mTunnelType = Integer.parseInt(_tunnelType);

					} else if (!_tunnelType.equals(Settings.bTUNNEL_TYPE_PAY_SSL)) {
						mTunnelType = Integer.parseInt(_tunnelType);
					}
				}
				
				if (mServidor == null) {
					throw new Exception();
				}

				String _proxyIp = mConfigFile.getProperty(Settings.PROXY_IP_KEY);
				String _proxyPort = mConfigFile.getProperty(Settings.PROXY_PORTA_KEY);
				prefsEdit.putString(Settings.PROXY_IP_KEY, _proxyIp != null ? _proxyIp : "");
				prefsEdit.putString(Settings.PROXY_PORTA_KEY, _proxyPort != null ? _proxyPort : "");

				prefsEdit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, !mConfigFile.getProperty(Settings.PROXY_USAR_DEFAULT_PAYLOAD).equals("1") ? false : true);
				
				String _customPayload = mConfigFile.getProperty(Settings.CUSTOM_PAYLOAD_KEY);
				prefsEdit.putString(Settings.CUSTOM_PAYLOAD_KEY, _customPayload != null ? _customPayload : "");
				
				if (mIsProteger) {
                    prefsEdit.putString(Settings.CONFIG_AUTOR_KEY, autor != null ? autor : "");
					prefsEdit.putString(Settings.CONFIG_MENSAGEM_KEY, msg != null ? msg : "");
					
					new Settings(mContext)
						.setModoDebug(false);

					String pedirLogin = mConfigFile.getProperty("file.pedirLogin");
					if (pedirLogin != null)
						prefsEdit.putBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, pedirLogin.equals("1") ? true : false);
					else
						prefsEdit.putBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false);
				}
				else {
					prefsEdit.putString(Settings.CONFIG_MENSAGEM_KEY, "");
					prefsEdit.putBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false);
				}
                if (mUsuario.isEmpty() && mSenha.isEmpty()) {
				    prefsEdit.putString("enable_auth", "_true");
				} else {
					prefsEdit.putString("enable_auth", "_false");
				}
				
				prefsEdit.putString(Settings.CHAVE_KEY, chave);
				prefsEdit.putString(Settings.NAMESERVER_KEY, nameserver);
				prefsEdit.putString(Settings.DNS_KEY, dns);
                prefsEdit.putBoolean(Settings.CONFIG_BLOCK_ONLY_DATA, mBlocWifi);
                prefsEdit.putString(Settings.UDP_SERVER, mUDPGATESCCN);
				prefsEdit.putString(Settings.UDP_AUTH, mUDPaGATESCCN);
				prefsEdit.putString(Settings.UDP_OBFS, mUDPbGATESCCN);
				prefsEdit.putString(Settings.UDP_DOWN, mUDPdGATESCCN);
				prefsEdit.putString(Settings.UDP_UP, mUDPuGATESCCN);
				prefsEdit.putString(Settings.V2RAY_JSON, mv2rayGATESCCN);
						
				prefsEdit.putString(Settings.CUSTOM_SNI, customSNI);
				prefsEdit.putString(Settings.SERVIDOR_KEY, mServidor);
				prefsEdit.putString(Settings.SERVIDOR_PORTA_KEY, mServidorPorta);
				prefsEdit.putString(Settings.USUARIO_KEY, mUsuario);
				prefsEdit.putString(Settings.SENHA_KEY, mSenha);
				prefsEdit.putString(Settings.PORTA_LOCAL_KEY, Integer.toString(mPortaLocal));
                prefsEdit.putString(Settings.CONFIG_LINE_INPUT, mInputL);
                settings.setLineProxy(InputProxy);
				prefsEdit.putInt(Settings.TUNNELTYPE_KEY, mTunnelType);
				prefsEdit.putBoolean(Settings.CONFIG_PROTEGER_KEY, mIsProteger);
				prefsEdit.putLong(Settings.CONFIG_VALIDADE_KEY, mValidade);
				prefsEdit.putBoolean(Settings.BLOQUEAR_ROOT_KEY, isBloquearRoot);
				
				String _isDnsForward = mConfigFile.getProperty(Settings.DNSFORWARD_KEY);
				boolean isDnsForward = _isDnsForward != null && _isDnsForward.equals("0") ? false : true;
				String dnsResolver1 = mConfigFile.getProperty(Settings.DNSRESOLVER_KEY1);
				String dnsResolver2 = mConfigFile.getProperty(Settings.DNSRESOLVER_KEY2);
				settings.setVpnDnsForward(isDnsForward);
				settings.setVpnDnsResolver1(dnsResolver1);
				settings.setVpnDnsResolver2(dnsResolver2);
                String _isWakelock = mConfigFile.getProperty(Settings.WAKELOCK_KEY);
                boolean isWakelock = _isWakelock != null && _isWakelock.equals("0") ? false : true;
				settings.setWakelock(isWakelock);
                String _is_replace = mConfigFile.getProperty(Settings.AUTOREPLACE_KEY);
                boolean isreplace = _is_replace != null && _is_replace.equals("0") ? false : true;
				String _isUdpForward = mConfigFile.getProperty(Settings.UDPFORWARD_KEY);
				boolean isUdpForward = _isUdpForward != null && _isUdpForward.equals("1") ? true : false;
				String udpResolver = mConfigFile.getProperty(Settings.UDPRESOLVER_KEY);
				settings.setVpnUdpForward(isUdpForward);
				settings.setVpnUdpResolver(udpResolver);
                settings.setAutoReplace(isreplace);
				
			} catch(Exception e) {
				if (settings.getModoDebug()) {
					SkStatus.logException("Error Settings", e);
				}
				throw new IOException(mContext.getString(R.string.error_file_settings_invalid));
			}
			
			return prefsEdit.commit();
		
		} catch(IOException e) {
			throw e;
		} catch(Exception e) {
			throw new IOException(mContext.getString(R.string.error_file_invalid), e);
		} catch (Throwable e) {
			throw new IOException(mContext.getString(R.string.error_file_invalid));
		}
	}
	
	public static void convertDataToFile(OutputStream fileOut, Context mContext,
			boolean mIsProteger, boolean mPedirSenha, boolean isBloquearRoot, String mMensagem, long mValidade, boolean mOnlyDataMovil, boolean mOnlyPlayStore, boolean mBlockOperador, String mOperador, boolean mBlockHwid, String mHwid, boolean mLoginHwid, String autormsg)
				throws IOException {
		
		Properties mConfigFile = new Properties();
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		
		Settings settings = new Settings(mContext);
		SharedPreferences prefs = settings.getPrefsPrivate();
		
		try {
			int targerId = getBuildId(mContext);
			// para versões betas
			//targerId = 15;
			
			mConfigFile.setProperty(SETTING_VERSION, Integer.toString(targerId));

			mConfigFile.setProperty(SETTING_AUTOR_MSG, mMensagem);
            
            
            
            
            
       
            
            
            mConfigFile.setProperty("autorkey", autormsg);
			mConfigFile.setProperty(SETTING_PROTEGER, mIsProteger ? "1" : "0");
            mConfigFile.setProperty("bloquearRoot", isBloquearRoot ? "1" : "0");
            mConfigFile.setProperty("onlydata", mOnlyDataMovil ? "1" : "0");
            mConfigFile.setProperty("onlyPlayStore", mOnlyPlayStore ? "1" : "0");
            mConfigFile.setProperty("blockOperator", mBlockOperador ? "1" : "0");
            mConfigFile.setProperty("operatorname", mOperador);
            mConfigFile.setProperty("blockHwid", mBlockHwid ? "1" : "0");
            mConfigFile.setProperty("hwidstring", mHwid);
            mConfigFile.setProperty("loginHwid",mLoginHwid ? "1" : "0");
            mConfigFile.setProperty("blockwifi", mOnlyDataMovil ? "1" : "0");
						
			mConfigFile.setProperty(SETTING_VALIDADE, Long.toString(mValidade));
			mConfigFile.setProperty("file.pedirLogin", mPedirSenha ? "1" : "0");

			String server = prefs.getString(Settings.SERVIDOR_KEY, "");
			String server_port = prefs.getString(Settings.SERVIDOR_PORTA_KEY, "");
			
			if (mIsProteger && (server.isEmpty() || server_port.isEmpty())) {
			   throw new Exception();
			}		
			mConfigFile.setProperty(Settings.SERVIDOR_KEY, server);
			mConfigFile.setProperty(Settings.SERVIDOR_PORTA_KEY, server_port);
            mConfigFile.setProperty(Settings.CONFIG_LINE_INPUT, prefs.getString(Settings.CONFIG_LINE_INPUT, ""));
            mConfigFile.setProperty(Settings.PROXY_LINE_INPUT, settings.getLineProxy());
			if (mLoginHwid) {
                mConfigFile.setProperty(Settings.USUARIO_KEY, VPNUtils.getHWID());
                mConfigFile.setProperty(Settings.SENHA_KEY, VPNUtils.getHWID());
                
                } else {
                mConfigFile.setProperty(Settings.USUARIO_KEY, prefs.getString(Settings.USUARIO_KEY, ""));
                mConfigFile.setProperty(Settings.SENHA_KEY, prefs.getString(Settings.SENHA_KEY, ""));
                }
			mConfigFile.setProperty(Settings.PORTA_LOCAL_KEY, prefs.getString(Settings.PORTA_LOCAL_KEY, "1080"));

			mConfigFile.setProperty(Settings.TUNNELTYPE_KEY, Integer.toString(prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT)));
			
			mConfigFile.setProperty(Settings.DNSFORWARD_KEY, settings.getVpnDnsForward() ? "1" : "0");
			mConfigFile.setProperty(Settings.DNSRESOLVER_KEY1, settings.getVpnDnsResolver1());
			mConfigFile.setProperty(Settings.DNSRESOLVER_KEY2, settings.getVpnDnsResolver2());
			mConfigFile.setProperty(Settings.UDP_SERVER, prefs.getString(Settings.UDP_SERVER, ""));
			mConfigFile.setProperty(Settings.UDP_AUTH, prefs.getString(Settings.UDP_AUTH, ""));
			mConfigFile.setProperty(Settings.UDP_OBFS, prefs.getString(Settings.UDP_OBFS, ""));
			mConfigFile.setProperty(Settings.UDP_DOWN, prefs.getString(Settings.UDP_DOWN, ""));
			mConfigFile.setProperty(Settings.UDP_UP, prefs.getString(Settings.UDP_UP, ""));
			mConfigFile.setProperty(Settings.V2RAY_JSON, prefs.getString(Settings.V2RAY_JSON, ""));
			mConfigFile.setProperty(Settings.UDPFORWARD_KEY, settings.getVpnUdpForward() ? "1" : "0");
			mConfigFile.setProperty(Settings.UDPRESOLVER_KEY, settings.getVpnUdpResolver());
			mConfigFile.setProperty(Settings.WAKELOCK_KEY, settings.getWakelock() ? "1" : "0");
			mConfigFile.setProperty(Settings.PROXY_IP_KEY, prefs.getString(Settings.PROXY_IP_KEY, ""));
			mConfigFile.setProperty(Settings.PROXY_PORTA_KEY, prefs.getString(Settings.PROXY_PORTA_KEY, ""));

			String isDefaultPayload = prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true) ? "1" : "0";
			String customPayload = prefs.getString(Settings.CUSTOM_PAYLOAD_KEY, "");
			String customSNI = prefs.getString(Settings.CUSTOM_SNI, "");
			
			String chave = prefs.getString(Settings.CHAVE_KEY, "");
			String nameserver = prefs.getString(Settings.NAMESERVER_KEY, "");
			String dns = prefs.getString(Settings.DNS_KEY, "");
				String serv = prefs.getString(Settings.UDP_SERVER, "");
           String obfs = prefs.getString(Settings.UDP_OBFS, "");
           String auth = prefs.getString(Settings.UDP_AUTH, "");
            String dow = prefs.getString(Settings.UDP_DOWN, "");
            String up = prefs.getString(Settings.UDP_UP, "");
            
            
			if (mIsProteger && isDefaultPayload.equals("0") && customPayload.isEmpty()) {
				throw new IOException();
			}
			
			int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
            
            if (tunnelType == Settings.bTUNNEL_TYPE_UDP) {
				if (mIsProteger && (
                serv.isEmpty() 
                || 
                obfs.isEmpty()
                ||
                auth.isEmpty()
                ||
                dow.isEmpty()
                ||
                up.isEmpty()
                
                
                
                )) {
					throw new IOException();
				}
			}		
            
            
            
			if (tunnelType == Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL) {
				if (mIsProteger && customSNI.isEmpty()) {
					throw new IOException();
				}
				} else if (tunnelType == Settings.bTUNNEL_TYPE_SSL_RP){
				if (mIsProteger && customSNI.isEmpty()) {
					throw new IOException();
			   }
			} else if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS) {
			   if (mIsProteger && (chave.isEmpty() || nameserver.isEmpty() || dns.isEmpty())) {
						throw new IOException();
			   }
			}		
	 		mConfigFile.setProperty(Settings.PROXY_USAR_DEFAULT_PAYLOAD, isDefaultPayload);
			mConfigFile.setProperty(Settings.CUSTOM_PAYLOAD_KEY, customPayload);
			mConfigFile.setProperty(Settings.CHAVE_KEY, chave);
			mConfigFile.setProperty(Settings.NAMESERVER_KEY, nameserver);
			mConfigFile.setProperty(Settings.DNS_KEY, dns);
			mConfigFile.setProperty(Settings.CUSTOM_SNI, customSNI);
			
		} catch(Exception e) {
			throw new IOException(mContext.getString(R.string.error_file_settings_invalid));
		}

		try {
			mConfigFile.storeToXML(tempOut,
				"Archivo de Configuracion");
		} catch(FileNotFoundException e) {
			throw new IOException("File Not Found");
		} catch(IOException e) {
			throw new IOException("Error Unknown", e);
		}
		
		try {

			InputStream input_encoded = encodeInput(new ByteArrayInputStream(tempOut.toByteArray()));
				FileUtils.copiarArquivo(input_encoded, fileOut);

		} catch(Throwable e) {
			throw new IOException(mContext.getString(R.string.error_save_settings));
		}
	}


	/**
	* Criptografia
	*/
	
	private static InputStream encodeInput(InputStream in) throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		Cripto.encrypt(KEY_PASSWORD, in, out);
		
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	private static InputStream decodeInput(InputStream in) throws Throwable {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Cripto.decrypt(KEY_PASSWORD, in, out);
		
		return new ByteArrayInputStream(out.toByteArray());
	}
	
	
	/**
	* Utils
	*/
	
	public static boolean isValidadeExpirou(long validadeDateMillis) {
		if (validadeDateMillis == 0) {
			return false;
		}
		
		// Get Current Date
		long date_atual = Calendar.getInstance()
			.getTime().getTime();
		
		if (date_atual >= validadeDateMillis) {
			return true;
		}
		
		return false;
	}
	
	public static int getBuildId(Context context) throws IOException {
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pinfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			throw new IOException("Build ID not found");
		}
	}
	
	public static boolean WifiCheckConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		
		boolean mWifiCheck = mWifi.isConnected();
		
		return mWifi.isConnected();
		
		
	}
	
	/**public boolean verifyInstallerId(Context context) {
    // A list with valid installers package name
    List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

    // The package name of the app that has installed your app
    final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

    // true if your app has been downloaded from Play Store 
	return installer != null && validInstallers.contains(installer);
	}**/
	
	public static boolean isDeviceRooted(Context context) {
        /*for (String pathDir : System.getenv("PATH").split(":")){
			if (new File(pathDir, "su").exists()) {
				return true;
			}
		}
		
		Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }*/
		
		RootBeer rootBeer = new RootBeer(context);
		
		boolean simpleTests = rootBeer.detectRootManagementApps() || rootBeer.detectPotentiallyDangerousApps() || rootBeer.checkForBinary("su")
			|| rootBeer.checkForDangerousProps() || rootBeer.checkForRWPaths()
			|| rootBeer.detectTestKeys() || rootBeer.checkSuExists() || rootBeer.checkForRootNative() || rootBeer.checkForMagiskBinary();
		//boolean experiementalTests = rootBeer.checkForMagiskNative();
			
		return simpleTests;
	}

}
