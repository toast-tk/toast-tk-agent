package com.synaptix.toast.plugin.swing.agent.record.gobbler.simple;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;

public class MenuClickEventStackGobbler extends EventStackGobbler {


	@Override
	public boolean isInterestedIn(
		AWTCapturedEvent capturedEvent) {
		return isMouseClick(capturedEvent.eventLabel) && 
			(isMenuType(capturedEvent.componentType));
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
		return null;
	}

	@Override
	public EventType getInterpretedEventType(
		AWTCapturedEvent capturedEvent) {
		return EventType.MENU_CLICK;
	}

	private static boolean isMenuType(
		String targetType) {
		return "JMenu".equals(targetType);
	}

	@Override
	public void reset() {
	}
}
