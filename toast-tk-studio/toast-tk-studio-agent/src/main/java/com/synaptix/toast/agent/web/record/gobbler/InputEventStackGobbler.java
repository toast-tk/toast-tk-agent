package com.synaptix.toast.agent.web.record.gobbler;

import com.synaptix.toast.agent.web.record.EventStackGobbler;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class InputEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;
	
	String currentTarget = null;


	@Override
	public boolean isInterestedIn(
			WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		boolean isButton = (component.equals("button") 
							|| component.equals("input:submit") 
							|| component.equals("input:button")
							|| component.equals("input:radio"));
		return "focus".equals(capturedEvent.getType()) && component.startsWith("input") && !isButton;
	}


	@Override
	public boolean isLooper() {
		return true;
	}

	@Override
	public EventStackGobbler digest(
			WebEventRecord capturedEvent) {
		if(currentTarget == null){
			currentTarget = capturedEvent.getTarget();
		}
		if(finalEvent == null && "blur".equals(capturedEvent.getType())){
			this.finalEvent = capturedEvent;
		}
		return this;
	}

	@Override
	public boolean isCompleted() {
		return this.finalEvent != null;
	}

	@Override
	public WebEventRecord getAdjustedEvent() {
		return finalEvent;
	}

	@Override
	public EventType getInterpretedEventType(
			WebEventRecord capturedEvent) {
		return EventType.KEY_INPUT;
	}

	@Override
	public void reset() {
		this.finalEvent = null;
		this.currentTarget = null;
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
