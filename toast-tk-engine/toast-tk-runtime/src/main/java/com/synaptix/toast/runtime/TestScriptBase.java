package com.synaptix.toast.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import com.synaptix.toast.adapter.web.AbstractSynaptixWebPage;
import com.synaptix.toast.adapter.web.WebAutoElement;
import com.synaptix.toast.automation.api.SynchronizedDriver;
import com.synaptix.toast.automation.driver.selenium.SeleniumHelper;
import com.synaptix.toast.automation.report.IReporter;
import com.synaptix.toast.automation.report.WebActionResult;
import com.synaptix.toast.automation.report.WebTestResult;
import com.synaptix.toast.automation.repository.PageProvider;
import com.synaptix.toast.automation.repository.WebRepository;
import com.synaptix.toast.core.runtime.IWebElement;

/**
 * base abstract class for test scripts
 * 
 * @author skokaina
 * 
 * @param <D>
 * @param <R>
 */
public abstract class TestScriptBase<D extends SynchronizedDriver, R extends WebRepository<?>> {

	R repo;

	D driver;

	private IReporter reporter;

	public abstract void run();

	@SuppressWarnings("unchecked")
	public void init()
		throws InstantiationException, IllegalAccessException {
		Class<D> driverClass = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0];
		Class<R> repoClass = (Class<R>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[1];
		repo = repoClass.newInstance();
		driver = driverClass.newInstance();
		for(Field f : getClass().getDeclaredFields()) {
			f.setAccessible(true);
			Class<?> pageClass = f.getType();
			if(AbstractSynaptixWebPage.class.isAssignableFrom(pageClass)) {
				AbstractSynaptixWebPage webPage = PageProvider.in(repo).getWebPage(
					(Class<AbstractSynaptixWebPage>) pageClass);
				f.set(this, webPage);
				System.out.println("Setting field: " + f.getName() + " => " + f);
				webPage.setDriver(driver);
			}
			else if(SeleniumTestScriptBase.class.isAssignableFrom(pageClass)) {
				SeleniumTestScriptBase<D, R> classBlock = ((Class<SeleniumTestScriptBase<D, R>>) pageClass)
					.newInstance();
				classBlock.init();
				f.set(this, classBlock);
			}
		}
	}

	public void setReporter(
		IReporter reporter) {
		this.reporter = reporter;
	}

	public void end() {
		if(driver != null) {
			driver.getWebDriver().quit();
			driver = null;
			reportAction(new WebActionResult("Closing Driver", null));
		}
		repo = null;
	}

	public void navigateTo(
		String url) {
		if(driver != null) {
			driver.getWebDriver().get(url);
			reportAction(new WebActionResult("Navigating To", url));
		}
	}

	public void wait(
		int sec) {
		reportAction(new WebActionResult("Waiting (seconds)", String.valueOf(sec)));
		SeleniumHelper.wait(sec * 1000);
	}

	private void reportAction(
		WebActionResult action) {
		if(reporter != null) {
			reporter.reportAction(action);
		}
	}

	private void reportResult(
		WebTestResult<?> result) {
		if(reporter != null) {
			reporter.reportResult(result);
		}
	}

	public <E> void assertEquals(
		String title,
		E expected,
		E current) {
		WebTestResult<E> result = new WebTestResult<E>();
		result.setTitle(title);
		result.setExpected(expected);
		result.setCurrent(current);
		if(expected == null && current == null) {
			result.setSuccess(true);
		}
		else if(expected == null && current != null) {
			result.setSuccess(false);
		}
		else if(expected != null && current == null) {
			result.setSuccess(false);
		}
		else {
			result.setSuccess(expected.equals(current));
		}
		reportResult(result);
	}

	public <E> void assertNotEquals(
		String title,
		E expected,
		E current) {
		WebTestResult<E> result = new WebTestResult<E>();
		result.setTitle(title);
		result.setExpected(expected);
		result.setCurrent(current);
		if(expected == null && current == null) {
			result.setSuccess(false);
		}
		else if(expected == null && current != null) {
			result.setSuccess(true);
		}
		else if(expected != null && current == null) {
			result.setSuccess(true);
		}
		else {
			result.setSuccess(!expected.equals(current));
		}
		reportResult(result);
	}

	public <E extends Double> void assertSuperior(
		String title,
		E expected,
		E current) {
		WebTestResult<E> result = new WebTestResult<E>();
		result.setTitle(title);
		result.setExpected(expected);
		result.setCurrent(current);
		if(expected == null || current == null) {
			result.setSuccess(false);
		}
		else {
			result.setSuccess(expected > current);
		}
		reportAction(new WebActionResult("Assert Supperior:", null));
		reportResult(result);
	}

	public <E extends Double> void assertInferior(
		String title,
		E expected,
		E current) {
		WebTestResult<E> result = new WebTestResult<E>();
		result.setTitle(title);
		result.setExpected(expected);
		result.setCurrent(current);
		if(expected == null || current == null) {
			result.setSuccess(false);
		}
		else {
			result.setSuccess(expected < current);
		}
		reportAction(new WebActionResult("Assert Inferior:", null));
		reportResult(result);
	}

	public void assertExist(
		String title,
		WebAutoElement expected) {
		WebTestResult<IWebElement> result = new WebTestResult<IWebElement>();
		result.setTitle(title);
		result.setExpected(expected.getWrappedElement());
		result.setSuccess(expected.exists());
		reportAction(new WebActionResult("Assert Exists:", String.valueOf(result.isSuccess())));
		reportResult(result);
	}

	public <E> void assertNull(
		String title,
		E value) {
		WebTestResult<E> result = new WebTestResult<E>();
		result.setTitle(title);
		result.setSuccess(value == null);
		reportAction(new WebActionResult("Assert Null:", null));
		reportResult(result);
	}

	public <E> void assertNotNull(
		String title,
		E value) {
		WebTestResult<E> result = new WebTestResult<E>();
		result.setTitle(title);
		result.setSuccess(value != null);
		reportAction(new WebActionResult("Assert Not Null:", null));
		reportResult(result);
	}
}
