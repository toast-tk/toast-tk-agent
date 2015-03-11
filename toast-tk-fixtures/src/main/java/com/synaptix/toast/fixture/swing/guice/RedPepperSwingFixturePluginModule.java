package com.synaptix.toast.fixture.swing.guice;

import com.synaptix.toast.core.guice.AbstractComponentFixtureModule;

public class RedPepperSwingFixturePluginModule extends AbstractComponentFixtureModule {

	@Override
	protected void configureModule() {
		addTypeHandler(RedPepperSwingWidgetHandler.class);
	}

}
