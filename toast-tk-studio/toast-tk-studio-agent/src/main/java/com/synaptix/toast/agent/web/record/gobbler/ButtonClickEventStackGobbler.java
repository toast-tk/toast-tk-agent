package com.synaptix.toast.agent.web.record.gobbler;

import com.synaptix.toast.agent.web.record.EventStackGobbler;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class ButtonClickEventStackGobbler extends EventStackGobbler {

	@Override
	public boolean isInterestedIn(
		WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		boolean isButton = (component.equals("button") 
				|| component.equals("submit") 
				|| component.equals("radio"));
		return "click".equals(capturedEvent.getEventType()) && isButton;
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

	@Override
	public String getStopEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getComponentType() {
		// TODO Auto-generated method stub
		return null;
	}
}
