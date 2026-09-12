package com.jepongdevxyz.vpn.fragments;

import com.jepongdevxyz.vpn.R;
import androidx.fragment.app.DialogFragment;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.jepongdevxyz.vpn.*;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jepongdevxyz.vpn.MainActivity;

import android.widget.Toast;
import android.view.ViewGroup;

import com.jepongdevxyz.vpn.config.Settings;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.AppCompatCheckBox;
import android.content.SharedPreferences;

public class ProxyRemoteDialogFragment extends DialogFragment {

	private Settings mConfig;
	private TextInputEditText proxyRemotoIpEdit;
	private TextInputEditText proxyRemotoPortaEdit;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);	
		mConfig = new Settings(getContext());
}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		getDialog().setCanceledOnTouchOutside(false);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
    LayoutInflater li = LayoutInflater.from(getContext());
    View view = li.inflate(R.layout.fragment_proxy_remote, null);
    proxyRemotoIpEdit = view.findViewById(R.id.fragment_proxy_remoteProxyIpEdit);
    proxyRemotoPortaEdit = view.findViewById(R.id.fragment_proxy_remoteProxyPortaEdit);
    proxyRemotoIpEdit.setText(mConfig.getPrivString(Settings.PROXY_IP_KEY));

    if (!mConfig.getPrivString(Settings.PROXY_PORTA_KEY).isEmpty())
      proxyRemotoPortaEdit.setText(mConfig.getPrivString(Settings.PROXY_PORTA_KEY));
    return new MaterialAlertDialogBuilder(getActivity())
        .setView(view)
          .setTitle("Proxy Configuracion")
        .setPositiveButton(
            "Guardar",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                saveState();
              }
            })
        .setNegativeButton(
            "Cancelar",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                dismiss();
              }
            })
        .show();
  }

  void saveState() {

    String proxy_ip = proxyRemotoIpEdit.getEditableText().toString();
    String proxy_porta = proxyRemotoPortaEdit.getEditableText().toString();

    if (proxy_porta == null
        || proxy_porta.isEmpty()
        || proxy_porta.equals("0")
        || proxy_ip == null
        || proxy_ip.isEmpty()) {
      Toast.makeText(
              getContext(),
              "Please make sure your remote proxy format is correct",
              Toast.LENGTH_SHORT)
          .show();
    } else {
      SharedPreferences.Editor edit = mConfig.getPrefsPrivate().edit();
      edit.putString(Settings.PROXY_IP_KEY, proxy_ip);
      edit.putString(Settings.PROXY_PORTA_KEY, proxy_porta);

      edit.apply();

      MainActivity.updateMainViews(getContext());

      dismiss();
    }
  }
}
