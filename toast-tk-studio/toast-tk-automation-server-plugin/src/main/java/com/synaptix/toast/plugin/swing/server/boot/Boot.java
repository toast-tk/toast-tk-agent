package com.synaptix.toast.plugin.swing.server.boot;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Module;
import com.synaptix.toast.adapter.swing.guice.SwingActionAdapterPluginModule;
import com.synaptix.toast.core.guice.plugin.ToastPluginBoot;
import com.synaptix.toast.plugin.swing.server.guice.SwingServerModule;

public class Boot implements ToastPluginBoot {

	private static List<Module> modules;
	
	@Override
	public void boot() {
		modules = new ArrayList<Module>();
		modules.add(new SwingServerModule());
		modules.add(new SwingActionAdapterPluginModule());
	}

	@Override
	public List<Module> getModules() {
		return modules;
	}

	public static void main(String[] args) {
		Boot b = new Boot();
		b.boot();
	}
}
