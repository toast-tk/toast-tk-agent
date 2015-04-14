package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.awt.Component;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.core.guice.ICustomFixtureHandler;

public abstract class AbstractCustomFixtureHandler implements ICustomFixtureHandler {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractCustomFixtureHandler.class);

	protected abstract String makeHandleFixtureCall(Component component, IIdRequest request);

	private final Executor exec;

	public AbstractCustomFixtureHandler() {
		final ICustomFixtureHandler customFixtureHandler = this;
		final ThreadFactory threadFactory = new CustomActionThreadFactory(customFixtureHandler);
		this.exec = Executors.newFixedThreadPool(1, threadFactory);
	}

	@Override
	public String hanldeFixtureCall(final Component component, final IIdRequest request) {
		try {
			return makeHandleFixtureCall(component, request);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Component locateComponentTarget(
			final String item,
			final String itemType,
			final Component value
	) {
		return null;
	}

	protected void runAction(final Runnable action) {
		exec.execute(action);
	}
}