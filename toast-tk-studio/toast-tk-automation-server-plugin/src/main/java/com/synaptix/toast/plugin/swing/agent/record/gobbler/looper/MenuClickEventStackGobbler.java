package com.synaptix.toast.plugin.swing.agent.record.gobbler.looper;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;


public class MenuClickEventStackGobbler extends EventStackGobbler{

	AWTCapturedEvent finalEvent = null;
	String contextMenu = null;
	
	@Override
	public boolean isInterestedIn(AWTCapturedEvent capturedEvent) {
		this.contextMenu = capturedEvent.componentName;
		return isMouseClick(capturedEvent.eventLabel) && 
				isMenuType(capturedEvent.componentType);
	}
	
	@Override
	public boolean isLooper() {
		return false;
	}
	
	@Override
	public EventStackGobbler digest(AWTCapturedEvent capturedEvent) {
		finalEvent = cloneEvent(capturedEvent);
		if (isMouseClick(capturedEvent.eventLabel)) {
			String subTargetType = capturedEvent.componentType;
			if (isMenuItemType(subTargetType)) {
				finalEvent.componentName = this.contextMenu + " / " + capturedEvent.componentName;
			}
		} else {
			finalEvent.componentName = this.contextMenu;
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
	public EventType getInterpretedEventType(AWTCapturedEvent capturedEvent) {
		return EventType.MENU_CLICK;
	}

	private static boolean isMenuType(String targetType) {
		return "JMenu".equals(targetType);
	}
	
	private static boolean isMenuItemType(String targetType) {
		return "JMenuItem".equals(targetType);
	}

}
