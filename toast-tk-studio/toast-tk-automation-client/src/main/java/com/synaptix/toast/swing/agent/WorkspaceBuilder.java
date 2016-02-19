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
import com.synaptix.toast.swing.agent.ui.ConfigPanel;

public class WorkspaceBuilder implements IWorkspaceBuilder {

	private static final Logger LOG = LogManager.getLogger(WorkspaceBuilder.class);

	private File toastPropertiesFile;

	private final Properties properties;

	private Config config;
	
	@Inject
	public WorkspaceBuilder(Config config){
		this.config = config;
		this.properties = new Properties();
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
				initAndStoreProperties(config);
				if(isNewEnv){
					openConfigDialog();
				}
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

	private void createHomeDirectories(final Config config, String workSpaceDir) {
		new File(workSpaceDir).mkdir();
		new File(config.getPluginDir()).mkdir();
		new File(workSpaceDir + "/log").mkdir();
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
		this.properties.load(FileUtils.openInputStream(this.toastPropertiesFile));
	}
	
	@Override
	public void openConfigDialog() {
		new ConfigPanel(properties, toastPropertiesFile);
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
	public Properties getProperties() {
		return properties;
	}
}
