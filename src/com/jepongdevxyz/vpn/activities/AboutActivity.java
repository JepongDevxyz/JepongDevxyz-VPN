package com.jepongdevxyz.vpn.activities;

import androidx.appcompat.widget.Toolbar;
import android.view.View.OnClickListener;
import android.os.Bundle;
import com.jepongdevxyz.vpn.R;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;
import android.text.Html;
import android.content.Intent;
import android.net.Uri;
import android.content.pm.PackageInfo;
import com.jepongdevxyz.vpn.util.Utils;
import com.jepongdevxyz.vpn.MyApplication;
import com.jepongdevxyz.vpn.tunnel.TunnelUtils;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private Toolbar tb;
	private View changelog, license, dev;
	private AlertDialog.Builder ab;
	private TextView app_info_text;
	private AdView adsBannerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		tb = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(tb);
		adsBannerView = (AdView) findViewById(R.id.adView2);
		changelog = findViewById(R.id.changelog);
		license = findViewById(R.id.license);
		dev = findViewById(R.id.developer);
		changelog.setOnClickListener(this);
		license.setOnClickListener(this);
		dev.setOnClickListener(this);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		PackageInfo pinfo = Utils.getAppInfo(this);
		if (pinfo != null) {
			String version_nome = pinfo.versionName;
			int version_code = pinfo.versionCode;
			String header_text = String.format("%s (%d)", version_nome, version_code);
			app_info_text = (TextView) findViewById(R.id.appVersion);
			app_info_text.setText(header_text);
		}
		
		if (TunnelUtils.isNetworkOnline(this)) {
			
			adsBannerView.setAdListener(new AdListener() {
				@Override
				public void onAdLoaded() {
					if (adsBannerView != null) {
						adsBannerView.setVisibility(View.VISIBLE);
					}
				}
			});

			adsBannerView.loadAd(new AdRequest.Builder()
				.build());
		}

	}

	@Override
	public void onClick(View view) {
		// TODO: Implement this method
		int id = view.getId();
		if (id == R.id.changelog) {
			changelog();
		} else if (id == R.id.license) {
			license();
		} else if (id == R.id.developer) {
			startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://wa.me/524811057455")));
		}
	}

	private void changelog() {
		// TODO: Implement this method
		ab = new AlertDialog.Builder(this);
		ab.setTitle((R.string.attention));
		ab.setMessage(Html.fromHtml("New version released <br> • Let us know when you find a bug\""));
		ab.setPositiveButton(android.R.string.ok, null);
		ab.create().show();
	}

	private void license() {
		// TODO: Implement this method
		startActivity(new Intent(this, LicenseActivity.class));
	}
}
