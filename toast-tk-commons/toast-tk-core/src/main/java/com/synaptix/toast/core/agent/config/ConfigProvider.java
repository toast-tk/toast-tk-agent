package com.synaptix.toast.core.agent.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.inject.Provider;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.annotation.craft.FixMe;

@FixMe(todo = "do external default configuration using a setting file")
public class ConfigProvider implements Provider<Config> {

	private Config config;
	

	public ConfigProvider() {
		super();
		initConfig();
	}

	private void initConfig() {
		String userHomepath = Config.TOAST_HOME_DIR;
		Properties p = null;
		if(userHomepath != null) {
			p = new Properties();
			try {
				p.load(new FileReader(userHomepath + "toast.properties"));
			}
			catch(IOException e) {
			}
		}
		config = new Config();
		String port = System.getProperty(Property.MONGO_PORT);
		int mongDbPort = port != null ? Integer.valueOf(port) : 27017;
		String mongDbPortProperty = p.getProperty(Property.MONGO_PORT);
		config.setMongoPort(mongDbPortProperty != null ? Integer.valueOf(mongDbPortProperty) : mongDbPort);
		String host = System.getProperty(Property.MONGO_HOST);
		String mongDbHost = host != null ? host : "localhost";
		String mongDbHostProperty = p.getProperty(Property.MONGO_HOST);
		config.setMongoServer(mongDbHostProperty != null ? mongDbHostProperty : mongDbHost);
		config.setRuntimeType(p.getProperty(Property.TOAST_RUNTIME_TYPE, "JNLP"));
		String sutJnlpHostDefaultValue = "http://swingrec-app.fret.sncf.fr:8081/RUSystem";
		config.setJnlpRuntimeHost(p.getProperty(Property.JNLP_RUNTIME_HOST, sutJnlpHostDefaultValue));
		String sutJnlpFileDefaultValue = "RUSystem.jnlp";
		config.setJnlpRuntimeFile(p.getProperty(Property.JNLP_RUNTIME_FILE, sutJnlpFileDefaultValue));
		config.setRuntimeCommand(p.getProperty(Property.TOAST_RUNTIME_CMD, sutJnlpHostDefaultValue + "/"
			+ sutJnlpFileDefaultValue));
		config.setWebAppAddr(p.getProperty(Property.WEBAPP_ADDR, "10.23.252.131"));
		config.setWebAppPort(p.getProperty(Property.WEBAPP_PORT, "9000"));
		config.setDebugArgs(p.getProperty(
			Property.AGENT_DEBUG_AGRS,
			"-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=4000,suspend=n"));
	}

	@Override
	public Config get() {
		return config;
	}
}
