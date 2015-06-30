package com.synaptix.toast.plugin.swing.agent.record.gobbler.looper;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;


public class ComboBoxClickEventStackGobbler extends EventStackGobbler{

	AWTCapturedEvent finalEvent = null;
	
	@Override
	public boolean isInterestedIn(AWTCapturedEvent capturedEvent) {
		return isMouseClick(capturedEvent.eventLabel) && 
				isComboBoxType(capturedEvent.componentType);
	}
	
	public static boolean isComboBoxType(String targetType) {
		return "JComboBox".equals(targetType) || "ComboBox.list".equals(targetType);
	}

	@Override
	public EventType getInterpretedEventType(AWTCapturedEvent capturedEvent) {
		return EventType.COMBOBOX_CLICK;
	}

	@Override
	public boolean isLooper() {
		return false;
	}

	@Override
	public EventStackGobbler digest(AWTCapturedEvent capturedEvent) {
		if (isFocusLostEvent(capturedEvent.eventLabel)) {
			finalEvent = cloneEvent(capturedEvent);
			String name = finalEvent.componentName;
			finalEvent.componentLocator = capturedEvent.componentLocator;
			finalEvent.componentName = name == null || "null".equals(name) ? finalEvent.componentLocator : name;
			finalEvent.businessValue = capturedEvent.businessValue;
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

}
