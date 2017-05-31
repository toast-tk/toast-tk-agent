package io.toast.tk.agent.ui.provider;


import com.google.inject.Inject;
import com.google.inject.Provider;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.panels.DropPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class DropPanelProvider implements Provider<DropPanel> {

    private final AgentConfigProvider provider;
    private DropPanel panel;
    private static final Logger LOG = LogManager.getLogger(PropertiesProvider.class);

    @Inject
    DropPanelProvider(AgentConfigProvider provider){
        this.provider = provider;
    }

    @Override
    public DropPanel get() {
        try {
        	if(panel == null) {
            	panel = new DropPanel(provider);
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
