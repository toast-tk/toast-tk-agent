package com.synpatix.toast.runtime.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

public abstract class AbstractRepositoryTypeParserModule extends AbstractModule {
	Multibinder<IRepositoryTypeParser> uriBinder;

	@Override
	protected void configure() {
		uriBinder = Multibinder.newSetBinder(binder(), IRepositoryTypeParser.class);
		configureModule();
	}

	protected abstract void configureModule();

	protected final void addTypeHandler(Class<? extends IRepositoryTypeParser> typeHandlerClass) {
		bind(typeHandlerClass).in(Singleton.class);
		uriBinder.addBinding().to(typeHandlerClass);
	}

}
