package com.jepongdevxyz.vpn.config;

public interface SettingsConstants
{
	
	// Geral
	public static final String
		AUTO_CLEAR_LOGS_KEY = "autoClearLogs",
		HIDE_LOG_KEY = "hideLog",
		MODO_DEBUG_KEY = "modeDebug",
		MODO_NOTURNO_KEY = "modeNight",
		BLOQUEAR_ROOT_KEY = "blockRoot",
		IDIOMA_KEY = "idioma",
		TETHERING_SUBNET = "tetherSubnet",
		DISABLE_DELAY_KEY = "disableDelaySSH",
		MAXIMO_THREADS_KEY = "numberMaxThreadSocks",
		NETWORK_SPEED = "speed_meter",
		FILTER_APPS = "filterApps",
		FILTER_BYPASS_MODE = "filterBypassMode",
		FILTER_APPS_LIST = "filterAppsList",
		
    WAKELOCK_KEY = "wakelock",
		PROXY_IP_KEY = "proxyRemoto",
		PROXY_PORTA_KEY = "proxyRemotoPorta",
		CUSTOM_PAYLOAD_KEY = "proxyPayload",
		PROXY_USAR_DEFAULT_PAYLOAD = "usarDefaultPayload",
		PROXY_USAR_AUTENTICACAO_KEY = "usarProxyAutenticacao"
	;
    
    public static final String
		CONFIG_BLOCK_HWID = "blockhwid",
		CONFIG_LOGIN_HWID = "loginHwid",
		CONFIG_HWDI_STRING = "hwidstring",
		CONFIG_BLOCK_OPERATOR = "blockoperator",
		CONFIG_OPERATOR_NAME = "operatorname",
		CONFIG_BLOCK_ONLY_DATA = "blockonlydata",
		CONFIG_ONLY_PLAY_STORE = "onlyPlayStore",
		CONFIG_PROTEGER_PAYLOAD = "protegerPayload",
		CONFIG_PROTEGER_SNI = "protegerSni",
		CONFIG_PROTEGER_SERVER = "protegerServer",
		CONFIG_PROTEGER_PORT = "protegerPort",
		CONFIG_PROTEGER_USUARIO = "protegerUser",
		CONFIG_PROTEGER_CONTRASENA = "protegerPass",
		CONFIG_PROTEGER_PROXY = "protegerProxy",
        PASSWORD = "passwordpanda",
    PASSWORDTRUE = "passordactivo",
    
        PROXY_LINE_INPUT = "proxy_input",
		CPB = "cpb",
		CP = "cp",
        CONFIG_AUTOR_KEY = "autorkey",
        AUTOREPLACE_KEY = "autoreplace"
	;
    public static final String
			UDP_SERVER = "udpserver",
			UDP_AUTH = "udpauth",
			UDP_OBFS = "udpobfs",
			UDP_DOWN = "udpdown",
			CONFIG_LINE_INPUT = "configlineinput",
	UDP_UP = "udpup",
			RECONNECT_UDP = "reconectarudp",
			UDP_WINDOW = "udpbuffer",
			V2RAY_JSON = "v2rayjson";
	
    	public static final String
		CONFIG_PROTEGER_KEY = "protegerConfig",
		CONFIG_MENSAGEM_KEY = "mensagemConfig",
		CONFIG_VALIDADE_KEY = "validadeConfig",
		CONFIG_MENSAGEM_EXPORTAR_KEY = "mensagemConfigExport",
		CONFIG_INPUT_PASSWORD_KEY = "inputPassword"
	;

	// Vpn
	public static final String
	DNSTYPE_KEY = "DNSType",
	DNSFORWARD_KEY = "dnsForward",
	DNSRESOLVER_KEY1 = "dnsResolver1",
	DNSRESOLVER_KEY2 = "dnsResolver2",
	UDPFORWARD_KEY = "udpForward",
	BYPASS_KEY = "bypassKey",
	UDPRESOLVER_KEY = "udpResolver";
	
	public static final String
	DNS_YANDEX_KEY = "DNS (Yandex DNS)",
	DNS_OPEN_KEY = "DNS (Open DNS)",
	DNS_DEFAULT_KEY = "DNS (Default DNS)",
	DNS_GOOGLE_KEY = "DNS (Google DNS)",
	DNS_CUSTOM_KEY = "DNS (Custom DNS)";

	// SSH
	public static final String
	SERVIDOR_KEY = "sshServer",
	SERVIDOR_PORTA_KEY = "sshPort",
	USUARIO_KEY = "sshUser",
	SENHA_KEY = "sshPass",
    VIBRATE = "vibrate",
	KEYPATH_KEY = "keyPath",
	PORTA_LOCAL_KEY = "sshPortaLocal",
	CHAVE_KEY = "chaveKey",
	NAMESERVER_KEY = "serverNameKey",
	DNS_KEY = "dnsKey",
	SSH_COMPRESSION = "data_compression",
	AUTO_PINGER = "auto_ping",
    PINGER = "ping_server",
	PINGER_KEY = "pingerSSH";
	
	public static final String
	PAYLOAD_DEFAULT = "CONNECT [host_port] [protocol][crlf][crlf]",
	CUSTOM_SNI = "customSNI",
	SSLTLS_DEFAULT = "com.google.com";
	// Tunnel Type
	public static final String
			TUNNELTYPE_KEY = "tunnelType",
			TUNNEL_TYPE_SSH_DIRECT = "sshDirect",
			TUNNEL_TYPE_SSH_PROXY = "sshProxy",
			TUNNEL_TYPE_SSH_SSLTUNNEL = "sshSslTunnel";
	;

	public static final int
			bTUNNEL_TYPE_SSH_DIRECT = 1,
			bTUNNEL_TYPE_SSH_PROXY = 2,
			bTUNNEL_TYPE_SSH_SSLTUNNEL = 3,
			bTUNNEL_TYPE_PAY_SSL = 4,
			bTUNNEL_TYPE_SLOWDNS = 5,
			bTUNNEL_TYPE_SSL_RP = 6,
			bTUNNEL_TYPE_SSH = 7,
			bTUNNEL_TYPE_UDP = 8,
    bTUNNEL_TYPE_V2RAY = 9;
}
