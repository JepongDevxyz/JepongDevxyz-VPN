package com.jepongdevxyz.vpn.activities;

import android.content.*;
import android.content.pm.*;
import android.os.*;
import android.view.*;
import android.graphics.*;
import androidx.preference.PreferenceManager;
import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.MainActivity;

public class SplashActivity extends BaseActivity  {
	
	private SharedPreferences sharedPreferences;
	
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
                @Override
                public void run() {
					// inicia atividade principal
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);

					// encerra o launcher
					finish();
                }
            }, 600);
    }
}
