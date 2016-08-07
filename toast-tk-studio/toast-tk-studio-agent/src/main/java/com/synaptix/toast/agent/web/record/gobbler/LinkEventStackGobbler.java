package com.synaptix.toast.agent.web.record.gobbler;

import java.util.List;

import com.synaptix.toast.agent.web.record.EventStackGobbler;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

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
	public List<String> getStopEvents() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getComponentType() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> getStartEvents() {
		// TODO Auto-generated method stub
		return null;
	}

}
