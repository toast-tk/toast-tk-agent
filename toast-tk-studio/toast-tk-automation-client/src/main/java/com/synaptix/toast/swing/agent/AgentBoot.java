package com.synaptix.toast.swing.agent;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.synaptix.toast.swing.agent.guice.SwingModule;
import com.synaptix.toast.swing.agent.runtime.DefaultSwingActionAdapter;

import io.toast.tk.adapter.swing.guice.SwingActionAdapterPluginModule;
import io.toast.tk.automation.driver.swing.RemoteSwingAgentDriverImpl;
import io.toast.tk.core.guice.AbstractActionAdapterModule;
import io.toast.tk.runtime.module.EngineModule;

public class AgentBoot {

	public static Injector injector;

	public static final void boot() {
		final List<Module> modules = new ArrayList<Module>();
		modules.add(new AbstractActionAdapterModule() {
			@Override
			protected void configure() {
				bind(RemoteSwingAgentDriverImpl.class).in(Singleton.class);
				bindActionAdapter(DefaultSwingActionAdapter.class);
			}
		});
		modules.add(new SwingModule());
		modules.add(new EngineModule());
		modules.add(new SwingActionAdapterPluginModule());
		injector = Guice.createInjector(modules);
	}

	public static void main(
		String[] args) {
		boot();
	}
}
