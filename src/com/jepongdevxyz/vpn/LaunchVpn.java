package com.jepongdevxyz.vpn;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import com.jepongdevxyz.vpn.logger.SkStatus;
import com.jepongdevxyz.vpn.logger.ConnectionStatus;
import com.jepongdevxyz.vpn.tunnel.TunnelManagerHelper;
import android.os.Build;
import com.jepongdevxyz.vpn.config.Settings;
import android.net.VpnService;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.widget.Toast;
import com.jepongdevxyz.vpn.tunnel.TunnelUtils;
import android.widget.EditText;
import android.text.InputType;
import com.jepongdevxyz.vpn.R;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.annotation.SuppressLint;
import android.widget.CheckBox;
import androidx.core.widget.CompoundButtonCompat;

import android.content.DialogInterface;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.jepongdevxyz.vpn.config.PasswordCache;
import android.content.SharedPreferences;
import android.widget.ImageButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

public class LaunchVpn extends AppCompatActivity implements DialogInterface.OnCancelListener {

  private Settings mConfig;
  public static final String EXTRA_HIDELOG = "com.jepongdevxyz.vpn.showNoLogWindow";
  public static final String CLEARLOG = "clearlogconnect";
  private static final int START_VPN_PROFILE = 70;
  private String mTransientAuthPW;
  private boolean mhideLog = false;
  private boolean isMostrarSenha = false;
  SharedPreferences prefs;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.launchvpn);
    mConfig = new Settings(this);
    prefs = mConfig.getPrefsPrivate();

    startVpnFromIntent();
  }

  protected void startVpnFromIntent() {
        // Resolve the intent
        final Intent intent = getIntent();
        final String action = intent.getAction();	
		// If the intent is a request to create a shortcut, we'll do that and exit
        if (Intent.ACTION_MAIN.equals(action)) {
            // Check if we need to clear the log
            if (mConfig.getAutoClearLog())
				SkStatus.clearLog();
            mhideLog = intent.getBooleanExtra(EXTRA_HIDELOG, false);	
            launchVPN();
        }
    }
	

	@Override
	public void onCancel(DialogInterface p1) {
		SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
								   ConnectionStatus.LEVEL_NOTCONNECTED);
		finish();
	}
	
	private void showLogWindow() {
        Intent updateView = new Intent("com.jepongdevxyz.vpn:openLogs");
		LocalBroadcastManager.getInstance(this)
			.sendBroadcast(updateView);
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
				SharedPreferences prefs = mConfig.getPrefsPrivate();
				
				if (!TunnelUtils.isNetworkOnline(this)) {
					SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
						ConnectionStatus.LEVEL_NOTCONNECTED);
					
					Toasty.error(this, (R.string.error_internet_off), Toast.LENGTH_SHORT, true).show();

					finish();
				}
				else if (prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT) == Settings.bTUNNEL_TYPE_SSH_PROXY &&
						(mConfig.getPrivString(Settings.PROXY_IP_KEY).isEmpty()  && !ismodeudp() && !isv2raymode() || mConfig.getPrivString(Settings.PROXY_PORTA_KEY).isEmpty()) && !ismodeudp() && !isv2raymode()) {
					SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
							ConnectionStatus.LEVEL_NOTCONNECTED);

					Toast.makeText(this, "proxy invalido",
							Toast.LENGTH_SHORT).show();

					finish();
				}
				else if (mConfig.getPrivString(Settings.SERVIDOR_KEY).isEmpty() && !ismodeudp() && !isv2raymode() || mConfig.getPrivString(Settings.SERVIDOR_PORTA_KEY).isEmpty() && !ismodeudp() && !isv2raymode()) {
					SkStatus.updateStateString("USER_VPN_PASSWORD_CANCELLED", "", R.string.state_user_vpn_password_cancelled,
							ConnectionStatus.LEVEL_NOTCONNECTED);

					//Toast.makeText(this, String.valueOf(isv2raymode()), 0).show();
					Toast.makeText(this, R.string.error_empty_settings,
							Toast.LENGTH_SHORT).show();
					finish();
				}
				else if (mConfig.getPrivString(Settings.USUARIO_KEY).isEmpty() && !ismodeudp() && !isv2raymode() || (mConfig.getPrivString(Settings.SENHA_KEY).isEmpty() &&
						(mTransientAuthPW == null || mTransientAuthPW.isEmpty())) && !ismodeudp() && !isv2raymode()) {
					SkStatus.updateStateString("USER_VPN_PASSWORD", "", R.string.state_user_vpn_password,
							ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
                
               } else {
                    if (!mhideLog) {
						showLogWindow();
					}
					
                    TunnelManagerHelper.startSocksHttp(this);
                    
					finish();
                }
				
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User does not want us to start, so we just vanish
                SkStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
      			ConnectionStatus.LEVEL_NOTCONNECTED);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                SkStatus.logError(R.string.nought_alwayson_warning);
                finish();
            }
        }
    }
	
	private void launchVPN() {
		Intent intent = VpnService.prepare(this);      	
        if (intent != null) {
            SkStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
				ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                // Shame on you Sony! At least one user reported that
                // an official Sony Xperia Arc S image triggers this exception
                SkStatus.logError(R.string.no_vpn_support_image);
                showLogWindow();
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }
    
    private boolean ismodeudp() {
		return prefs.getInt(Settings.TUNNELTYPE_KEY, 0) == Settings.bTUNNEL_TYPE_UDP;
	}

	private boolean isv2raymode() {
		return prefs.getInt(Settings.TUNNELTYPE_KEY, 0) == Settings.bTUNNEL_TYPE_V2RAY;
	}
	
}
