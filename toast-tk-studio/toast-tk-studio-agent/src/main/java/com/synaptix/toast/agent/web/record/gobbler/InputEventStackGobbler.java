package com.synaptix.toast.agent.web.record.gobbler;

import java.util.List;

import com.synaptix.toast.agent.web.record.EventStackGobbler;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class InputEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;
	
	String currentTarget = null;


	@Override
	public boolean isInterestedIn(
			WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		boolean isButton = (component.equals("button") 
							|| component.equals("input:submit") 
							|| component.equals("input:button"));
		return "focus".equals(capturedEvent.getEventType()) && component.startsWith("input") && !isButton;
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
		if(finalEvent == null && "blur".equals(capturedEvent.getEventType())){
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
	public List<String> getStopEvents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getStartEvents() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<String> getSupportedComponents() {
		// TODO Auto-generated method stub
		return null;
	}

}
