package io.toast.tk.agent.config;

import org.apache.commons.lang.SystemUtils;

public class AgentConfig {

	public static final String TOAST_PROPERTIES_FILE = getToastHome() + SystemUtils.FILE_SEPARATOR + "agent.properties";

	public static final String TOAST_LOG_DIR = getToastHome() + SystemUtils.FILE_SEPARATOR + "log.web";
		
	private String webStartRecordingUrl;

	private String driverSelected;
	private String chrome32DriverPath;
	private String chrome64DriverPath;
	private String firefox32DriverPath;
	private String firefox64DriverPath;
	private String ie32DriverPath;
	private String ie64DriverPath;

	private String webAppUrl;
	
	private String apiKey;

	private String scriptsDir;
	
	private String pluginDirectory;

	private String proxyActivate;
	private String proxyAdress;
	private String proxyPort;
	private String proxyUser;
	private String proxyPswd;
	
	public static String getToastHome() {
		return System.getProperty("user.home") + SystemUtils.FILE_SEPARATOR + ".toast";
	}

	public String getLogDir() {
		return getToastHome() + "log/";
	}

	public String getPluginDir() {
		return this.pluginDirectory == null ? getToastHome() + SystemUtils.FILE_SEPARATOR + "plugins/": this.pluginDirectory;
	}
	
	public void setWebInitRecordingUrl(
		String webStartRecordingUrl) {
		this.webStartRecordingUrl = webStartRecordingUrl;
	}
	public String getWebInitRecordingUrl() {
		return this.webStartRecordingUrl;
	}

	public void setDriverSelected(
		String driverSelected) {
		this.driverSelected = driverSelected;
	}
	public String getDriverSelected() {
		return this.driverSelected;
	}
	
	public String getDriverPath() {
		switch(this.getDriverSelected()) {
			case AgentConfigProvider.TOAST_CHROMEDRIVER_32_PATH :
				return getChrome32DriverPath();
			case AgentConfigProvider.TOAST_CHROMEDRIVER_64_PATH :
				return getChrome64DriverPath();
			case AgentConfigProvider.TOAST_FIREFOXDRIVER_32_PATH :
				return getFirefox32DriverPath();
			case AgentConfigProvider.TOAST_FIREFOXDRIVER_64_PATH :
				return getFirefox64DriverPath();
			case AgentConfigProvider.TOAST_IEDRIVER_32_PATH :
				return getIe32DriverPath();
			case AgentConfigProvider.TOAST_IEDRIVER_64_PATH :
				return getIe64DriverPath();
			default :
				return null;
		}
	}
	
	public void setChrome32DriverPath(
		String driverPath) {
		this.chrome32DriverPath = driverPath;
	}
	public String getChrome32DriverPath() {
		return this.chrome32DriverPath;
	}
	public void setChrome64DriverPath(
			String driverPath) {
			this.chrome64DriverPath = driverPath;
	}
	public String getChrome64DriverPath() {
		return this.chrome64DriverPath;
	}

	public void setFirefox32DriverPath(
		String driverPath) {
		this.firefox32DriverPath = driverPath;
	}
	public String getFirefox32DriverPath() {
		return this.firefox32DriverPath;
	}
	public void setFirefox64DriverPath(
			String driverPath) {
			this.firefox64DriverPath = driverPath;
	}
	public String getFirefox64DriverPath() {
		return this.firefox64DriverPath;
	}

	public void setIe32DriverPath(
		String driverPath) {
		this.ie32DriverPath = driverPath;
	}
	public String getIe32DriverPath() {
		return this.ie32DriverPath;
	}
	public void setIe64DriverPath(
			String driverPath) {
			this.ie64DriverPath = driverPath;
	}
	public String getIe64DriverPath() {
		return this.ie64DriverPath;
	}
	
	public void setWebAppUrl(
		String webAppUrl) {
		this.webAppUrl = webAppUrl;
	}
	public String getWebAppUrl() {
		return this.webAppUrl;
	}

	public void setApiKey(
		String apikey) {
		this.apiKey = apikey;
	}
	public String getApiKey() {
		return this.apiKey;
	}


	public String getScriptsDir() {
		return this.scriptsDir;		
	}
	
	public void setScriptsDir(String scriptsDir) {
		this.scriptsDir = scriptsDir;		
	}

	public void setPluginDir(
		String pluginDirectory) {
		this.pluginDirectory = pluginDirectory;
	}

	public void setProxyActivate(
		String proxyActivate) {
		this.proxyActivate = proxyActivate;
	}
	public String getProxyActivate() {
		return this.proxyActivate;
	}
	
	public void setProxyAdress(
		String proxyAdress) {
		this.proxyAdress = proxyAdress;
	}
	public String getProxyAdress() {
		return this.proxyAdress;
	}

	public void setProxyPort(
		String proxyPort) {
		this.proxyPort = proxyPort;
	}
	public String getProxyPort() {
		return this.proxyPort;
	}

	public void setProxyUserName(
		String proxyUser) {
		this.proxyUser = proxyUser;
	}
	public String getProxyUserName() {
		return this.proxyUser;
	}

	public void setProxyUserPswd(
		String proxyPswd) {
		this.proxyPswd = proxyPswd;
	}
	public String getProxyUserPswd() {
		return this.proxyPswd;
	}
}
