package com.jepongdevxyz.vpn.preference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import android.os.Handler;
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;
import com.jepongdevxyz.vpn.MainActivity;
import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.MyApplication;
import com.jepongdevxyz.vpn.config.Settings;
import com.jepongdevxyz.vpn.config.SettingsConstants;
import com.jepongdevxyz.vpn.util.Utils;
import com.jepongdevxyz.vpn.logger.*;
import com.jepongdevxyz.vpn.activities.SplashActivity;


public class SettingsPreference extends PreferenceFragmentCompat
    implements Preference.OnPreferenceChangeListener, SettingsConstants, SkStatus.StateListener {
  private Handler mHandler;
  private SharedPreferences mPref;

  public static final String SSHSERVER_PREFERENCE_KEY = "screenSSHSettings",
      ADVANCED_SCREEN_PREFERENCE_KEY = "screenAdvancedSettings";

  private String[] settings_disabled_keys = {
    UDPFORWARD_KEY,
    TETHERING_SUBNET,
    MAXIMO_THREADS_KEY,
    AUTO_PINGER,
    WAKELOCK_KEY,
        DNSRESOLVER_KEY1,
        DNSRESOLVER_KEY2,
        DNSFORWARD_KEY,
    VIBRATE,
    DISABLE_DELAY_KEY,
    UDPRESOLVER_KEY,
    MODO_NOTURNO_KEY,
    PINGER_KEY,
    SSH_COMPRESSION
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mHandler = new Handler();
  }

  @Override
  public void onResume() {
    super.onResume();

    SkStatus.addStateListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();

    SkStatus.removeStateListener(this);
  }

  @Override
  public void onCreatePreferences(Bundle bundle, String root_key) {
    // Load the Preferences from the XML file
    setPreferencesFromResource(R.xml.app_preferences, root_key);

    mPref = getPreferenceManager().getDefaultSharedPreferences(getContext());

    Preference udpForwardPreference = (SwitchPreferenceCompat) findPreference(UDPFORWARD_KEY);
    udpForwardPreference.setOnPreferenceChangeListener(this);

        Preference dnsForwardPreference = (SwitchPreferenceCompat)
			findPreference(DNSFORWARD_KEY);
		dnsForwardPreference.setOnPreferenceChangeListener(this);	
        
    ListPreference modoNoturno = (ListPreference) findPreference(MODO_NOTURNO_KEY);
    modoNoturno.setOnPreferenceChangeListener(this);

    // update view
    setRunningTunnel(SkStatus.isTunnelActive());
  }

  private void onChangeUseVpn(boolean use_vpn) {
    Preference udpResolverPreference = (EditTextPreference) findPreference(UDPRESOLVER_KEY);

        Preference dnsResolverPreference = (EditTextPreference)
			findPreference(DNSRESOLVER_KEY1);
        Preference dnsResolverPreference2 = (EditTextPreference)
            findPreference(DNSRESOLVER_KEY2);    
        
        
    for (String key : settings_disabled_keys) {
      getPreferenceManager().findPreference(key).setEnabled(use_vpn);
    }

    use_vpn = true;
    if (use_vpn) {
      boolean isUdpForward = mPref.getBoolean(UDPFORWARD_KEY, false);

      boolean isDnsForward = mPref.getBoolean(DNSFORWARD_KEY, false);
			
			udpResolverPreference.setEnabled(isUdpForward);
			dnsResolverPreference.setEnabled(isDnsForward);
            dnsResolverPreference2.setEnabled(isDnsForward);
    } else {
      String[] list = {
        UDPFORWARD_KEY, AUTO_PINGER, UDPRESOLVER_KEY,
                DNSFORWARD_KEY,
				DNSRESOLVER_KEY1,
                DNSRESOLVER_KEY2
      };
      for (String key : list) {
        getPreferenceManager().findPreference(key).setEnabled(false);
      }
    }
  }

  private void setRunningTunnel(boolean isRunning) {
    if (isRunning) {
      for (String key : settings_disabled_keys) {
        getPreferenceManager().findPreference(key).setEnabled(false);
      }
    } else {
      onChangeUseVpn(true);
    }
  }

  /** Preference.OnPreferenceChangeListener Implementação */
  @Override
  public boolean onPreferenceChange(Preference pref, Object newValue) {
    switch (pref.getKey()) {
      
            
            case DNSFORWARD_KEY:
				boolean isDnsForward = (boolean) newValue;

				Preference dnsResolverPreference = (EditTextPreference)
					findPreference(DNSRESOLVER_KEY1);
				dnsResolverPreference.setEnabled(isDnsForward);
                
                Preference dnsResolverPreference2 = (EditTextPreference)
                    findPreference(DNSRESOLVER_KEY2);
                dnsResolverPreference2.setEnabled(isDnsForward);
			break;
            
            

      case MODO_NOTURNO_KEY:
        final String enableModoNoturno = (String) newValue;

        if (enableModoNoturno.equals(mPref.getString(MODO_NOTURNO_KEY, "off"))) {
          return false;
        }

        Context context = MyApplication.getApp();

        new Settings(context).setModoNoturno(enableModoNoturno);

        // reinicia app
        Intent startActivity = new Intent(context, MainActivity.class);
        int pendingIntentId = 123456;
        PendingIntent pendingIntent =
            PendingIntent.getActivity(
                context, pendingIntentId, startActivity, PendingIntent.FLAG_IMMUTABLE);

        restart_app();
        Utils.exitAll(getActivity());

        getActivity().finish();
        return false;

      case UDPFORWARD_KEY:
        boolean isUdpForward = (boolean) newValue;

        Preference udpResolverPreference = (EditTextPreference) findPreference(UDPRESOLVER_KEY);
        udpResolverPreference.setEnabled(isUdpForward);
        break;
    }
    return true;
  }

  private void restart_app() {
    Context context = MyApplication.getApp();
    context.startActivity(
        Intent.makeRestartActivityTask(
            context
                .getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName())
                .getComponent()));
    Runtime.getRuntime().exit(0);
  }

  @Override
  public void updateState(
      String state, String logMessage, int localizedResId, ConnectionStatus level, Intent intent) {
    mHandler.post(
        new Runnable() {
          @Override
          public void run() {
            setRunningTunnel(SkStatus.isTunnelActive());
          }
        });
  }
}
