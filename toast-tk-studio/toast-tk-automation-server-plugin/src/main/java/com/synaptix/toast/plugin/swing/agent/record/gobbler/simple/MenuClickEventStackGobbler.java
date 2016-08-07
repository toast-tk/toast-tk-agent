package com.synaptix.toast.plugin.swing.agent.record.gobbler.simple;

import javax.swing.JMenu;

import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;

import io.toast.tk.core.agent.interpret.AWTCapturedEvent;
import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;

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
		try {
			Class<?> tClass = Class.forName(targetType);
			boolean isCompliant = JMenu.class.isAssignableFrom(tClass) || targetType.contains("JMenu");
			return isCompliant;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void reset() {
	}
}
