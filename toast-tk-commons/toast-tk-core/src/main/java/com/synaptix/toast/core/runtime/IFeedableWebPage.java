package com.synaptix.toast.core.runtime;

/**
 * 
 * @author skokaina
 * 
 */
public interface IFeedableWebPage {

	/**
	 * provide a locator object to init the web element
	 * 
	 * @param locator
	 */
	public void initElement(
		IWebElement locator);
}
