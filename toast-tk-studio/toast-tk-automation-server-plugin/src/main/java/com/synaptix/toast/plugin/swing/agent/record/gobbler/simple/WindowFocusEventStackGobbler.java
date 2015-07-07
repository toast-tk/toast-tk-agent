package com.synaptix.toast.plugin.swing.agent.record.gobbler.simple;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;

public class WindowFocusEventStackGobbler extends EventStackGobbler {

	@Override
	public boolean isInterestedIn(
		AWTCapturedEvent capturedEvent) {
		return isWindowClickEvent(capturedEvent.eventLabel);
	}

	private static boolean isWindowClickEvent(
		String eventLabel) {
		return "TimedWindowEvent".equals(eventLabel);
	}

	@Override
	public EventType getInterpretedEventType(
		AWTCapturedEvent capturedEvent) {
		return EventType.WINDOW_DISPLAY;
	}

	@Override
	public boolean isLooper() {
		return false;
	}

	@Override
	public EventStackGobbler digest(
		AWTCapturedEvent capturedEvent) {
		return this;
	}

	@Override
	public boolean isCompleted() {
		return true;
	}

	@Override
	public AWTCapturedEvent getAdjustedEvent() {
		// TODO Auto-generated method stub
		return null;
	}
}
