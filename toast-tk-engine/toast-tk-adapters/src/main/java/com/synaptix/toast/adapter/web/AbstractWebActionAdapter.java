package com.synaptix.toast.adapter.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.google.inject.Inject;
import com.synaptix.toast.automation.api.SynchronizedDriver;
import com.synaptix.toast.automation.driver.selenium.DriverFactory;
import com.synaptix.toast.automation.driver.selenium.SeleniumSynchronizedDriver;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.adapter.ActionAdapterSentenceRef;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.annotation.ActionAdapter;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.core.runtime.IFeedableWebPage;
import com.synaptix.toast.core.runtime.IRepositorySetup;

@ActionAdapter(name="default-web-driver", value= ActionAdapterKind.web)
public class AbstractWebActionAdapter {

	private final SynchronizedDriver driver;
	private final IRepositorySetup repo;

	@Inject
	public AbstractWebActionAdapter(IRepositorySetup repo) {
		this.repo = repo;
		driver = new SeleniumSynchronizedDriver(DriverFactory.getFactory().getFirefoxDriver());
		for (IFeedableWebPage page : repo.getWebPages()) {
			((DefaultWebPage)page).setDriver(driver);
		}
	}

	@Action(action = "Close browser", description = "")
	public TestResult closeBrowser() {
		try {
			driver.getWebDriver().quit();
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = "Open browser at "+ ActionAdapterSentenceRef.VALUE_REGEX, description = "")
	public TestResult openBrowserIn(String url) {
		try {
			driver.getWebDriver().get(url);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = "Type "+ActionAdapterSentenceRef.VALUE_REGEX+" in {{component:web}}", description = "")
	public TestResult typeIn(String text, String pageName, String widgetName) throws Exception {
		try {
			WebElement pageField = getPageField(pageName, widgetName);
			pageField.sendKeys(text);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = "Type {{value:string}} in {{component:web}}", description = "")
	public TestResult typeVariableIn(String variableName, String pageName, String widgetName) throws Exception {
		String text = (String) repo.getUserVariables().get(variableName);
		return typeIn(text, pageName, widgetName);
	}

	@Action(action = "Suggest {{value:string}} in {{component:web}}", description = "")
	public TestResult typeInStraight(String text, String pageName, String widgetName) throws Exception {
		try {
			WebElement pageField = getPageField(pageName, widgetName);
			pageField.sendKeys(text);
			driver.getWebDriver().findElements(By.cssSelector("div.typeahead .item")).get(0).click();
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = "Click on {{component:web}}", description = "")
	public TestResult ClickOn(String pageName, String widgetName) throws Exception {
		try {
			WebElement pageField = getPageField(pageName, widgetName);
			pageField.click();
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = "Select {{value:string}} in {{component:web}}", description = "")
	public TestResult SelectAtPos(String pos, String pageName, String widgetName) throws Exception {
		try {
			WebAutoElement pageFieldAuto = getPageFieldAuto(pageName, widgetName);
			WebSelectElement pageField = (WebSelectElement) pageFieldAuto;
			pageField.selectByIndex(Integer.valueOf(pos));
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = "{{component:web}} exists", description = "")
	public TestResult checkExist(String pageName, String widgetName) {
		WebElement element = getPageField(pageName, widgetName);
		if (element != null) {
			if (element.isDisplayed()) {
				return new TestResult("Element is available !", ResultKind.SUCCESS);
			} else {
				return new TestResult("Element is not available !", ResultKind.FAILURE);
			}
		}
		return null;
	}

	private WebAutoElement getPageFieldAuto(String pageName, String widgetName) {
		DefaultWebPage page = (DefaultWebPage) repo.getPage(pageName);
		WebAutoElement autoElement = page.getAutoElement(widgetName);
		return autoElement;
	}

	private WebElement getPageField(String pageName, String fieldName) {
		DefaultWebPage page = (DefaultWebPage) repo.getPage(pageName);
		WebAutoElement autoElement = page.getAutoElement(fieldName);
		return autoElement.getWebElement();
	}
}
