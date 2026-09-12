package com.jepongdevxyz.vpn.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.jepongdevxyz.vpn.MainActivity;
import com.jepongdevxyz.vpn.R;
import com.jepongdevxyz.vpn.config.Settings;
import com.jepongdevxyz.vpn.databinding.ActivityEditV2rayConfigBinding;
import es.dmoral.toasty.Toasty;
import org.json.JSONArray;
import org.json.JSONObject;

public class EditV2rayConfigActivity extends AppCompatActivity {

    private ActivityEditV2rayConfigBinding binding;
    private Settings mConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditV2rayConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar Settings
        mConfig = new Settings(this);

        // Configurar toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurar Spinners
        setupSecuritySpinner();
        setupNetworkSpinner();
        setupHeaderTypeSpinner();
        setupXhttpModeSpinner();
        setupGrpcModeSpinner();
        setupTlsSpinner();
        setupFingerprintSpinner();
        setupAlpnSpinner();
        setupAllowInsecureSpinner();

        // Cargar datos del JSON recibido
        String jsonString = getIntent().getStringExtra("json_config");
        populateFields(jsonString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_v2ray_config_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            saveConfig();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSecuritySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.security_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSecurity.setAdapter(adapter);
    }

    private void setupNetworkSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.network_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerNetwork.setAdapter(adapter);

        // Listener para actualizar visibilidad de campos
        binding.spinnerNetwork.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFieldsVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void setupHeaderTypeSpinner() {
        // Inicialmente, establecer un adaptador predeterminado
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.header_type_tcp_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerHeaderType.setAdapter(adapter);
    }

    private void setupXhttpModeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.xhttp_mode_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerXhttpMode.setAdapter(adapter);
    }

    private void setupGrpcModeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.grpc_mode_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGrpcMode.setAdapter(adapter);
    }

    private void setupTlsSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tls_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTls.setAdapter(adapter);

        // Listener para actualizar visibilidad de campos
        binding.spinnerTls.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFieldsVisibility();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void setupFingerprintSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.fingerprint_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFp.setAdapter(adapter);
    }

    private void setupAlpnSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.alpn_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAlpn.setAdapter(adapter);
    }

    private void setupAllowInsecureSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.allow_insecure_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAllowInsecure.setAdapter(adapter);
    }

    private void updateFieldsVisibility() {
        // Actualizar visibilidad de campos de network
        String network = binding.spinnerNetwork.getSelectedItem().toString();
        binding.tvHeaderType.setVisibility(View.GONE);
        binding.tilHeaderType.setVisibility(View.GONE);
        binding.spinnerHeaderType.setEnabled(false);
        binding.tvXhttpMode.setVisibility(View.GONE);
        binding.tilXhttpMode.setVisibility(View.GONE);
        binding.spinnerXhttpMode.setEnabled(false);
        binding.tvGrpcMode.setVisibility(View.GONE);
        binding.tilGrpcMode.setVisibility(View.GONE);
        binding.spinnerGrpcMode.setEnabled(false);
        binding.tilHost.setVisibility(View.GONE);
        binding.tilPath.setVisibility(View.GONE);
        binding.tilKcpSeed.setVisibility(View.GONE);
        binding.tilXhttpExtra.setVisibility(View.GONE);
        binding.tilGrpcAuthority.setVisibility(View.GONE);
        binding.tilGrpcServiceName.setVisibility(View.GONE);

        // Configurar adaptador de Header Type según el protocolo
        ArrayAdapter<CharSequence> headerTypeAdapter;
        String currentHeaderType = binding.spinnerHeaderType.getSelectedItem() != null ?
                binding.spinnerHeaderType.getSelectedItem().toString() : "none";

        if (network.equals("tcp")) {
            headerTypeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.header_type_tcp_options,
                    android.R.layout.simple_spinner_item
            );
            binding.tvHeaderType.setVisibility(View.VISIBLE);
            binding.tilHeaderType.setVisibility(View.VISIBLE);
            binding.spinnerHeaderType.setEnabled(true);
            binding.tilHost.setVisibility(View.VISIBLE);
            binding.tilHost.setHint(getString(R.string.http_host));
            binding.tilPath.setVisibility(View.VISIBLE);
            binding.tilPath.setHint(getString(R.string.http_path));
        } else if (network.equals("kcp")) {
            headerTypeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.header_type_kcp_options,
                    android.R.layout.simple_spinner_item
            );
            binding.tvHeaderType.setVisibility(View.VISIBLE);
            binding.tilHeaderType.setVisibility(View.VISIBLE);
            binding.spinnerHeaderType.setEnabled(true);
            binding.tilHost.setVisibility(View.VISIBLE);
            binding.tilHost.setHint(getString(R.string.kcp_host));
            binding.tilKcpSeed.setVisibility(View.VISIBLE);
            binding.tilKcpSeed.setHint(getString(R.string.kcp_seed));
        } else {
            headerTypeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.header_type_tcp_options,
                    android.R.layout.simple_spinner_item
            );
            switch (network) {
                case "ws":
                    binding.tilHost.setVisibility(View.VISIBLE);
                    binding.tilHost.setHint(getString(R.string.ws_host));
                    binding.tilPath.setVisibility(View.VISIBLE);
                    binding.tilPath.setHint(getString(R.string.ws_path));
                    break;
                case "httpupgrade":
                    binding.tilHost.setVisibility(View.VISIBLE);
                    binding.tilHost.setHint(getString(R.string.httpupgrade_host));
                    binding.tilPath.setVisibility(View.VISIBLE);
                    binding.tilPath.setHint(getString(R.string.httpupgrade_path));
                    break;
                case "xhttp":
                    binding.tvXhttpMode.setVisibility(View.VISIBLE);
                    binding.tilXhttpMode.setVisibility(View.VISIBLE);
                    binding.spinnerXhttpMode.setEnabled(true);
                    binding.tilHost.setVisibility(View.VISIBLE);
                    binding.tilHost.setHint(getString(R.string.xhttp_host));
                    binding.tilPath.setVisibility(View.VISIBLE);
                    binding.tilPath.setHint(getString(R.string.xhttp_path));
                    binding.tilXhttpExtra.setVisibility(View.VISIBLE);
                    binding.tilXhttpExtra.setHint(getString(R.string.xhttp_extra));
                    break;
                case "h2":
                    binding.tilHost.setVisibility(View.VISIBLE);
                    binding.tilHost.setHint(getString(R.string.h2_host));
                    binding.tilPath.setVisibility(View.VISIBLE);
                    binding.tilPath.setHint(getString(R.string.h2_path));
                    break;
                case "grpc":
                    binding.tvGrpcMode.setVisibility(View.VISIBLE);
                    binding.tilGrpcMode.setVisibility(View.VISIBLE);
                    binding.spinnerGrpcMode.setEnabled(true);
                    binding.tilGrpcAuthority.setVisibility(View.VISIBLE);
                    binding.tilGrpcAuthority.setHint(getString(R.string.grpc_authority));
                    binding.tilGrpcServiceName.setVisibility(View.VISIBLE);
                    binding.tilGrpcServiceName.setHint(getString(R.string.grpc_service_name));
                    break;
            }
        }

        // Actualizar el adaptador del Spinner de Header Type
        headerTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerHeaderType.setAdapter(headerTypeAdapter);

        // Restablecer la selección del Header Type si es válida
        int selection = 0;
        for (int i = 0; i < headerTypeAdapter.getCount(); i++) {
            if (headerTypeAdapter.getItem(i).toString().equals(currentHeaderType)) {
                selection = i;
                break;
            }
        }
        binding.spinnerHeaderType.setSelection(selection);

        // Actualizar visibilidad de campos de TLS
        String tls = binding.spinnerTls.getSelectedItem().toString();
        binding.tilSni.setVisibility(View.GONE);
        binding.tvFp.setVisibility(View.GONE);
        binding.tilFp.setVisibility(View.GONE);
        binding.spinnerFp.setEnabled(false);
        binding.tvAlpn.setVisibility(View.GONE);
        binding.tilAlpn.setVisibility(View.GONE);
        binding.spinnerAlpn.setEnabled(false);
        binding.tvAllowInsecure.setVisibility(View.GONE);
        binding.tilAllowInsecure.setVisibility(View.GONE);
        binding.spinnerAllowInsecure.setEnabled(false);
        binding.tilPublicKey.setVisibility(View.GONE);
        binding.tilShortId.setVisibility(View.GONE);
        binding.tilSpiderX.setVisibility(View.GONE);

        switch (tls) {
            case "tls":
                binding.tilSni.setVisibility(View.VISIBLE);
                binding.tvFp.setVisibility(View.VISIBLE);
                binding.tilFp.setVisibility(View.VISIBLE);
                binding.spinnerFp.setEnabled(true);
                binding.tvAlpn.setVisibility(View.VISIBLE);
                binding.tilAlpn.setVisibility(View.VISIBLE);
                binding.spinnerAlpn.setEnabled(true);
                binding.tvAllowInsecure.setVisibility(View.VISIBLE);
                binding.tilAllowInsecure.setVisibility(View.VISIBLE);
                binding.spinnerAllowInsecure.setEnabled(true);
                break;
            case "reality":
                binding.tilSni.setVisibility(View.VISIBLE);
                binding.tvFp.setVisibility(View.VISIBLE);
                binding.tilFp.setVisibility(View.VISIBLE);
                binding.spinnerFp.setEnabled(true);
                binding.tilPublicKey.setVisibility(View.VISIBLE);
                binding.tilShortId.setVisibility(View.VISIBLE);
                binding.tilSpiderX.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void populateFields(String jsonString) {
        try {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                Log.w("EditV2rayConfig", "JSON vacío, usando valores predeterminados");
                updateFieldsVisibility();
                return;
            }

            JSONObject json = new JSONObject(jsonString);
            JSONObject outbound = json.optJSONArray("outbounds") != null && json.getJSONArray("outbounds").length() > 0 ?
                    json.getJSONArray("outbounds").getJSONObject(0) : new JSONObject();
            JSONObject settings = outbound.optJSONObject("settings") != null ?
                    outbound.getJSONObject("settings") : new JSONObject();
            JSONObject vnext = settings.optJSONArray("vnext") != null && settings.getJSONArray("vnext").length() > 0 ?
                    settings.getJSONArray("vnext").getJSONObject(0) : new JSONObject();
            JSONObject user = vnext.optJSONArray("users") != null && vnext.getJSONArray("users").length() > 0 ?
                    vnext.getJSONArray("users").getJSONObject(0) : new JSONObject();
            JSONObject streamSettings = outbound.optJSONObject("streamSettings") != null ?
                    outbound.getJSONObject("streamSettings") : new JSONObject();

            binding.editRemarks.setText(json.optString("remarks", ""));
            binding.editAddress.setText(vnext.optString("address", ""));
            binding.editPort.setText(vnext.optInt("port", 0) == 0 ? "" : String.valueOf(vnext.optInt("port", 0)));
            binding.editId.setText(user.optString("id", ""));
            int alterId = user.optInt("alterId", 0);
            binding.editAlterId.setText(String.valueOf(alterId));

            // Configurar Spinner para security
            String security = user.optString("security", "auto");
            ArrayAdapter<CharSequence> securityAdapter = (ArrayAdapter<CharSequence>) binding.spinnerSecurity.getAdapter();
            for (int i = 0; i < securityAdapter.getCount(); i++) {
                if (securityAdapter.getItem(i).toString().equals(security)) {
                    binding.spinnerSecurity.setSelection(i);
                    break;
                }
            }

            // Configurar Spinner para network
            String network = streamSettings.optString("network", "tcp");
            ArrayAdapter<CharSequence> networkAdapter = (ArrayAdapter<CharSequence>) binding.spinnerNetwork.getAdapter();
            for (int i = 0; i < networkAdapter.getCount(); i++) {
                if (networkAdapter.getItem(i).toString().equals(network)) {
                    binding.spinnerNetwork.setSelection(i);
                    break;
                }
            }

            // Configurar Spinner para headerType
            String headerType = "none";
            if (streamSettings.has("tcpSettings") && streamSettings.getJSONObject("tcpSettings").has("header")) {
                headerType = streamSettings.getJSONObject("tcpSettings").getJSONObject("header").optString("type", "none");
            } else if (streamSettings.has("kcpSettings") && streamSettings.getJSONObject("kcpSettings").has("header")) {
                headerType = streamSettings.getJSONObject("kcpSettings").getJSONObject("header").optString("type", "none");
            }

            // Actualizar el adaptador de Header Type antes de establecer la selección
            updateFieldsVisibility();
            ArrayAdapter<CharSequence> headerTypeAdapter = (ArrayAdapter<CharSequence>) binding.spinnerHeaderType.getAdapter();
            int headerTypeSelection = 0;
            for (int i = 0; i < headerTypeAdapter.getCount(); i++) {
                if (headerTypeAdapter.getItem(i).toString().equals(headerType)) {
                    headerTypeSelection = i;
                    break;
                }
            }
            binding.spinnerHeaderType.setSelection(headerTypeSelection);

            // Cargar campos según el protocolo
            if (network.equals("tcp") && streamSettings.has("tcpSettings") && streamSettings.getJSONObject("tcpSettings").has("request")) {
                JSONObject request = streamSettings.getJSONObject("tcpSettings").getJSONObject("request");
                binding.editHost.setText(request.optJSONObject("headers") != null ?
                        request.getJSONObject("headers").optString("Host", "") : "");
                binding.editPath.setText(request.optString("path", ""));
            } else if (network.equals("kcp") && streamSettings.has("kcpSettings")) {
                binding.editHost.setText("");
                binding.editKcpSeed.setText(streamSettings.getJSONObject("kcpSettings").optString("seed", ""));
            } else if (network.equals("ws") && streamSettings.has("wsSettings")) {
                binding.editHost.setText(streamSettings.getJSONObject("wsSettings").optJSONObject("headers") != null ?
                        streamSettings.getJSONObject("wsSettings").getJSONObject("headers").optString("Host", "") : "");
                binding.editPath.setText(streamSettings.getJSONObject("wsSettings").optString("path", ""));
            } else if (network.equals("httpupgrade") && streamSettings.has("httpupgradeSettings")) {
                binding.editHost.setText(streamSettings.getJSONObject("httpupgradeSettings").optJSONObject("headers") != null ?
                        streamSettings.getJSONObject("httpupgradeSettings").getJSONObject("headers").optString("Host", "") : "");
                binding.editPath.setText(streamSettings.getJSONObject("httpupgradeSettings").optString("path", ""));
            } else if (network.equals("xhttp") && streamSettings.has("xhttpSettings")) {
                JSONObject xhttpSettings = streamSettings.getJSONObject("xhttpSettings");
                binding.editHost.setText(xhttpSettings.optJSONObject("headers") != null ?
                        xhttpSettings.getJSONObject("headers").optString("Host", "") : "");
                binding.editPath.setText(xhttpSettings.optString("path", ""));
                JSONObject xhttpExtra = new JSONObject(xhttpSettings.toString());
                xhttpExtra.remove("mode");
                xhttpExtra.remove("headers");
                xhttpExtra.remove("path");
                binding.editXhttpExtra.setText(xhttpExtra.toString(2));
            } else if (network.equals("h2") && streamSettings.has("httpSettings")) {
                binding.editHost.setText(streamSettings.getJSONObject("httpSettings").optJSONArray("host") != null ?
                        streamSettings.getJSONObject("httpSettings").getJSONArray("host").optString(0, "") : "");
                binding.editPath.setText(streamSettings.getJSONObject("httpSettings").optString("path", ""));
            } else if (network.equals("grpc") && streamSettings.has("grpcSettings")) {
                binding.editGrpcAuthority.setText(streamSettings.getJSONObject("grpcSettings").optString("authority", ""));
                binding.editGrpcServiceName.setText(streamSettings.getJSONObject("grpcSettings").optString("serviceName", ""));
            } else {
                binding.editHost.setText("");
                binding.editPath.setText("");
                binding.editKcpSeed.setText("");
                binding.editXhttpExtra.setText("");
                binding.editGrpcAuthority.setText("");
                binding.editGrpcServiceName.setText("");
            }

            // Configurar Spinner para xhttpMode
            String xhttpMode = "auto";
            if (streamSettings.has("xhttpSettings") && streamSettings.getJSONObject("xhttpSettings").has("mode")) {
                xhttpMode = streamSettings.getJSONObject("xhttpSettings").optString("mode", "auto");
            }
            ArrayAdapter<CharSequence> xhttpModeAdapter = (ArrayAdapter<CharSequence>) binding.spinnerXhttpMode.getAdapter();
            for (int i = 0; i < xhttpModeAdapter.getCount(); i++) {
                if (xhttpModeAdapter.getItem(i).toString().equals(xhttpMode)) {
                    binding.spinnerXhttpMode.setSelection(i);
                    break;
                }
            }

            // Configurar Spinner para grpcMode
            String grpcMode = "gun";
            boolean multiMode = streamSettings.has("grpcSettings") && streamSettings.getJSONObject("grpcSettings").optBoolean("multiMode", false);
            if (multiMode) {
                grpcMode = "multi";
            }
            ArrayAdapter<CharSequence> grpcModeAdapter = (ArrayAdapter<CharSequence>) binding.spinnerGrpcMode.getAdapter();
            for (int i = 0; i < grpcModeAdapter.getCount(); i++) {
                if (grpcModeAdapter.getItem(i).toString().equals(grpcMode)) {
                    binding.spinnerGrpcMode.setSelection(i);
                    break;
                }
            }

            // Configurar Spinner para TLS
            String tls = streamSettings.optString("security", "");
            ArrayAdapter<CharSequence> tlsAdapter = (ArrayAdapter<CharSequence>) binding.spinnerTls.getAdapter();
            for (int i = 0; i < tlsAdapter.getCount(); i++) {
                if (tlsAdapter.getItem(i).toString().equals(tls)) {
                    binding.spinnerTls.setSelection(i);
                    break;
                }
            }

            // Cargar campos de TLS o Reality
            if (tls.equals("tls") && streamSettings.has("tlsSettings")) {
                JSONObject tlsSettings = streamSettings.getJSONObject("tlsSettings");
                binding.editSni.setText(tlsSettings.optString("serverName", ""));
                
                // Configurar Spinner para Fingerprint
                String fingerprint = tlsSettings.optString("fingerprint", "");
                ArrayAdapter<CharSequence> fpAdapter = (ArrayAdapter<CharSequence>) binding.spinnerFp.getAdapter();
                for (int i = 0; i < fpAdapter.getCount(); i++) {
                    if (fpAdapter.getItem(i).toString().equals(fingerprint)) {
                        binding.spinnerFp.setSelection(i);
                        break;
                    }
                }

                // Configurar Spinner para ALPN
                String alpn = tlsSettings.has("alpn") && tlsSettings.getJSONArray("alpn").length() > 0 ?
                        tlsSettings.getJSONArray("alpn").join(",").replace("\"", "") : "";
                ArrayAdapter<CharSequence> alpnAdapter = (ArrayAdapter<CharSequence>) binding.spinnerAlpn.getAdapter();
                for (int i = 0; i < alpnAdapter.getCount(); i++) {
                    if (alpnAdapter.getItem(i).toString().equals(alpn)) {
                        binding.spinnerAlpn.setSelection(i);
                        break;
                    }
                }

                // Configurar Spinner para Allow Insecure
                String allowInsecure = tlsSettings.optBoolean("allowInsecure", false) ? "true" : "false";
                if (!tlsSettings.has("allowInsecure")) {
                    allowInsecure = "";
                }
                ArrayAdapter<CharSequence> allowInsecureAdapter = (ArrayAdapter<CharSequence>) binding.spinnerAllowInsecure.getAdapter();
                for (int i = 0; i < allowInsecureAdapter.getCount(); i++) {
                    if (allowInsecureAdapter.getItem(i).toString().equals(allowInsecure)) {
                        binding.spinnerAllowInsecure.setSelection(i);
                        break;
                    }
                }
            } else if (tls.equals("reality") && streamSettings.has("realitySettings")) {
                JSONObject realitySettings = streamSettings.getJSONObject("realitySettings");
                binding.editSni.setText(realitySettings.optString("serverName", ""));
                
                // Configurar Spinner para Fingerprint
                String fingerprint = realitySettings.optString("fingerprint", "");
                ArrayAdapter<CharSequence> fpAdapter = (ArrayAdapter<CharSequence>) binding.spinnerFp.getAdapter();
                for (int i = 0; i < fpAdapter.getCount(); i++) {
                    if (fpAdapter.getItem(i).toString().equals(fingerprint)) {
                        binding.spinnerFp.setSelection(i);
                        break;
                    }
                }

                binding.editPublicKey.setText(realitySettings.optString("publicKey", ""));
                binding.editShortId.setText(realitySettings.optString("shortId", ""));
                binding.editSpiderX.setText(realitySettings.optString("spiderX", ""));
            } else {
                binding.editSni.setText("");
                binding.spinnerFp.setSelection(0);
                binding.spinnerAlpn.setSelection(0);
                binding.spinnerAllowInsecure.setSelection(0);
                binding.editPublicKey.setText("");
                binding.editShortId.setText("");
                binding.editSpiderX.setText("");
            }

            Log.d("EditV2rayConfig", "Campos poblados: remarks=" + json.optString("remarks", ""));
        } catch (Exception e) {
            Log.e("EditV2rayConfig", "Error al cargar la configuración: " + e.getMessage());
            Toasty.error(this, "Error al cargar la configuración: " + e.getMessage(), Toasty.LENGTH_LONG).show();
            updateFieldsVisibility();
        }
    }

    private void saveConfig() {
        try {
            // Validar campos obligatorios
            String remarks = binding.editRemarks.getText().toString().trim();
            String address = binding.editAddress.getText().toString().trim();
            String portStr = binding.editPort.getText().toString().trim();
            String id = binding.editId.getText().toString().trim();
            if (address.isEmpty() || portStr.isEmpty() || id.isEmpty()) {
                Toasty.error(this, R.string.error_missing_fields, Toasty.LENGTH_SHORT).show();
                return;
            }

            int port;
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                Toasty.error(this, R.string.error_invalid_port, Toasty.LENGTH_SHORT).show();
                return;
            }

            // Validar Alter ID
            String alterIdStr = binding.editAlterId.getText().toString().trim();
            int alterId = 0;
            if (!alterIdStr.isEmpty()) {
                try {
                    alterId = Integer.parseInt(alterIdStr);
                } catch (NumberFormatException e) {
                    Toasty.error(this, R.string.error_invalid_alter_id, Toasty.LENGTH_SHORT).show();
                    return;
                }
            }

            // Validar XHTTP Extra raw JSON
            String xhttpExtraRaw = binding.editXhttpExtra.getText().toString().trim();
            JSONObject xhttpExtra = null;
            if (!xhttpExtraRaw.isEmpty()) {
                try {
                    xhttpExtra = new JSONObject(xhttpExtraRaw);
                } catch (Exception e) {
                    Toasty.error(this, R.string.error_invalid_xhttp_json, Toasty.LENGTH_SHORT).show();
                    return;
                }
            }

            // Validar Reality (publicKey es obligatorio)
            String tls = binding.spinnerTls.getSelectedItem().toString();
            if (tls.equals("reality")) {
                String publicKey = binding.editPublicKey.getText().toString().trim();
                if (publicKey.isEmpty()) {
                    Toasty.error(this, R.string.error_missing_public_key, Toasty.LENGTH_SHORT).show();
                    return;
                }
            }

            // Crear configuración JSON completa
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
            user.put("security", binding.spinnerSecurity.getSelectedItem().toString());
            user.put("level", 8);
            JSONObject server = new JSONObject();
            server.put("address", address);
            server.put("port", port);
            server.put("users", new JSONArray().put(user));
            protocolSettings.put("vnext", new JSONArray().put(server));
            outbound.put("settings", protocolSettings);
            JSONObject streamSettings = new JSONObject();
            String network = binding.spinnerNetwork.getSelectedItem().toString();
            streamSettings.put("network", network);

            // Configurar según el protocolo
            if (network.equals("tcp")) {
                JSONObject tcpSettings = new JSONObject();
                JSONObject header = new JSONObject();
                header.put("type", binding.spinnerHeaderType.getSelectedItem().toString());
                tcpSettings.put("header", header);
                JSONObject request = new JSONObject();
                String host = binding.editHost.getText().toString().trim();
                String path = binding.editPath.getText().toString().trim();
                if (!host.isEmpty() || !path.isEmpty()) {
                    JSONObject headers = new JSONObject();
                    if (!host.isEmpty()) {
                        headers.put("Host", host);
                    }
                    request.put("headers", headers);
                    request.put("path", path);
                    tcpSettings.put("request", request);
                }
                streamSettings.put("tcpSettings", tcpSettings);
            } else if (network.equals("kcp")) {
                JSONObject kcpSettings = new JSONObject();
                JSONObject header = new JSONObject();
                header.put("type", binding.spinnerHeaderType.getSelectedItem().toString());
                kcpSettings.put("header", header);
                String seed = binding.editKcpSeed.getText().toString().trim();
                if (!seed.isEmpty()) {
                    kcpSettings.put("seed", seed);
                }
                streamSettings.put("kcpSettings", kcpSettings);
            } else if (network.equals("ws")) {
                JSONObject wsSettings = new JSONObject();
                String host = binding.editHost.getText().toString().trim();
                String path = binding.editPath.getText().toString().trim();
                if (!host.isEmpty()) {
                    wsSettings.put("headers", new JSONObject().put("Host", host));
                }
                wsSettings.put("path", path);
                streamSettings.put("wsSettings", wsSettings);
            } else if (network.equals("httpupgrade")) {
                JSONObject httpupgradeSettings = new JSONObject();
                String host = binding.editHost.getText().toString().trim();
                String path = binding.editPath.getText().toString().trim();
                if (!host.isEmpty()) {
                    httpupgradeSettings.put("headers", new JSONObject().put("Host", host));
                }
                httpupgradeSettings.put("path", path);
                streamSettings.put("httpupgradeSettings", httpupgradeSettings);
            } else if (network.equals("xhttp")) {
                JSONObject xhttpSettings = xhttpExtra != null ? new JSONObject(xhttpExtra.toString()) : new JSONObject();
                String mode = binding.spinnerXhttpMode.getSelectedItem().toString();
                String host = binding.editHost.getText().toString().trim();
                String path = binding.editPath.getText().toString().trim();
                xhttpSettings.put("mode", mode);
                if (!host.isEmpty()) {
                    xhttpSettings.put("headers", new JSONObject().put("Host", host));
                }
                xhttpSettings.put("path", path);
                streamSettings.put("xhttpSettings", xhttpSettings);
            } else if (network.equals("h2")) {
                JSONObject httpSettings = new JSONObject();
                String host = binding.editHost.getText().toString().trim();
                String path = binding.editPath.getText().toString().trim();
                if (!host.isEmpty()) {
                    httpSettings.put("host", new JSONArray().put(host));
                }
                httpSettings.put("path", path);
                streamSettings.put("httpSettings", httpSettings);
            } else if (network.equals("grpc")) {
                JSONObject grpcSettings = new JSONObject();
                String mode = binding.spinnerGrpcMode.getSelectedItem().toString();
                String authority = binding.editGrpcAuthority.getText().toString().trim();
                String serviceName = binding.editGrpcServiceName.getText().toString().trim();
                grpcSettings.put("multiMode", mode.equals("multi"));
                if (!authority.isEmpty()) {
                    grpcSettings.put("authority", authority);
                }
                if (!serviceName.isEmpty()) {
                    grpcSettings.put("serviceName", serviceName);
                }
                streamSettings.put("grpcSettings", grpcSettings);
            }

            // Configurar TLS o Reality
            if (!tls.isEmpty()) {
                streamSettings.put("security", tls);
                if (tls.equals("tls")) {
                    JSONObject tlsSettings = new JSONObject();
                    String sni = binding.editSni.getText().toString().trim();
                    String fingerprint = binding.spinnerFp.getSelectedItem().toString();
                    String alpn = binding.spinnerAlpn.getSelectedItem().toString();
                    String allowInsecure = binding.spinnerAllowInsecure.getSelectedItem().toString();

                    if (!sni.isEmpty()) {
                        tlsSettings.put("serverName", sni);
                    }
                    if (!fingerprint.isEmpty()) {
                        tlsSettings.put("fingerprint", fingerprint);
                    }
                    if (!alpn.isEmpty()) {
                        tlsSettings.put("alpn", new JSONArray(alpn.split(",")));
                    }
                    if (!allowInsecure.isEmpty()) {
                        tlsSettings.put("allowInsecure", Boolean.parseBoolean(allowInsecure));
                    } else {
                        tlsSettings.put("allowInsecure", false);
                    }
                    tlsSettings.put("show", false);
                    streamSettings.put("tlsSettings", tlsSettings);
                } else if (tls.equals("reality")) {
                    JSONObject realitySettings = new JSONObject();
                    String sni = binding.editSni.getText().toString().trim();
                    String fingerprint = binding.spinnerFp.getSelectedItem().toString();
                    String publicKey = binding.editPublicKey.getText().toString().trim();
                    String shortId = binding.editShortId.getText().toString().trim();
                    String spiderX = binding.editSpiderX.getText().toString().trim();

                    if (!sni.isEmpty()) {
                        realitySettings.put("serverName", sni);
                    }
                    if (!fingerprint.isEmpty()) {
                        realitySettings.put("fingerprint", fingerprint);
                    }
                    if (!publicKey.isEmpty()) {
                        realitySettings.put("publicKey", publicKey);
                    }
                    if (!shortId.isEmpty()) {
                        realitySettings.put("shortId", shortId);
                    }
                    if (!spiderX.isEmpty()) {
                        realitySettings.put("spiderX", spiderX);
                    }
                    streamSettings.put("realitySettings", realitySettings);
                }
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
            v2rayConfig.put("remarks", remarks.isEmpty() ? "Configuración Personalizada" : remarks);

            // Guardar en SharedPreferences
            String jsonConfig = v2rayConfig.toString(2);
            SharedPreferences.Editor editor = mConfig.getPrefsPrivate().edit();
            editor.putString(Settings.V2RAY_JSON, jsonConfig);
            editor.apply();

            // Devolver JSON a la actividad anterior (v2ray.java)
            Intent result = new Intent();
            result.putExtra("json_config", jsonConfig);
            setResult(RESULT_OK, result);

            // Mostrar mensaje de éxito
            Toasty.success(this, R.string.save_success, Toasty.LENGTH_SHORT).show();

            // Navegar a MainActivity
            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainIntent);
            finish();

        } catch (Exception e) {
            Log.e("EditV2rayConfig", "Error al guardar la configuración: " + e.getMessage());
            Toasty.error(this, "Error al guardar la configuración: " + e.getMessage(), Toasty.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
