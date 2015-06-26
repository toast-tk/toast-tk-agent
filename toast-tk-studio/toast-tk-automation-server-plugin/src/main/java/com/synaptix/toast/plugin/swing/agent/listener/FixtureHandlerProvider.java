package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javassist.NotFoundException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.synaptix.toast.core.guice.ICustomFixtureHandler;
import com.synaptix.toast.core.net.request.CommandRequest;

@Singleton
public class FixtureHandlerProvider {

	private static final Logger LOG = LogManager.getLogger(FixtureHandlerProvider.class);
	
	private final Set<ICustomFixtureHandler> fixtureHandlers;

	@Inject
	public FixtureHandlerProvider(Set<ICustomFixtureHandler> fixtureHandlers) throws IllegalAccessException {
		if (fixtureHandlers == null || fixtureHandlers.size() == 0) {
			throw new IllegalAccessException("No Fixture Handler available !");
		}
		this.fixtureHandlers = fixtureHandlers;
	}

	public String processCustomCall(final CommandRequest request) throws NotFoundException {
		ICustomFixtureHandler handlerInterestedIn = getHandlerInterestedIn(request);
		if (handlerInterestedIn == null) {
			throw new NotFoundException("No Fixture Handler found for request id: " + request.getId());
		}
		LOG.info("found CustomFixtureHandler : {} ", handlerInterestedIn.getName());
		return handlerInterestedIn.processCustomCall(request);
	}

	public String processFixtureCall(Component target, CommandRequest request) {
		ICustomFixtureHandler handlerInterestedIn = getHandlerInterestedIn(request);
		String response = null;
		if (handlerInterestedIn != null) {
			response = handlerInterestedIn.hanldeFixtureCall(target, request);
		}
		else {
			LOG.info("No CustomFixtureHandler finded");
		}
		return response;
	}

	private ICustomFixtureHandler getHandlerInterestedIn(final CommandRequest request) {
		final List<ICustomFixtureHandler> res = new ArrayList<ICustomFixtureHandler>();
		for(final ICustomFixtureHandler handler : fixtureHandlers) {
			LOG.info("searching CustomFixtureHandler : {} ", handler.getName());
			final List<String> commandRequestWhiteList = handler.getCommandRequestWhiteList();
			if(commandRequestWhiteList == null || commandRequestWhiteList.isEmpty()) {
				LOG.warn("{} - Handler has no command request white list defined: ", handler.getName());
			} 
			else if(findRequestClass(commandRequestWhiteList, request)) {
				LOG.info("adding CustomFixtureHandler : {} ", handler.getName());
				res.add(handler);
			}
		}
		LOG.info("reflection ");
		final String reflectionToString = ToStringBuilder.reflectionToString(request, ToStringStyle.SIMPLE_STYLE);
		LOG.info("reflection done {}", reflectionToString);
		if (res.size() == 0) {
			LOG.warn("No Fixture Handler is interested in request: {}", reflectionToString);
			return null;
		}
		if (res.size() > 1) {
			LOG.warn("More than one Handler is interested in request: {}", reflectionToString);
		}
		ICustomFixtureHandler iCustomFixtureHandler = res.get(0);
		LOG.info("Handler {} will process request: {}", iCustomFixtureHandler.getName(), reflectionToString);
		return iCustomFixtureHandler;
	}

	private static boolean findRequestClass(
			final List<String> commandRequestWhiteList, 
			final CommandRequest request
	) {
		final boolean containsClassName = commandRequestWhiteList.contains(request.getClass().getName());
		if(containsClassName) {
			return true;
		}
		final String itemType = request.itemType;
		for(final String requestWhite : commandRequestWhiteList) {
			if(requestWhite.equals(itemType)) {
				return true;
			}
		}
		return false;
	}
	
	private ICustomFixtureHandler getHandlerInterestedIn(Component component) {
		final List<ICustomFixtureHandler> res = new ArrayList<ICustomFixtureHandler>();
		for (ICustomFixtureHandler handler : fixtureHandlers) {
			if (handler.isInterestedIn(component)) {
				res.add(handler);
			}
		}
		final String reflectionToString = ToStringBuilder.reflectionToString(component, ToStringStyle.SIMPLE_STYLE);
		if (res.size() == 0) {
			LOG.warn("No Fixture Handler is interested in component: {}", reflectionToString);
			return null;
		}
		if (res.size() > 1) {
			LOG.warn("More than one Handler is interested in component: {}", reflectionToString);
		}
		ICustomFixtureHandler iCustomFixtureHandler = res.get(0);
		LOG.info("Handler {} will process component: {}", iCustomFixtureHandler.getName(), reflectionToString);
		return iCustomFixtureHandler;
	}

	public Component locateComponentTarget(String item, String itemType, Component component) {
		ICustomFixtureHandler handlerInterestedIn = getHandlerInterestedIn(component);
		Component target = null;
		if (handlerInterestedIn != null) {
			target = handlerInterestedIn.locateComponentTarget(item, itemType, component);
		}
		return target;
	}
}
