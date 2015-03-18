package com.synaptix.toast.fixture.swing;

import static com.synaptix.toast.fixture.FixtureSentenceRef.ClickOn;
import static com.synaptix.toast.fixture.FixtureSentenceRef.ClickOnIn;
import static com.synaptix.toast.fixture.FixtureSentenceRef.SelectContectualMenu;
import static com.synaptix.toast.fixture.FixtureSentenceRef.SelectMenuPath;
import static com.synaptix.toast.fixture.FixtureSentenceRef.SelectSubMenu;
import static com.synaptix.toast.fixture.FixtureSentenceRef.SelectTableRow;
import static com.synaptix.toast.fixture.FixtureSentenceRef.SelectValueInList;
import static com.synaptix.toast.fixture.FixtureSentenceRef.TypeValueInInput;
import static com.synaptix.toast.fixture.FixtureSentenceRef.TypeVarIn;
import static com.synaptix.toast.fixture.FixtureSentenceRef.Wait;

import java.io.IOException;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.AutoSwingType;
import com.synaptix.toast.core.IFeedableSwingPage;
import com.synaptix.toast.core.IRepositorySetup;
import com.synaptix.toast.core.annotation.Check;
import com.synaptix.toast.core.annotation.Fixture;
import com.synaptix.toast.core.annotation.FixtureKind;
import com.synaptix.toast.core.setup.TestResult;
import com.synaptix.toast.core.setup.TestResult.ResultKind;
import com.synaptix.toast.fixture.facade.ClientDriver;
import com.synaptix.toast.fixture.facade.HasClickAction;
import com.synaptix.toast.fixture.facade.HasSubItems;

@Fixture(FixtureKind.swing)
public abstract class RedPepperSwingFixture {
	protected IRepositorySetup repo;
	protected ClientDriver driver;

	public RedPepperSwingFixture(IRepositorySetup repo) {
		this.repo = repo;
		try {
			driver = getDriver();
			for (IFeedableSwingPage page : repo.getSwingPages()) {
				((DefaultSwingPage) page).setDriver(driver);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public abstract ClientDriver getDriver() throws IOException;

	public abstract SwingAutoElement overrideElementInstance(SwingAutoElement autoElement);

	protected SwingAutoElement getPageField(String pageName, String fieldName) {
		DefaultSwingPage page = (DefaultSwingPage) repo.getSwingPage(pageName);
		SwingAutoElement autoElement = page.getAutoElement(fieldName);
		if (autoElement instanceof DefaultSwingAutoElement) {
			autoElement = overrideElementInstance(autoElement);
		}
		return autoElement;
	}

	@Check(TypeValueInInput)
	public TestResult typeIn(String text, String pageName, String widgetName) throws Exception {
		try {
			SwingInputElement input = (SwingInputElement) getPageField(pageName, widgetName);
			input.setInput(text);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Check(ClickOnIn)
	public TestResult clickOnIn(String pageName, String widgetName, String parentPage, String parentWidgetName) throws Exception {
		try {
			HasSubItems input = (HasSubItems) getPageField(parentPage, parentWidgetName);
			SwingAutoElement subElement = (SwingAutoElement) getPageField(pageName, widgetName);
			input.clickOn(subElement.getWrappedElement().getLocator());
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Check(ClickOn)
	public TestResult clickOn(String pageName, String widgetName) throws Exception {
		try {
			HasClickAction input = (HasClickAction) getPageField(pageName, widgetName);
			boolean click = input.click();
			return new TestResult(String.valueOf(click), click ? ResultKind.SUCCESS : ResultKind.ERROR);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}
	
	@Check("(\\w+).(\\w+) exists")
	public TestResult exists(String pageName, String widgetName) throws Exception {
		try {
			SwingAutoElement input = getPageField(pageName, widgetName);
			if (input.exists()) {
				return new TestResult("true", ResultKind.SUCCESS);
			} else {
				return new TestResult("false", ResultKind.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}

	@Check("Count (\\w+).(\\w+) results")
	public TestResult count(String pageName, String widgetName) throws Exception {
		try {
			SwingTableElement table = (SwingTableElement) getPageField(pageName, widgetName);
			return new TestResult(table.count());
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}

	@Check(TypeVarIn)
	public TestResult typeVarIn(String variable, String pageName, String widgetName) throws Exception {
		try {
			Object object = repo.getUserVariables().get(variable);
			typeIn(object.toString(), pageName, widgetName);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Check(Wait)
	public TestResult wait(String time) throws Exception {
		try {
			Thread.sleep(Integer.valueOf(time) * 1000);
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Check(SelectMenuPath)
	public TestResult selectPath(String menu) throws Exception {
		try {
			String[] locator = menu.split(" / "); 
			SwingAutoUtils.confirmExist(getDriver(), locator[0], AutoSwingType.menu.name());
			getDriver().process(new CommandRequest.CommandRequestBuilder(null).with(locator[0]).ofType(AutoSwingType.menu.name()).select(locator[1]).build());
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Check(SelectSubMenu)
	public TestResult select(String pageName, String widgetName, String parentPage, String parentWidgetName) throws Exception {
		return clickOnIn(pageName, widgetName, parentPage, parentWidgetName);
	}

	@Check(SelectValueInList)
	public TestResult selectIn(String value, String pageName, String widgetName) throws Exception {
		try {
			SwingListElement list = (SwingListElement) getPageField(pageName, widgetName);
			list.select(value);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}
	
    @Check(SelectTableRow)
    public TestResult selectMission(String pageName, String widgetName, String tableColumnFinder) throws Exception {
        try {
        	String col = tableColumnFinder.split("=")[0];
        	String val = tableColumnFinder.split("=")[1];
            SwingTableElement table = (SwingTableElement) getPageField(pageName, widgetName);
            String outputVal = table.find(col, val, col);
			return new TestResult(outputVal, ResultKind.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
        }
    }    
    
	@Check(SelectContectualMenu)
	public TestResult selectCtxMenu(String menu) throws Exception {
		try {
			getDriver().process(new CommandRequest.CommandRequestBuilder(null).with(menu).ofType(AutoSwingType.menu.name()).select(menu).build());
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}
   
	
}
