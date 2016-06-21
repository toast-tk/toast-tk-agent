package com.synaptix.toast.agent.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.agent.ui.MainApp;
import com.synaptix.toast.agent.web.AgentServerImpl;
import com.synaptix.toast.agent.web.BrowserManager;
import com.synaptix.toast.agent.web.IAgentServer;
import com.synaptix.toast.agent.web.ScriptInjector;
import com.synaptix.toast.agent.web.rest.RecordHandler;
import com.synaptix.toast.agent.web.rest.StopHandler;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.config.ConfigProvider;
import com.synaptix.toast.core.agent.config.WebConfig;
import com.synaptix.toast.core.agent.config.WebConfigProvider;

public class WebAgentModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(ConfigProvider.class).in(Singleton.class);
		bind(WebConfigProvider.class).in(Singleton.class);
		bind(Config.class).toProvider(ConfigProvider.class);
		bind(WebConfig.class).toProvider(WebConfigProvider.class);
		bind(BrowserManager.class).in(Singleton.class);
		bind(ScriptInjector.class).in(Singleton.class);
		bind(MainApp.class).in(Singleton.class);
		bind(IAgentServer.class).to(AgentServerImpl.class).in(Singleton.class);
		bind(RecordHandler.class).in(Singleton.class);
		bind(StopHandler.class).in(Singleton.class);
	}

}
