package com.jepongdevxyz.vpn.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.widget.Button;
import android.widget.EditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.io.File;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.jepongdevxyz.vpn.R;
import androidx.appcompat.widget.Toolbar;

import android.widget.LinearLayout;
import android.widget.Toast;
import android.net.Uri;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.io.IOException;
import com.jepongdevxyz.vpn.adapter.ManagerFilesAdapter;
import com.jepongdevxyz.vpn.logger.SkStatus;
import com.jepongdevxyz.vpn.MainActivity;
import com.jepongdevxyz.vpn.fragments.RequestPermissionDialogFragment;
import com.jepongdevxyz.vpn.config.ConfigParser;
import java.io.FileInputStream;
import androidx.appcompat.app.AlertDialog;
import java.text.DateFormat;
import es.dmoral.toasty.Toasty;

import com.jepongdevxyz.vpn.config.Settings;

public class ConfigImportFileActivity extends BaseActivity implements ManagerFilesAdapter.OnItemClickListener, RequestPermissionDialogFragment.RequestPermissionListener {
	private static final String RESTORE_CURRENT_PATH = "restoreCurrentPath";
	private static final String HOME_PATH = Environment.getExternalStorageDirectory().toString();
	private static final int PERMISSION_REQUEST_CODE = 1;
	private static final String BACK_DIR = "../";
	
	private RecyclerView rvManagerList;
	private ManagerFilesAdapter adapter;

	private List<ManagerFilesAdapter.ManagerItem> folderList;
	private List<ManagerFilesAdapter.ManagerItem> fileList;
	private String currentPath;
	private File backDir;
	private String pathAbertoPeloInicio;
    private int PICK_FILE = 123;
    private Toasty Aviso;
    public static SharedPreferences sShared;
    private LinearLayout tvItemLayout2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        

		if (SkStatus.isTunnelActive()) {
            Toasty.error(this, R.string.error_tunnel_service_execution, Toast.LENGTH_SHORT, true).show();
		//	Aviso.ToastError(getString(R.string.error_tunnel_service_execution));
			finish();
			return;
		}
		
		// Get the intent that started this activity
        Intent intent = getIntent();
		String scheme = intent.getScheme();
		
		// Figure out what to do based on the intent type
		if (scheme != null && (scheme.equals("file") || scheme.equals("content"))) {
			// mostra loading bonitinho
			setContentView(R.layout.launchvpn);
			
			Uri data = intent.getData();
			
			File file = new File(data.getPath());

			String file_extensao = getExtension(file);
			if (file_extensao != null && file_extensao.equals(ConfigParser.FILE_EXTENSAO)) {
				
				try {
					importarConfigInputFile(getContentResolver()
						.openInputStream(data));
				} catch(FileNotFoundException e) {
                    Toasty.error(this, R.string.error_file_config_incompatible, Toast.LENGTH_SHORT, true).show();
				//	Aviso.ToastError(getString(R.string.error_file_config_incompatible));
				}
			
			} else if (file_extensao != null && file_extensao.equals("")) {
                try {
					importarConfigInputFile(getContentResolver()
						.openInputStream(data));
				} catch(FileNotFoundException e) {
                    Toasty.error(this, R.string.error_file_config_incompatible, Toast.LENGTH_SHORT, true).show();
				//	Aviso.ToastError(getString(R.string.error_file_config_incompatible));
				}
            } else {
               Toasty.error(this, R.string.error_file_config_incompatible, Toast.LENGTH_SHORT, true).show();
			//	Aviso.ToastError(getString(R.string.error_file_config_incompatible));
			}
			
			finish();
			return;
		}
		
		// set Views
		setContentView(R.layout.activity_config_import);
		this.setFinishOnTouchOutside(false);
		rvManagerList = (RecyclerView) findViewById(R.id.rvMain_ManagerList);
		setToolbar();
        startGetFiles();
        
		
		// Para Android 6.0 Marshmallow e superior
	
		/*tvItemLayout2 = findViewById(R.id.ivManagerAdapter_ItemLayout2);
		tvItemLayout2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), OnlineConfig.class);
				startActivity(i);
			}
		});*/


	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(RESTORE_CURRENT_PATH, currentPath);
		super.onSaveInstanceState(outState);
	}

	// onClickPositiveButton na caixa de diálogo para confirmar permissões
	@Override
	public void onClickRequestPermissionDialog(DialogInterface dialog) {
		ActivityCompat.requestPermissions(ConfigImportFileActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		dialog.dismiss();
	}

	
	/**
	* ItemListener implementação
	*/
	
	@Override
	public void onItemClick(View view, int position) {
		File file = new File(folderList.get(position).getDirPath());
        if (Build.VERSION.SDK_INT >= 30) {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE);
        }
		else if (file.isDirectory()) {
			fileManager(folderList.get(position).getDirPath());
		} else {
			// Tratamento para arquivos
			String file_extensao = getExtension(file);
			if (file_extensao != null && file_extensao.equals(ConfigParser.FILE_EXTENSAO)) {
				try {
					importarConfigInputFile(new FileInputStream(file));
				} catch (FileNotFoundException e) {
                    Toasty.error(this, R.string.error_file_not_found, Toast.LENGTH_SHORT, true).show();
				//	Aviso.ToastError(getString(R.string.error_file_not_found));
				}
			}
		}
	}

	@Override
	public void onItemLongClick(View view, int position)
	{
		File file = new File(folderList.get(position).getDirPath());
		if (!file.isDirectory()) {
			showApagarPrompt(file);
		}
	}
    
    /**private void RecibirLinks() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink.getQueryParameter("token") != null) {
								setContentView(R.layout.launchvpn);
                                String token = deepLink.getQueryParameter("token");
                                
                                if (SkStatus.isTunnelActive()) {
                                    Toasty.error(ConfigImportFileActivity.this, "Detiene el servicio primero", Toast.LENGTH_SHORT, true).show();
									return;
                                } else {
                                    try {
                                        NetFreeCloud.Importar(ConfigImportFileActivity.this, ConfigImportFileActivity.this, token, "configimportclass");
                                    } catch (IOException e) {
                                        Toasty.error(ConfigImportFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT, true).show();
                                    }
                                }
                               
                            }
                        } else {
                            //Toasty.error(MainActivity.this, "Ocurrio un Error", Toast.LENGTH_SHORT, true).show();
                            //Log.d("WALKIRIA", "ERROR WITH DYNAMIC LINK OR NO LINK AT ALL");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
						Toasty.error(ConfigImportFileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        //Log.d("WALKIRIA", "ERROR WITH DYNAMIC LINK " + e.toString());

                    }
                });
    }**/
	
	private void startMainListManager() {
		folderList = new ArrayList<>();
		fileList = new ArrayList<>();
		currentPath = null;
		pathAbertoPeloInicio = null;
		backDir = null;
		String[] listDirs = {
			HOME_PATH,
			HOME_PATH + "/Download",
			HOME_PATH + "/V2 injector"
		};
		
		for (String dir : listDirs) {
			File file = new File(dir);
			
			if (file.exists() && !file.isHidden() && file.canRead()) {
				if (file.isDirectory()) {
					String dir_name = file.getName();
					
					if (dir.equals(HOME_PATH))
						dir_name = getString(R.string.dir_home_name);
					
					folderList.add(new ManagerFilesAdapter.ManagerItem(dir_name, file.getPath(), getString(R.string.dir_name)));

				}
			}
			
		}
		folderList.addAll(fileList);
		adapter = new ManagerFilesAdapter(this, folderList);
		rvManagerList.setLayoutManager(new LinearLayoutManager(this));
		adapter.setOnItemClickListener(this);
		rvManagerList.setAdapter(adapter);
	}
    private void startGetFiles() {
		folderList = new ArrayList<>();
		fileList = new ArrayList<>();
		currentPath = null;
		pathAbertoPeloInicio = null;
		backDir = null;
		String[] listDirs = {
			HOME_PATH,
		};
		
		for (String dir : listDirs) {
			File file = new File(dir);
			
			if (file.exists() && !file.isHidden() && file.canRead()) {
				if (file.isDirectory()) {
					String dir_name = file.getName();
					
					if (dir.equals(HOME_PATH))
						dir_name = getString(R.string.dir_home_name);
					
					folderList.add(new ManagerFilesAdapter.ManagerItem(dir_name, file.getPath(), getString(R.string.dir_name)));
				}
			}
			
		}
		folderList.addAll(fileList);
		adapter = new ManagerFilesAdapter(this, folderList);
		rvManagerList.setLayoutManager(new LinearLayoutManager(this));
		adapter.setOnItemClickListener(this);
		rvManagerList.setAdapter(adapter);
	}
    
    private void getFilesBuild30() {
		folderList = new ArrayList<>();
		fileList = new ArrayList<>();
		currentPath = null;
		pathAbertoPeloInicio = null;
		backDir = null;
		String[] listDirs = {
			HOME_PATH + "/V2 injector",
		};
		
		for (String dir : listDirs) {
			File file = new File(dir);
			
			if (file.exists() && !file.isHidden() && file.canRead()) {
				if (file.isDirectory()) {
					String dir_name = file.getName();
					
					if (dir.equals(HOME_PATH))
						dir_name = getString(R.string.dir_home_name);
					
					folderList.add(new ManagerFilesAdapter.ManagerItem(dir_name, file.getPath(), getString(R.string.dir_name)));
				}
			}
			
		}
		folderList.addAll(fileList);
		adapter = new ManagerFilesAdapter(this, folderList);
		rvManagerList.setLayoutManager(new LinearLayoutManager(this));
		adapter.setOnItemClickListener(this);
		rvManagerList.setAdapter(adapter);
	}

	private void fileManager(String folderPath) {
		if (folderPath == null || folderPath.equals(BACK_DIR)) {
			if (backDir != null && canGoBackFolder())
				folderPath = backDir.getPath();
			else {
				startMainListManager();
				return;
			}
		}
		folderList = new ArrayList<>();
		fileList = new ArrayList<>();
		
		// está vindo do inicio
		if (currentPath == null) {
			pathAbertoPeloInicio = folderPath;
		}
		currentPath = folderPath;
		File path = new File(folderPath);
		if (path.getParentFile() != null && !currentPath.equals(pathAbertoPeloInicio)) {
			backDir = path.getParentFile();
		}
		
		for (File file : path.listFiles()) {
			if (!file.isHidden() && file.canRead()) {
				if (file.isDirectory()) {
					folderList.add(new ManagerFilesAdapter.ManagerItem(file.getName(), file.getPath(), getString(R.string.dir_name)));
				} else {
					String file_extensao = getExtension(file);
					if (file_extensao != null && file_extensao.equals(ConfigParser.FILE_EXTENSAO)) {
						String dateLastModified = String.format("%s %s",
							android.text.format.DateFormat.getDateFormat(this).format(file.lastModified()),
								android.text.format.DateFormat.getTimeFormat(this).format(file.lastModified()));
						
						fileList.add(new ManagerFilesAdapter.ManagerItem(file.getName(), file.getPath(), dateLastModified));
					}
				}
			}
		}

		Collections.sort(folderList);
		Collections.sort(fileList);
		folderList.addAll(fileList);

		// Adiciona a opção de voltar
		if (canGoBackFolder() || currentPath.equals(pathAbertoPeloInicio)) {
			folderList.add(0, new ManagerFilesAdapter.ManagerItem("..", BACK_DIR, folderPath));
		}

		adapter = new ManagerFilesAdapter(this, folderList);
		rvManagerList.setLayoutManager(new LinearLayoutManager(this));
		adapter.setOnItemClickListener(this);
		rvManagerList.setAdapter(adapter);
	}

	@Override
	public void onBackPressed() {
		if (canGoBackFolder() || currentPath != null && currentPath.equals(pathAbertoPeloInicio)) {
			fileManager(BACK_DIR);
		}
		else {
			super.onBackPressed();
		}
	}

	private boolean canGoBackFolder() {
		if (backDir != null) {
			return backDir.canRead() && !backDir.getPath().equals(currentPath);
		}
		return false;
	}

	private void requestPermissionInfo() {
		if (ActivityCompat.shouldShowRequestPermissionRationale(ConfigImportFileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			// Dialog de informação para caso o usuário já tenha negado as permissões pelo menos uma vez
			RequestPermissionDialogFragment dialog = RequestPermissionDialogFragment.newInstance();
			dialog.show(getSupportFragmentManager(), dialog.getTag());
		} else {
			ActivityCompat.requestPermissions(ConfigImportFileActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
		}
	}

	// Método chamado assim que o usuário concede ou nega uma permissão
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSION_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//fileManager(HOME_PATH);
                    if (Build.VERSION.SDK_INT >= 30) {
                        getFilesBuild30();
                    } else {
                        startMainListManager();
                    }
				} else {
					requestPermissionInfo();
				}
			break;
		}
	}

	private boolean permissionGranted() {
		int result = ContextCompat.checkSelfPermission(ConfigImportFileActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (result == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		return false;
	}
	
	public String getExtension(File file) {
		String filename = file.getAbsolutePath();
		
		if (filename.contains(".")) {
			return filename.substring(filename.lastIndexOf(".") + 1);
		}
		
		return "";
	}
    
    public void onActivityResult(int int1, int int2, Intent intent) {
        super.onActivityResult(int1, int2, intent);
        if (int1 == PICK_FILE && int2 == -1) {
            try {
                importarConfigInputFile(getContentResolver().openInputStream(intent.getData()));
            } catch (FileNotFoundException unused) {
            
            }
        }
    }
	
	public void importarConfigInputFile(InputStream inputFile) {
		try {
			
			if (!ConfigParser.convertInputAndSave(inputFile, this)) {
				throw new IOException(getString(R.string.error_save_settings));
			}
			
            
			long mValidade = new Settings(this)
				.getPrefsPrivate().getLong(Settings.CONFIG_VALIDADE_KEY, 0);
			
			if (mValidade > 0) {
				SkStatus.logInfo(R.string.log_settings_valid,
					android.text.format.DateFormat.getDateFormat(this).format(mValidade));
			}
Toasty.success(this, R.string.success_import_settings, Toast.LENGTH_SHORT, true).show();
		//	Aviso.ToastExito(getString(R.string.success_import_settings));
            
            if (mValidade > 0) {
				Calendar c = Calendar.getInstance();
				final long time_hoje = c.getTimeInMillis();
				
				c.setTimeInMillis(time_hoje+(1000*60*60*24));
				
				long dias = ((mValidade-time_hoje)/1000/60/60/24);
				DateFormat df = DateFormat.getDateInstance();
				mValidade = c.getTimeInMillis();
				Toasty.success(this,dias + " Dias Restantes" + " - " + "Expira: " + df.format(mValidade) ,Toast.LENGTH_SHORT, true).show();
			//	Aviso.ToastExitoLargo(dias + " Dias Restantes" + " - " + "Expira: " + df.format(mValidade));
			}
                
				
			// atualiza views
			MainActivity.updateMainViews(this);
			
		} catch(IOException e) {
            Toasty.error(this ,e.getMessage(), Toast.LENGTH_SHORT, true).show();
		//	Aviso.ToastError(e.getMessage());
		}
            
		
		Intent intent = new Intent(this,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
	
	
	/**
	* Toolbar
	*/
	
	private Toolbar mToolbar;
	private void setToolbar() {
		mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
     
    
    
	private void showApagarPrompt(final File file) {
		AlertDialog dialog = new AlertDialog.Builder(this)
			.create();
		
			dialog.setTitle(R.string.title_delete_file);
			dialog.setMessage(getString(R.string.alert_delete_file));

			dialog.setButton(dialog.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{
					if (file.delete())
						fileManager(currentPath);
					else
						Toast.makeText(getApplicationContext(), R.string.error_delete_file,
							Toast.LENGTH_SHORT).show();
				}
			});

			dialog.setButton(dialog.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2) {}
			});

		dialog.show();
	}





	}
























