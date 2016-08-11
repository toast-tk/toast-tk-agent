package io.toast.tk.agent.web.record.gobbler.loop;

import java.util.Arrays;
import java.util.List;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public abstract class InputEventStackGobbler extends LoopEventStackGobbler {

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
	public List<String> getStopEvents() {
		return Arrays.asList("change", "blur");
	}

}
