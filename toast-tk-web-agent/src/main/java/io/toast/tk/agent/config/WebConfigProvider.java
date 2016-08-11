package io.toast.tk.agent.config;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Provider;

import io.toast.tk.agent.ui.ConfigPanel;


public class WebConfigProvider implements Provider<WebConfig> {
	
	private static final Logger LOG = LogManager.getLogger(WebConfigProvider.class);

	private WebConfig webConfig;

	
	public static final String TOAST_TEST_WEB_INIT_RECORDING_URL = "toast.web.recording.url";

	public static final String TOAST_CHROMEDRIVER_PATH = "toast.chromedriver.path";
	
	public static final String TOAST_TEST_WEB_APP_URL = "toast.webapp.url";


	public WebConfigProvider() {
		super();
	}

	private void initConfig() {
		String userHomepath = WebConfig.getToastHome() + "/";
		Properties p = null;
		if(userHomepath != null) {
			p = new Properties();
			try {
				p.load(new FileReader(userHomepath + "toast.web.properties"));
			}
			catch(IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		webConfig = new WebConfig();
		webConfig.setWebInitRecordingUrl(p.getProperty(TOAST_TEST_WEB_INIT_RECORDING_URL, "url to record"));
		webConfig.setChromeDriverPath(p.getProperty(TOAST_CHROMEDRIVER_PATH, "ChromeDriver Path"));
		webConfig.setWebAppUrl(p.getProperty(TOAST_TEST_WEB_APP_URL, "Toast WebApp url"));
	}

	@Override
	public WebConfig get() {
		initConfig();
		return webConfig;
	}
}
