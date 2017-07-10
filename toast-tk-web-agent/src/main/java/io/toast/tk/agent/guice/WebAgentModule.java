package io.toast.tk.agent.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import com.google.inject.multibindings.MapBinder;
import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.IAgentApp;
import io.toast.tk.agent.ui.MainApp;
import io.toast.tk.agent.ui.verify.*;
import io.toast.tk.agent.web.AgentServerImpl;
import io.toast.tk.agent.web.BrowserManager;
import io.toast.tk.agent.web.IAgentServer;
import io.toast.tk.agent.web.ScriptInjector;
import io.toast.tk.agent.web.UriChangeListener;
import io.toast.tk.agent.web.record.WebRecorder;
import io.toast.tk.agent.web.rest.RecordHandler;
import io.toast.tk.agent.web.rest.StopHandler;
import io.toast.tk.agent.web.rest.AsyncHttpClientProvider;
import org.asynchttpclient.AsyncHttpClient;

public class WebAgentModule extends AbstractModule{

	@Override
	protected void configure() {
		install(new UiModule());

		bind(AgentConfigProvider.class).in(Singleton.class);
		bind(AgentConfig.class).toProvider(AgentConfigProvider.class);
		bind(BrowserManager.class).in(Singleton.class);
		bind(ScriptInjector.class).in(Singleton.class);
		bind(IAgentApp.class).to(MainApp.class).in(Singleton.class);
		bind(IAgentServer.class).to(AgentServerImpl.class).in(Singleton.class);
		bind(RecordHandler.class).in(Singleton.class);
		bind(StopHandler.class).in(Singleton.class);
		bind(UriChangeListener.class).in(Singleton.class);
		bind(WebRecorder.class).in(Singleton.class);
		bind(AsyncHttpClient.class).toProvider(AsyncHttpClientProvider.class);

		MapBinder<String, IPropertyVerifier> verifierMapBinder = MapBinder.newMapBinder(binder(), String.class, IPropertyVerifier.class);
		verifierMapBinder.addBinding(AgentConfigProvider.TOAST_TEST_WEB_APP_URL).to(WebAppUrlVerifier.class);
		verifierMapBinder.addBinding(AgentConfigProvider.TOAST_PLUGIN_DIR).to(PluginDirectoryVerifier.class);
		verifierMapBinder.addBinding(AgentConfigProvider.TOAST_SCRIPTS_DIR).to(ScriptsDirectoryVerifier.class);
		verifierMapBinder.addBinding(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL).to(WebRecordingVerifier.class);
		verifierMapBinder.addBinding(AgentConfigProvider.TOAST_DRIVER_SELECT).to(DriverPathVerifier.class);
		verifierMapBinder.addBinding(AgentConfigProvider.TOAST_MAIL_TO).to(MailVerifier.class);
	}

}
