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
import com.synaptix.toast.core.guice.ICustomRequestHandler;
import com.synaptix.toast.core.net.request.CommandRequest;

@Singleton
public class FixtureHandlerProvider {

	private static final Logger LOG = LogManager.getLogger(FixtureHandlerProvider.class);

	private final Set<ICustomRequestHandler> fixtureHandlers;

	@Inject
	public FixtureHandlerProvider(
		Set<ICustomRequestHandler> fixtureHandlers)
		throws IllegalAccessException {
		if(fixtureHandlers == null || fixtureHandlers.size() == 0) {
			throw new IllegalAccessException("No Fixture Handler available !");
		}
		this.fixtureHandlers = fixtureHandlers;
	}

	public String processCustomCall(
		final CommandRequest request)
		throws NotFoundException {
		ICustomRequestHandler handlerInterestedIn = getHandlerInterestedIn(request);
		if(handlerInterestedIn == null) {
			throw new NotFoundException("No Fixture Handler found for request id: " + request.getId());
		}
		LOG.info("found CustomFixtureHandler : {} ", handlerInterestedIn.getName());
		return handlerInterestedIn.processCustomCall(request);
	}

	public String processFixtureCall(
		Component target,
		CommandRequest request) {
		ICustomRequestHandler handlerInterestedIn = getHandlerInterestedIn(request);
		String response = null;
		if(handlerInterestedIn != null) {
			response = handlerInterestedIn.hanldeFixtureCall(target, request);
		}
		else {
			LOG.info("No CustomFixtureHandler finded");
		}
		return response;
	}

	private List<ICustomRequestHandler> collectCustomFixtureRequestHanlders(
		final CommandRequest request) {
		final List<ICustomRequestHandler> res = new ArrayList<ICustomRequestHandler>();
		for(final ICustomRequestHandler handler : fixtureHandlers) {
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
		return res;
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

	private ICustomRequestHandler getHandlerInterestedIn(
		final CommandRequest request) {
		final List<ICustomRequestHandler> res = collectCustomFixtureRequestHanlders(request);
		final String reflectionToString = ToStringBuilder.reflectionToString(request, ToStringStyle.SIMPLE_STYLE);
		if(res.size() == 0) {
			LOG.warn("No Fixture Handler is interested in request: {}", reflectionToString);
			return null;
		}
		if(res.size() > 1) {
			LOG.warn("More than one Handler is interested in request: {}", reflectionToString);
		}
		ICustomRequestHandler customFixtureHandler = res.get(0);
		LOG.info("Handler {} will process request: {}", customFixtureHandler.getName(), reflectionToString);
		return customFixtureHandler;
	}

	private ICustomRequestHandler getHandlerInterestedIn(
		Component component) {
		final List<ICustomRequestHandler> res = new ArrayList<ICustomRequestHandler>();
		for(ICustomRequestHandler handler : fixtureHandlers) {
			if(handler.isInterestedIn(component)) {
				res.add(handler);
			}
		}
		final String reflectionToString = ToStringBuilder.reflectionToString(component, ToStringStyle.SIMPLE_STYLE);
		if(res.size() == 0) {
			LOG.warn("No Fixture Handler is interested in component: {}", reflectionToString);
			return null;
		}
		if(res.size() > 1) {
			LOG.warn("More than one Handler is interested in component: {}", reflectionToString);
		}
		ICustomRequestHandler customFixtureHandler = res.get(0);
		LOG.info("Handler {} will process component: {}", customFixtureHandler.getName(), reflectionToString);
		return customFixtureHandler;
	}

	public Component locateComponentTarget(
		String item,
		String itemType,
		Component component) {
		ICustomRequestHandler handlerInterestedIn = getHandlerInterestedIn(component);
		Component target = null;
		if(handlerInterestedIn != null) {
			target = handlerInterestedIn.locateComponentTarget(item, itemType, component);
		}
		return target;
	}
}
