package com.synaptix.toast.core.agent.config;

import java.io.File;

public class Config {


	public static final String TOAST_PROPERTIES_FILE =System.getProperty("user.home") + "/.toast" + File.separatorChar + "toast.properties";

	public static final String TOAST_PLUGIN_DIR = System.getProperty("user.home") + "/.toast"+ File.separatorChar + "plugins";

	public static final String TOAST_RUNTIME_DIR = System.getProperty("user.home") + "/.toast" + File.separatorChar + "runtime";

	public static final String TOAST_LOG_DIR =System.getProperty("user.home") + "/.toast" + File.separatorChar + "log";
	
	public static final String TOAST_HOME_DIR = System.getProperty("user.home") + "/.toast" + File.separatorChar;
	
	private String mongoServer;

	private String runtimeType;

	private int mongoPort;

	private String webAppAddr;

	private String webAppPort;

	private String runtimeCommand;

	private String jnlpRuntimeHost;

	private String jnlpRuntimeFile;

	private String debugArgs;

	public String getMongoServer() {
		return mongoServer;
	}

	public void setMongoServer(
		String mongoServer) {
		this.mongoServer = mongoServer;
	}

	public int getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(
		int mongoPort) {
		this.mongoPort = mongoPort;
	}

	public void setRuntimeType(
		String runtimeType) {
		this.runtimeType = runtimeType;
	}

	public String getToastHome() {
		return System.getProperty("user.home") + "/.toast";
	}

	public String getLogDir() {
		return getToastHome() + "log/";
	}

	public String getPluginDir() {
		return getToastHome() + File.separatorChar + "plugins/";
	}

	public void setWebAppAddr(
		String webAppAddr) {
		this.webAppAddr = webAppAddr;
	}

	public void setWebAppPort(
		String webAppPort) {
		this.webAppPort = webAppPort;
	}

	public void setRuntimeCommand(
		String runtimeCommand) {
		this.runtimeCommand = runtimeCommand;
	}

	public String getWebAppAddr() {
		return this.webAppAddr;
	}

	public String getWebAppPort() {
		return this.webAppPort;
	}

	public String getRuntimeCommand() {
		return this.runtimeCommand;
	}

	public String getRuntimeType() {
		return this.runtimeType;
	}

	public String getJnlpRuntimeHost() {
		return this.jnlpRuntimeHost;
	}

	public void setJnlpRuntimeHost(
		String runtimeHost) {
		this.jnlpRuntimeHost = runtimeHost;
	}

	public void setJnlpRuntimeFile(
		String runtimeFile) {
		this.jnlpRuntimeFile = runtimeFile;
	}

	public String getJnlpRuntimeFile() {
		return this.jnlpRuntimeFile;
	}

	public void setDebugArgs(
		String debugArgs) {
		this.debugArgs = debugArgs;
	}

	public String getDebugArgs() {
		return debugArgs;
	}
}
