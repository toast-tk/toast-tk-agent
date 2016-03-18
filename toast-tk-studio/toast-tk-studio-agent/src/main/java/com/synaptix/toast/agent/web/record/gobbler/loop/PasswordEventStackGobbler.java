package com.synaptix.toast.agent.web.record.gobbler.loop;

import java.util.Arrays;
import java.util.List;

import com.synaptix.toast.agent.web.record.LoopEventStackGobbler;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class PasswordEventStackGobbler extends LoopEventStackGobbler {

	@Override
	public EventType getInterpretedEventType(
			WebEventRecord capturedEvent) {
		return EventType.KEY_INPUT;
	}

	@Override
	public List<String> getStartEvents() {
		return Arrays.asList("click", "focus", "keypress");
	}
	
	@Override
	public String getStopEvent() {
		return "change";
	}

	@Override
	public String getComponentType() {
		return "password";
	}


}
