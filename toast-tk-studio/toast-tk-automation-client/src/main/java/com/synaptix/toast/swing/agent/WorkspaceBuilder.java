package com.synaptix.toast.swing.agent;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.config.WebConfig;
import com.synaptix.toast.swing.agent.ui.ConfigPanel;

public class WorkspaceBuilder implements IWorkspaceBuilder {

	private static final Logger LOG = LogManager.getLogger(WorkspaceBuilder.class);

	private File toastPropertiesFile;

	private File toastWebPropertiesFile;

	private final Properties properties;

	private final Properties swingProperties;

	private final Properties webProperties;

	private Config config;
	
	private WebConfig webConfig;
	
	@Inject
	public WorkspaceBuilder(
			Config config, 
			WebConfig webConfig){
		this.config = config;
		this.webConfig = webConfig;
		this.properties = new Properties();
		this.swingProperties = new Properties();
		this.webProperties = new Properties();
	}

	@Override
	public void initWorkspace() {
		if (config.getToastHome() != null) {
			try {
				boolean isNewEnv = false;
				final String workSpaceDir = config.getToastHome();
				createHomeDirectories(config, workSpaceDir);
				this.toastPropertiesFile = new File(workSpaceDir + "/toast.properties");
				if (!toastPropertiesFile.exists()) {
					isNewEnv = true;
					toastPropertiesFile.createNewFile();
				}
				this.toastWebPropertiesFile = new File(workSpaceDir + "/toast.web.properties");
				if (!toastWebPropertiesFile.exists()) {
					isNewEnv = true;
					toastWebPropertiesFile.createNewFile();
				}
				initAndStoreProperties(config, webConfig);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				e.printStackTrace();
				throw new Error(e.getMessage());
			}
		} else {
			String message = "$(toast.home) system property not defined, application stopped !";
			LOG.error(message);
			throw new Error(message);
		}
	}

	public void propertiesChanged() {
		this.properties.clear();
		this.properties.putAll(this.swingProperties);
		this.properties.putAll(this.webProperties);
	}

	private void createHomeDirectories(final Config config, String workSpaceDir) {
		new File(workSpaceDir).mkdir();
	}

	private void initAndStoreProperties(final Config config, final WebConfig webConfig) throws IOException {
		initAndStoreProperties(config);
		initAndStoreProperties(webConfig);
		propertiesChanged();
	}

	private void initAndStoreProperties(final Config config) throws IOException {
		Properties p = new Properties();
		p.setProperty(Property.TOAST_RUNTIME_TYPE, config.getRuntimeType());
		p.setProperty(Property.TOAST_RUNTIME_CMD, config.getRuntimeCommand());
		p.setProperty(Property.TOAST_RUNTIME_AGENT, config.getPluginDir() + Property.AGENT_JAR_NAME);
		p.setProperty(Property.WEBAPP_ADDR, config.getWebAppAddr());
		System.getProperties().put(Property.WEBAPP_ADDR, config.getWebAppAddr());
		p.setProperty(Property.WEBAPP_PORT, config.getWebAppPort());
		System.getProperties().put(Property.WEBAPP_PORT, config.getWebAppPort());
		p.setProperty(Property.JNLP_RUNTIME_HOST, config.getJnlpRuntimeHost());
		p.setProperty(Property.JNLP_RUNTIME_FILE, config.getJnlpRuntimeFile());
		p.store(FileUtils.openOutputStream(this.toastPropertiesFile), null);
		this.swingProperties.load(FileUtils.openInputStream(this.toastPropertiesFile));
	}

	private void initAndStoreProperties(final WebConfig webConfig) throws IOException {
		Properties p = new Properties();
		p.setProperty(Property.TOAST_TEST_WEB_INIT_RECORDING_URL, webConfig.getWebInitRecordingUrl());
		p.setProperty(Property.TOAST_CHROMEDRIVER_PATH, webConfig.getChromeDriverPath());
		p.store(FileUtils.openOutputStream(this.toastWebPropertiesFile), null);
		this.webProperties.load(FileUtils.openInputStream(this.toastWebPropertiesFile));
	}

	@Override @Deprecated
	public void openConfigDialog() {
//		new ConfigPanel(properties, toastPropertiesFile);
	}
	
	@Override
	public String getRuntimeType() {
		return (String) properties.get(Property.TOAST_RUNTIME_TYPE);
	}

	@Override
	public File getToastPropertiesFile() {
		return toastPropertiesFile;
	}

	@Override
	public File getToastWebPropertiesFile() {
		return toastWebPropertiesFile;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public Properties getSwingProperties() {
		return swingProperties;
	}

	@Override
	public Properties getWebProperties() {
		return webProperties;
	}
}
