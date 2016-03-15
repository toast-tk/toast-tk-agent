package com.synaptix.toast.swing.agent.web.record.component;

import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.swing.agent.web.record.EventStackGobbler;

public class ButtonClickEventStackGobbler extends EventStackGobbler {

	@Override
	public boolean isInterestedIn(
		WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		boolean isButton = (component.equals("button") 
				|| component.equals("input:submit") 
				|| component.equals("input:button")
				|| component.equals("input:radio"));
		return "click".equals(capturedEvent.getType()) && isButton;
	}


	@Override
	public EventType getInterpretedEventType(
			WebEventRecord capturedEvent) {
		return EventType.BUTTON_CLICK;
	}

	@Override
	public boolean isLooper() {
		return false;
	}

	@Override
	public EventStackGobbler digest(
			WebEventRecord capturedEvent) {
		return this;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public WebEventRecord getAdjustedEvent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void reset() {
	}
}
