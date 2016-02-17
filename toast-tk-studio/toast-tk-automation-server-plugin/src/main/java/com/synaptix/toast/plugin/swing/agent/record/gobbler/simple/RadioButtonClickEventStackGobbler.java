package com.synaptix.toast.plugin.swing.agent.record.gobbler.simple;

import javax.swing.JRadioButton;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;

public class RadioButtonClickEventStackGobbler extends EventStackGobbler {

	@Override
	public boolean isInterestedIn(
		AWTCapturedEvent capturedEvent) {
		return isValidEvent(capturedEvent) &&
			isRadioButtonClick(capturedEvent.componentType);
	}

	private boolean isValidEvent(AWTCapturedEvent capturedEvent){
		return (isMouseClick(capturedEvent.eventLabel) || isFocusLostEvent(capturedEvent.eventLabel));
	}
	
	
	public static boolean isRadioButtonClick(
		String targetType) {
		try {
			Class<?> tClass = Class.forName(targetType);
			boolean isCompliant = JRadioButton.class.isAssignableFrom(tClass) || targetType.contains("RadioButton");
			return isCompliant;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public EventType getInterpretedEventType(
		AWTCapturedEvent capturedEvent) {
		return EventType.RADIO_CLICK;
	}

	@Override
	public boolean isLooper() {
		return false;
	}

	@Override
	public EventStackGobbler digest(
		AWTCapturedEvent capturedEvent) {
		return this;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public AWTCapturedEvent getAdjustedEvent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void reset() {
	}
}
