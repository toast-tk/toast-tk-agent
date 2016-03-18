package com.synaptix.toast.agent.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.agent.ui.SysTrayHook;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.config.ConfigProvider;
import com.synaptix.toast.core.agent.config.WebConfig;
import com.synaptix.toast.core.agent.config.WebConfigProvider;

public class WebAgentModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(Config.class).toProvider(ConfigProvider.class).in(Singleton.class);
		bind(WebConfig.class).toProvider(WebConfigProvider.class).in(Singleton.class);
		bind(SysTrayHook.class).in(Singleton.class);
	}

}
