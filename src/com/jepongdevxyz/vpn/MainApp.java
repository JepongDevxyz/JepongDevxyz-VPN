package com.jepongdevxyz.vpn;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatDelegate;
import com.jepongdevxyz.vpn.config.Settings;
import java.io.StringReader;
import org.conscrypt.Conscrypt;
import java.security.Security;

public class MainApp extends Application {
	private int night_mode;
	private Settings mConfig;
	public static SharedPreferences sp;
	public static SharedPreferences dsp;
	public static Context ctx;

	@Override
	public void onCreate() {
		super.onCreate();
	/*	if (!chetoszclass.z(getApplicationContext()).equals(chetoszclass.x())) {
			return;
		}*/
//		Logger.initialize(this);
		try {
			Process p = Runtime.getRuntime().exec("su");
		} catch (Exception e) {
		}
		Security.insertProviderAt(Conscrypt.newProvider(), 1);
		mConfig = new Settings(this);
		sp = mConfig.getPrefsPrivate();
		dsp = PreferenceManager.getDefaultSharedPreferences(this);
		ctx = this;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		// LocaleHelper.setLocale(this);
	}
    
    public static SharedPreferences getSharedPrefs() {
        return sp;
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// LocaleHelper.setLocale(this);
	}


	
}
