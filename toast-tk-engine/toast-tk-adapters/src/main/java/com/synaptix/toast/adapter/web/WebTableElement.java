package com.synaptix.toast.adapter.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.synaptix.toast.automation.api.SynchronizedDriver;
import com.synaptix.toast.automation.driver.selenium.SeleniumHelper;
import com.synaptix.toast.core.runtime.IWebElement;

/**
 * gwt bootstrap table element
 * 
 * @author skokaina
 * 
 */
public class WebTableElement extends WebAutoElement implements ITableElement {

	List<String> cols;
	List<WebElement> rows;

	static String rowSelector = "tbody[style=''] tr"; // specific to psc
	static String colHeadhSelector = "thead th";// specific to psc

	public WebTableElement(IWebElement element, SynchronizedDriver driver) {
		super(element, driver);
	}

	public WebTableElement(IWebElement element) {
		super(element);
	}

	@Override
	public void dbClickAtRow(final int index) {
		safeAction(new ISyncCall() {
			@Override
			public void execute(WebElement find) {
				List<WebElement> findElements = find.findElements(By.cssSelector(rowSelector));
				if (index > 0 && index < findElements.size()) {
					// Actions action = new Actions(frontEndDriver.getWebDriver());
					WebElement onElement = findElements.get(index);
					// Action doubleClick = action.doubleClick(onElement).build();
					// doubleClick.perform();
					onElement.click();
					SeleniumHelper.wait(1000);
					onElement.click();
				}
			}
		});
	}

	@Override
	public int getNbRows() {
		if (rows == null) {
			WebElement webElement = getWebElement();
			if (webElement != null) {
				List<WebElement> findElements = webElement.findElements(By.cssSelector(rowSelector));
				if (findElements != null) {
					rows = findElements;
				}
			}
		}
		return rows == null ? -1 : rows.size();
	}

	@Override
	public List<String> getColumns() {
		if (cols == null) {
			WebElement webElement = getWebElement();
			if (webElement != null) {
				List<WebElement> colElements = webElement.findElements(By.cssSelector(colHeadhSelector));
				if (colElements != null && colElements.size() > 0) {
					cols = new ArrayList<String>();
					for (WebElement e : colElements) {
						cols.add(e.getText());
					}
				}
			}
		}
		return cols;
	}

	@Override
	public String getValue(String columnName, int indexRow) {
		String val = null;
		if (cols == null) {
			getColumns();
		}
		if (cols.contains(columnName)) {
			int i = cols.indexOf(columnName);
			if (indexRow > 0 && indexRow < getNbRows()) {
				WebElement row = rows.get(indexRow);
				List<WebElement> colElements = row.findElements(By.cssSelector("td"));
				val = colElements.get(i).getText();
			}
		}
		return val;
	}

	@Override
	public String getValue(int indexCol, int indexRow) {
		String val = null;
		if (cols == null) {
			getColumns();
		}
		if (indexRow > 0 && indexRow < getNbRows()) {
			WebElement row = rows.get(indexRow);
			List<WebElement> colElements = row.findElements(By.cssSelector("td"));
			val = colElements.get(indexCol).getText();
		}
		return val;
	}

	@Override
	public boolean containsText(String text) {
		return false;
	}

}
