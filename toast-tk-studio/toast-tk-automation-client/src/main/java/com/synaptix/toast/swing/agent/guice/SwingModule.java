package com.synaptix.toast.swing.agent.guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synaptix.toast.runtime.AbstractTestManagerImpl;
import com.synaptix.toast.swing.agent.IStudioApplication;
import com.synaptix.toast.swing.agent.StudioApplicationImpl;
import com.synaptix.toast.swing.agent.config.Config;
import com.synaptix.toast.swing.agent.config.ConfigProvider;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;
import com.synaptix.toast.swing.agent.runtime.StudioRemoteSwingAgentDriverImpl;
import com.synaptix.toast.swing.agent.ui.SwingAgentScriptRunnerPanel;
import com.synaptix.toast.swing.agent.ui.SwingInspectionFrame;
import com.synaptix.toast.swing.agent.ui.SwingInspectorPanel;
import com.synaptix.toast.swing.agent.ui.record.SwingInspectionRecorderPanel;

public class SwingModule extends AbstractModule {

	@Override
	protected void configure() {
		bindConstant().annotatedWith(Names.named("host")).to("localhost");
		bind(IStudioApplication.class).to(StudioApplicationImpl.class).asEagerSingleton();
		bind(SwingInspectionFrame.class).asEagerSingleton();
		bind(Config.class).toProvider(ConfigProvider.class).in(Singleton.class);
		bind(SwingAgentScriptRunnerPanel.class).in(Singleton.class);
		bind(SwingInspectorPanel.class).in(Singleton.class);
		bind(SwingInspectionRecorderPanel.class).in(Singleton.class);
		bind(ISwingAutomationClient.class).to(StudioRemoteSwingAgentDriverImpl.class).in(Singleton.class);
		bind(MongoRepositoryCacheWrapper.class).in(Singleton.class);
		bind(EventBus.class).in(Singleton.class);
		bind(ITestManager.class).to(NotAbstractTestManagerImpl.class);
	}

	public static class NotAbstractTestManagerImpl extends AbstractTestManagerImpl {

		@Override
		public <T> T getClassInstance(
			Class<T> serviceClass) {
			return null;
		}
	}
}
