package com.synaptix.toast.plugin.swing.agent.record.gobbler.simple;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;

public class MenuItemClickEventStackGobbler extends EventStackGobbler {

	AWTCapturedEvent finalEvent = null;

	@Override
	public boolean isInterestedIn(
		AWTCapturedEvent capturedEvent) {
		return isMouseClick(capturedEvent.eventLabel) && 
			(isMenuItemType(capturedEvent.componentType) || isMenuItemType(capturedEvent.componentType));
	}

	@Override
	public boolean isLooper() {
		return true;
	}

	@Override
	public EventStackGobbler digest(
		AWTCapturedEvent capturedEvent) {
		finalEvent = cloneEvent(capturedEvent);
		if(isMouseClick(capturedEvent.eventLabel)) {
			finalEvent.componentName = capturedEvent.componentName;
		}
		return this;
	}

	@Override
	public boolean isCompleted() {
		return finalEvent != null;
	}

	@Override
	public AWTCapturedEvent getAdjustedEvent() {
		return finalEvent;
	}

	@Override
	public EventType getInterpretedEventType(
		AWTCapturedEvent capturedEvent) {
		return EventType.MENU_CLICK;
	}

	private static boolean isMenuItemType(
		String targetType) {
		return "JMenuItem".equals(targetType);
	}
	
	@Override
	public void reset() {
		this.finalEvent = null;
	}
}
