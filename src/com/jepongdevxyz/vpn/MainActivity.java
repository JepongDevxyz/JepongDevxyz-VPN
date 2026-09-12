package com.jepongdevxyz.vpn;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
// import android.preference.PreferenceManager;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Html;
import android.view.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.jepongdevxyz.vpn.activities.BaseActivity;
import com.jepongdevxyz.vpn.activities.ConfigExportFileActivity;
import com.jepongdevxyz.vpn.activities.ConfigGeralActivity;
import com.jepongdevxyz.vpn.activities.ConfigImportFileActivity;
import com.jepongdevxyz.vpn.activities.LicenseActivity;
import com.jepongdevxyz.vpn.activities.v2ray;
import com.jepongdevxyz.vpn.adapter.LogsAdapter;
import com.jepongdevxyz.vpn.config.ConfigParser;
import com.jepongdevxyz.vpn.config.Settings;
import com.jepongdevxyz.vpn.fragments.ProxyRemoteDialogFragment;
import com.jepongdevxyz.vpn.fragments.sni;
import com.jepongdevxyz.vpn.fragments.udp;
import com.jepongdevxyz.vpn.logger.ConnectionStatus;
import com.jepongdevxyz.vpn.logger.SkStatus;
import com.jepongdevxyz.vpn.model.ExceptionHandler;
import com.jepongdevxyz.vpn.tunnel.TunnelManagerHelper;
import com.jepongdevxyz.vpn.tunnel.TunnelUtils;
import com.jepongdevxyz.vpn.util.Utils;
import com.jepongdevxyz.vpn.util.VPNUtils;
import com.jepongdevxyz.vpn.util.securepreferences.SecurePreferences;
import com.jepongdevxyz.vpn.view.PayloadGenerator;
import es.dmoral.toasty.Toasty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.dawson.proxyserver.ui.ProxySettings;

public class MainActivity extends BaseActivity
    implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        SkStatus.StateListener {
  private static final String TAG = MainActivity.class.getSimpleName();
  private static final String UPDATE_VIEWS = "MainUpdate";
  /*private static final String a1 =
      new Object() {
        int MDevz;

        public String toString() {
          byte[] buf = new byte[5];
          MDevz = -351;
          buf[0] = (byte) (MDevz >>> 1);
          MDevz = 133220;
          buf[1] = (byte) (MDevz >>> 11);
          MDevz = 327155758;
          buf[2] = (byte) (MDevz >>> 22);
          MDevz = -1531921;
          buf[3] = (byte) (MDevz >>> 13);
          MDevz = -398458988;
          buf[4] = (byte) (MDevz >>> 21);
          return new String(buf);
        }
      }.toString();

  private static final String b =
      new Object() {
        int MDevz;

        public String toString() {
          byte[] buf = new byte[19];
          MDevz = -48764;
          buf[0] = (byte) (MDevz >>> 8);
          MDevz = -2220;
          buf[1] = (byte) (MDevz >>> 4);
          MDevz = 1946157173;
          buf[2] = (byte) (MDevz >>> 24);
          MDevz = -580;
          buf[3] = (byte) (MDevz >>> 2);
          MDevz = 116858;
          buf[4] = (byte) (MDevz >>> 10);
          MDevz = 553648125;
          buf[5] = (byte) (MDevz >>> 24);
          MDevz = 6553664;
          buf[6] = (byte) (MDevz >>> 16);
          MDevz = 52953189;
          buf[7] = (byte) (MDevz >>> 19);
          MDevz = -2408547;
          buf[8] = (byte) (MDevz >>> 14);
          MDevz = -913468;
          buf[9] = (byte) (MDevz >>> 12);
          MDevz = 58885;
          buf[10] = (byte) (MDevz >>> 9);
          MDevz = -4954;
          buf[11] = (byte) (MDevz >>> 5);
          MDevz = -36962366;
          buf[12] = (byte) (MDevz >>> 18);
          MDevz = -275;
          buf[13] = (byte) (MDevz >>> 1);
          MDevz = 211;
          buf[14] = (byte) (MDevz >>> 1);
          MDevz = -1245;
          buf[15] = (byte) (MDevz >>> 3);
          MDevz = -4718696;
          buf[16] = (byte) (MDevz >>> 15);
          MDevz = 7471231;
          buf[17] = (byte) (MDevz >>> 16);
          MDevz = 237591;
          buf[18] = (byte) (MDevz >>> 12);
          return new String(buf);
        }
      }.toString();
      */
  private Settings mConfig;
  private Toolbar toolbar_main;
  private Handler mHandler;
  private LinearLayout mainLayout;
  private Button starterButton;
  private AlertDialog ppd;
  private EditText textInput;
  private TextView app_info_text;
  private CardView configMsgLayout;

  private SharedPreferences spref;
  private TextView configMsgText;
  private static final String NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS";

  private static final int PERMISSION_REQUEST_CODE = 1;
  private static final int CAMERS = 1;
  private static final int ALMYWW = 1;
  private static final String INTER = "ca-app-pub-3940256099942544/1033170000";
  private PayloadGenerator paygen;
  private static final String[] tabTitle = {"Home", "Register", "Tools", "Help"};
  private LogsAdapter mLogAdapter;
  private RecyclerView logList;
  private String messageUpdate;
  private ViewPager vp;
  private TabLayout tabs;
  private View changelog, license, devw, dev;
  private MenuItem settings;
  private MenuItem ifolder;
  public static boolean isHomeTab = true;
  private CardView tunnelLayout;
  private CardView card_tools1;
  private CardView card_tools2;
  private CardView card_tools3;
  private LinearProgressIndicator progress_indicator;
  private CheckBox dnsCheckBox;
  private CardView connectionCardview;
  private TextView tunnelInfo;
  private TextInputLayout payloadLayout;
  private View proxyLayout, payload;
  private static final int REQUEST_CODE = 1;
  private View sslLayout;
  private String proxyStr;
  private TextView proxyText;
  private FloatingActionButton deleteLogs, infow;
  private TextInputEditText payloadEdit;
  private TextView sniText;
  private NavigationView drawerNavigationView;
  private MenuItem auth;
  private boolean isLoading;
  private MenuItem settingsSSH;
  private InterstitialAd success;
  private InterstitialAd interstitialAd;
  private AdView adsBannerView;
  public static SharedPreferences sShared;
  private TextView custom;
  private LinearLayout layoutForceTLS;
  private LinearLayout LayoutForceTLS;
  private ConstraintLayout save, expor;
  private Spinner Spinner;
  private BottomSheetBehavior bottomSheetBehavior;
  private Spinner spinner;
  private BottomSheetBehavior<NestedScrollView> behavior;
  private ArrayList<String> ArrayList;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mHandler = new Handler();
    mConfig = new Settings(this);
    ReviewManager manager = ReviewManagerFactory.create(this);
    paygen = new PayloadGenerator(this);
    sShared = this.mConfig.getPrefsPrivate();
    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
    SharedPreferences prefs = getSharedPreferences(MyApplication.PREFS_GERAL, Context.MODE_PRIVATE);
    int lastVersion = prefs.getInt("last_version", 0);
    // se primeira vez
    MobileAds.initialize(this);

    try {
      int idAtual = ConfigParser.getBuildId(this);

      if (lastVersion < idAtual) {
        SharedPreferences.Editor pEdit = prefs.edit();
        pEdit.putInt("last_version", idAtual);
        pEdit.apply();
        showBoasVindas();
      }
    } catch (IOException e) {
    }

    doLayout();
    IntentFilter filter = new IntentFilter();
    filter.addAction(UPDATE_VIEWS);
    LocalBroadcastManager.getInstance(this).registerReceiver(mActivityReceiver, filter);

    doUpdateLayout();
    adsPopUp();
  }

  private void doLayout() {
    setContentView(R.layout.activity_main_drawer);
    final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
    final SecurePreferences prefsPrivate = this.mConfig.getPrefsPrivate();

    toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
    doDrawerMain(toolbar_main);
    setSupportActionBar(toolbar_main);
    doTabs();
    if (Build.VERSION.SDK_INT >= 33
        && checkSelfPermission(NOTIFICATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] {NOTIFICATION_PERMISSION}, PERMISSION_REQUEST_CODE);
    }

    NestedScrollView bottomSheet = findViewById(R.id.bottomSheet);
    behavior = BottomSheetBehavior.from(bottomSheet);

    // Configurar el estado inicial del BottomSheet
    behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    // Configurar el deslizamiento
    behavior.setPeekHeight(0); // Altura mínima al arrastrar el BottomSheet

    // Agregar un listener para manejar eventos del BottomSheet
    behavior.addBottomSheetCallback(
        new BottomSheetBehavior.BottomSheetCallback() {
          @Override
          public void onStateChanged(@NonNull View bottomSheet, int newState) {
            // Manejar cambios de estado
          }

          @Override
          public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // Manejar el deslizamiento del BottomSheet
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
          }
        });

    progress_indicator = (LinearProgressIndicator) findViewById(R.id.progress_indicator);
    mainLayout = (LinearLayout) findViewById(R.id.activity_mainLinearLayout);
    starterButton = (Button) findViewById(R.id.activity_starterButtonMain);
    starterButton.setOnClickListener(this);
    configMsgLayout = (CardView) findViewById(R.id.activitymainCardView1);
    configMsgText = (TextView) findViewById(R.id.activity_mainMensagemConfigTextView);
    tunnelLayout = (CardView) findViewById(R.id.tunnelCardView);
    tunnelLayout.setOnClickListener(this);
    tunnelInfo = (TextView) findViewById(R.id.activitymainTextView1);
    connectionCardview = (CardView) findViewById(R.id.connection_cardView);
    proxyText = (TextView) findViewById(R.id.proxyText);
    payloadEdit = (TextInputEditText) findViewById(R.id.payloadEdit);

    save = (ConstraintLayout) findViewById(R.id.save);
    save.setOnClickListener(
        new OnClickListener() {

          @Override
          public void onClick(View p1) {

            ooo();
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
          }
        });

    expor = (ConstraintLayout) findViewById(R.id.expor);
    expor.setOnClickListener(
        new OnClickListener() {

          @Override
          public void onClick(View p1) {

            oo();
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
          }
        });

    license = findViewById(R.id.license);
    dev = findViewById(R.id.developer);
    devw = findViewById(R.id.developerpv);

    devw.setOnClickListener(this);
    license.setOnClickListener(this);
    dev.setOnClickListener(this);
    PackageInfo pinfo = Utils.getAppInfo(this);
    if (pinfo != null) {
      String version_nome = pinfo.versionName;
      int version_code = pinfo.versionCode;
      String header_text = String.format("%s (%d)", version_nome, version_code);
      app_info_text = (TextView) findViewById(R.id.appVersion);
      app_info_text.setText(header_text);
    }

    payload = (View) findViewById(R.id.payload);
    proxyLayout = (View) findViewById(R.id.proxyLayout);
    proxyLayout.setOnClickListener(this);
    sslLayout = (View) findViewById(R.id.sslLayout);
    sslLayout.setOnClickListener(this);
    sniText = (TextView) findViewById(R.id.sslText);
    

    // set ADS
    adsBannerView = (AdView) findViewById(R.id.adView1);

    if (TunnelUtils.isNetworkOnline(MainActivity.this)) {
      adsBannerView.setAdListener(
          new AdListener() {
            @Override
            public void onAdLoaded() {
              if (adsBannerView != null) {
                adsBannerView.setVisibility(View.VISIBLE);
              }
            }
          });
      adsBannerView.loadAd(new AdRequest.Builder().build());
    }

    this.card_tools1 = (CardView) findViewById(R.id.cardtools1);
    this.card_tools1.setOnClickListener(
        new View.OnClickListener() {
          public void onClick(View view) {
            MainActivity.this.startActivity(
                new Intent(MainActivity.this, me.dawson.proxyserver.ui.ProxySettings.class));
          }
        });
    
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    mLogAdapter = new LogsAdapter(layoutManager, this);
    mLogAdapter.scrollToLastPosition();
  }

  private synchronized void doSaveData() {
    SharedPreferences prefs = mConfig.getPrefsPrivate();
    SharedPreferences.Editor edit = prefs.edit();
    if (!prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
      if (payloadEdit != null && !prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true)) {
        if (mainLayout != null) mainLayout.requestFocus();
        edit.putString(Settings.CUSTOM_PAYLOAD_KEY, payloadEdit.getText().toString());
      }
    }
    int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
    if (tunnelType == Settings.bTUNNEL_TYPE_SLOWDNS) {
      edit.putString(Settings.SERVIDOR_KEY, "127.0.0.1");
      edit.putString(Settings.SERVIDOR_PORTA_KEY, "2222");
    }

        if (tunnelType == Settings.bTUNNEL_TYPE_UDP) {
      edit.putString(Settings.UDP_WINDOW, "8888");
    }

        
        
        
    edit.apply();
  }

  private void ooo() {
    SharedPreferences prefs2 = mConfig.getPrefsPrivate();
    if (SkStatus.isTunnelActive()) {
      Toasty.error(this, (R.string.error_tunnel_service_execution), Toast.LENGTH_SHORT, true)
          .show();
    } else if (prefs2.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
      Toasty.error(this, (R.string.locked_msg), Toast.LENGTH_SHORT, true).show();
    } else {
      Intent intentExport = new Intent(this, ConfigExportFileActivity.class);
      startActivity(intentExport);
    }
  }

  private void oo() {
    if (SkStatus.isTunnelActive()) {
      Toasty.error(this, (R.string.error_tunnel_service_execution), Toast.LENGTH_SHORT, true)
          .show();
    } else {
      Intent intentImport = new Intent(this, ConfigImportFileActivity.class);
      startActivity(intentImport);
    }
  }

  protected void showBoasVindas() {
    new MaterialAlertDialogBuilder(this)
        .setTitle(R.string.attention)
        .setMessage(R.string.first_start_msg)
        .setPositiveButton(
            R.string.ok,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface di, int p) {
                // ok
              }
            })
        .setCancelable(false)
        .show();
  }

  public void hype(View v) {
    MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(MainActivity.this);

    builder1.setTitle(Html.fromHtml("<b>NOVEDADES<b>"));
    builder1.setMessage(
        Html.fromHtml(
            "Telegram Grupo :<br><a href=\"https://t.me/AlfSilic\">https://t.me/AlfSilic</a><br>"));

    builder1.setCancelable(false);
    builder1.setPositiveButton(
        "Aceptar",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {}
        });
    AlertDialog Alert1 = builder1.create();
    Alert1.show();
  }

  private void doUpdateLayout() {
    SharedPreferences prefs = mConfig.getPrefsPrivate();
    final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
    boolean isRunning = SkStatus.isTunnelActive();
    setStarterButton(starterButton, this);
    boolean protect = prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false);
    String proxy = mConfig.getPrivString(Settings.PROXY_IP_KEY);


    int msgVisibility = View.GONE;
    String msgText = "";

    if (prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
      String msg = mConfig.getPrivString(Settings.CONFIG_MENSAGEM_KEY);
      if (!msg.isEmpty()) {
        msgText = msg.replace("\n", "<br/>");
        msgVisibility = View.VISIBLE;
      }

      if (mConfig.getPrivString(Settings.PROXY_IP_KEY).isEmpty()
          || mConfig.getPrivString(Settings.PROXY_PORTA_KEY).isEmpty()) {}
    }
    configMsgText.setText(msgText.isEmpty() ? "" : Html.fromHtml(msgText));
    configMsgLayout.setVisibility(msgVisibility);

    if (prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
      proxyText.setText("******");

      proxyLayout.setEnabled(false);

    } else {
      proxyLayout.setEnabled(!isRunning);

      if (proxy.equals("")) {
        proxyText.setText(R.string.squid);
      } else {

        proxyText.setText(
            String.format("%s:%s", proxy, mConfig.getPrivString(Settings.PROXY_PORTA_KEY)));
      }
    }

    int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);
    switch (tunnelType) {
      case Settings.bTUNNEL_TYPE_SSH_DIRECT:
        if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true)) {
          connectionCardview.setVisibility(View.VISIBLE);
          payload.setVisibility(View.VISIBLE);
          proxyLayout.setVisibility(View.GONE);
          sslLayout.setVisibility(View.GONE);
          tunnelInfo.setText(getString(R.string.direct) + getString(R.string.custom_payload1));
          if (protect) {
            payloadEdit.setEnabled(false);
            payloadEdit.setText("******");
          } else {
            payloadEdit.setEnabled(!isRunning);
            payloadEdit.setText(mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY));
          }

        } else {
          connectionCardview.setVisibility(View.GONE);
          payload.setVisibility(View.GONE);
          proxyLayout.setVisibility(View.GONE);
          sslLayout.setVisibility(View.GONE);
          tunnelInfo.setText(getString(R.string.direct));
        }
        break;
      case Settings.bTUNNEL_TYPE_UDP:
        connectionCardview.setVisibility(View.GONE);
        tunnelInfo.setText("Hysteria");

        break;

      case Settings.bTUNNEL_TYPE_V2RAY:
        connectionCardview.setVisibility(View.GONE);
        tunnelInfo.setText("V2ray");

        break;

      case Settings.bTUNNEL_TYPE_SSL_RP:
        if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true)) {
          connectionCardview.setVisibility(View.VISIBLE);
          tunnelInfo.setText(getString(R.string.sslrp) + getString(R.string.custom_payload1));
          proxyLayout.setVisibility(View.VISIBLE);
          if (protect) {
            payloadEdit.setEnabled(false);
            payloadEdit.setText("******");
          } else {
            payloadEdit.setEnabled(!isRunning);
            payloadEdit.setText(mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY));
            sslLayout.setEnabled(!isRunning);
            String ssl = mConfig.getPrivString(Settings.CUSTOM_SNI);
            if (ssl.isEmpty()) {
              sniText.setText("www.google.com");
            } else {
              sniText.setText(ssl);
            }
          }
        } else {
          connectionCardview.setVisibility(View.VISIBLE);
          proxyLayout.setVisibility(View.VISIBLE);
          sslLayout.setVisibility(View.VISIBLE);
          payload.setVisibility(View.GONE);
          tunnelInfo.setText(getString(R.string.sslrp));
        }
        break;

      case Settings.bTUNNEL_TYPE_SSH_SSLTUNNEL:
        if (protect) {
          sslLayout.setEnabled(false);
          sniText.setText("******");
        } else {
          sslLayout.setEnabled(!isRunning);
          String ssl = mConfig.getPrivString(Settings.CUSTOM_SNI);
          if (ssl.isEmpty()) {
            sniText.setText("www.google.com");
          } else {
            sniText.setText(ssl);
          }
        }
        connectionCardview.setVisibility(View.VISIBLE);
        payload.setVisibility(View.GONE);
        proxyLayout.setVisibility(View.GONE);
        sslLayout.setVisibility(View.VISIBLE);
        tunnelInfo.setText(getString(R.string.ssl));
        break;

      case Settings.bTUNNEL_TYPE_PAY_SSL:
        if (protect) {
          payloadEdit.setEnabled(false);
          payloadEdit.setText("******");
          sslLayout.setEnabled(false);
          sniText.setText("******");
        } else {
          payloadEdit.setEnabled(!isRunning);
          payloadEdit.setText(mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY));
          sslLayout.setEnabled(!isRunning);
          String ssl = mConfig.getPrivString(Settings.CUSTOM_SNI);
          if (ssl.isEmpty()) {
            sniText.setText("com.google.com");
          } else {
            sniText.setText(ssl);
          }
        }
        connectionCardview.setVisibility(View.VISIBLE);
        payload.setVisibility(View.VISIBLE);
        proxyLayout.setVisibility(View.GONE);
        sslLayout.setVisibility(View.VISIBLE);
        tunnelInfo.setText(getString(R.string.sslpay));
        break;

      case Settings.bTUNNEL_TYPE_SLOWDNS:
        connectionCardview.setVisibility(View.GONE);
        tunnelInfo.setText(getString(R.string.slowdns));

        break;

      case Settings.bTUNNEL_TYPE_SSH_PROXY:
        if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true)) {
          connectionCardview.setVisibility(View.VISIBLE);
          tunnelInfo.setText(getString(R.string.http) + getString(R.string.custom_payload1));
          proxyLayout.setVisibility(View.VISIBLE);
          if (protect) {
            payloadEdit.setEnabled(false);
            payloadEdit.setText("******");
          } else {
            payloadEdit.setEnabled(!isRunning);
            payloadEdit.setText(mConfig.getPrivString(Settings.CUSTOM_PAYLOAD_KEY));
          }
        }

        connectionCardview.setVisibility(View.VISIBLE);
        payload.setVisibility(View.VISIBLE);
        proxyLayout.setVisibility(View.VISIBLE);
        sslLayout.setVisibility(View.GONE);
        tunnelInfo.setText(getString(R.string.webssl));
        break;
    }
  }

  private void generator() {
    paygen.setDialogTitle(getString(R.string.pay_gen));
    paygen.setCancelListener(
        getString(R.string.cancel),
        new PayloadGenerator.OnCancelListener() {

          @Override
          public void onCancelListener() {}
        });
    paygen.setGenerateListener(
        getString(R.string.gen),
        new PayloadGenerator.OnGenerateListener() {

          @Override
          public void onGenerate(String payloadGenerated) {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
            if (!prefs.getBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true)) {
              payloadEdit.setText(payloadGenerated);
            } else {
              Toast.makeText(MainActivity.this, R.string.custom_payload_msg, Toast.LENGTH_SHORT)
                  .show();
            }
          }
        });
    paygen.show();
  }

  /** Tunnel SSH */
  public void doTabs() {
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    // deleteLogs = (FloatingActionButton)findViewById(R.id.delete_log);
    mLogAdapter = new LogsAdapter(layoutManager, this);
    logList = (RecyclerView) findViewById(R.id.recyclerLog);
    logList.setAdapter(mLogAdapter);
    logList.setLayoutManager(layoutManager);
    mLogAdapter.scrollToLastPosition();
    vp = (ViewPager) findViewById(R.id.viewpager);
    tabs = (TabLayout) findViewById(R.id.tablayout);
    vp.setAdapter(new MyAdapter(Arrays.asList(tabTitle)));
    vp.setOffscreenPageLimit(4);
    tabs.setTabMode(TabLayout.MODE_FIXED);
    tabs.setTabGravity(TabLayout.GRAVITY_FILL);
    tabs.setupWithViewPager(vp);
    deleteLogs = (FloatingActionButton) findViewById(R.id.clearLog);
    deleteLogs.setOnClickListener(
        new OnClickListener() {

          @Override
          public void onClick(View p1) {
            mLogAdapter.clearLog();
            // SkStatus.logInfo("+ "Registro Limpiado");
          }
        });
  }

  public class MyAdapter extends PagerAdapter {

    @Override
    public int getCount() {
      // TODO: Implement this method
      return 4;
    }

    @Override
    public boolean isViewFromObject(View p1, Object p2) {
      // TODO: Implement this method
      return p1 == p2;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      int[] ids = new int[] {R.id.tab1, R.id.tab2, R.id.tab3, R.id.tab4};
      int id = 0;
      id = ids[position];
      // TODO: Implement this method
      return findViewById(id);
    }

    @Override
    public CharSequence getPageTitle(int position) {
      // TODO: Implement this method
      return titles.get(position);
    }

    private List<String> titles;

    public MyAdapter(List<String> str) {
      titles = str;
    }
  }

  public void startOrStopTunnel(Activity activity) {
    SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();

    if (SkStatus.isTunnelActive()) {
      TunnelManagerHelper.stopSocksHttp(activity);

    } else {
      // oculta teclado se vísivel, tá com bug, tela verde
      // Utils.hideKeyboard(activity);
      Settings config = new Settings(activity);
      Intent intent = new Intent(activity, LaunchVpn.class);
      intent.setAction(Intent.ACTION_MAIN);
      if (config.getHideLog()) {
        intent.putExtra(LaunchVpn.EXTRA_HIDELOG, true);
      }
      activity.startActivity(intent);
    }
  }

  public void setStarterButton(Button starterButton, Activity activity) {
    String state = SkStatus.getLastState();
    boolean isRunning = SkStatus.isTunnelActive();

    if (starterButton != null) {
      int resId;
      SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();

      if (ConfigParser.isValidadeExpirou(prefsPrivate.getLong(Settings.CONFIG_VALIDADE_KEY, 0))) {
        resId = R.string.expired;
        starterButton.setEnabled(false);
        if (isRunning) {
          startOrStopTunnel(activity);
        }
      } else if (prefsPrivate.getBoolean(Settings.BLOQUEAR_ROOT_KEY, false)
          && ConfigParser.isDeviceRooted(activity)) {
        resId = R.string.blocked;
        starterButton.setEnabled(false);

        Toast.makeText(activity, R.string.error_root_detected, Toast.LENGTH_SHORT).show();

        if (isRunning) {
          startOrStopTunnel(activity);
        }
      } else if (SkStatus.SSH_INICIANDO.equals(state)) {
        resId = R.string.stop;

        progress_indicator.setVisibility(View.VISIBLE);
      } else if (SkStatus.SSH_PARANDO.equals(state)) {
        resId = R.string.state_stopping;

        progress_indicator.setVisibility(View.GONE);
      } else {
        resId = isRunning ? R.string.stop : R.string.start;
      }

      starterButton.setText(resId);
    }
  }

  /** Drawer Main */
  private DrawerLayout drawerLayout;

  private ActionBarDrawerToggle toggle;

  public void doDrawerMain(Toolbar toolbar) {
    drawerNavigationView = (NavigationView) findViewById(R.id.drawerNavigationView);

    drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayoutMain);

    // set drawer
    toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.cancel);

    drawerLayout.setDrawerListener(toggle);
    final SecurePreferences prefsPrivate = mConfig.getPrefsPrivate();
    toggle.syncState();

    // set app info
    PackageInfo pinfo = Utils.getAppInfo(this);
    if (pinfo != null) {
      String version_nome = pinfo.versionName;
      int version_code = pinfo.versionCode;
      String header_text = String.format("%s (%d)", version_nome, version_code);

      View view = drawerNavigationView.getHeaderView(0);

      TextView app_info_text = view.findViewById(R.id.nav_headerAppVersion);
      app_info_text.setText(header_text);
    }

    // set navigation view
    drawerNavigationView.setNavigationItemSelectedListener(this);
  }

  @Override
  public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
    super.onPostCreate(savedInstanceState, persistentState);
    if (toggle != null) toggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (toggle != null) toggle.onConfigurationChanged(newConfig);
  }

  @Override
  public void onClick(View p1) {
    SharedPreferences prefs = mConfig.getPrefsPrivate();
    boolean isRunning = SkStatus.isTunnelActive();
    switch (p1.getId()) {
      case R.id.activity_starterButtonMain:
        doSaveData();
        startOrStopTunnel(this);
        break;

      case R.id.license:
        license();
        break;

      case R.id.developer:
        startActivity(
            new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/AlfSilic")));
        break;

      case R.id.developerpv:
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/AlfSilic")));
        break;

      case R.id.tunnelCardView:
        if (!isRunning) {
          if (!prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
            startActivity(new Intent(this, TunnelActivity.class));
          }
        }
        break;

      case R.id.proxyLayout:
        final SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        doSaveData();
        mPref.edit().putBoolean(Settings.DNSFORWARD_KEY, true).apply();
        mPref.edit().putString(Settings.DNSTYPE_KEY, Settings.DNS_GOOGLE_KEY).apply();
        //custom.setText(mPref.getString(Settings.DNSTYPE_KEY, Settings.DNS_DEFAULT_KEY));
        if (!prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
          if (!isRunning) {
            DialogFragment fragProxy = new ProxyRemoteDialogFragment();
            fragProxy.show(getSupportFragmentManager(), "proxyDialog");
          }
        }
        break;


      case R.id.sslLayout:
        doSaveData();
        if (!prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
          if (!isRunning) {
            DialogFragment fragProxy = new sni();
            fragProxy.show(getSupportFragmentManager(), "sni");
          }
        }
        break;
    }
  }

  @Override
  public void updateState(
      final String state,
      String msg,
      int localizedResId,
      final ConnectionStatus level,
      Intent intent) {
    mHandler.post(
        new Runnable() {
          @Override
          public void run() {
            doUpdateLayout();
            if (SkStatus.isTunnelActive()) {
              if (level.equals(ConnectionStatus.LEVEL_CONNECTED)) {
                progress_indicator.setVisibility(View.GONE);

                adsPopUp();
              }
              if (level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)) {
                // connectionStatus.setText(R.string.servicestop);
                // checkupdate(false);
              }

              if (level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)) {
                // connectionStatus.setText(R.string.authenticating);
              }

              if (level.equals(ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET)) {
                // connectionStatus.setText(R.string.connecting);
                adsPopUp();
              }
              if (level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)) {
                // connectionStatus.setText(R.string.authfailed);
              }
              if (level.equals(ConnectionStatus.UNKNOWN_LEVEL)) {
                // connectionStatus.setText(R.string.disconnected);

                adsPopUp();
              }
            }
            if (level.equals(ConnectionStatus.LEVEL_NONETWORK)) {
              // connectionStatus.setText(R.string.nonetwork);
            }
          }
        });

    switch (state) {
      case SkStatus.SSH_CONECTADO:
        mHandler.postDelayed(
            new Runnable() {
              @Override
              public void run() {

                // carrega ads banner
                if (adsBannerView != null && TunnelUtils.isNetworkOnline(MainActivity.this)) {
                  adsBannerView.setAdListener(
                      new AdListener() {
                        @Override
                        public void onAdLoaded() {
                          if (adsBannerView != null && !isFinishing()) {
                            adsBannerView.setVisibility(View.VISIBLE);
                          }
                        }
                      });
                }
              }
            },
            1000);
        break;
    }
  }

  /** Recebe locais Broadcast */
  private void license() {
    // TODO: Implement this method
    startActivity(new Intent(this, LicenseActivity.class));
  }

  public void licencia(View v) {
    // TODO: Implement this method
    startActivity(new Intent(this, LicenseActivity.class));
  }

  private BroadcastReceiver mActivityReceiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          String action = intent.getAction();
          if (action == null) return;

          if (action.equals(UPDATE_VIEWS)) {
            doUpdateLayout();
          }
        }
      };

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main_menu, menu);
    settings = menu.findItem(R.id.miSettings);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    SharedPreferences prefs = mConfig.getPrefsPrivate();
    boolean isRunning = SkStatus.isTunnelActive();

    if (toggle != null && toggle.onOptionsItemSelected(item)) {
      return true;
    }

    // Menu Itens
    switch (item.getItemId()) {
      case R.id.miLimparConfig:
        if (!SkStatus.isTunnelActive()) {
          ext();
        } else {
          Toasty.error(this, (R.string.error_tunnel_service_execution), Toast.LENGTH_SHORT, true)
              .show();
        }
        break;

      case R.id.grupo:
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        break;

      case R.id.v2ray:
        Intent intent = new Intent(MainActivity.this, v2ray.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        break;

      case R.id.udp:
        SharedPreferences prefs21 = mConfig.getPrefsPrivate();
        if (SkStatus.isTunnelActive()) {
          Toasty.error(this, (R.string.error_tunnel_service_execution), Toast.LENGTH_SHORT, true)
              .show();
        } else if (prefs21.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
          Toasty.error(this, (R.string.locked_msg), Toast.LENGTH_SHORT, true).show();
        } else {
          DialogFragment fragProxy = new udp();
          fragProxy.show(getSupportFragmentManager(), "sni");
        }
        break;

      case R.id.slowdns:
        Intent intent27 = new Intent(MainActivity.this, ConfigGeralActivity.class);
        intent27.setAction(ConfigGeralActivity.OPEN_SETTINGS_DNS);
        intent27.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent27);
        break;

      case R.id.miSettings:
        Intent intentg = new Intent(MainActivity.this, ConfigGeralActivity.class);
        intentg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentg);
        break;

      case R.id.ssh:
        Intent intent25 = new Intent(MainActivity.this, ConfigGeralActivity.class);
        intent25.setAction(ConfigGeralActivity.OPEN_SETTINGS_SSH);
        intent25.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent25);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {

    int id = item.getItemId();
    switch (id) {
      case R.id.payload_generator:
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        if (SkStatus.isTunnelActive()) {
          Toasty.error(this, (R.string.error_tunnel_service_execution), Toast.LENGTH_SHORT, true)
              .show();
        } else if (prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {
          Toasty.error(this, (R.string.locked_msg), Toast.LENGTH_SHORT, true).show();
        } else {
          generator();
        }
        break;

      case R.id.BATTERY_OPTIMIZATION:
        E0();
        break;

      case R.id.miPhoneConfg:
        if (Build.VERSION.SDK_INT >= 30) {
          Intent in = new Intent(Intent.ACTION_MAIN);
          in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          in.setClassName("com.android.phone", "com.android.phone.settings.RadioInfo");
          this.startActivity(in);
        } else {
          Intent inTen = new Intent(Intent.ACTION_MAIN);
          inTen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          inTen.setClassName("com.android.settings", "com.android.settings.RadioInfo");
          this.startActivity(inTen);
        }
        break;

      case R.id.idha:
        id();
        break;

      case R.id.miSettings:
        Intent intent = new Intent(MainActivity.this, ConfigGeralActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        break;

      case R.id.wifi_hp:
        Intent intentr = new Intent(MainActivity.this, ProxySettings.class);
        intentr.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentr);
        break;

      case R.id.tele_group:
        String url2 = "https://t.me/AlfSilic";
        Intent intent5 = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
        intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent5, getText(R.string.open_with)));
        break;

      case R.id.miExit:
        showExitDialog();
        break;
    }
    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawers();
    }
    return true;
  }

  @Override
  public void onBackPressed() {

    showExitDialog();
  }

  @Override
  public void onResume() {
    super.onResume();
    SkStatus.addStateListener(this);
    adsPopUp();

    doSaveData();
  }

  @Override
  protected void onPause() {
    super.onPause();

    SkStatus.removeStateListener(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    doSaveData();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityReceiver);
  }

  private void id() {
    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
    String hadweridr = (VPNUtils.getHWID());
    alertDialogBuilder.setTitle("ID de dispositivo");

    alertDialogBuilder.setMessage(VPNUtils.getHWID());
    alertDialogBuilder.setPositiveButton(
        "Copiar",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface arg0, int arg1) {
            ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", hadweridr);
            clipboard.setPrimaryClip(clip);
          }
        });
    alertDialogBuilder.setNegativeButton("Salir", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        // no-op
      }
    });
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  private void E0() {
    try {
      Intent intent = new Intent("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
      startActivity(intent.setData(Uri.parse("package:" + getPackageName())));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void showExitDialog() {
    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
    alertDialogBuilder.setTitle("Atencion");
    alertDialogBuilder.setMessage("¿Estás seguro de queres salir de la app ?");
    alertDialogBuilder.setNegativeButton(
        "Minimizar",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface arg0, int arg1) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
          }
        });
    alertDialogBuilder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        Utils.exitAll(MainActivity.this);
      }
    });
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  private void ext() {
    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);

    alertDialogBuilder.setTitle("Atencion");
    alertDialogBuilder.setMessage(
        "¿Estás seguro de que quieres restaurar la configuración actual?");
    alertDialogBuilder.setPositiveButton(
        "Acepto",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface arg0, int arg1) {
            Settings.clearSettings(MainActivity.this);
            SkStatus.clearLog();
            pr();
            MainActivity.updateMainViews(MainActivity.this);
          }
        });
    alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        // no-op
      }
    });

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
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

  private void pr() {
    MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
    Context context = MyApplication.getApp();
    alertDialogBuilder.setTitle("Atencion");
    alertDialogBuilder.setMessage(
        "Acabas de restaurar las configuraciones , debes reiniciar la app");
    alertDialogBuilder.setPositiveButton(
        "Acepto",
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface arg0, int arg1) {
            restart_app();
            Utils.exitAll(MainActivity.this);

            Intent startActivity = new Intent(context, MainActivity.class);
            int pendingIntentId = 123456;
            PendingIntent pendingIntent =
                PendingIntent.getActivity(
                    context, pendingIntentId, startActivity, PendingIntent.FLAG_IMMUTABLE);
          }
        });
    alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface arg0, int arg1) {
        // no-op
      }
    });

    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  private void vread() {
    if (interstitialAd == null) {
      interstitialAd = new InterstitialAd(this);
      interstitialAd.setAdUnitId(INTER);
      interstitialAd.setAdListener(
          new AdListener() {
            @Override
            public void onAdClosed() {
              // A shown interstitial cannot be reused. Prepare the next one.
              interstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
              // Keep the instance so a later call can retry loading it.
            }
          });
    }

    if (!interstitialAd.isLoaded() && !interstitialAd.isLoading()) {
      interstitialAd.loadAd(new AdRequest.Builder().build());
    }
  }

  private void adsPopUp() {
    if (interstitialAd != null && interstitialAd.isLoaded()) {
      interstitialAd.show();
    } else {
      vread();
    }
  }

  /** Utils */
  public static void updateMainViews(Context context) {
    Intent updateView = new Intent(UPDATE_VIEWS);
    LocalBroadcastManager.getInstance(context).sendBroadcast(updateView);
  }
}
