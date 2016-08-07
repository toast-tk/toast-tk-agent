package com.synaptix.toast.agent.web.record.gobbler;

import java.util.Arrays;
import java.util.List;

import com.synaptix.toast.agent.web.record.EventStackGobbler;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class SelectEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;

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
		return EventType.JLIST_CLICK;
	}

	@Override
	public void reset() {
	}

	@Override
	public List<String> getSupportedComponents() {
		return Arrays.asList("select");
	}

	@Override
	public List<String> getStartEvents() {
		return Arrays.asList("change");
	}

	@Override
	public List<String> getStopEvents() {
		// TODO Auto-generated method stub
		return null;
	}

}
