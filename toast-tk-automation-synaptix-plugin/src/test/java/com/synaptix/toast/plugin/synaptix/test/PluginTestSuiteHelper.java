package com.synaptix.toast.plugin.synaptix.test;


public class PluginTestSuiteHelper {
	
	//static Injector injector;

	/*public static void initInjector() {
		final List<Module> modules = new ArrayList<Module>();
		modules.add(new PluginTestBootModule());
		injector = Guice.createInjector(modules);
	}*/
	
	/*public static Injector getInjector() {
		if(injector == null){
			initInjector();
		}
		return injector;
		return null;
	}*/
	
	public static void sleepInSeconds(final int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}