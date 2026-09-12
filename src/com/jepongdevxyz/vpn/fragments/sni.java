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

public class sni extends DialogFragment {

	private Settings mConfig;
	private TextInputEditText proxyRemotoIpEdit;
	private TextInputEditText superpro;
	
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
    View view = li.inflate(R.layout.sni, null);
    superpro = view.findViewById(R.id.snipro);
    superpro.setText(mConfig.getPrivString(Settings.CUSTOM_SNI));
 
    return new MaterialAlertDialogBuilder(getActivity())
        .setView(view)
        .setTitle("SNI Configeration")
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

    String sni = superpro.getEditableText().toString();
    

      SharedPreferences.Editor edit = mConfig.getPrefsPrivate().edit();
      edit.putString(Settings.CUSTOM_SNI, sni);
      

      edit.apply();

      MainActivity.updateMainViews(getContext());

      dismiss();
    }
  }
