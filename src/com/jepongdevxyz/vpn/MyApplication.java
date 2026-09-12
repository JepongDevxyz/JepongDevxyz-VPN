package com.jepongdevxyz.vpn;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;

import com.jepongdevxyz.vpn.config.Settings;

import android.content.res.Configuration;
import com.jepongdevxyz.vpn.preference.LocaleHelper;

public class MyApplication extends Application {
	public static final String PREFS_GERAL = "SocksHttpGERAL";
	
	private static MyApplication mApp;
    private static SharedPreferences sp;
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		mApp = this;
		
		// captura dados para análise
		/*new FlurryAgent.Builder()
			.withCaptureUncaughtExceptions(true)
            .withIncludeBackgroundSessionsInMetrics(true)
            .withLogLevel(Log.VERBOSE)
            .withPerformanceMetrics(FlurryPerformance.ALL)
			.build(this, APP_FLURRY_KEY);*/
			
		// inicia
	//	SocksHttpCore.init(this);
		
		// protege o app
	//	SkProtect.init(this);
		
		// modo noturno
		setModoNoturno(this);
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		//LocaleHelper.setLocale(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//LocaleHelper.setLocale(this);
	}
	
	private void setModoNoturno(Context context) {
		boolean is = new Settings(context)
			.getModoNoturno().equals("on");

		int night_mode = is ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
		AppCompatDelegate.setDefaultNightMode(night_mode);

	}
	
	public static MyApplication getApp() {
		return mApp;
	}
    
    public static SharedPreferences getSharedPrefs() {
        return sp;
		}
}
