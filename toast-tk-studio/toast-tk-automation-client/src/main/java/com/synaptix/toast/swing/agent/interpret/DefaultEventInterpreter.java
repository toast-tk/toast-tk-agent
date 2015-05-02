package com.synaptix.toast.swing.agent.interpret;

import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter;


public class DefaultEventInterpreter implements IEventInterpreter {

	@Override
	public String onButtonClick(AWTEventCapturedObject eventObject) {
		return String.format("Cliquer sur le button '%s'", eventObject.componentName);
	}

	@Override
	public String onClick(AWTEventCapturedObject eventObject) {
		return String.format("Cliquer sur '%s' de type '%s'", eventObject.componentLocator, eventObject.componentType);
	}

	@Override
	public String onTableClick(AWTEventCapturedObject eventObject) {
		return String.format("Selectionner dans le tableau la ligne ayant %s", eventObject.businessValue);
	}

	@Override
	public String onMenuClick(AWTEventCapturedObject eventObject) {
		return String.format("Choisir le menu '%s'", eventObject.componentName);
	}

	@Override
	public String onComboBoxClick(AWTEventCapturedObject eventObject) {
		return String.format("Selectionner *%s* dans *%s*", eventObject.businessValue, eventObject.componentName);
	}

	@Override
	public String onWindowDisplay(AWTEventCapturedObject eventObject) {
		String type = eventObject.componentType;
		if("JDialog".equals(eventObject.componentType) || "JSyDialog".equals(eventObject.componentType)){
			type = "dialogue";
		}
		return String.format("Affichage %s '%s'", type, eventObject.componentName);
	}

	@Override
	public String onKeyInput(AWTEventCapturedObject eventObject) {
		return  String.format("Saisir *%s* dans *%s*", eventObject.businessValue, eventObject.componentName);
	}

	@Override
	public String onBringOnTop(AWTEventCapturedObject eventObject) {
		return String.format("Selection Fenetre '%s'", eventObject.componentName == null || "null".equals(eventObject.componentName) ? eventObject.componentType : eventObject.componentName);
	}

	@Override
	public String onPopupMenuClick(AWTEventCapturedObject eventObject) {
		return String.format("Selectionner le menu '%s'", eventObject.componentName);
	}

	@Override
	public String onCheckBoxClick(AWTEventCapturedObject eventObject) {
		return String.format("Cliquer sur la checkbox '%s'", eventObject.componentName);
	}

}
