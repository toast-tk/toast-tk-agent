package com.synpatix.toast.runtime.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.core.IRepositorySetup;
import com.synpatix.toast.runtime.core.runtime.DefaultRepositoryTypeHandler;
import com.synpatix.toast.runtime.core.runtime.RedPepperBackendFixture;
import com.synpatix.toast.runtime.core.runtime.RepositorySetup;

public class BackendModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IRepositorySetup.class).to(RepositorySetup.class).in(Singleton.class);
		bind(RedPepperBackendFixture.class).in(Singleton.class);
		install(new DefaultRepositoryTypeHandler());
	}

}
