package io.toast.tk.agent.web.record.gobbler.simple;

import java.util.List;

import io.toast.tk.agent.web.record.EventStackGobbler;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public abstract class SimpleEventStackGobbler extends EventStackGobbler {

	@Override
	public boolean isLooper() {
		return false;
	}

	@Override
	public EventStackGobbler digest(
			WebEventRecord capturedEvent) {
		return this;
	}
	
	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public void reset() {
	}
	
	@Override
	public WebEventRecord getAdjustedEvent() {
		return null;
	}

	@Override
	public List<String> getStopEvents() {
		return null;
	}
}
