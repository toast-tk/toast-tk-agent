package com.synaptix.toast.plugin.swing.agent.record.gobbler;

import java.awt.event.MouseEvent;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;

public abstract class EventStackGobbler {
	
	public abstract boolean isInterestedIn(AWTCapturedEvent capturedEvent);
	
	public abstract EventType getInterpretedEventType(AWTCapturedEvent capturedEvent);

	public abstract boolean isLooper();

	public abstract EventStackGobbler digest(AWTCapturedEvent capturedEvent);

	public abstract boolean isCompleted();
	
	protected boolean isFocusLostEvent(String eventLabel) {
		return "CausedFocusEvent<".equals(eventLabel);
	}
	
	protected boolean isMouseClick(String eventLabel) {
		return MouseEvent.class.getSimpleName().equals(eventLabel);
	}

	protected AWTCapturedEvent cloneEvent(final AWTCapturedEvent capturedEvent) {
		String locator = capturedEvent.componentLocator;
		String name = capturedEvent.componentName;
		String type = capturedEvent.componentType;
		String value = capturedEvent.businessValue;
		String container = capturedEvent.container;
		long timeStamp = capturedEvent.timeStamp;
		return new AWTCapturedEvent(container, locator, name, type, value, timeStamp);
	}

	public abstract AWTCapturedEvent getAdjustedEvent();
}
