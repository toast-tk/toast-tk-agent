package com.synaptix.toast.core.agent.interpret;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.annotation.craft.FixMe;

@FixMe(todo="make immutable")
public class AWTEventCapturedObject {
	
	public String componentLocator = ""; 
	public String componentName = "";
	public String componentType = "";
	public String businessValue = "";
	public String container = "";
	public String eventLine = "";
	public String eventLabel;
	public long timeStamp;
	private EventType eventType;

	
	public AWTEventCapturedObject(){
		
	}
	
	public AWTEventCapturedObject(String container, String locator, String name ,String type,String value, long timeStamp){
		this.container = container != null ? container.replace(" ", "_") : "COMMON_CONTAINER";
		this.componentLocator = locator;
		this.componentName = name;
		this.componentType = type;
		this.businessValue = value;
		this.timeStamp = timeStamp;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public boolean isFocusLostEvent() {
		return "CausedFocusEvent<".equals(eventLabel);
	}

	public boolean isFocusGainedEvent() {
		return "CausedFocusEvent>".equals(eventLabel);
	}

	public boolean isInputEvent() {
		return KeyEvent.class.getSimpleName().equals(eventLabel);
	}

	public boolean isMouseClickEvent() {
		return MouseEvent.class.getSimpleName().equals(eventLabel);
	}

	public boolean isWindowClickEvent() {
		return "TimedWindowEvent".equals(eventLabel);
	}

	
}
