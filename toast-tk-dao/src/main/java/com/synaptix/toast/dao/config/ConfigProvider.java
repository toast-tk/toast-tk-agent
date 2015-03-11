package com.synaptix.toast.dao.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.Provider;
import com.synaptix.toast.core.Property;

public class ConfigProvider implements Provider<Config> {

	private Config config;

	public ConfigProvider() {
		super();

		initConfig();
	}

	private void initConfig() {
		String userHomepath = Property.TOAST_HOME_DIR;
		Properties p = null;
		if (userHomepath != null) {
			p = new Properties();
			try {
				p.load(new FileReader(userHomepath + "toast.properties"));
			} catch (IOException e) {
			}
		} 
		config = new Config();
		
		String port = System.getProperty(Property.MONGO_PORT);
		int mongDbPort = port != null ? Integer.valueOf(port) : 27017;
		String mongDbPortProperty = p.getProperty(Property.MONGO_PORT);
		config.setMongoPort(mongDbPortProperty!=null ? Integer.valueOf(mongDbPortProperty) : mongDbPort);
		
		String host = System.getProperty(Property.MONGO_HOST);
		String mongDbHost = host != null ? host : "localhost";
		String mongDbHostProperty = p.getProperty(Property.MONGO_HOST);
		config.setMongoServer(mongDbHostProperty != null ? mongDbHostProperty : mongDbHost);
	}

	@Override
	public Config get() {
		return config;
	}
}
