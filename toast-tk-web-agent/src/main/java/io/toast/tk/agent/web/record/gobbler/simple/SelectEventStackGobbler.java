package io.toast.tk.agent.web.record.gobbler.simple;

import java.util.Arrays;
import java.util.List;

import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class SelectEventStackGobbler extends SimpleEventStackGobbler {

	@Override
	public EventType getInterpretedEventType(
			WebEventRecord capturedEvent) {
		return EventType.JLIST_CLICK;
	}

	@Override
	public List<String> getSupportedComponents() {
		return Arrays.asList("select");
	}

	@Override
	public List<String> getStartEvents() {
		return Arrays.asList("change");
	}

}
