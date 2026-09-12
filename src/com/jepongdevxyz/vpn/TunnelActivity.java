package com.jepongdevxyz.vpn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.*;
import com.jepongdevxyz.vpn.config.Settings;
import com.jepongdevxyz.vpn.tunnel.TunnelUtils;
import com.jepongdevxyz.vpn.view.MaterialButton;
import android.view.Menu;
import android.view.MenuItem;
import com.jepongdevxyz.vpn.logger.SkStatus;
import android.widget.Toast;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

public class TunnelActivity extends AppCompatActivity implements View.OnClickListener {

	private Toolbar toolbar_main;
	private RadioButton btnDirect;
	private RadioButton btnHTTP;
	private RadioButton btnSSL;
	private RadioButton btnSslPayload ,btnV2RAY;
	private RadioButton btnSslRp;
	private RadioButton btnSlowDNS;
	private RadioButton btnUDP;
	private RadioButton btnTYPE;
	private CheckBox customPayload;
	private Settings mConfig;
	private SharedPreferences prefs;
	private Button save;
	private TextView mTextView;

	//@SuppressLint({"NonConstantResourceId", "SetTextI18n"})
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btnDirect:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
            btnV2RAY.setChecked(false);
				btnHTTP.setChecked(false);
				btnSSL.setChecked(false);
				btnSslPayload.setChecked(false);
				btnSlowDNS.setChecked(false);
				customPayload.setEnabled(true);
            customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				if (customPayload.isChecked()) {
					mTextView.setText(getString(R.string.direct) + getString(R.string.custom_payload1));
				} else {
					mTextView.setText(getString(R.string.direct));
				}
				break;

			case R.id.btnHTTP:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
				btnDirect.setChecked(false);
           btnV2RAY.setChecked(false);
            mTextView.setText(getString(R.string.http));
				btnSSL.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnSslPayload.setChecked(false);
            customPayload.setChecked(true);
				customPayload.setEnabled(false);
				btnSslRp.setChecked(false);
				
				break;

			case R.id.btnSSL:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
           btnV2RAY.setChecked(false);
				btnHTTP.setChecked(false);
				btnDirect.setChecked(false);
				btnSlowDNS.setChecked(false);
				customPayload.setEnabled(false);
				btnSslPayload.setChecked(false);
				customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				mTextView.setText(getString(R.string.ssl));
				break;

			case R.id.btnSslPayload:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
           btnV2RAY.setChecked(false);
				btnHTTP.setChecked(false);
				btnDirect.setChecked(false);
				btnSSL.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnSslPayload.setChecked(true);
				customPayload.setEnabled(false);
            customPayload.setChecked(true);
				btnSslRp.setChecked(false);
            mTextView.setText(getString(R.string.sslpayload));
				
				break;

			case R.id.btnSlowDNS:
				btnUDP.setChecked(false);
				btnUDP.setEnabled(true);
            btnV2RAY.setChecked(false);
            btnV2RAY.setEnabled(true);
            
				btnV2RAY.setEnabled(true);
				btnTYPE.setChecked(false);
				btnSlowDNS.setChecked(true);
				btnHTTP.setChecked(false);
				btnSslRp.setChecked(false);
				btnSslPayload.setChecked(false);
				btnSSL.setChecked(false);
				btnDirect.setChecked(false);
				customPayload.setEnabled(false);
				btnSSL.setEnabled(false);
				btnSSL.setEnabled(false);
				btnHTTP.setEnabled(false);
				btnSlowDNS.setEnabled(true);
				btnSslRp.setEnabled(false);
				btnDirect.setEnabled(false);
				btnSslPayload.setEnabled(false);
				btnSslPayload.setChecked(false);
				customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				mTextView.setText(getString(R.string.slowdns));
				break;

			case R.id.btnUDP:
				btnUDP.setChecked(true);
				btnUDP.setEnabled(true);
				btnTYPE.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnHTTP.setChecked(false);
				btnSslRp.setChecked(false);
            btnV2RAY.setChecked(false);
            btnV2RAY.setEnabled(true);
				btnSslPayload.setChecked(false);
				btnSSL.setChecked(false);
				btnDirect.setChecked(false);
				customPayload.setEnabled(false);
				btnSSL.setEnabled(false);
				btnSSL.setEnabled(false);
				btnHTTP.setEnabled(false);
				btnSlowDNS.setEnabled(true);
				btnSslRp.setEnabled(false);
				btnDirect.setEnabled(false);
				btnSslPayload.setEnabled(false);
				btnSslPayload.setChecked(false);
				customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				mTextView.setText("Hysteria");
				break;
            
            case R.id.btnV2RAY:
				btnV2RAY.setChecked(true);
				btnV2RAY.setEnabled(true);
           btnUDP.setChecked(false);
				btnUDP.setEnabled(true);
				btnTYPE.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnHTTP.setChecked(false);
				btnSslRp.setChecked(false);
				btnSslPayload.setChecked(false);
				btnSSL.setChecked(false);
				btnDirect.setChecked(false);
				customPayload.setEnabled(false);
				btnSSL.setEnabled(false);
				btnSSL.setEnabled(false);
				btnHTTP.setEnabled(false);
				btnSlowDNS.setEnabled(true);
				btnSslRp.setEnabled(false);
				btnDirect.setEnabled(false);
				btnSslPayload.setEnabled(false);
				btnSslPayload.setChecked(false);
				customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				mTextView.setText("V2ray");
				break;
            
            
            
           
            
			case R.id.btnSslrp:
				btnUDP.setChecked(false);
            btnV2RAY.setChecked(false);
				btnTYPE.setChecked(true);
				btnHTTP.setChecked(false);
				btnDirect.setChecked(false);
				btnSSL.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnSslPayload.setChecked(false);
				customPayload.setEnabled(false);
            customPayload.setChecked(false);
				btnSslRp.setChecked(true);
				mTextView.setText(getString(R.string.sslrp));
				break;

			case R.id.btnTYPE:
				btnSslPayload.setChecked(false);
				btnSslPayload.setEnabled(true);
				btnSSL.setChecked(false);
				btnSSL.setEnabled(true);
				btnHTTP.setChecked(false);
				btnHTTP.setEnabled(true);
				btnDirect.setChecked(true);
				btnDirect.setEnabled(true);
				btnSSL.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnSlowDNS.setEnabled(true);
				btnUDP.setChecked(false);
           customPayload.setEnabled(true);
            btnV2RAY.setChecked(false);
            btnV2RAY.setEnabled(true);
				btnUDP.setEnabled(true);
				btnSslRp.setChecked(false);
				mTextView.setText(getString(R.string.direct));
				btnSslRp.setEnabled(true);
				
				break;

			case R.id.customPayload:
				if (customPayload.isChecked()) {
					if (btnDirect.isChecked()) {
						mTextView.setText(getString(R.string.direct) + getString(R.string.custom_payload1));
					} else if (btnHTTP.isChecked()) {
						mTextView.setText(getString(R.string.http));
					} else if (btnSslPayload.isChecked()) {
						mTextView.setText(getString(R.string.sslpayload));
					}
				} else {
					if (btnDirect.isChecked()) {
						mTextView.setText(getString(R.string.direct));
					} else if (btnHTTP.isChecked()) {
						mTextView.setText(getString(R.string.http));
					} else if (btnSslPayload.isChecked()) {
						mTextView.setText(getString(R.string.sslpayload));
					}
				}
				break;

			case R.id.saveButton:
				doSave();
				break;
		}
	}

	private void doSave() {
		SharedPreferences.Editor edit = mConfig.getPrefsPrivate().edit();

		if (btnDirect.isChecked()) {
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);

		} else if (btnHTTP.isChecked()) {
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_PROXY);

		} else if (btnSSL.isChecked()) {
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL);

		} else if (btnSslPayload.isChecked()) {
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_PAY_SSL);

		} else if (btnSlowDNS.isChecked()) {
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SLOWDNS);

		}else if(btnUDP.isChecked()){
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_UDP);

		}
        else if(btnV2RAY.isChecked()){
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_V2RAY);

		} 
         else if (btnSslRp.isChecked()) {
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSL_RP);

		} else if (btnTYPE.isChecked()) {
			edit.putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH);
		}

		edit.putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, !customPayload.isChecked());
		edit.apply();
		startActivity(new Intent(this, MainActivity.class));
		MainActivity.updateMainViews(getApplicationContext());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tunnel_type);
		mConfig = new Settings(this);
		prefs = mConfig.getPrefsPrivate();
		toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setupButton();
	}

	//@SuppressLint("SetTextI18n")
	private void setupButton() {
		btnUDP = (RadioButton) findViewById(R.id.btnUDP);
		btnUDP.setOnClickListener(this);
		btnTYPE = (RadioButton) findViewById(R.id.btnTYPE);
		btnTYPE.setOnClickListener(this);
        btnV2RAY = (RadioButton) findViewById(R.id.btnV2RAY);
		btnV2RAY.setOnClickListener(this);
        
		mTextView = (TextView) findViewById(R.id.tunneltypeTextView1);
		btnDirect = (RadioButton) findViewById(R.id.btnDirect);
		btnDirect.setOnClickListener(this);
		btnHTTP = (RadioButton) findViewById(R.id.btnHTTP);
		btnHTTP.setOnClickListener(this);
		btnSslPayload = (RadioButton) findViewById(R.id.btnSslPayload);
		btnSslPayload.setOnClickListener(this);
		btnSSL = (RadioButton) findViewById(R.id.btnSSL);
		btnSSL.setOnClickListener(this);
		btnSlowDNS = (RadioButton) findViewById(R.id.btnSlowDNS);
		btnSlowDNS.setOnClickListener(this);
		btnSslRp = (RadioButton) findViewById(R.id.btnSslrp);
		btnSslRp.setOnClickListener(this);
		customPayload = (CheckBox) findViewById(R.id.customPayload);
		customPayload.setOnClickListener(this);
		save = (Button) findViewById(R.id.saveButton);
		save.setOnClickListener(this);
        save.setText("SAVE");
		int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
		customPayload.setChecked(!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true));

		switch (tunnelType) {
			case Settings.bTUNNEL_TYPE_SSH_DIRECT:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
				btnDirect.setChecked(true);
				btnHTTP.setChecked(false);
				btnSSL.setChecked(false);
            btnV2RAY.setChecked(false);
				btnSlowDNS.setChecked(false);
				customPayload.setEnabled(true);
            
            
            
				btnSslPayload.setChecked(false);
				btnSslRp.setChecked(false);
				if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true)) {
					mTextView.setText(getString(R.string.direct) + getString(R.string.custom_payload1));
				} else {
					mTextView.setText(getString(R.string.direct));
				}
				break;

			case Settings.bTUNNEL_TYPE_SSH_PROXY:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
				btnHTTP.setChecked(true);
				btnDirect.setChecked(false);
           btnV2RAY.setChecked(false);
				btnSSL.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnSslPayload.setChecked(false);
            mTextView.setText(getString(R.string.http));
				customPayload.setEnabled(false);
            customPayload.setChecked(true);
				btnSslRp.setChecked(false);
				
				break;

			case Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
				btnSSL.setChecked(true);
				btnHTTP.setChecked(false);
            btnV2RAY.setChecked(false);
				btnDirect.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnSslPayload.setChecked(false);
				customPayload.setEnabled(false);
				btnSslRp.setChecked(false);
				mTextView.setText(getString(R.string.ssl));
				break;

			case Settings.bTUNNEL_TYPE_SLOWDNS:
				btnUDP.setChecked(false);
				btnUDP.setEnabled(true);
            btnV2RAY.setChecked(false);
            btnV2RAY.setEnabled(true);
            
				btnV2RAY.setEnabled(true);
				btnTYPE.setChecked(false);
				btnSlowDNS.setChecked(true);
				btnHTTP.setChecked(false);
				btnSslRp.setChecked(false);
				btnSslPayload.setChecked(false);
				btnSSL.setChecked(false);
				btnDirect.setChecked(false);
				customPayload.setEnabled(false);
				btnSSL.setEnabled(false);
				btnSSL.setEnabled(false);
				btnHTTP.setEnabled(false);
				btnSlowDNS.setEnabled(true);
				btnSslRp.setEnabled(false);
				btnDirect.setEnabled(false);
				btnSslPayload.setEnabled(false);
				btnSslPayload.setChecked(false);
				customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				mTextView.setText(getString(R.string.slowdns));
				break;

			case Settings.bTUNNEL_TYPE_UDP:
				btnUDP.setChecked(true);
				btnUDP.setEnabled(true);
				btnTYPE.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnHTTP.setChecked(false);
				btnSslRp.setChecked(false);
            btnV2RAY.setChecked(false);
            btnV2RAY.setEnabled(true);
				btnSslPayload.setChecked(false);
				btnSSL.setChecked(false);
				btnDirect.setChecked(false);
				customPayload.setEnabled(false);
				btnSSL.setEnabled(false);
				btnSSL.setEnabled(false);
				btnHTTP.setEnabled(false);
				btnSlowDNS.setEnabled(true);
				btnSslRp.setEnabled(false);
				btnDirect.setEnabled(false);
				btnSslPayload.setEnabled(false);
				btnSslPayload.setChecked(false);
				customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				mTextView.setText("Hysteria");
				break;

            
            case Settings.bTUNNEL_TYPE_V2RAY:
				btnV2RAY.setChecked(true);
				btnV2RAY.setEnabled(true);
           btnUDP.setChecked(false);
				btnUDP.setEnabled(true);
				btnTYPE.setChecked(false);
				btnSlowDNS.setChecked(false);
				btnHTTP.setChecked(false);
				btnSslRp.setChecked(false);
				btnSslPayload.setChecked(false);
				btnSSL.setChecked(false);
				btnDirect.setChecked(false);
				customPayload.setEnabled(false);
				btnSSL.setEnabled(false);
				btnSSL.setEnabled(false);
				btnHTTP.setEnabled(false);
				btnSlowDNS.setEnabled(true);
				btnSslRp.setEnabled(false);
				btnDirect.setEnabled(false);
				btnSslPayload.setEnabled(false);
				btnSslPayload.setChecked(false);
				customPayload.setChecked(false);
				btnSslRp.setChecked(false);
				mTextView.setText("V2ray");
				break;
            
			case Settings.bTUNNEL_TYPE_PAY_SSL:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
				btnSlowDNS.setChecked(false);
				btnHTTP.setChecked(false);
				btnSSL.setChecked(false);
				btnDirect.setChecked(false);
            btnV2RAY.setChecked(false);
				btnSslPayload.setChecked(true);
				customPayload.setEnabled(false);
            customPayload.setChecked(true);
				btnSslRp.setChecked(false);
				mTextView.setText(getString(R.string.sslpayload));
				
				break;

            
            
            
            case Settings.bTUNNEL_TYPE_SSH:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
			customPayload.setEnabled(true);
				btnSlowDNS.setChecked(false);
            
            btnV2RAY.setChecked(false);
            mTextView.setText(getString(R.string.direct));
				
				break;
            
			case Settings.bTUNNEL_TYPE_SSL_RP:
				btnUDP.setChecked(false);
				btnTYPE.setChecked(true);
				btnSlowDNS.setChecked(false);
				btnHTTP.setChecked(false);
				btnSSL.setChecked(false);
           btnV2RAY.setChecked(false);
				btnDirect.setChecked(false);
				btnSslPayload.setChecked(false);
				customPayload.setEnabled(false);
				btnSslRp.setChecked(true);
				mTextView.setText(getString(R.string.sslrp));
				
				break;
		}
	}
}
