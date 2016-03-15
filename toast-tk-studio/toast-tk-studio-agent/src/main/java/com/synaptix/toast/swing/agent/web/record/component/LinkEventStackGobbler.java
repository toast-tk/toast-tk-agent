package com.synaptix.toast.swing.agent.web.record.component;

import java.awt.event.KeyEvent;

import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.swing.agent.web.record.EventStackGobbler;

public class LinkEventStackGobbler extends EventStackGobbler {

	WebEventRecord finalEvent = null;


	@Override
	public boolean isInterestedIn(
			WebEventRecord capturedEvent) {
		String component = capturedEvent.getComponent() != null ? capturedEvent.getComponent() : "";
		return "focus".equals(capturedEvent.getType()) && component.startsWith("a");
	}

	public boolean isInputEvent(
		String eventLabel) {
		return KeyEvent.class.getSimpleName().equals(eventLabel);
	}

	@Override
	public boolean isLooper() {
		return true;
	}

	@Override
	public EventStackGobbler digest(
			WebEventRecord capturedEvent) {
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
	}

}
