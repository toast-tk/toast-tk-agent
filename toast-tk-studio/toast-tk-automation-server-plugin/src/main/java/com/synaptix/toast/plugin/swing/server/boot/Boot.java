package com.synaptix.toast.plugin.swing.server.boot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.inject.Module;
import com.synaptix.toast.adapter.swing.guice.SwingActionAdapterPluginModule;
import com.synaptix.toast.adapter.swing.handler.DefaultSwingCustomWidgetHandler;
import com.synaptix.toast.core.guice.plugin.ToastPluginBoot;
import com.synaptix.toast.plugin.swing.server.guice.SwingServerModule;

public class Boot implements ToastPluginBoot {

	private static List<Module> modules;

	@Override
	public void boot() {
		modules = new ArrayList<>();
		modules.add(new SwingServerModule());
//		modules.add(new SwingActionAdapterPluginModule());
	}

	@Override
	public List<Module> getModules() {
		return modules;
	}

	@Override
	public boolean isFrameworkInternalPlugin() {
		return true;
	}

	@Override
	public Set<Class<?>> getCustomActionAdapters() {
		HashSet<Class<?>> classes = new HashSet<>();
		classes.add(DefaultSwingCustomWidgetHandler.class);
		return classes;
	}

	public static void main(
		String[] args) {
		Boot b = new Boot();
		b.boot();
	}
}
