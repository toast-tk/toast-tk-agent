package io.toast.tk.agent.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import io.toast.tk.agent.config.WebConfig;
import io.toast.tk.agent.config.WebConfigProvider;
import io.toast.tk.agent.ui.IAgentApp;
import io.toast.tk.agent.ui.MainApp;
import io.toast.tk.agent.web.AgentServerImpl;
import io.toast.tk.agent.web.BrowserManager;
import io.toast.tk.agent.web.IAgentServer;
import io.toast.tk.agent.web.ScriptInjector;
import io.toast.tk.agent.web.UriChangeListener;
import io.toast.tk.agent.web.record.WebRecorder;
import io.toast.tk.agent.web.rest.RecordHandler;
import io.toast.tk.agent.web.rest.StopHandler;

public class WebAgentModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(WebConfigProvider.class).in(Singleton.class);
		bind(WebConfig.class).toProvider(WebConfigProvider.class);
		bind(BrowserManager.class).in(Singleton.class);
		bind(ScriptInjector.class).in(Singleton.class);
		bind(IAgentApp.class).to(MainApp.class).in(Singleton.class);
		bind(IAgentServer.class).to(AgentServerImpl.class).in(Singleton.class);
		bind(RecordHandler.class).in(Singleton.class);
		bind(StopHandler.class).in(Singleton.class);
		bind(UriChangeListener.class).in(Singleton.class);
		bind(WebRecorder.class).in(Singleton.class);
	}

}
