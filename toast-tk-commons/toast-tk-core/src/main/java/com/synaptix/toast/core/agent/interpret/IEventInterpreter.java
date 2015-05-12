package com.synaptix.toast.core.agent.interpret;


public interface IEventInterpreter {

	public enum EventType { 
		CHECKBOX_CLICK, BUTTON_CLICK, CLICK,TABLE_CLICK, MENU_CLICK, POPUP_MENU_CLICK, JLIST_CLICk, COMBOBOX_CLICK, WINDOW_DISPLAY, KEY_INPUT, BRING_ON_TOP_DISPLAY
	}
	
	public String onCheckBoxClick(AWTEventCapturedObject eventObject);
	
	public String onButtonClick(AWTEventCapturedObject eventObject);

	public String onClick(AWTEventCapturedObject eventObject);

	public String onTableClick(AWTEventCapturedObject eventObject);

	public String onMenuClick(AWTEventCapturedObject eventObject);

	public String onComboBoxClick(AWTEventCapturedObject eventObject);

	public String onWindowDisplay(AWTEventCapturedObject eventObject);

	public String onKeyInput(AWTEventCapturedObject eventObject);

	public String onBringOnTop(AWTEventCapturedObject eventObject);

	public String onPopupMenuClick(AWTEventCapturedObject eventObject);

	/**
	 * Checks if the interpreter is connected to a remote repository
	 * hosted within the webapp
	 * 
	 * @return
	 */
	public boolean isConnectedToWebApp();


}
