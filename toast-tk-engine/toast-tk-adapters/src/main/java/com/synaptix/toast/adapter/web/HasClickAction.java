package com.synaptix.toast.adapter.web;

import java.util.concurrent.TimeoutException;

/**
 * 
 * @author skokaina
 * 
 */
public interface HasClickAction {

	/**
	 * represent a click action
	 * @throws TimeoutException 
	 */
	public boolean click() throws TimeoutException;

	/**
	 * represents a double click action
	 */
	public void dbClick();

}
