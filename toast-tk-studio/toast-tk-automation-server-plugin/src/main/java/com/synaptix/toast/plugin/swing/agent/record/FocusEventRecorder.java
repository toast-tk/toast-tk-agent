package com.synaptix.toast.plugin.swing.agent.record;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.FocusEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.record.IEventRecorder;

public class FocusEventRecorder extends AbstractEventRecorder {

	public FocusEventRecorder(
			final InputState state,
			final IEventRecorder eventRecorder
	) {
		super(state, eventRecorder);
	}

	@Override
	public void processEvent(final AWTEvent event) {
		if (isFocusGained(event)) {
			final AWTEventCapturedObject captureEvent = buildFocusGainEventCapturedEventObject(event);
			if(captureEvent != null) {
				appendEventRecord(captureEvent);
			}
		} 
		else if(isFocusLost(event)) {
			final AWTEventCapturedObject captureEvent = buildFocusLostEventCapturedObject(event);
			appendEventRecord(captureEvent);
		}
	}

	private AWTEventCapturedObject buildFocusGainEventCapturedEventObject(final AWTEvent event) {
		final FocusEvent wEvent = (FocusEvent) event;
		final Component component = wEvent.getComponent();
		if (interestingInstance(component)) {
			final String container = getEventComponentContainer(event);
			eventRecorder.scanUi(true);
			String eventComponentName = getEventComponentName(component);
			final AWTEventCapturedObject captureEvent = buildFocusGainEvent(event, component, eventComponentName, container);
			return captureEvent;
		}
		return null;
	}

	private AWTEventCapturedObject buildFocusLostEventCapturedObject(final AWTEvent event) {
		final FocusEvent wEvent = (FocusEvent) event;
		final String container = getEventComponentContainer(event);
		final AWTEventCapturedObject captureEvent = buildFocusLostEvent(event, wEvent, container);
		return captureEvent;
	}

	private static boolean isFocusLost(final AWTEvent event) {
		return event.getID() == FocusEvent.FOCUS_LOST;
	}

	private static boolean isFocusGained(final AWTEvent event) {
		return event.getID() == FocusEvent.FOCUS_GAINED;
	}

	private AWTEventCapturedObject buildFocusGainEvent(
			final AWTEvent event,
			final Component component, 
			String eventComponentName,
			final String container
	) {
		final AWTEventCapturedObject captureEvent = new AWTEventCapturedObject();
		captureEvent.eventLabel = event.getClass().getSimpleName() + ">";
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.componentType = component.getClass().getSimpleName();
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentName = eventComponentName;
		captureEvent.container = container;
		captureEvent.timeStamp = System.nanoTime();
		return captureEvent;
	}

	private AWTEventCapturedObject buildFocusLostEvent(
			final AWTEvent event,
			final FocusEvent wEvent, 
			final String container
	) {
		final AWTEventCapturedObject captureEvent = new AWTEventCapturedObject();
		captureEvent.eventLabel = event.getClass().getSimpleName() + "<";
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.componentType = wEvent.getComponent().getClass().getSimpleName();
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentName = wEvent.getComponent().getName();
		captureEvent.container = container;
		captureEvent.timeStamp = System.nanoTime();
		return captureEvent;
	}

	private static String getEventComponentName(final Component component) {
		if (component instanceof JLayeredPane) {
			final JLayeredPane p = (JLayeredPane) component;
			return p.getToolTipText();
		} 
		else if (component instanceof JTabbedPane) {
			final JTabbedPane panel = (JTabbedPane) component;
			return panel.getTitleAt(panel.getSelectedIndex());
		} 
		else if (component.getClass().equals(JFrame.class)) {
			final JFrame panel = (JFrame) component;
			return panel.getTitle();
		} 
		return component.getName();
	}

	private static boolean interestingInstance(final Component component) {
		return 	(component instanceof JTabbedPane)
				|| 
				(component instanceof JLayeredPane)
				|| 
				component.getClass().equals(JFrame.class);
	}

	@Override
	public long getEventMask() {
		return AWTEvent.FOCUS_EVENT_MASK;
	}
}