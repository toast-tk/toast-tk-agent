package com.synaptix.toast.core.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public abstract class AbstractFixtureModule extends AbstractModule {

	protected final void bindFixture(Class<?> fixtureClass) {
		bind(fixtureClass).in(Singleton.class);
	}
}
