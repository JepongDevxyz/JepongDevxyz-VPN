package com.jepongdevxyz.vpn.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.app.ProgressDialog;
import android.text.method.LinkMovementMethod;
import android.os.Environment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jepongdevxyz.vpn.MainActivity;
import host.stjin.expandablecardview.*;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.config.ConfigParser;
import com.jepongdevxyz.vpn.config.Settings;
import com.jepongdevxyz.vpn.util.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import es.dmoral.toasty.Toasty;

import com.google.android.material.textfield.TextInputLayout;
import com.jepongdevxyz.vpn.util.VPNUtils;

import java.util.Random;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kimchangyoun.rootbeerFresh.RootBeer;

import android.content.pm.PackageInfo;
import androidx.annotation.NonNull;
import android.content.pm.PackageManager;
import java.util.HashMap;
import java.util.Map;
import android.net.NetworkInfo;
import android.net.ConnectivityManager;


import com.google.android.gms.tasks.OnCompleteListener;

public class ConfigExportFileActivity
extends BaseActivity
implements CompoundButton.OnCheckedChangeListener, View.OnClickListener
{
	private Settings mConfig;
	public static int REQUEST_CODE_ARBITRARY = 1;
	private Button PreviewMSG;
	public File fileDir;
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mConfig = new Settings(this);

		doLayout();

		// requista permissões ao armazenamento externo
		requestPermissions();
		
		if (Build.VERSION.SDK_INT >= 30) {
			fileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));
		} else {
            fileDir = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        }
		
		TelephonyManager telephonyManager = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
		OperadorString = telephonyManager.getSimOperatorName();
	}

	@Override
	public boolean onSupportNavigateUp()
	{
		super.onBackPressed();
		return true;
	}


	/**
	 * Main Views
	 */
	private CheckBox validadeCheck;
	private CheckBox BlockOperador;
	private AppCompatCheckBox OnlyData;
	private TextView validadeText;
	private TextView operadorText;
	private String OperadorString;
	private String mOperador = "";
	private String P = "";
	private String mHwid = "";
	private EditText nomeEdit,editpaa;
	private EditText mensagemEdit;
	public EditText mensageEdit;
	public TextInputLayout MensajeInputLayout;
    private LinearLayout autorlayout;
	private AdView adsBannerView;
	private boolean mIsProteger = false;
	private String mMensagem = "";
    private String mAutor = "";
	private boolean mPedirSenha = false;
	private boolean mOnlyDataMovil = false;
	private boolean mOnlyPlayStore = false;
	private boolean mBloquearRoot = false;
	private boolean mPAll = false;
	private boolean mBlockOperador = false;
	private boolean mBlockHwid = false;
	private boolean mLoginHwid = false;
	private boolean mMessage = false;
	private boolean mPPayload = false;
	private boolean mPProxy =  false;
	private boolean mPSni = false;
	private boolean mPServer = false;
	private boolean mPPort = false;
	private boolean mPUser = false;
	private boolean Pass = false;
	private boolean mPPass = false;
    private boolean IsAutor = false;
	private boolean IsPasso = false;
    
    
    private EditText autoredit;
	
	private TextInputLayout InputLayoutMensaje;
	private FloatingActionButton exportarButton;
	private LinearLayout LinearMensaje;
	private LinearLayout LinearHwid;
    public EditText SshHwid;
    public TextInputLayout InputLayoutHwid1,passlauoy;
	private TextInputEditText inputpass;
	private LinearLayout Options;
	private CheckBox PAll;
	private CheckBox PPayload;
	private CheckBox PProxy;
	private CheckBox PSni;
	private CheckBox PServer;
	private CheckBox PPort;
	private CheckBox PUser;
	private CheckBox PPass;
	private CheckBox CPass;
    private CheckBox autorcb;
	private ExpandableCardView cardssh;
	private ExpandableCardView cardsecureop;
    private ProgressDialog progressDialog;
    private androidx.appcompat.app.AlertDialog exportando;
    private androidx.appcompat.app.AlertDialog generando;
	
	
	private void doLayout() {
		setContentView(R.layout.activity_config_export);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// impede autoinicio dos editText
		/**findViewById(R.id.activity_config_exportLinearLayout)
			.requestFocus();**/

		nomeEdit = (EditText) findViewById(R.id.activity_config_exportNomeEdit);
		Random random = new Random();
		StringBuilder sb2 = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb2.append("0123456789abcdefghijklmnopqrz".charAt(random.nextInt(10)));
        }
		String Name = getString(R.string.app_name) + "_" + sb2.toString();
		//nomeEdit.setText(Name);
		Options = (LinearLayout) findViewById(R.id.card_options);
		AppCompatCheckBox protegerCheck = (AppCompatCheckBox) findViewById(R.id.activity_config_exportProtegerCheck);
		//OnlyData = (AppCompatCheckBox) findViewById(R.id.activity_config_onlydata);
		validadeCheck = (CheckBox) findViewById(R.id.activity_config_exportValidadeCheck);
		//validadeText = (TextView) findViewById(R.id.activity_config_exportValidadeText);
		//operadorText = (TextView) findViewById(R.id.activity_config_operadorText);
		mensagemEdit = (EditText) findViewById(R.id.activity_config_exportMensagemEdit);
        
        
		SshHwid = (EditText) findViewById(R.id.hwidtext);
		InputLayoutHwid1 = (TextInputLayout) findViewById(R.id.inputlayouthwid);
		InputLayoutHwid1.setEnabled(false);
        passlauoy = (TextInputLayout) findViewById(R.id.passlauoy);
		passlauoy.setEnabled(false);
       
        
        
        LinearHwid = (LinearLayout) findViewById(R.id.layout_hwid);
        CheckBox BlockHwid = (CheckBox) findViewById(R.id.activity_config_exportBlockHwid);
        
        
		exportarButton = (FloatingActionButton) findViewById(R.id.activity_config_exportButton);
		LinearMensaje = (LinearLayout) findViewById(R.id.layout_msg);
		
		CheckBox blockRootCheck = (CheckBox) findViewById(R.id.activity_config_exportBlockRootCheck);
		CheckBox onlyDataMovil = (CheckBox) findViewById(R.id.activity_config_exportOnlyDataBlock);
		CheckBox onlyPlayStore = (CheckBox) findViewById(R.id.activity_config_exportOnlyPlayStore);
		BlockOperador = (CheckBox) findViewById(R.id.activity_config_exportOperadorCheck);
		
        
        
        
		CheckBox LoginHwid = (CheckBox) findViewById(R.id.activity_config_exportHdwidLogin);
		CheckBox EnableMessage = (CheckBox) findViewById(R.id.msg_check);
		//CPass = (CheckBox) findViewById(R.id.activity_config_exportPass);
		
        autorcb = (CheckBox) findViewById(R.id.autor_check);
        autoredit = (EditText) findViewById(R.id.activity_config_exportautoredit);
        
        
        
	
        autorlayout = (LinearLayout) findViewById(R.id.layout_autor);
		cardsecureop = (ExpandableCardView) findViewById(R.id.card_secure);
		
		//Separador1 = (TextView) findViewById(R.id.separador1);
		//PreviewMSG = (Button) findViewById(R.id.export_preview_msg_id);
		//PreviewMSG.setOnClickListener(this);
		//mensageEdit = (EditText) findViewById(R.id.etMessage);
		//TextInputLayout MensajeInputLayout = (TextInputLayout) findViewById(R.id.messageLayout);
		showSegurancaLayout(false);
		mensagemEdit.setText(mConfig.getMensagemConfigExportar());
        autoredit.setText(mConfig.getAutorMsg());
		InputLayoutMensaje = (TextInputLayout) findViewById(R.id.activity_config_exportLayoutMensagemEdit);
		InputLayoutMensaje.setEndIconOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
					//builder.setTitle(R.string.preview_msg);
					String obj = ConfigExportFileActivity.this.mensagemEdit.getText().toString();
					if (obj.isEmpty()) {
						//obj = getString(R.string.msg_empty);
						} else if (!obj.contains("<br>") && !obj.contains("</br>") && !obj.contains("<p>") && !obj.contains("</p>")) {
						obj = obj.replace("\n", "<br></br>");
					}
					ScrollView scrollView = new ScrollView(ConfigExportFileActivity.this);
					scrollView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
					scrollView.setSmoothScrollingEnabled(true);
					TextView textView = new TextView(ConfigExportFileActivity.this);
					textView.setMovementMethod(LinkMovementMethod.getInstance());
					if (Build.VERSION.SDK_INT >= 24) {
						textView.setText(Html.fromHtml(obj, 63));
						} else {
						textView.setText(Html.fromHtml(obj));
					}
					textView.setPadding(10, 10, 10, 10);
					scrollView.addView(textView);
					builder.setView(scrollView);
					builder.setPositiveButton((R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							dialogInterface.dismiss();
						}
					});
					builder.create().show();
				}
				});
		validadeCheck.setOnCheckedChangeListener(this);
		
		
		protegerCheck.setOnCheckedChangeListener(this);
		
		exportarButton.setOnClickListener(this);
		blockRootCheck.setOnCheckedChangeListener(this);
		
		onlyDataMovil.setOnCheckedChangeListener(this);
		//CPass.setOnCheckedChangeListener(this);
		
		onlyPlayStore.setOnCheckedChangeListener(this);
		BlockOperador.setOnCheckedChangeListener(this);
		
		
		autorcb.setOnCheckedChangeListener(this);
		LoginHwid.setOnCheckedChangeListener(this);
        BlockHwid.setOnCheckedChangeListener(this);
		EnableMessage.setOnCheckedChangeListener(this);
		
		
		

		/**adsBannerView = (AdView) findViewById(R.id.adView3);
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
		}**/
        
	}
    
    /*private void uploadsucess(String token) {



		copytoken();




        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Configuracion Exportada Exitosamente");
        builder.setMessage("Token: " + token +"\n\nToken copiado al portapapeles");
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((ClipboardManager)getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("token", token));
                onBackPressed();     
            }    
        });       

        AlertDialog dialog = builder.create();
        dialog.show();
    }*/

	







	private void exportConfiguracao(String nome)
	throws IOException {
		if (!FileUtils.isExternalStorageWritable()) {
			throw new IOException(getString(R.string.error_permission_writer_required));
		}

		if (!fileDir.exists()) {
			fileDir.mkdir();
		}

		File fileExport = new File(fileDir, String.format("%s.%s", nome, ConfigParser.FILE_EXTENSAO));
		if (!fileExport.exists()) {
			try {
				fileExport.createNewFile();
			} catch(IOException e) {
				throw new IOException(getString(R.string.error_save_settings));
			}
		}

		// salva mensagem para ser reutilizada
		if (mIsProteger) {
		//	mConfig.setMensagemConfigExportar(mMensagem);
            mConfig.setAutorMsg(mAutor);
		}

		try {
			ConfigParser.convertDataToFile(new FileOutputStream(fileExport), this,
										   mIsProteger, mPedirSenha, mBloquearRoot, mMensagem, mValidade, mOnlyDataMovil, mOnlyPlayStore, mBlockOperador, mOperador, mBlockHwid, mHwid, mLoginHwid, mAutor);
		} catch(IOException e) {
			fileExport.delete();
			throw e;
		}
	}
    
    
	/**
	 * Validade
	 */

	private long mValidade = 0;

	private void setValidadeDate() {

		// Get Current Date
		Calendar c = Calendar.getInstance();
		final long time_hoje = c.getTimeInMillis();

		c.setTimeInMillis(time_hoje+(1000*60*60*24));

		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);

		mValidade = c.getTimeInMillis();

		final DatePickerDialog dialog = new DatePickerDialog(this,
			new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker p1, int year, int monthOfYear, int dayOfMonth) {
					Calendar c = Calendar.getInstance();
					c.set(year, monthOfYear, dayOfMonth);

					mValidade = c.getTimeInMillis();
				}
			},
			mYear, mMonth, mDay);

		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog2, int which) {
					DateFormat df = DateFormat.getDateInstance();
					DatePicker date = dialog.getDatePicker();

					Calendar c = Calendar.getInstance();
					c.set(date.getYear(), date.getMonth(), date.getDayOfMonth());

					mValidade = c.getTimeInMillis();

					if (mValidade < time_hoje) {
						mValidade = 0;

						Toasty.error(ConfigExportFileActivity.this, R.string.error_date_selected_invalid, Toast.LENGTH_SHORT, true).show();

						if (validadeCheck != null)
							validadeCheck.setChecked(false);
					}
					else {
						long dias = ((mValidade-time_hoje)/1000/60/60/24);

						if (validadeText != null) {
							validadeCheck.setText(String.format("%s (%s)", dias, df.format(mValidade)));
						}
					}
				}
			}
		);

		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mValidade = 0;

					if (validadeCheck != null) {
						validadeCheck.setChecked(false);
					}
				}
			}
		);

		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface v1) {
					mValidade = 0;
					if (validadeCheck != null) {
						validadeCheck.setChecked(false);
					}
				}
			});

		dialog.show();
	}

	private void requestPermissions() {
		FileUtils.requestForPermissionExternalStorage(this);
	}


	/**
	 * Oculta/Mostra layout com opções
	 */

	private int[] idsProtegerViews = {
		R.id.activity_config_exportValidadeCheck,
	
		//R.id.activity_config_exportValidadeText,
		R.id.activity_config_exportMensagemEdit,
		
		R.id.activity_config_exportLayoutMensagemEdit,
		R.id.activity_config_exportBlockRootCheck,
		//R.id.activity_config_exportPass,
		R.id.activity_config_exportOnlyDataBlock,
		R.id.activity_config_exportOnlyPlayStore,
		R.id.activity_config_exportOperadorCheck,
		R.id.activity_config_exportBlockHwid,
        	R.id.activity_config_exportHdwidLogin,
		
		R.id.msg_check,
	
        R.id.autor_check
	};

	private int[] idsProtegerChecksView = {
		R.id.activity_config_exportValidadeCheck,
		R.id.activity_config_exportBlockRootCheck,
		R.id.activity_config_exportOnlyDataBlock,
		//R.id.activity_config_exportPass,
	
		R.id.activity_config_exportOnlyPlayStore,
	
		R.id.activity_config_exportOperadorCheck,
		
		R.id.activity_config_exportBlockHwid,
		R.id.activity_config_exportHdwidLogin,
		R.id.msg_check,
		
        R.id.autor_check
	};

	private void showSegurancaLayout(boolean is) {
		if (is) {
			Toasty.error(this, R.string.alert_block_settings, Toast.LENGTH_SHORT, true).show();
		}
		else {
			for (int id : idsProtegerChecksView) {
				((CheckBox) findViewById(id)).setChecked(false);
			}
		}

		for (int id : idsProtegerViews) {
			findViewById(id).setEnabled(is);
		}
	}


	@Override
	public void onCheckedChanged(CompoundButton p1, boolean is)
	{
		switch (p1.getId()) {
			case R.id.activity_config_exportValidadeCheck:
				if (is) {
					setValidadeDate();
				}
				else {
					mValidade = 0;
					/**if (validadeText != null) {
						validadeCheck.setText(R.string.check_date_valid);
					}**/
				}
				break;

			case R.id.activity_config_exportProtegerCheck:
				mIsProteger = is;
				showSegurancaLayout(is);
				if (mIsProteger != false) {
					Options.setVisibility(View.VISIBLE);

				} else {
					
					Options.setVisibility(View.GONE);
				}
				break;
			
					
			case R.id.activity_config_exportBlockRootCheck:
				mBloquearRoot = is;
				break;
				
			/**case R.id.activity_config_exportPass:
			SharedPreferences prefs = mConfig.getPrefsPrivate();
			    //String checkpass = mConfig.getPrivString(Settings.CP);
				Pass = is;
				if (Pass != false) {
					// load the dialog_promt_user.xml layout and inflate to view
					LayoutInflater layoutinflater = LayoutInflater.from(context);
					View promptUserView = layoutinflater.inflate(R.layout.dialog_input, null);
					
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
					alertDialogBuilder.setView(promptUserView);
					
					inputpass = (TextInputEditText) promptUserView.findViewById(R.id.input_pass);
					alertDialogBuilder.setCancelable(false);
					alertDialogBuilder.setTitle("Bloquear Servidor con Contraseña");
					alertDialogBuilder.setMessage("Ingresa tu Contraseña");
					
					alertDialogBuilder.setNegativeButton((R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
						CPass.setChecked(false);
                        dialog.cancel();
						
						}
						});
					// prompt for username
					alertDialogBuilder.setPositiveButton((R.string.ok),new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String checkpass1 = inputpass.getText().toString();
							// and display the username on main activity layout
							if (checkpass1.isEmpty()) {
								CPass.setChecked(false);
								Toasty.warning(ConfigExportFileActivity.this, "La contraseña no puede estar vacia", Toast.LENGTH_SHORT, true).show();
							} else {
								CPass.setChecked(true);
								SharedPreferences.Editor edit = mConfig.getPrefsPrivate().edit();
								edit.putString(Settings.CUSTOM_SNI, checkpass1);
								edit.apply();
							}
							
							}
							});
							
							// all set and time to build and show up!
							AlertDialog alertDialog = alertDialogBuilder.create();
							alertDialog.show();
				}
				break;**/
			case R.id.activity_config_exportOnlyDataBlock:
			    mOnlyDataMovil = is;
				break;
				
			
			case R.id.activity_config_exportOnlyPlayStore:
			    mOnlyPlayStore = is;
				break;
			case R.id.activity_config_exportOperadorCheck:
			    mBlockOperador = is;
				if (mBlockOperador != false) {
					BlockOperador.setText(getString(R.string.operator_checkbox_tittle) + " " + "(" + OperadorString + ")");
				} else {
						BlockOperador.setText(R.string.operator_checkbox_tittle);
					}
				break;
			case R.id.activity_config_exportBlockHwid:
			    mBlockHwid = is;
				if (mBlockHwid != false) {
					InputLayoutHwid1.setEnabled(true);
					LinearHwid.setVisibility(View.VISIBLE);
					SshHwid.setText(VPNUtils.getHWID());
				} else {
					LinearHwid.setVisibility(View.GONE);
					InputLayoutHwid1.setEnabled(false);
				}
				break;
				
			case R.id.activity_config_exportHdwidLogin:
			    mLoginHwid = is;
				break;
			case R.id.msg_check:
			    mMessage = is;
			    if (mMessage != false) {
					LinearMensaje.setVisibility(View.VISIBLE);
				} else {
					LinearMensaje.setVisibility(View.INVISIBLE);
				}
				break;
            case R.id.autor_check:
                IsAutor = is;
                if (IsAutor != false) {
                    autorlayout.setEnabled(true);
                    autorlayout.setVisibility(View.VISIBLE);
                } else {
                    autorlayout.setEnabled(false);
                    autorlayout.setVisibility(View.GONE);
                }
                break;
            
           
            
		}
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {






			case R.id.activity_config_exportButton:

				exportarLayout();









				/*AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Exportar:");
                builder.setMessage("Selecciona una opcion");        // add the buttons
                builder.setPositiveButton("Local", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();        
                        String nomeConfig = nomeEdit.getText().toString();
			        	mMensagem = mIsProteger ? mensagemEdit.getText().toString() : "";
			        	mOperador = mIsProteger ? OperadorString : "";
			        	mHwid = mIsProteger ? SshHwid.getText().toString() : "";

			        	if (nomeConfig.isEmpty()) {
				        	Toasty.error(ConfigExportFileActivity.this, R.string.error_empty_name_file, Toast.LENGTH_SHORT, true).show();
				        	return;
			        	}

				        if (mIsProteger == false || mValidade < 0) {
				        	mValidade = 0;
			        	}

				        try {
					        exportConfiguracao(nomeConfig);
						    Toasty.success(ConfigExportFileActivity.this, getString(R.string.success_export_settings) + getString(R.string.file_saved_to) + "" + fileDir, Toast.LENGTH_LONG, true).show();
				        } catch (IOException e) {
						    Toasty.error(ConfigExportFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT, true).show();
                            
			        	}
				        	onBackPressed();
                    }        
                });;





                builder.setNeutralButton("Cloud", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setTitle("Exportando");
                        progressDialog.setMessage("Por favor espere...");
                        progressDialog.show();
                        Random random = new Random();
	                	StringBuilder sb2 = new StringBuilder(12);
                        for (int i = 0; i < 12; i++) {
                            sb2.append("0123456789abcdefghijklmnopqrz".charAt(random.nextInt(10)));
                        }
                        
                        String nomeConfig = nomeEdit.getText().toString();
			        	mMensagem = mIsProteger ? mensagemEdit.getText().toString() : "";
			       	 mOperador = mIsProteger ? OperadorString : "";
			        	mHwid = mIsProteger ? SshHwid.getText().toString() : "";
                        
                        if (nomeConfig.isEmpty()) {
                            progressDialog.dismiss();
				        	Toasty.error(ConfigExportFileActivity.this, R.string.error_empty_name_file, Toast.LENGTH_SHORT, true).show();
				        	return;
			        	}

			        	if (mIsProteger == false || mValidade < 0) {
			       	 	mValidade = 0;
			        	}
                        try {
                            exportConfiguracaotoCloud(sb2.toString());
                        } catch(IOException e) {
                            Toasty.error(ConfigExportFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                        final Handler handler = new Handler();
	                	handler.postDelayed(new Runnable() {
                             @Override
                             public void run() {
                                 progressDialog.dismiss();
                                 uploadsucess(sb2.toString());
                                 //onBackPressed();   
                             }
                       }, 1500);         
                    }
                });



                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });        
            AlertDialog dialog = builder.create();
                dialog.show();*/
				
	     }
	}




	private void exportarLayout(){
		String nomeConfig = nomeEdit.getText().toString();
						mMensagem = mIsProteger ? mensagemEdit.getText().toString() : "";
						mOperador = mIsProteger ? OperadorString : "";
						mHwid = mIsProteger ? SshHwid.getText().toString() : "";
                        mAutor = mIsProteger ? autoredit.getText().toString() : "";

						if (nomeConfig.isEmpty()) {
							Toasty.error(ConfigExportFileActivity.this, R.string.error_empty_name_file, Toast.LENGTH_SHORT, true).show();
							return;
						}

						if (mIsProteger == false || mValidade < 0) {
							mValidade = 0;
						}

						try {
							exportConfiguracao(nomeConfig);
							Toasty.success(ConfigExportFileActivity.this, getString(R.string.success_export_settings) + getString(R.string.file_saved_to) + "" + fileDir, Toast.LENGTH_LONG, true).show();
						} catch (IOException e) {
							Toasty.error(ConfigExportFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT, true).show();

						}
						onBackPressed();
        
       
					
	}

	
    
    public static int getBuildId(Context context) throws IOException {
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pinfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			throw new IOException("Build ID not found");
		}
	}
    
    public static boolean isValidadeExpirou(long validadeDateMillis) {
		if (validadeDateMillis == 0) {
			return false;
		}
		
		// Get Current Date
		long date_atual = Calendar.getInstance()
			.getTime().getTime();
		
		if (date_atual >= validadeDateMillis) {
			return true;
		}
		
		return false;
	}
    
    public static boolean isDeviceRooted(Context context) {
        /*for (String pathDir : System.getenv("PATH").split(":")){
			if (new File(pathDir, "su").exists()) {
				return true;
			}
		}
		
		Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }*/
		
		RootBeer rootBeer = new RootBeer(context);
		
		boolean simpleTests = rootBeer.detectRootManagementApps() || rootBeer.detectPotentiallyDangerousApps() || rootBeer.checkForBinary("su")
			|| rootBeer.checkForDangerousProps() || rootBeer.checkForRWPaths()
			|| rootBeer.detectTestKeys() || rootBeer.checkSuExists() || rootBeer.checkForRootNative() || rootBeer.checkForMagiskBinary();
		//boolean experiementalTests = rootBeer.checkForMagiskNative();
			
		return simpleTests;
	}
    

}
