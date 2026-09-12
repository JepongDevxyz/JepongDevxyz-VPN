package com.jepongdevxyz.vpn.activities;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.view.Menu;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.view.MenuInflater;
import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.logger.SkStatus;
import es.dmoral.toasty.Toasty;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;
import androidx.preference.PreferenceManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import com.jepongdevxyz.vpn.activities.BaseActivity;
import com.jepongdevxyz.vpn.config.Settings;
import com.jepongdevxyz.vpn.util.securepreferences.SecurePreferences;
import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class v2ray extends BaseActivity {

    private static final int REQUEST_CODE_EDIT_CONFIG = 100;

    private Toolbar tb;
    private AlertDialog.Builder ab;
    private TextView bloqueda;
    private ClipboardManager clipboardManager;
    private TextView codeView;
    public Settings mConfig;
    private boolean isEditMode = false; // Estado de edición manual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_v2ray);

        final SharedPreferences defaultSharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        Settings settings = new Settings(this);
        mConfig = settings;
        tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        bloqueda = findViewById(R.id.bloqueda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        codeView = findViewById(R.id.v2ray_json_config);

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        doUpdateLayout();

        final SecurePreferences prefsPrivate = mConfig.getPrefsPrivate();
        codeView.setText(prefsPrivate.getString(Settings.V2RAY_JSON, ""));
    }

    private void doUpdateLayout() {
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        boolean isRunning = SkStatus.isTunnelActive();
        boolean protect = prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false);

        if (protect) {
            codeView.setEnabled(false);
            bloqueda.setVisibility(View.VISIBLE);
            codeView.setVisibility(View.GONE);
        } else {
            // Permitir edición si está en modo edición manual o si el túnel no está activo
            codeView.setEnabled(isEditMode || !isRunning);
            codeView.setVisibility(View.VISIBLE);
            bloqueda.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.v2, menu);
        // Actualizar el título del botón "Editar" según el estado
        MenuItem editItem = menu.findItem(R.id.editar);
        if (editItem != null) {
            editItem.setTitle(isEditMode ? R.string.edit_mode_disabled : R.string.edit_config_title);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences prefs = mConfig.getPrefsPrivate();
        boolean isRunning = SkStatus.isTunnelActive();
        boolean protect = prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false);

        switch (item.getItemId()) {
            case R.id.pega:
                if (protect) {
                    error();
                } else {
                    if (!isRunning) {
                        pasteFromClipboard();
                    } else {
                        Toasty.error(this, R.string.error_tunnel_service_execution, Toast.LENGTH_SHORT, true).show();
                    }
                }
                break;

            case R.id.borrar:
                if (protect) {
                    error();
                } else {
                    if (!isRunning) {
                        codeView.setText("");
                    } else {
                        Toasty.error(this, R.string.error_tunnel_service_execution, Toast.LENGTH_SHORT, true).show();
                    }
                }
                break;

            case R.id.editar:
                if (protect) {
                    error();
                } else {
                    if (isEditMode) {
                        isEditMode = false; // Desactivar modo edición manual
                        doUpdateLayout();
                        invalidateOptionsMenu();
                        Toasty.info(this, R.string.edit_mode_disabled, Toast.LENGTH_SHORT, true).show();
                    } else {
                        // Iniciar actividad de edición en formulario
                        Intent intent = new Intent(this, EditV2rayConfigActivity.class);
                        String jsonConfig = codeView.getText().toString();
                        intent.putExtra("json_config", jsonConfig);
                        startActivityForResult(intent, REQUEST_CODE_EDIT_CONFIG);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_CONFIG && resultCode == RESULT_OK && data != null) {
            String jsonConfig = data.getStringExtra("json_config");
            if (jsonConfig != null && !jsonConfig.isEmpty()) {
                try {
                    new JSONObject(jsonConfig); // Validar JSON
                    codeView.setText(jsonConfig);
                    // Guardar en SecurePreferences
                    SharedPreferences.Editor edit = mConfig.getPrefsPrivate().edit();
                    edit.putString(Settings.V2RAY_JSON, jsonConfig);
                    edit.apply();
                    Toasty.success(this, R.string.update_success, Toast.LENGTH_SHORT, true).show();
                } catch (JSONException e) {
                    Toasty.error(this, R.string.error_invalid_json, Toasty.LENGTH_SHORT, true).show();
                }
            } else {
                Toasty.error(this, R.string.error_invalid_json, Toasty.LENGTH_SHORT, true).show();
            }
        }
    }

    private void error() {
        Toasty.error(this, R.string.error_config_locked, Toast.LENGTH_SHORT, true).show();
    }

    private void pasteFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboard.getPrimaryClip();
        if (clipData != null && clipData.getItemCount() > 0) {
            CharSequence text = clipData.getItemAt(0).getText();
            if (text != null) {
                String input = text.toString();
                if (input.startsWith("vmess://")) {
                    try {
                        String jsonConfig = convertVmessToJson(input);
                        codeView.setText(jsonConfig);
                    } catch (Exception e) {
                        Toasty.error(this, getString(R.string.error_invalid_vmess) + ": " + e.getMessage(), Toast.LENGTH_LONG, true).show();
                    }
                } else {
                    // Asumir que es JSON y validar
                    try {
                        new JSONObject(input); // Validar JSON
                        codeView.setText(input);
                    } catch (JSONException e) {
                        Toasty.error(this, R.string.error_invalid_json, Toasty.LENGTH_SHORT, true).show();
                    }
                }
            } else {
                Toasty.error(this, R.string.error_no_text_clipboard, Toasty.LENGTH_SHORT, true).show();
            }
        } else {
            Toasty.error(this, R.string.error_empty_clipboard, Toasty.LENGTH_SHORT, true).show();
        }
    }

    private String convertVmessToJson(String vmessUrl) throws Exception {
        // Separar la URL para manejar parámetros en la query string
        String[] urlParts = vmessUrl.split("\\?");
        String base64Part = urlParts[0].replace("vmess://", "");
        Map<String, String> queryParams = new HashMap<>();
        if (urlParts.length > 1) {
            String[] params = urlParts[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    queryParams.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
                }
            }
        }

        // Decodificar Base64
        byte[] decodedBytes;
        try {
            decodedBytes = Base64.decode(base64Part, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            throw new Exception(getString(R.string.error_invalid_base64));
        }

        String decodedString = new String(decodedBytes);

        // Parsear la cadena decodificada como JSON
        JSONObject vmessConfig;
        try {
            vmessConfig = new JSONObject(decodedString);
        } catch (JSONException e) {
            throw new Exception(getString(R.string.error_invalid_vmess) + ": " + e.getMessage());
        }

        // Extraer campos de la configuración VMess
        String id = vmessConfig.optString("id", "");
        String address = vmessConfig.optString("add", "");
        int port = vmessConfig.optInt("port", 0);
        int alterId = vmessConfig.optInt("aid", 0);
        Log.d("v2ray", "Valor de alterId desde vmess://: " + alterId);
        String security = vmessConfig.optString("scy", "auto");
        String network = vmessConfig.optString("net", "tcp");
        String remark = vmessConfig.optString("ps", "");
        String path = vmessConfig.optString("path", "");
        String host = vmessConfig.optString("host", "");
        String tls = vmessConfig.optString("tls", "");
        String sni = vmessConfig.optString("sni", "");
        String alpn = vmessConfig.optString("alpn", "");
        String fp = vmessConfig.optString("fp", "");
        String type = vmessConfig.optString("type", "none"); // Para header type o modo gRPC
        String allowInsecure = vmessConfig.optString("allowInsecure", "");

        // Manejar serviceName para gRPC
        String serviceName = vmessConfig.optString("serviceName", "");
        if (serviceName.isEmpty()) {
            serviceName = vmessConfig.optString("servicename", "");
        }
        if (serviceName.isEmpty()) {
            serviceName = vmessConfig.optString("service", "");
        }
        if (serviceName.isEmpty() && queryParams.containsKey("serviceName")) {
            serviceName = queryParams.get("serviceName");
        }
        // Para gRPC, usar path como serviceName si no se especifica explícitamente
        if (serviceName.isEmpty() && network.equals("grpc")) {
            serviceName = path;
        }
        Log.d("v2ray", "Valor de serviceName extraído: " + serviceName);

        // Usar type como modo para gRPC
        String mode = type; // "gun" o "multi"
        Log.d("v2ray", "Valor de mode para gRPC: " + mode);

        // Validar campos obligatorios
        if (id.isEmpty() || address.isEmpty() || port == 0) {
            throw new Exception(getString(R.string.error_missing_vmess_fields));
        }

        // Crear configuración JSON completa para V2Ray
        JSONObject v2rayConfig = new JSONObject();

        // Sección DNS
        JSONObject dns = new JSONObject();
        JSONObject dnsHosts = new JSONObject();
        dnsHosts.put("geosite:category-ads-all", "127.0.0.1");
        dnsHosts.put("domain:googleapis.cn", "googleapis.com");
        dnsHosts.put("dns.alidns.com", new JSONArray()
                .put("223.5.5.5")
                .put("223.6.6.6")
                .put("2400:3200::1")
                .put("2400:3200:baba::1"));
        dnsHosts.put("one.one.one.one", new JSONArray()
                .put("1.1.1.1")
                .put("1.0.0.1")
                .put("2606:4700:4700::1111")
                .put("2606:4700:4700::1001"));
        dnsHosts.put("dot.pub", new JSONArray()
                .put("1.12.12.12")
                .put("120.53.53.53"));
        dnsHosts.put("dns.google", new JSONArray()
                .put("8.8.8.8")
                .put("8.8.4.4")
                .put("2001:4860:4860::8888")
                .put("2001:4860:4860::8844"));
        dnsHosts.put("dns.quad9.net", new JSONArray()
                .put("9.9.9.9")
                .put("149.112.112.112")
                .put("2620:fe::fe")
                .put("2620:fe::9"));
        dnsHosts.put("common.dot.dns.yandex.net", new JSONArray()
                .put("77.88.8.8")
                .put("77.88.8.1")
                .put("2a02:6b8::feed:0ff")
                .put("2a02:6b8:0:1::feed:0ff"));
        dns.put("hosts", dnsHosts);
        JSONArray dnsServers = new JSONArray()
                .put("1.1.1.1")
                .put(new JSONObject()
                        .put("address", "1.1.1.1")
                        .put("domains", new JSONArray()
                                .put("domain:googleapis.cn")
                                .put("domain:gstatic.com")))
                .put(new JSONObject()
                        .put("address", "223.5.5.5")
                        .put("domains", new JSONArray()
                                .put("domain:dns.alidns.com")
                                .put("domain:dns.pub")
                                .put("domain:doh.pub")
                                .put("domain:dot.pub")
                                .put("domain:doh.360.cn")
                                .put("domain:dot.360.cn")
                                .put("geosite:cn")
                                .put("geosite:geolocation-cn"))
                        .put("expectIPs", new JSONArray().put("geoip:cn"))
                        .put("skipFallback", true));
        dns.put("servers", dnsServers);
        v2rayConfig.put("dns", dns);

        // Sección inbounds
        JSONObject inbound = new JSONObject();
        inbound.put("listen", "127.0.0.1");
        inbound.put("port", 10808);
        inbound.put("protocol", "socks");
        JSONObject inboundSettings = new JSONObject();
        inboundSettings.put("auth", "noauth");
        inboundSettings.put("udp", true);
        inboundSettings.put("userLevel", 8);
        inbound.put("settings", inboundSettings);
        JSONObject sniffing = new JSONObject();
        sniffing.put("destOverride", new JSONArray()
                .put("http")
                .put("tls"));
        sniffing.put("enabled", false);
        sniffing.put("routeOnly", false);
        inbound.put("sniffing", sniffing);
        inbound.put("tag", "socks");
        v2rayConfig.put("inbounds", new JSONArray().put(inbound));

        // Sección log
        JSONObject log = new JSONObject();
        log.put("loglevel", "warning");
        v2rayConfig.put("log", log);

        // Sección outbounds
        JSONObject outbound = new JSONObject();
        JSONObject mux = new JSONObject();
        mux.put("concurrency", -1);
        mux.put("enabled", false);
        outbound.put("mux", mux);
        outbound.put("protocol", "vmess");
        JSONObject protocolSettings = new JSONObject();
        JSONObject user = new JSONObject();
        user.put("id", id);
        user.put("alterId", alterId);
        user.put("security", security);
        user.put("level", 8);
        JSONObject server = new JSONObject();
        server.put("address", address);
        server.put("port", port);
        server.put("users", new JSONArray().put(user));
        protocolSettings.put("vnext", new JSONArray().put(server));
        outbound.put("settings", protocolSettings);
        JSONObject streamSettings = new JSONObject();
        streamSettings.put("network", network);

        // Configurar streamSettings según el protocolo
        if (network.equals("tcp")) {
            JSONObject tcpSettings = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("type", type);
            tcpSettings.put("header", header);
            if (!host.isEmpty() || !path.isEmpty()) {
                JSONObject request = new JSONObject();
                if (!host.isEmpty()) {
                    request.put("headers", new JSONObject().put("Host", host));
                }
                request.put("path", path);
                tcpSettings.put("request", request);
            }
            streamSettings.put("tcpSettings", tcpSettings);
        } else if (network.equals("kcp")) {
            JSONObject kcpSettings = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("type", type);
            kcpSettings.put("header", header);
            if (!path.isEmpty()) {
                kcpSettings.put("seed", path);
            }
            streamSettings.put("kcpSettings", kcpSettings);
        } else if (network.equals("ws")) {
            JSONObject wsSettings = new JSONObject();
            if (!host.isEmpty()) {
                wsSettings.put("headers", new JSONObject().put("Host", host));
            }
            wsSettings.put("path", path);
            streamSettings.put("wsSettings", wsSettings);
        } else if (network.equals("httpupgrade")) {
            JSONObject httpupgradeSettings = new JSONObject();
            if (!host.isEmpty()) {
                httpupgradeSettings.put("headers", new JSONObject().put("Host", host));
            }
            httpupgradeSettings.put("path", path);
            streamSettings.put("httpupgradeSettings", httpupgradeSettings);
        } else if (network.equals("xhttp")) {
            JSONObject xhttpSettings = new JSONObject();
            if (!host.isEmpty()) {
                xhttpSettings.put("headers", new JSONObject().put("Host", host));
            }
            xhttpSettings.put("path", path);
            streamSettings.put("xhttpSettings", xhttpSettings);
        } else if (network.equals("h2")) {
            JSONObject httpSettings = new JSONObject();
            if (!host.isEmpty()) {
                httpSettings.put("host", new JSONArray().put(host));
            }
            httpSettings.put("path", path);
            streamSettings.put("httpSettings", httpSettings);
        } else if (network.equals("grpc")) {
            JSONObject grpcSettings = new JSONObject();
            grpcSettings.put("serviceName", serviceName);
            grpcSettings.put("multiMode", mode.equals("multi"));
            streamSettings.put("grpcSettings", grpcSettings);
        }

        // Configurar TLS
        if (tls.equals("tls")) {
            streamSettings.put("security", "tls");
            JSONObject tlsSettings = new JSONObject();
            if (!sni.isEmpty()) {
                tlsSettings.put("serverName", sni);
            }
            if (!alpn.isEmpty()) {
                tlsSettings.put("alpn", new JSONArray(alpn.split(",")));
            }
            if (!fp.isEmpty()) {
                tlsSettings.put("fingerprint", fp);
            }
            tlsSettings.put("allowInsecure", allowInsecure.isEmpty() ? false : Boolean.parseBoolean(allowInsecure));
            tlsSettings.put("show", false);
            streamSettings.put("tlsSettings", tlsSettings);
        }

        outbound.put("streamSettings", streamSettings);
        outbound.put("tag", "proxy");

        // Outbound para freedom
        JSONObject freedomOutbound = new JSONObject();
        freedomOutbound.put("protocol", "freedom");
        JSONObject freedomSettings = new JSONObject();
        freedomSettings.put("domainStrategy", "UseIP");
        freedomOutbound.put("settings", freedomSettings);
        freedomOutbound.put("tag", "direct");

        // Outbound para blackhole
        JSONObject blackholeOutbound = new JSONObject();
        blackholeOutbound.put("protocol", "blackhole");
        JSONObject blackholeSettings = new JSONObject();
        JSONObject response = new JSONObject();
        response.put("type", "http");
        blackholeSettings.put("response", response);
        blackholeOutbound.put("settings", blackholeSettings);
        blackholeOutbound.put("tag", "block");

        // Agregar todos los outbounds
        v2rayConfig.put("outbounds", new JSONArray()
                .put(outbound)
                .put(freedomOutbound)
                .put(blackholeOutbound));

        // Sección routing
        JSONObject routing = new JSONObject();
        routing.put("domainStrategy", "AsIs");
        JSONArray rules = new JSONArray();
        rules.put(new JSONObject()
                .put("type", "field")
                .put("ip", new JSONArray().put("1.1.1.1"))
                .put("port", "53")
                .put("outboundTag", "proxy"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("ip", new JSONArray().put("223.5.5.5"))
                .put("port", "53")
                .put("outboundTag", "direct"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("domain", new JSONArray()
                        .put("domain:googleapis.cn")
                        .put("domain:gstatic.com"))
                .put("outboundTag", "proxy"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("network", "udp")
                .put("port", "443")
                .put("outboundTag", "block"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("domain", new JSONArray().put("geosite:category-ads-all"))
                .put("outboundTag", "block"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("ip", new JSONArray().put("geoip:private"))
                .put("outboundTag", "direct"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("domain", new JSONArray().put("geosite:private"))
                .put("outboundTag", "direct"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("domain", new JSONArray()
                        .put("domain:dns.alidns.com")
                        .put("domain:dns.pub")
                        .put("domain:doh.pub")
                        .put("domain:dot.pub")
                        .put("domain:doh.360.cn")
                        .put("domain:dot.360.cn")
                        .put("geosite:cn")
                        .put("geosite:geolocation-cn"))
                .put("outboundTag", "direct"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("ip", new JSONArray()
                        .put("223.5.5.5/32")
                        .put("223.6.6.6/32")
                        .put("2400:3200::1/128")
                        .put("2400:3200:baba::1/128")
                        .put("119.29.29.29/32")
                        .put("1.12.12.12/32")
                        .put("120.53.53.53/32")
                        .put("2402:4e00::/128")
                        .put("2402:4e00:1::/128")
                        .put("180.76.76.76/32")
                        .put("2400:da00::6666/128")
                        .put("114.114.114.114/32")
                        .put("114.114.115.115/32")
                        .put("180.184.1.1/32")
                        .put("180.184.2.2/32")
                        .put("101.226.4.6/32")
                        .put("218.30.118.6/32")
                        .put("123.125.81.6/32")
                        .put("140.207.198.6/32")
                        .put("geoip:cn"))
                .put("outboundTag", "direct"));
        rules.put(new JSONObject()
                .put("type", "field")
                .put("port", "0-65535")
                .put("outboundTag", "proxy"));
        routing.put("rules", rules);
        v2rayConfig.put("routing", routing);

        // Campo remarks
        v2rayConfig.put("remarks", remark.isEmpty() ? "Configuración VMess" : remark);

        // Devolver JSON formateado
        return v2rayConfig.toString(2); // Formato con indentación
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
