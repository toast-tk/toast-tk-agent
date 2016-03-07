package com.synaptix.toast.core.agent.config;

import java.io.File;

public class WebConfig {

	public static final String TOAST_HOME_PROPERTY = "toast.home";

	public static final String TOAST_PROPERTIES_FILE = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar + "toast.web.properties";

	public static final String TOAST_LOG_DIR = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar + "log.web";
	
	public static final String TOAST_HOME_DIR = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar;
	
	private String webStartRecordingUrl;

	public String getToastHome() {
		return System.getProperty(TOAST_HOME_PROPERTY);
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
}
