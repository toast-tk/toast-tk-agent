/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 5 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */

package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.guice.ICustomFixtureHandler;

@Singleton
public class FixtureHandlerProvider {

	private static final Logger LOG = LogManager.getLogger(FixtureHandlerProvider.class);
	
	private final Set<ICustomFixtureHandler> fixtureHandlers;

	@Inject
	public FixtureHandlerProvider(Set<ICustomFixtureHandler> fixtureHandlers) {
		assert (this.fixtureHandlers != null && fixtureHandlers.size() > 0) : "No Fixture Handler available !";
		this.fixtureHandlers = fixtureHandlers;
	}

	public void processCustomCall(final CommandRequest request) {
		ICustomFixtureHandler handlerInterestedIn = getHandlerInterestedIn(request);
		if (handlerInterestedIn != null) {
			handlerInterestedIn.processCustomCall(request);
		}
	}

	public String processFixtureCall(Component target, CommandRequest request) {
		ICustomFixtureHandler handlerInterestedIn = getHandlerInterestedIn(request);
		String response = null;
		if (handlerInterestedIn != null) {
			response = handlerInterestedIn.hanldeFixtureCall(target, request);
		}
		return response;
	}

	private ICustomFixtureHandler getHandlerInterestedIn(final CommandRequest request) {
		final List<ICustomFixtureHandler> res = new ArrayList<ICustomFixtureHandler>();
		for (ICustomFixtureHandler handler : fixtureHandlers) {
			List<Class<? extends CommandRequest>> commandRequestWhiteList = handler.getCommandRequestWhiteList();
			if (commandRequestWhiteList == null) {
				LOG.warn(String.format("%s - Handler has no command request white list defined: " + handler.getName()));
			} else if (commandRequestWhiteList.contains(request.getClass())) {
				res.add(handler);
			}
		}
		final String reflectionToString = ToStringBuilder.reflectionToString(request, ToStringStyle.SIMPLE_STYLE);
		if (res.size() == 0) {
			LOG.warn("No Fixture Handler is interested in request: " + reflectionToString);
			return null;
		}
		if (res.size() > 1) {
			LOG.warn("More than one Handler is interested in request: " + reflectionToString);
		}
		ICustomFixtureHandler iCustomFixtureHandler = res.get(0);
		LOG.info(String.format("Handler %s will process request: %s", iCustomFixtureHandler.getName(), reflectionToString));
		return iCustomFixtureHandler;
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
			LOG.warn("No Fixture Handler is interested in component: " + reflectionToString);
			return null;
		}
		if (res.size() > 1) {
			LOG.warn("More than one Handler is interested in component: " + reflectionToString);
		}
		ICustomFixtureHandler iCustomFixtureHandler = res.get(0);
		LOG.info(String.format("Handler %s will process component: %s", iCustomFixtureHandler.getName(), reflectionToString));
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
