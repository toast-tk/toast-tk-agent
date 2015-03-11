package com.synaptix.toast.fixture.web;

import org.openqa.selenium.WebElement;

import com.synaptix.toast.core.IWebElement;
import com.synaptix.toast.fixture.facade.ISyncCall;
import com.synaptix.toast.fixture.facade.SynchronizedDriver;

/**
 * Automation super class
 * 
 * @author skokaina
 * 
 */
public abstract class WebAutoElement {

	protected IWebElement wrappedElement;
	protected SynchronizedDriver frontEndDriver;

	protected void setWrappedElement(IWebElement wrappedElement) {
		this.wrappedElement = wrappedElement;
	}

	protected SynchronizedDriver getFrontEndDriver() {
		return frontEndDriver;
	}

	protected void setFrontEndDriver(SynchronizedDriver frontEndDriver) {
		this.frontEndDriver = frontEndDriver;
	}

	public WebAutoElement(IWebElement element, SynchronizedDriver driver) {
		this.wrappedElement = element;
		this.frontEndDriver = driver;
	}

	public WebAutoElement(IWebElement element) {
		this.wrappedElement = element;
	}

	public WebAutoElement() {

	}

	public boolean exists() {
		return frontEndDriver.find(wrappedElement) != null;
	}

	public WebElement getWebElement() {
		return frontEndDriver.find(wrappedElement);
	}

	protected void safeAction(ISyncCall res) {
		WebElement find = frontEndDriver.find(wrappedElement);
		if (find != null) {
			res.execute(find);
		}
	}

	public IWebElement getWrappedElement() {
		return wrappedElement;
	}
}
