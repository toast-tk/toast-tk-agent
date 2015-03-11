package com.synaptix.toast.fixture.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.synaptix.toast.core.IWebElement;
import com.synaptix.toast.fixture.facade.HasClickAction;
import com.synaptix.toast.fixture.facade.ISyncCall;
import com.synaptix.toast.fixture.facade.SynchronizedDriver;

/**
 * link element
 * 
 * @author skokaina
 * 
 */
public class WebLinkElement extends WebAutoElement implements HasClickAction {

	public WebLinkElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebLinkElement(IWebElement element) {
		super(element);
	}

	@Override
	public boolean click() {
		safeAction(new ISyncCall() {
			@Override
			public void execute(WebElement e) {
				e.click();
			}
		});
		return true;
	}

	@Override
	public void dbClick() {
		safeAction(new ISyncCall() {
			@Override
			public void execute(WebElement e) {
				Actions action = new Actions(frontEndDriver.getWebDriver());
				action.doubleClick(e);
				action.perform();
				e.click();
			}
		});
	}

}
