package com.synaptix.toast.core.agent.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.Provider;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.annotation.craft.FixMe;


@FixMe(todo = "do external default configuration using a setting file")
public class WebConfigProvider implements Provider<WebConfig> {

	private WebConfig webConfig;
	

	public WebConfigProvider() {
		super();
	}

	private void initConfig() {
		String userHomepath = Config.TOAST_HOME_DIR;
		Properties p = null;
		if(userHomepath != null) {
			p = new Properties();
			try {
				p.load(new FileReader(userHomepath + "toast.web.properties"));
			}
			catch(IOException e) {
			}
		}
		webConfig = new WebConfig();
		webConfig.setWebInitRecordingUrl(p.getProperty(Property.TOAST_TEST_WEB_INIT_RECORDING_URL, "url to record"));
		webConfig.setChromeDriverPath(p.getProperty(Property.TOAST_CHROMEDRIVER_PATH, "ChromeDriver Path"));
		webConfig.setWebAppUrl(p.getProperty(Property.TOAST_TEST_WEB_APP_URL, "Toast WebApp url"));
	}

	@Override
	public WebConfig get() {
		initConfig();
		return webConfig;
	}
}
