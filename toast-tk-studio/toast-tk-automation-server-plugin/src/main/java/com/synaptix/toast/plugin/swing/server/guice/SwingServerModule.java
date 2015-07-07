package com.synaptix.toast.plugin.swing.server.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.core.agent.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.record.IEventRecorder;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.FixtureHandlerProvider;
import com.synaptix.toast.plugin.swing.agent.listener.ISynchronizationPoint;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.RepositoryHolder;
import com.synaptix.toast.plugin.swing.agent.listener.SynchronizationPointImpl;
import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.plugin.swing.server.SwingInspectionServer;

public class SwingServerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(RepositoryHolder.class).in(Singleton.class);
		bind(ISynchronizationPoint.class).to(SynchronizationPointImpl.class).in(Singleton.class);
		bind(ISwingInspectionServer.class).to(SwingInspectionServer.class).asEagerSingleton();
		bind(SwingActionRequestListener.class).in(Singleton.class);
		bind(InitRequestListener.class).in(Singleton.class);
		bind(IEventRecorder.class).to(SwingInspectionRecorder.class).in(Singleton.class);
		bind(FixtureHandlerProvider.class).in(Singleton.class);
	}
}
