package com.synaptix.toast.fixture.web;

import com.synaptix.toast.core.IWebElement;
import com.synaptix.toast.fixture.facade.HasTextInput;
import com.synaptix.toast.fixture.facade.SynchronizedDriver;

/**
 * input element
 * 
 * @author skokaina
 * 
 */
public class WebInputElement extends WebAutoElement implements HasTextInput {

	public WebInputElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebInputElement(IWebElement element) {
		super(element);
	}

	@Override
	public void setInput(String e) {
		frontEndDriver.find(wrappedElement).sendKeys(e);
	}

	@Override
	public String getValue() {
		return frontEndDriver.find(wrappedElement).getText();
	}
}
