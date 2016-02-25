package com.synaptix.toast.core.agent.config;

import java.io.File;

public class WebConfig {

	public static final String TOAST_HOME_PROPERTY = "toast.home";

	public static final String TOAST_PROPERTIES_FILE = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar + "toast.web.properties";

//	public static final String TOAST_PLUGIN_DIR = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar + "plugins.web";
//
//	public static final String TOAST_RUNTIME_DIR = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar + "runtime.web";

	public static final String TOAST_LOG_DIR = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar + "log.web";
	
	public static final String TOAST_HOME_DIR = System.getProperty(TOAST_HOME_PROPERTY) + File.separatorChar;
	
	private String toastWebPropertyFixeMe;

	public String getToastHome() {
		return System.getProperty(TOAST_HOME_PROPERTY);
	}

	public String getLogDir() {
		return getToastHome() + "log/";
	}

	public String getPluginDir() {
		return getToastHome() + File.separatorChar + "plugins/";
	}

	public void setToastWebPropertyFixeMe(
		String toastWebPropertyFixeMe) {
		this.toastWebPropertyFixeMe = toastWebPropertyFixeMe;
	}

	public String getToastWebPropertyFixeMe() {
		return this.toastWebPropertyFixeMe;
	}
}
