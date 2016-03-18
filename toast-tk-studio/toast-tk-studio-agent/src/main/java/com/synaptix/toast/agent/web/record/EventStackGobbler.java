package com.synaptix.toast.agent.web.record;

import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public abstract class EventStackGobbler {

	public abstract boolean isInterestedIn(WebEventRecord capturedEvent);
			
	public abstract EventType getInterpretedEventType(
			WebEventRecord capturedEvent);

	public abstract boolean isLooper();

	public abstract EventStackGobbler digest(
			WebEventRecord capturedEvent);

	public abstract boolean isCompleted();

	public abstract WebEventRecord getAdjustedEvent();
	
	public abstract void reset();
	
	public abstract String getStopEvent();
	
	public abstract String getComponentType();
}
