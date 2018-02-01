package io.toast.tk.agent.ui.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.PropertiesHolder;
import io.toast.tk.runtime.utils.EncryptHelper;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;


public class PropertiesProvider implements Provider<PropertiesHolder> {

    private final AgentConfigProvider configProvider;
    private File toastWebPropertiesFile;
    private static final Logger LOG = LogManager.getLogger(PropertiesProvider.class);

    @Inject
    public PropertiesProvider(AgentConfigProvider configProvider) throws IOException {
        this.configProvider = configProvider;
        this.toastWebPropertiesFile = new File(AgentConfig.TOAST_PROPERTIES_FILE);
        if(!Paths.get(AgentConfig.getToastHome()).toFile().exists()){
            LOG.info("creating workspace directory at: " + AgentConfig.getToastHome() );
            new File(AgentConfig.getToastHome()).mkdir();
        }
        if (!toastWebPropertiesFile.exists()) {
            toastWebPropertiesFile.createNewFile();
        }
    }

    @Override
    public PropertiesHolder get() {
        try {
            Properties p = buildAndStoreProperties(this.configProvider.get());
            return new PropertiesHolder(p, this.toastWebPropertiesFile);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private Properties buildAndStoreProperties(AgentConfig webConfig) throws IOException {
        Properties p = new Properties();
        p.setProperty(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL, webConfig.getWebInitRecordingUrl());
        p.setProperty(AgentConfigProvider.TOAST_DRIVER_SELECT, webConfig.getDriverSelected());
        p.setProperty(AgentConfigProvider.TOAST_CHROMEDRIVER_32_PATH, webConfig.getChrome32DriverPath());
        p.setProperty(AgentConfigProvider.TOAST_CHROMEDRIVER_64_PATH, webConfig.getChrome64DriverPath());
        p.setProperty(AgentConfigProvider.TOAST_FIREFOXDRIVER_32_PATH, webConfig.getFirefox32DriverPath());
        p.setProperty(AgentConfigProvider.TOAST_FIREFOXDRIVER_64_PATH, webConfig.getFirefox64DriverPath());
        p.setProperty(AgentConfigProvider.TOAST_IEDRIVER_32_PATH, webConfig.getIe32DriverPath());
        p.setProperty(AgentConfigProvider.TOAST_IEDRIVER_64_PATH, webConfig.getIe64DriverPath());
        p.setProperty(AgentConfigProvider.TOAST_TEST_WEB_APP_URL, webConfig.getWebAppUrl());
        p.setProperty(AgentConfigProvider.TOAST_API_KEY, webConfig.getApiKey());
        p.setProperty(AgentConfigProvider.TOAST_PLUGIN_DIR, webConfig.getPluginDir());
        p.setProperty(AgentConfigProvider.TOAST_SCRIPTS_DIR, webConfig.getScriptsDir());
        p.setProperty(AgentConfigProvider.TOAST_PROXY_ACTIVATE, webConfig.getProxyActivate());
        p.setProperty(AgentConfigProvider.TOAST_PROXY_ADRESS, webConfig.getProxyAdress());
        p.setProperty(AgentConfigProvider.TOAST_PROXY_PORT, webConfig.getProxyPort());
        p.setProperty(AgentConfigProvider.TOAST_PROXY_USER_NAME, webConfig.getProxyUserName());
        p.setProperty(AgentConfigProvider.TOAST_PROXY_USER_PSWD, EncryptHelper.encrypt(webConfig.getProxyUserPswd()));
        p.setProperty(AgentConfigProvider.TOAST_SMTP_ACTIVATE, webConfig.getSmtpActivate());
        p.setProperty(AgentConfigProvider.TOAST_SMTP_HOST, webConfig.getSmtpHost());
        p.setProperty(AgentConfigProvider.TOAST_SMTP_PORT, webConfig.getSmtpPort());
        p.setProperty(AgentConfigProvider.TOAST_SMTP_USER, webConfig.getSmtpUser());
        p.setProperty(AgentConfigProvider.TOAST_SMTP_PSWD, EncryptHelper.encrypt(webConfig.getSmtpUserPswd()));
        p.store(FileUtils.openOutputStream(this.toastWebPropertiesFile), null);
        
        // Use of the uncrypted password
        p.setProperty(AgentConfigProvider.TOAST_PROXY_USER_PSWD, webConfig.getProxyUserPswd());
        p.setProperty(AgentConfigProvider.TOAST_SMTP_PSWD, webConfig.getSmtpUserPswd());
        return p;
    }

}
