package com.synaptix.toast.plugin.swing.agent.record;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JTable;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.record.IEventRecorder;

public class MouseEventRecorder extends AbstractEventRecorder {

	public MouseEventRecorder(
		final InputState state,
		final IEventRecorder recorder) {
		super(state, recorder);
	}

	@Override
	public void processEvent(
		final AWTEvent event) {
		if(isReleasedMouseEvent(event)) {
			final MouseEvent mEvent = (MouseEvent) event;
			final AWTCapturedEvent captureEvent = buildMouseEventCapturedObject(event);
			if(isCapturedEventUninteresting(captureEvent)) {
				return;
			}
			final String classTypeSimpleName = findClassName(mEvent);
			captureEvent.componentType = classTypeSimpleName;
			captureEvent.timeStamp = System.nanoTime();
			appendEventRecord(captureEvent);
		}
	}

	private static String findClassName(
		final MouseEvent mEvent) {
		final Component component = mEvent.getComponent();
		final Class<? extends Component> componentClass = component.getClass();
		final String classTypeSimpleName = componentClass.getSimpleName();
		if(classTypeSimpleName == null || "".equals(classTypeSimpleName)) {
			if(component instanceof JTable) {
				return "JTable";
			}
			else if(component instanceof JCheckBox) {
				return "JCheckBox";
			}
			else if(component instanceof JList) {
				return "JList";
			}
			else {
				return componentClass.getName();
			}
		}
		return classTypeSimpleName;
	}

	private static boolean isReleasedMouseEvent(
		final AWTEvent event) {
		return event.getID() == MouseEvent.MOUSE_RELEASED;
	}

	private AWTCapturedEvent buildMouseEventCapturedObject(
		final AWTEvent event) {
		final AWTCapturedEvent captureEvent = new AWTCapturedEvent();
		captureEvent.eventLabel = event.getClass().getSimpleName();
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentName = getEventComponentLabel(event);
		captureEvent.container = getEventComponentContainer(event);
		return captureEvent;
	}

	@Override
	public long getEventMask() {
		return AWTEvent.MOUSE_EVENT_MASK;
	}
}