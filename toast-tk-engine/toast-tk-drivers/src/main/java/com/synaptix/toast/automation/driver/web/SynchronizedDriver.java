package com.synaptix.toast.automation.driver.web;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.core.runtime.IWebElement;

@FixMe(todo="add abstraction to avoid talking directly to selenium")
public interface SynchronizedDriver {

	/**
	 * 
	 * @param element
	 *            locator description
	 * @return
	 */
	public WebElement find(
		IWebElement element);

	/**
	 * selenium driver
	 * 
	 * @return
	 */
	public WebDriver getWebDriver();
}
