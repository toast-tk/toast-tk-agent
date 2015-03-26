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
import static com.synaptix.toast.fixture.FixtureSentenceRef.StoreComponentValueInVar;
import static com.synaptix.toast.fixture.FixtureSentenceRef.RemplacerVarParValue;
import static com.synaptix.toast.fixture.FixtureSentenceRef.DiviserVarByValue;
import static com.synaptix.toast.fixture.FixtureSentenceRef.MultiplyVarByValue;
import static com.synaptix.toast.fixture.FixtureSentenceRef.SubstractValueFromVar;
import static com.synaptix.toast.fixture.FixtureSentenceRef.AddValueInVar;
import static com.synaptix.toast.fixture.FixtureSentenceRef.Wait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.IllegalAddException;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.TableCommandRequestQueryCriteria;
import com.synaptix.toast.core.AutoSwingType;
import com.synaptix.toast.core.IFeedableSwingPage;
import com.synaptix.toast.core.IRepositorySetup;
import com.synaptix.toast.core.Property;
import com.synaptix.toast.core.annotation.Check;
import com.synaptix.toast.core.annotation.Fixture;
import com.synaptix.toast.core.annotation.FixtureKind;
import com.synaptix.toast.core.setup.TestResult;
import com.synaptix.toast.core.setup.TestResult.ResultKind;
import com.synaptix.toast.fixture.facade.ClientDriver;
import com.synaptix.toast.fixture.facade.HasClickAction;
import com.synaptix.toast.fixture.facade.HasStringValue;
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

	protected SwingAutoElement getPageField(String pageName, String fieldName) throws IllegalAccessException {
		DefaultSwingPage page = (DefaultSwingPage) repo.getSwingPage(pageName);
		SwingAutoElement autoElement = page.getAutoElement(fieldName);
		if (autoElement instanceof DefaultSwingAutoElement) {
			autoElement = overrideElementInstance(autoElement);
		}
		if (autoElement == null) {
			throw new IllegalAccessException(pageName + "." + fieldName + " not found in repository !");
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

	@Check(StoreComponentValueInVar)
	public TestResult selectComponentValue(String pageName, String widgetName, String variable) throws Exception {
		try {
			SwingAutoElement pageField = getPageField(pageName, widgetName);
			if (!(pageField instanceof HasStringValue)) {
				throw new IllegalAccessException(pageName + "." + widgetName + " isn't supporting value fetching !");
			}
			HasStringValue stringValueProvider = (HasStringValue) pageField;
			String value = stringValueProvider.getValue();
			repo.getUserVariables().put(variable, value);
			return new TestResult(value, ResultKind.SUCCESS);
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
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
			getDriver().process(
					new CommandRequest.CommandRequestBuilder(null).with(locator[0]).ofType(AutoSwingType.menu.name()).select(locator[1]).build());
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
	public TestResult selectMission(String pageName, String widgetName, String tableColumnFinder) {
		try {
			SwingTableElement table = (SwingTableElement) getPageField(pageName, widgetName);

			String[] criteria = tableColumnFinder.split(Property.TABLE_CRITERIA_SEPARATOR);
			List<TableCommandRequestQueryCriteria> tableCriteria = new ArrayList<TableCommandRequestQueryCriteria>();
			if (criteria.length > 0) {
				for (String criterion : criteria) {
					String col = criterion.split(Property.TABLE_KEY_VALUE_SEPARATOR)[0];
					String val = criterion.split(Property.TABLE_KEY_VALUE_SEPARATOR)[1];
					TableCommandRequestQueryCriteria tableCriterion = new TableCommandRequestQueryCriteria(col, val);
					tableCriteria.add(tableCriterion);
				}
			} else {
				String col = tableColumnFinder.split(Property.TABLE_KEY_VALUE_SEPARATOR)[0];
				String val = tableColumnFinder.split(Property.TABLE_KEY_VALUE_SEPARATOR)[1];
				TableCommandRequestQueryCriteria tableCriterion = new TableCommandRequestQueryCriteria(col, val);
				tableCriteria.add(tableCriterion);
			}

			String outputVal = table.find(tableCriteria);
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

	// /////////////////////////////////////// 
	// TO MOVE IN A DRIVER AGNOSTIC FIXUTRE 
	//////////////////////////////////////////
	@Check(AddValueInVar)
	public TestResult addValueToVar(String value, String var) throws Exception {
		try {
			Object object = repo.getUserVariables().get(var);
			if (object == null) {
				throw new IllegalAccessException("Variable not defined !");
			}
			if (object instanceof String) { // for the time being we store only
											// strings !!
				Double v = Double.valueOf(value);
				Double d = Double.valueOf((String) object);
				d = d + v;
				repo.getUserVariables().put(var, d.toString());
				return new TestResult(d.toString(), ResultKind.SUCCESS);
			} else {
				throw new IllegalAccessException("Variable not in a proper format: current -> " + object.getClass().getSimpleName());
			}
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}

	@Check(SubstractValueFromVar)
	public TestResult substractValueToVar(String value, String var) throws Exception {
		try {
			Object object = repo.getUserVariables().get(var);
			if (object == null) {
				throw new IllegalAccessException("Variable not defined !");
			}
			if (object instanceof String) { // for the time being we store only
											// strings !!
				Double v = Double.valueOf(value);
				Double d = Double.valueOf((String) object);
				d = d - v;
				repo.getUserVariables().put(var, d.toString());
				return new TestResult(d.toString(), ResultKind.SUCCESS);
			} else {
				throw new IllegalAccessException("Variable not in a proper format: current -> " + object.getClass().getSimpleName());
			}
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}

	@Check(MultiplyVarByValue)
	public TestResult multiplyVarByBal(String var, String value) throws Exception {
		try {
			Object object = repo.getUserVariables().get(var);
			if (object == null) {
				throw new IllegalAccessException("Variable not defined !");
			}
			if (object instanceof String) { // for the time being we store only
											// strings !!
				Double v = Double.valueOf(value);
				Double d = Double.valueOf((String) object);
				d = d * v;
				repo.getUserVariables().put(var, d.toString());
				return new TestResult(d.toString(), ResultKind.SUCCESS);
			} else {
				throw new IllegalAccessException("Variable not in a proper format: current -> " + object.getClass().getSimpleName());
			}
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}

	@Check(DiviserVarByValue)
	public TestResult divideVarByValue(String var, String value) throws Exception {
		try {
			Object object = repo.getUserVariables().get(var);
			if (object == null) {
				throw new IllegalAccessException("Variable not defined !");
			}
			if (object instanceof String) { // for the time being we store only
											// strings !!
				Double v = Double.valueOf(value);
				Double d = Double.valueOf((String) object);
				d = d / v;
				repo.getUserVariables().put(var, d.toString());
				return new TestResult(d.toString(), ResultKind.SUCCESS);
			} else {
				throw new IllegalAccessException("Variable not in a proper format: current -> " + object.getClass().getSimpleName());
			}
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}

	@Check(RemplacerVarParValue)
	public TestResult replaceVarByVal(String var, String value) throws Exception {
		try {
			repo.getUserVariables().put(var, value);
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

}
