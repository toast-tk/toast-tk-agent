package com.synaptix.toast.agent.web.record;

import java.util.List;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public abstract class EventStackGobbler {

	public boolean isInterestedIn(
			WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		return getStartEvents().contains(capturedEvent.getEventType()) && getSupportedComponents().contains(component);
	}
			
	public abstract EventType getInterpretedEventType(
			WebEventRecord capturedEvent);

	public abstract boolean isLooper();

	public abstract EventStackGobbler digest(
			WebEventRecord capturedEvent);

	public abstract boolean isCompleted();

	public abstract WebEventRecord getAdjustedEvent();
	
	public abstract void reset();

	public abstract List<String> getSupportedComponents();
	
	public abstract List<String> getStartEvents();
	
	public abstract List<String> getStopEvents();
	
}
