package io.toast.tk.agent.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Provider;


public class AgentConfigProvider implements Provider<AgentConfig> {
	
	private static final Logger LOG = LogManager.getLogger(AgentConfigProvider.class);

	private AgentConfig webConfig;
	
	public static final String TOAST_TEST_WEB_INIT_RECORDING_URL = "toast.web.recording.url";

	public static final String TOAST_CHROMEDRIVER_PATH = "toast.chromedriver.path";
	
	public static final String TOAST_TEST_WEB_APP_URL = "toast.webapp.url";

	public static final String TOAST_API_KEY = "toast.api.key";
	
	public static final String TOAST_SCRIPTS_DIR = "toast.agent.scripts.dir";
	
	public static final String TOAST_PLUGIN_DIR = "toast.plugin.dir";

	public static final String TOAST_PROXY_ACTIVATE = "toast.proxy.activate";
	
	public static final String TOAST_PROXY_ADRESS = "toast.proxy.adress";

	public static final String TOAST_PROXY_PORT = "toast.proxy.port";

	public static final String TOAST_PROXY_USER_NAME = "toast.proxy.username";

	public static final String TOAST_PROXY_USER_PSWD = "toast.proxy.userpswd";



	public AgentConfigProvider() {
		super();
	}

	private void initConfig() {
		String userHomepath = AgentConfig.getToastHome() + "/";
		Properties p = null;
		if(userHomepath != null) {
			p = new Properties();
			try {
				p.load(new FileReader(userHomepath + "agent.properties"));
			}
			catch(IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		webConfig = new AgentConfig();
		webConfig.setWebInitRecordingUrl(p.getProperty(TOAST_TEST_WEB_INIT_RECORDING_URL, "URL to record"));
		webConfig.setChromeDriverPath(p.getProperty(TOAST_CHROMEDRIVER_PATH, userHomepath + "chromedriver.exe"));
		webConfig.setWebAppUrl(p.getProperty(TOAST_TEST_WEB_APP_URL, "Toast WebApp url"));
		webConfig.setApiKey(p.getProperty(TOAST_API_KEY, "Web App Api Key"));
		webConfig.setPluginDir(p.getProperty(TOAST_PLUGIN_DIR, webConfig.getPluginDir()));
		webConfig.setScriptsDir(p.getProperty(TOAST_SCRIPTS_DIR, "Scripts Directory Path"));
		webConfig.setProxyActivate(p.getProperty(TOAST_PROXY_ACTIVATE, "false"));
		webConfig.setProxyAdress(p.getProperty(TOAST_PROXY_ADRESS, "Proxy Adress"));
		webConfig.setProxyPort(p.getProperty(TOAST_PROXY_PORT, "Proxy Port"));
		webConfig.setProxyUserName(p.getProperty(TOAST_PROXY_USER_NAME, "Proxy User Name"));
		webConfig.setProxyUserPswd(p.getProperty(TOAST_PROXY_USER_PSWD, "Proxy User Password"));
	}

	@Override
	public AgentConfig get() {
		initConfig();
		return webConfig;
	}

}
