package com.synaptix.toast.plugin.swing.agent.record.gobbler.looper;

import java.awt.event.KeyEvent;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;


public class InputEventStackGobbler extends EventStackGobbler{

	AWTCapturedEvent finalEvent = null;
	String inputTypeUnderCapture = null;
	
	@Override
	public boolean isInterestedIn(AWTCapturedEvent capturedEvent) {
		this.inputTypeUnderCapture = capturedEvent.componentType;
		return isInputEvent(capturedEvent.eventLabel);
	}
	


	public boolean isInputEvent(String eventLabel) {
		return KeyEvent.class.getSimpleName().equals(eventLabel);
	}

	
	@Override
	public boolean isLooper() {
		return false;
	}
	
	@Override
	public EventStackGobbler digest(AWTCapturedEvent capturedEvent) {
//		if (isInputEvent(capturedEvent.eventLabel) || isMouseClick(capturedEvent.eventLabel)) {
//			return this;
//		}
		if (isFocusLostEvent(capturedEvent.eventLabel)) {
			String inputCapturedType = capturedEvent.componentType;
			if (inputCapturedType.equals(inputTypeUnderCapture)) {
				finalEvent = cloneEvent(capturedEvent);
				String name = capturedEvent.componentName;
				String locator = capturedEvent.componentLocator;
				finalEvent.componentName = "null".equals(name) || name == null ? locator : name;
				finalEvent.businessValue = capturedEvent.businessValue;
			}
		}
		return this;
	}

	@Override
	public boolean isCompleted() {
		return finalEvent != null;
	}

	@Override
	public AWTCapturedEvent getAdjustedEvent() {
		return finalEvent;
	}
	
	@Override
	public EventType getInterpretedEventType(AWTCapturedEvent capturedEvent) {
		return EventType.KEY_INPUT;
	}

	private static boolean isMenuType(String targetType) {
		return "JMenu".equals(targetType);
	}
	
	private static boolean isMenuItemType(String targetType) {
		return "JMenuItem".equals(targetType);
	}

}
