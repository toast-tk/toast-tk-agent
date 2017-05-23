package io.toast.tk.agent.ui.provider;


import com.google.inject.Inject;
import com.google.inject.Provider;
import io.toast.tk.agent.ui.ConfigPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ConfigPanelProvider implements Provider<ConfigPanel> {

    private final PropertiesProvider propertiesProvider;
    private static final Logger LOG = LogManager.getLogger(PropertiesProvider.class);

    @Inject
    ConfigPanelProvider(PropertiesProvider propertiesProvider){
        this.propertiesProvider = propertiesProvider;
    }

    @Override
    public ConfigPanel get() {
        try {
            return new ConfigPanel(this.propertiesProvider.get());
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
