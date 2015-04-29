package com.synaptix.toast.core.interpret;




public interface IEventInterpreter {

	public enum EventType { 
		CHECKBOX_CLICK, BUTTON_CLICK, CLICK,TABLE_CLICK, MENU_CLICK, POPUP_MENU_CLICK, JLIST_CLICk, COMBOBOX_CLICK, WINDOW_DISPLAY, KEY_INPUT, BRING_ON_TOP_DISPLAY
	}
	
	public String onCheckBoxClick(EventCapturedObject eventObject);
	
	public String onButtonClick(EventCapturedObject eventObject);

	public String onClick(EventCapturedObject eventObject);

	public String onTableClick(EventCapturedObject eventObject);

	public String onMenuClick(EventCapturedObject eventObject);

	public String onComboBoxClick(EventCapturedObject eventObject);

	public String onWindowDisplay(EventCapturedObject eventObject);

	public String onKeyInput(EventCapturedObject eventObject);

	public String onBringOnTop(EventCapturedObject eventObject);

	public String onPopupMenuClick(EventCapturedObject eventObject);


}
