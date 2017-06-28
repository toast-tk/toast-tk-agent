package io.toast.tk.agent.ui.provider;


import com.google.inject.Inject;
import com.google.inject.Provider;

import io.toast.tk.agent.ui.panels.ConfigPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ConfigPanelProvider implements Provider<ConfigPanel> {

    private final PropertiesProvider propertiesProvider;
    private ConfigPanel panel;
    private static final Logger LOG = LogManager.getLogger(PropertiesProvider.class);

    @Inject
    ConfigPanelProvider(PropertiesProvider propertiesProvider){
        this.propertiesProvider = propertiesProvider;
    }

    @Override
    public ConfigPanel get() {
        try {
        	if(panel == null) {
        		panel = new ConfigPanel(this.propertiesProvider.get());
        	} else {
        		panel.setVisible(true);
        	}
            return panel;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
