package com.synaptix.toast.adapter.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;

import com.synaptix.toast.automation.api.SynchronizedDriver;
import com.synaptix.toast.core.runtime.IWebElement;

/**
 * button element
 * 
 * @author skokaina
 * 
 */
public class WebButtonElement extends WebAutoElement implements HasClickAction {

	public WebButtonElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebButtonElement(IWebElement element) {
		super(element);
	}

	@Override
	public boolean click() {
		WebElement find = frontEndDriver.find(wrappedElement);
		find.click();
		return true;
	}

	@Override
	public void dbClick() {
		Actions action = new Actions(frontEndDriver.getWebDriver());
		WebElement find = frontEndDriver.find(wrappedElement);
		Action doubleClick = action.doubleClick(find).build();
		doubleClick.perform();
	}

}
