package com.synaptix.toast.plugin.swing.agent.record.gobbler.simple;

import javax.swing.JTable;

import com.synaptix.toast.plugin.swing.agent.record.gobbler.EventStackGobbler;

import io.toast.tk.core.agent.interpret.AWTCapturedEvent;
import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;

public class TableClickEventStackGobbler extends EventStackGobbler {

	@Override
	public boolean isInterestedIn(
		AWTCapturedEvent capturedEvent) {
		return isMouseClick(capturedEvent.eventLabel) &&
			isTableType(capturedEvent.componentType);
	}

	public static boolean isTableType(
		String targetType) {
		try {
			Class<?> tClass = Class.forName(targetType);
			boolean isCompliant = JTable.class.isAssignableFrom(tClass) || targetType.contains("Table");
			return isCompliant;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public EventType getInterpretedEventType(
		AWTCapturedEvent capturedEvent) {
		return EventType.TABLE_CLICK;
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
	
	@Override
	public void reset() {
	}
}
