package com.synaptix.toast.swing.agent.interpret;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter;

/**
 * Keep all stored to build things later
 * 
 * @author PSKA09831
 *
 */
public class DefaultRawEventInterpreter implements IEventInterpreter {

	@Override
	public String onButtonClick(
		AWTCapturedEvent eventObject) {
		return String.format("Cliquer sur le button '%s'", eventObject.componentName);
	}

	@Override
	public String onClick(
		AWTCapturedEvent eventObject) {
		return String.format("Cliquer sur '%s' de type '%s'", eventObject.componentLocator, eventObject.componentType);
	}

	@Override
	public String onTableClick(
		AWTCapturedEvent eventObject) {
		return String.format("Selectionner dans le tableau la ligne ayant %s", eventObject.businessValue);
	}

	@Override
	public String onMenuClick(
		AWTCapturedEvent eventObject) {
		return String.format("Choisir le menu '%s", eventObject.componentName);
	}

	@Override
	public String onComboBoxClick(
		AWTCapturedEvent eventObject) {
		return String.format("Selectionner *%s* dans *%s*", eventObject.businessValue, eventObject.componentName);
	}

	@Override
	public String onWindowDisplay(
		AWTCapturedEvent eventObject) {
		String type = eventObject.componentType;
		if("JDialog".equals(eventObject.componentType) || "JSyDialog".equals(eventObject.componentType)) {
			type = "dialogue";
		}
		return String.format("Affichage %s '%s'", type, eventObject.componentName);
	}

	@Override
	public String onKeyInput(
		AWTCapturedEvent eventObject) {
		return String.format("Saisir *%s* dans *%s*", eventObject.businessValue, eventObject.componentName);
	}

	@Override
	public String onBringOnTop(
		AWTCapturedEvent eventObject) {
		return String.format("Selection %s '%s'", eventObject.componentType, eventObject.componentName);
	}

	@Override
	public String onPopupMenuClick(
		AWTCapturedEvent eventObject) {
		return String.format("Selectionner le menu '%s'", eventObject.componentName);
	}

	@Override
	public String onCheckBoxClick(
		AWTCapturedEvent eventObject) {
		return String.format("Cliquer sur la checkbox '%s'", eventObject.componentName);
	}

	@Override
	public boolean isConnectedToWebApp() {
		return false;
	}

	@Override
	public String onOpenPopupMenu(AWTCapturedEvent value) {
		return String.format("Choisir le menu contextuel '%s", value.componentName);
	}
}
