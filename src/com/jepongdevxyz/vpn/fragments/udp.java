package com.jepongdevxyz.vpn.fragments;

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

public class udp extends DialogFragment {

	private Settings mConfig;
	private TextInputEditText proxyRemotoIpEdit;
	private TextInputEditText buffer, server,obfs,auth,udpup,udpdown;
	
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
    View view = li.inflate(R.layout.udp, null);
    server = view.findViewById(R.id.server);
    server.setText(mConfig.getPrivString(Settings.UDP_SERVER));
        
        obfs = view.findViewById(R.id.obfs);
    obfs.setText(mConfig.getPrivString(Settings.UDP_OBFS));
 
        auth = view.findViewById(R.id.auth);
    auth.setText(mConfig.getPrivString(Settings.UDP_AUTH));
        
        udpup = view.findViewById(R.id.udpup);
    udpup.setText(mConfig.getPrivString(Settings.UDP_UP));
        
        
        udpdown = view.findViewById(R.id.udpdown);
    udpdown.setText(mConfig.getPrivString(Settings.UDP_DOWN));
        
        
        buffer = view.findViewById(R.id.buffer);
    buffer.setText(mConfig.getPrivString(Settings.UDP_WINDOW));
        
        
    return new MaterialAlertDialogBuilder(getActivity())
        .setView(view)
        .setTitle("Udp Configeration")
        .setPositiveButton(
            "Save",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                saveState();
              }
            })
        .setNegativeButton(
            "Cancel",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                dismiss();
              }
            })
        .show();
  }

  void saveState() {

    String ip = server.getEditableText().toString();
    String ob = obfs.getEditableText().toString();
        String aut = auth.getEditableText().toString();
String up = udpup.getEditableText().toString();
        String dow = udpdown.getEditableText().toString();
        String bf = buffer.getEditableText().toString();
        
        
      SharedPreferences.Editor edit = mConfig.getPrefsPrivate().edit();
        SharedPreferences.Editor edit2 = mConfig.getPrefsPrivate().edit();
      edit.putString(Settings.UDP_SERVER, ip);
        
      edit.putString(Settings.UDP_OBFS, ob);
        
        
      edit.putString(Settings.UDP_AUTH, aut);
      
      edit.putString(Settings.UDP_UP, up);
        
      edit.putString(Settings.UDP_DOWN, dow);
      
      edit.putString(Settings.UDP_WINDOW, bf);
      edit.apply();

      MainActivity.updateMainViews(getContext());

      dismiss();
    }
  }
