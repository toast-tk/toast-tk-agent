package com.synaptix.toast.adapter.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.synaptix.toast.automation.api.SynchronizedDriver;
import com.synaptix.toast.core.runtime.IWebElement;

/**
 * dual suggestion popup
 * 
 * @author skokaina
 * 
 */
public class WebDualSuggestElement extends WebAutoElement {
	Map<String, WebElement> suggestions;
	public static final String itemCss = "div.typeahead-dual td[class*='item']";

	public WebDualSuggestElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebDualSuggestElement(IWebElement element) {
		super(element);
	}

	public List<String> getSuggestions() {
		WebElement webElement = getWebElement();
		suggestions = new HashMap<String, WebElement>();
		List<String> list = new ArrayList<String>();
		if (webElement != null) {
			List<WebElement> findElement = getFrontEndDriver().getWebDriver().findElements(By.cssSelector(itemCss));
			if (findElement != null && findElement.size() > 0) {
				for (WebElement el : findElement) {
					suggestions.put(el.getText(), el);
				}
			}
		}
		list.addAll(suggestions.keySet());
		return list;
	}

	public void selectSuggestion(int index) {
		if (index > 0) {
			List<String> sug = getSuggestions();
			if (index < sug.size()) {
				String key = sug.get(index);
				suggestions.get(key).click();
			}
		}
	}

	public int hasHowManySuggestionForKey(String key) {
		int resultCount = 0;
		WebElement webElement = getWebElement();
		if (webElement != null) {
			webElement.sendKeys(key);
			List<String> sug = getSuggestions();
			if (sug != null && sug.size() > 0) {
				resultCount = sug.size() - 2; // -2 for both sections title
			}
		}
		return resultCount;
	}

}
