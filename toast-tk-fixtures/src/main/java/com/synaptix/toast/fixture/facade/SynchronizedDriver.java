package com.synaptix.toast.fixture.facade;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.synaptix.toast.core.IWebElement;


/**
 * 
 * @author skokaina
 * 
 */
// FIXME add abstraction to avoid talking directly to selenium
public interface SynchronizedDriver {

	/**
	 * 
	 * @param element
	 *            locator description
	 * @return
	 */
	public WebElement find(IWebElement element);

	/**
	 * selenium driver
	 * 
	 * @return
	 */
	public WebDriver getWebDriver();

}
