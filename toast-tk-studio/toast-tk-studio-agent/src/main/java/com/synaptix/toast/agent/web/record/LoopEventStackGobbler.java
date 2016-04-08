package com.synaptix.toast.agent.web.record;

import java.util.List;

import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public abstract class LoopEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;
	
	String currentTarget = null;
	
	public boolean isInterestedIn(
			WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		return getStartEvents().contains(capturedEvent.getEventType()) && component.equals(getComponentType());
	}
	

	public abstract List<String> getStartEvents();
	
	
	public LoopEventStackGobbler digest(
			WebEventRecord capturedEvent) {
		if(currentTarget == null){
			currentTarget = capturedEvent.getTarget();
		}
		if(finalEvent == null 
				&&
				capturedEvent.getTarget().equals(currentTarget)
				&& 
				getStopEvent().equals(capturedEvent.getEventType())){
			this.finalEvent = capturedEvent;
		}
		return this;
	}

	public boolean isCompleted() {
		return this.finalEvent != null;
	}
	
	public abstract EventType getInterpretedEventType(
			WebEventRecord capturedEvent);

	public boolean isLooper(){
		return true;
	}

	@Override
	public void reset() {
		this.finalEvent = null;
		this.currentTarget = null;
	}
	@Override
	public WebEventRecord getAdjustedEvent() {
		return this.finalEvent;
	}

}
