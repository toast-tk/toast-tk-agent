package com.synaptix.toast.core.guice;

import java.awt.Component;
import java.util.List;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;

/**
 * Interface to implement within toast plugins in order to handle custom swing ui components
 * 
 * @author Sallah Kokaina <sallah.kokaina@gmail.com>
 *
 */
public interface ICustomFixtureHandler {
	
	/**
	 * Method called to perform a given request (command) on the target Component
	 * 
	 * @param target Component
	 * @param request to indicate which action we need to perform on the target Component
	 * @return any response
	 */
	public String hanldeFixtureCall(Component target, IIdRequest request);

	/**
	 * Locate an element, the method should return the component (value) if it happens that it
	 * is a component the fixture handler would like to process afterward through the {@link ICustomFixtureHandler#hanldeFixtureCall(Component, IIdRequest)}
	 * @param item
	 * @param itemType
	 * @param value
	 * @return
	 */
	public Component locateComponentTarget(String item, String itemType, Component value);

	/**
	 * Handle a custom request not covered by the framework standard commands
	 * At this point, the plugin is expected to know how to handle this Custom Command Request
	 * 
	 * @param command -> Custom Command Request
	 */
	public String processCustomCall(CommandRequest command);
	
	
	/**
	 * Check the handler is interested in handling the command request
	 * 
	 * @param command
	 * @return
	 */
	public List<Class<? extends CommandRequest>> getCommandRequestWhiteList();

	/**
	 * A unique name
	 * 
	 * @return
	 */
	public String getName();


	/**
	 * Check the handler is interested in handling the Swing component
	 * 
	 * @param component
	 * @return
	 */
	public boolean isInterestedIn(Component component);
}
