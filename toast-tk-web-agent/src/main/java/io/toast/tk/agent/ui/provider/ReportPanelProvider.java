package io.toast.tk.agent.ui.provider;


import com.google.inject.Inject;
import com.google.inject.Provider;

import io.toast.tk.agent.ui.panels.ReportPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ReportPanelProvider implements Provider<ReportPanel> {

    private ReportPanel panel;
    private static final Logger LOG = LogManager.getLogger(PropertiesProvider.class);

    @Inject
    ReportPanelProvider(){

    }

    @Override
    public ReportPanel get() {
        try {
        	if(panel != null) {
        		panel.dispose();
        	}
            panel = new ReportPanel();
        	return panel;
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
