package io.toast.tk.agent.guice;

import com.google.inject.AbstractModule;

import io.toast.tk.agent.ui.PropertiesHolder;
import io.toast.tk.agent.ui.panels.ConfigPanel;
import io.toast.tk.agent.ui.provider.ConfigPanelProvider;
import io.toast.tk.agent.ui.provider.PropertiesProvider;

public class UiModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PropertiesHolder.class).toProvider(PropertiesProvider.class);
        bind(ConfigPanel.class).toProvider(ConfigPanelProvider.class);
    }
}
