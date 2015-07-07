package com.synaptix.toast.swing.agent.config;

public class Config {

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

	public String getUserHome() {
		return System.getProperty("user.home");
	}

	public String getWorkSpaceDir() {
		return getUserHome() + "/.toast/";
	}

	public String getLogDir() {
		return getWorkSpaceDir() + "log/";
	}

	public String getPluginDir() {
		return getWorkSpaceDir() + "plugins/";
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
