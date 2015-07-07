package com.synaptix.toast.runtime.core.runtime;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Created by Sallah Kokaina on 24/11/2014.
 */
public abstract class AbstractTestManager {

	private Injector injectorServer;

	protected AbstractTestManager() {
		super();
		try {
			injectorServer = Guice.createInjector(getGuiceModule());
		}
		catch(Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Injector getInjectorServer() {
		return injectorServer;
	}

	protected abstract AbstractModule getGuiceModule();
}
