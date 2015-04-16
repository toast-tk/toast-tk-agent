package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.util.concurrent.ThreadFactory;

import com.synaptix.toast.core.guice.ICustomFixtureHandler;

final class CustomActionThreadFactory implements ThreadFactory {

	private final ICustomFixtureHandler customFixtureHandler;

	CustomActionThreadFactory(final ICustomFixtureHandler customFixtureHandler) {
		this.customFixtureHandler = customFixtureHandler;
	}

	@Override
	public Thread newThread(final Runnable r) {
		return new Thread(r, "custom action " + customFixtureHandler.getClass().getName());
	}
}