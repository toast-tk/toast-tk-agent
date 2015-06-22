package com.synaptix.toast.adapter.web;

import java.util.List;

import org.openqa.selenium.WebElement;

import com.synaptix.toast.automation.api.SynchronizedDriver;
import com.synaptix.toast.core.runtime.IWebElement;

/**
 * auto-complete element
 * 
 * @author skokaina
 * 
 */
public class WebSuggestElement extends WebAutoElement {

	List<WebElement> suggestions;

	public WebSuggestElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebSuggestElement(IWebElement element) {
		super(element);
	}

	public List<String> getSuggestions() {
		return null;
	}

	public void selectSuggestion(int index) {

	}

	public boolean hasSuggestionForKey(String key) {
		return false;
	}

}
