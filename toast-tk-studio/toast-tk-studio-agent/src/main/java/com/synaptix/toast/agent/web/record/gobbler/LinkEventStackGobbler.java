package com.synaptix.toast.agent.web.record.gobbler;

import com.synaptix.toast.agent.web.record.EventStackGobbler;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class LinkEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;


	@Override
	public boolean isInterestedIn(
			WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		return "click".equals(capturedEvent.getEventType()) && component.startsWith("a");
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
		return null;
	}

	@Override
	public EventType getInterpretedEventType(
			WebEventRecord capturedEvent) {
		return EventType.BUTTON_CLICK;
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
