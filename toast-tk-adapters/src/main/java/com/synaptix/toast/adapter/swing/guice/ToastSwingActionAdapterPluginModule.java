package com.synaptix.toast.adapter.swing.guice;

import com.synaptix.toast.core.guice.AbstractComponentFixtureModule;

public class ToastSwingActionAdapterPluginModule extends AbstractComponentFixtureModule {

	@Override
	protected void configureModule() {
		addTypeHandler(DefaultSwingCustomWidgetHandler.class);
	}

}
