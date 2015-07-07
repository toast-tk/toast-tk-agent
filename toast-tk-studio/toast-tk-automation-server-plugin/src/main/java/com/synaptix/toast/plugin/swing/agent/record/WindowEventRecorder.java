package com.synaptix.toast.plugin.swing.agent.record;

import java.awt.AWTEvent;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.record.IEventRecorder;

public class WindowEventRecorder extends AbstractEventRecorder {

	public WindowEventRecorder(
		final InputState state,
		final IEventRecorder eventRecorder) {
		super(state, eventRecorder);
	}

	@Override
	public void processEvent(
		final AWTEvent event) {
		if(isGainedFocusWindowEvent(event)) {
			eventRecorder.scanUi(true);
			String eventComponentName = null;
			final WindowEvent wEvent = (WindowEvent) event;
			final Window w = wEvent.getWindow();
			if(w instanceof Dialog) {
				eventComponentName = ((Dialog) w).getTitle();
			}
			if(eventComponentName != null) {
				final AWTCapturedEvent captureEvent = buildWindowsEventCapturedObject(event, eventComponentName, wEvent);
				appendEventRecord(captureEvent);
			}
		}
	}

	private AWTCapturedEvent buildWindowsEventCapturedObject(
		final AWTEvent event,
		final String eventComponentName,
		final WindowEvent wEvent
		) {
		AWTCapturedEvent captureEvent = new AWTCapturedEvent();
		captureEvent.eventLabel = event.getClass().getSimpleName();
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.componentType = wEvent.getComponent().getClass().getSimpleName();
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentName = eventComponentName;
		captureEvent.container = getEventComponentContainer(event);
		captureEvent.timeStamp = System.nanoTime();
		return captureEvent;
	}

	private static boolean isGainedFocusWindowEvent(
		final AWTEvent event) {
		return event.getID() == WindowEvent.WINDOW_GAINED_FOCUS;
	}

	@Override
	public long getEventMask() {
		return AWTEvent.WINDOW_EVENT_MASK;
	}
}