package io.toast.tk.agent.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Provider;

import io.toast.tk.agent.config.DriverFactory.DRIVER;

public class AgentConfigProvider implements Provider<AgentConfig> {
	
	private static final Logger LOG = LogManager.getLogger(AgentConfigProvider.class);

	private AgentConfig webConfig;
	
	public static final String TOAST_TEST_WEB_INIT_RECORDING_URL = "toast.web.recording.url";

	public static final String TOAST_DRIVER_SELECT = "toast.driver.select";
	
	public static final String TOAST_CHROMEDRIVER_32_PATH = "toast.chromedriver.32.path";

	public static final String TOAST_CHROMEDRIVER_64_PATH = "toast.chromedriver.64.path";

	public static final String TOAST_FIREFOXDRIVER_32_PATH = "toast.firefoxdriver.32.path";

	public static final String TOAST_FIREFOXDRIVER_64_PATH = "toast.firefoxdriver.64.path";

	public static final String TOAST_IEDRIVER_32_PATH = "toast.iedriver.32.path";

	public static final String TOAST_IEDRIVER_64_PATH = "toast.iedriver.64.path";
	
	public static final String TOAST_TEST_WEB_APP_URL = "toast.webapp.url";

	public static final String TOAST_API_KEY = "toast.api.key";
	
	public static final String TOAST_SCRIPTS_DIR = "toast.agent.scripts.dir";
	
	public static final String TOAST_PLUGIN_DIR = "toast.plugin.dir";

	public static final String TOAST_PROXY_ACTIVATE = "toast.proxy.activate";
	
	public static final String TOAST_PROXY_ADRESS = "toast.proxy.adress";

	public static final String TOAST_PROXY_PORT = "toast.proxy.port";

	public static final String TOAST_PROXY_USER_NAME = "toast.proxy.username";

	public static final String TOAST_PROXY_USER_PSWD = "toast.proxy.userpswd";

	public static final String TOAST_MAIL_SEND = "toast.mail.send";

	public static final String TOAST_MAIL_TO = "toast.mail.to";

	private static final String PATH_DELIM = "/";

	private void initConfig() {
		String userHomepath = AgentConfig.getToastHome() + PATH_DELIM;
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(userHomepath + "agent.properties"));
		}
		catch(IOException e) {
			LOG.error(e.getMessage(), e);
		}
		
		String driverSelected = prop.getProperty(TOAST_DRIVER_SELECT, DRIVER.CHROME_64.toString());
		DriverFactory.setSelected(driverSelected);
		
		webConfig = new AgentConfig();
		webConfig.setWebInitRecordingUrl(prop.getProperty(TOAST_TEST_WEB_INIT_RECORDING_URL, "URL to record"));
		webConfig.setDriverSelected(prop.getProperty(TOAST_DRIVER_SELECT, DriverFactory.getSelected().toString()));
		webConfig.setChrome32DriverPath(prop.getProperty(TOAST_CHROMEDRIVER_32_PATH, userHomepath + "chromedriver.exe"));
		webConfig.setChrome64DriverPath(prop.getProperty(TOAST_CHROMEDRIVER_64_PATH, userHomepath + "chromedriver.exe"));
		webConfig.setFirefox32DriverPath(prop.getProperty(TOAST_FIREFOXDRIVER_32_PATH, userHomepath + "geckodriver_32.exe"));
		webConfig.setFirefox64DriverPath(prop.getProperty(TOAST_FIREFOXDRIVER_64_PATH, userHomepath + "geckodriver.exe"));
		webConfig.setIe32DriverPath(prop.getProperty(TOAST_IEDRIVER_32_PATH, userHomepath + "MicrosoftWebDriver.exe"));
		webConfig.setIe64DriverPath(prop.getProperty(TOAST_IEDRIVER_64_PATH, userHomepath + "MicrosoftWebDriver.exe"));
		webConfig.setWebAppUrl(prop.getProperty(TOAST_TEST_WEB_APP_URL, "Toast WebApp url"));
		webConfig.setApiKey(prop.getProperty(TOAST_API_KEY, "Web App Api Key"));
		webConfig.setPluginDir(prop.getProperty(TOAST_PLUGIN_DIR, webConfig.getPluginDir()));
		webConfig.setScriptsDir(prop.getProperty(TOAST_SCRIPTS_DIR, "Scripts Directory Path"));
		webConfig.setProxyActivate(prop.getProperty(TOAST_PROXY_ACTIVATE, "false"));
		webConfig.setProxyAdress(prop.getProperty(TOAST_PROXY_ADRESS, "Proxy Address"));
		webConfig.setProxyPort(prop.getProperty(TOAST_PROXY_PORT, "Proxy Port"));
		webConfig.setProxyUserName(prop.getProperty(TOAST_PROXY_USER_NAME, "Proxy User Name"));
		webConfig.setProxyUserPswd(prop.getProperty(TOAST_PROXY_USER_PSWD, "Proxy User Password"));
		webConfig.setProxyUserName(prop.getProperty(TOAST_MAIL_SEND, "false"));
		webConfig.setProxyUserPswd(prop.getProperty(TOAST_MAIL_TO, "Adress to send mail"));
	}

	@Override
	public AgentConfig get() {
		initConfig();
		return webConfig;
	}

}
