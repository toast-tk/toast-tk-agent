
package com.synaptix.toast.test.server;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.synaptix.toast.plugin.swing.server.boot.Boot;

public class TestSuiteHelper {

	static Injector injector;

	public static void initInjector() {
		Boot b = new Boot();
		b.boot();
		injector = Guice.createInjector(b.getModules());
	}
	
	public static void initInjector(List<Module> modules) {
		injector = Guice.createInjector(modules);
	}
	
	public static Injector getInjector() {
		if(injector == null){
			initInjector();
		}
		return injector;
	}
}
