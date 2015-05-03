package com.synaptix.toast.adapter.swing;

import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.AddValueInVar;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.ClickOn;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.ClickOnIn;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.DiviserVarByValue;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.GetComponentValue;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.MultiplyVarByValue;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.RemplacerVarParValue;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.SelectContectualMenu;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.SelectMenuPath;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.SelectSubMenu;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.SelectTableRow;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.SelectValueInList;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.StoreComponentValueInVar;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.SubstractValueFromVar;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.TypeValueInInput;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.TypeVarIn;
import static com.synaptix.toast.core.adapter.ActionAdapterSentenceRef.Wait;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.esotericsoftware.kryonet.FrameworkMessage;
import com.synaptix.toast.adapter.web.HasClickAction;
import com.synaptix.toast.adapter.web.HasStringValue;
import com.synaptix.toast.adapter.web.HasSubItems;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.annotation.ActionAdapter;
import com.synaptix.toast.core.driver.IClientDriver;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.request.TableCommandRequestQueryCriteria;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.core.runtime.IFeedableSwingPage;
import com.synaptix.toast.core.runtime.IRepositorySetup;

@ActionAdapter(value = ActionAdapterKind.swing, name="")
public abstract class ToastSwingActionAdapter {
	protected IRepositorySetup repo;
	protected IClientDriver driver;

	public ToastSwingActionAdapter(IRepositorySetup repo, IClientDriver driver) {
		this.repo = repo;
		this.driver = driver;
		try {
			for (IFeedableSwingPage page : repo.getSwingPages()) {
				((DefaultSwingPage) page).setDriver(driver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract SwingAutoElement overrideElementInstance(SwingAutoElement autoElement);

	protected SwingAutoElement getPageField(String pageName, String fieldName) throws IllegalAccessException {
		if (repo.getSwingPage(pageName) == null) {
			throw new IllegalAccessException(pageName + " swing page not found in repository !");
		}
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

	@Action(action = TypeValueInInput, description = "Saisir une valeur dans un composant graphique")
	public TestResult typeIn(String text, String pageName, String widgetName) throws Exception {
		try {
			SwingAutoElement pageField = getPageField(pageName, widgetName);
			if(pageField instanceof SwingInputElement){
				SwingInputElement input = (SwingInputElement) pageField;
				input.setInput(text);
			}else if(pageField instanceof SwingDateElement){
				SwingDateElement input = (SwingDateElement) pageField;
				input.setDateText(text);
			}else{
				throw new IllegalAccessException(String.format("%s.%s is not handled to type values in !", pageName, pageField));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = ClickOnIn, description ="Cliquer sur un composant présent dans un contenant de composant")
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

	@Action(action = ClickOn, description = "Cliquer sur un composant graphique")
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

	@Action(action = "(\\w+).(\\w+) exists", description = "Verifier qu'un composant graphique existe")
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

	@Action(action = "Count (\\w+).(\\w+) results", description = "Compter le nombre de ligne dans un tableau")
	public TestResult count(String pageName, String widgetName) throws Exception {
		try {
			SwingTableElement table = (SwingTableElement) getPageField(pageName, widgetName);
			return new TestResult(table.count());
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}

	@Action(action = TypeVarIn, description = "Saisir la valeur d'une variable dans un champs graphique de type input")
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

	@Action(action = GetComponentValue, description = "Lire la valeur d'un composant graphique")
	public TestResult getComponentValue(String pageName, String widgetName) throws Exception {
		try {
			SwingAutoElement pageField = getPageField(pageName, widgetName);
			if (!(pageField instanceof HasStringValue)) {
				throw new IllegalAccessException(pageName + "." + widgetName + " isn't supporting value fetching !");
			}
			HasStringValue stringValueProvider = (HasStringValue) pageField;
			String value = stringValueProvider.getValue();
			return new TestResult(value, ResultKind.SUCCESS);
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}
	
	@Action(action = StoreComponentValueInVar, description = "Lire la valeur d'un composant graphique et la stocker dans une variable")
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

	@Action(action = Wait, description = "Attendre n secondes avant la prochaine action")
	public TestResult wait(String time) throws Exception {
		try {
			Thread.sleep(Integer.valueOf(time) * 1000);
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = SelectMenuPath, description = "Selectionner un menu, avec / comme séparateur")
	public TestResult selectPath(String menu) throws Exception {
		try {
			String[] locator = menu.split(" / ");
			SwingAutoUtils.confirmExist(driver, locator[0], AutoSwingType.menu.name());
			driver.process(
					new CommandRequest.CommandRequestBuilder(null).with(locator[0])
					.ofType(AutoSwingType.menu.name()).select(locator[1]).build());
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	@Action(action = SelectSubMenu, description = "Selectionner un sous menu")
	public TestResult select(String pageName, String widgetName, String parentPage, String parentWidgetName) throws Exception {
		return clickOnIn(pageName, widgetName, parentPage, parentWidgetName);
	}

	@Action(action = SelectValueInList, description = "Selectionner une valeur dans une liste")
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

	@Action(action = SelectTableRow, description = "Selectionner une ligne de tableau avec critères")
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

	@Action(action = SelectContectualMenu , description = "selectionner un menu dans une popup contextuelle")
	public TestResult selectCtxMenu(String menu) throws Exception {
		try {
			driver.process(new CommandRequest.CommandRequestBuilder(null).with(menu).ofType(AutoSwingType.menu.name()).select(menu).build());
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}

	// /////////////////////////////////////// 
	// TO MOVE IN A DRIVER AGNOSTIC FIXUTRE 
	//////////////////////////////////////////
	@Action(action = AddValueInVar, description = "Additionner deux valeurs numériques")
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

	@Action(action = SubstractValueFromVar, description = "Soustraire deux valeurs numériques")
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

	@Action(action = MultiplyVarByValue, description = "Multiplier deux valeurs")
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

	@Action(action = DiviserVarByValue, description = "Diviseur le premier argument par le deuxième")
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

	@Action(action = RemplacerVarParValue, description = "Assigner une valeur à une variable")
	public TestResult replaceVarByVal(String var, String value) throws Exception {
		try {
			repo.getUserVariables().put(var, value);
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}
	
	@Action(action = "Ajuster date (\\w+).(\\w+) à plus (\\w+) jours", description = "Rajouter n Jours à au composant graphique de date")
	public TestResult setDate(String pageName, String widgetName, String days) throws Exception {
		try {
			SwingDateElement input = (SwingDateElement) getPageField(pageName, widgetName);
			input.setInput(days);
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}
	
	@Action(action = "\\$(\\w+) == \\$(\\w+)", description = "Comparer deux variables")
	public TestResult VarEqVar(String var1, String var2) throws Exception {
		try {
			Object object = repo.getUserVariables().get(var1) == null ? "undefined" : repo.getUserVariables().get(var1);
			Object object2 = repo.getUserVariables().get(var2)== null ? "undefined" : repo.getUserVariables().get(var2);
			if(object.equals(object2)){
				return new TestResult(Boolean.TRUE.toString(), ResultKind.SUCCESS);
			}else{
				return new TestResult(String.format("%s == %s => %s", object.toString(), object2.toString(), Boolean.FALSE.toString()), ResultKind.FAILURE);
			}
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}
	
	@Action(action = "([\\w\\W]+) égale à \\$(\\w+)", description = "Comparer une valeur à une variable")
	public TestResult ValueEqVar(String value, String var) throws Exception {
		try {
			Object object = repo.getUserVariables().get(var);
			if(value.equals(object)){
				return new TestResult(Boolean.TRUE.toString(), ResultKind.SUCCESS);
			}else{
				return new TestResult(String.format("%s == %s => %s", value, object, Boolean.FALSE.toString()), ResultKind.FAILURE);
			}
		} catch (Exception e) {
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
	}
	
	@Action(action = "Clear (\\w+).(\\w+)", description = "Effacer le contenu d'un composant input graphique")
	public TestResult clear(String pageName, String widgetName) throws Exception {
		try {
			SwingInputElement input = (SwingInputElement) getPageField(pageName, widgetName);
			input.clear();
		} catch (Exception e) {
			e.printStackTrace();
			return new TestResult(e.getCause().getMessage(), ResultKind.ERROR);
		}
		return new TestResult();
	}
}
