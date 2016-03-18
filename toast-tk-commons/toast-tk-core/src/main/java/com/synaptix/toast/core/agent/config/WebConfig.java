package com.synaptix.toast.core.agent.config;

import java.io.File;

public class WebConfig {


	public static final String TOAST_PROPERTIES_FILE = System.getProperty("user.home") + "/.toast" + File.separatorChar + "toast.web.properties";

	public static final String TOAST_LOG_DIR = System.getProperty("user.home") + "/.toast" + File.separatorChar + "log.web";
	
	public static final String TOAST_HOME_DIR = System.getProperty("user.home") + "/.toast" + File.separatorChar;
	
	private String webStartRecordingUrl;

	private String chromeDriverPath;

	private String webAppUrl;

	public String getToastHome() {
		return System.getProperty("user.home") + "/.toast";
	}

	public String getLogDir() {
		return getToastHome() + "log/";
	}

	public String getPluginDir() {
		return getToastHome() + File.separatorChar + "plugins/";
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
