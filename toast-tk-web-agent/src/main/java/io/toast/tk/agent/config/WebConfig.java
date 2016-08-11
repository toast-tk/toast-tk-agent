package io.toast.tk.agent.config;

import java.io.File;

public class WebConfig {


	public static final String TOAST_PROPERTIES_FILE = getToastHome() + "/" + "toast.web.properties";

	public static final String TOAST_LOG_DIR = getToastHome() + "/" + "log.web";
		
	private String webStartRecordingUrl;

	private String chromeDriverPath;

	private String webAppUrl;
	
	public static String getToastHome() {
		return System.getProperty("user.home") + "/" + ".toast";
	}

	public String getLogDir() {
		return getToastHome() + "log/";
	}

	public String getPluginDir() {
		return getToastHome() + "/" + "plugins/";
	}

	public void setWebInitRecordingUrl(
		String webStartRecordingUrl) {
		this.webStartRecordingUrl = webStartRecordingUrl;
	}

	public String getWebInitRecordingUrl() {
		return this.webStartRecordingUrl;
	}
	
	public void setChromeDriverPath(
		String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public String getChromeDriverPath() {
		return this.chromeDriverPath;
	}
	
	
	public void setWebAppUrl(
		String webAppUrl) {
		this.webAppUrl = webAppUrl;
	}

	public String getWebAppUrl() {
		return this.webAppUrl;
	}
}
