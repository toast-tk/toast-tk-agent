package com.synaptix.toast.adapter.web;

import java.util.concurrent.TimeoutException;

import com.synaptix.toast.core.runtime.ErrorResultReceivedException;

/**
 * 
 * @author skokaina
 * 
 */
public interface HasClickAction {

	/**
	 * represent a click action
	 * @throws TimeoutException 
	 * @throws ErrorResultReceivedException 
	 */
	public boolean click() throws TimeoutException, ErrorResultReceivedException;

	/**
	 * represents a double click action
	 */
	public void dbClick();

}
