package com.synaptix.toast.agent.web.record;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public abstract class LoopEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;
	
	String currentTarget = null;
	

	public LoopEventStackGobbler digest(
			WebEventRecord capturedEvent) {
		if(currentTarget == null){
			currentTarget = capturedEvent.getTarget();
		}
		if(finalEvent == null 
				&&
				capturedEvent.getTarget().equals(currentTarget)
				&& 
				getStopEvents().contains(capturedEvent.getEventType())){
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
