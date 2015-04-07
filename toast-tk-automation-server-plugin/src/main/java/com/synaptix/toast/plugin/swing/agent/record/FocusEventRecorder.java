/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 6 févr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */

package com.synaptix.toast.plugin.swing.agent.record;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.FocusEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.interpret.EventCapturedObject;
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
			final EventCapturedObject captureEvent = buildFocusGainEventCapturedEventObject(event);
			if(captureEvent != null) {
				appendEventRecord(captureEvent);
			}
		} 
		else if(isFocusLost(event)) {
			final EventCapturedObject captureEvent = buildFocusLostEventCapturedObject(event);
			appendEventRecord(captureEvent);
		}
	}

	private EventCapturedObject buildFocusGainEventCapturedEventObject(final AWTEvent event) {
		final FocusEvent wEvent = (FocusEvent) event;
		final Component component = wEvent.getComponent();
		if (interestingInstance(component)) {
			final String container = getEventComponentContainer(event);
			eventRecorder.scanUi(true);
			String eventComponentName = getEventComponentName(component);
			final EventCapturedObject captureEvent = buildFocusGainEvent(event, component, eventComponentName, container);
			return captureEvent;
		}
		return null;
	}

	private EventCapturedObject buildFocusLostEventCapturedObject(final AWTEvent event) {
		final FocusEvent wEvent = (FocusEvent) event;
		final String container = getEventComponentContainer(event);
		final EventCapturedObject captureEvent = buildFocusLostEvent(event, wEvent, container);
		return captureEvent;
	}

	private static boolean isFocusLost(final AWTEvent event) {
		return event.getID() == FocusEvent.FOCUS_LOST;
	}

	private static boolean isFocusGained(final AWTEvent event) {
		return event.getID() == FocusEvent.FOCUS_GAINED;
	}

	private EventCapturedObject buildFocusGainEvent(
			final AWTEvent event,
			final Component component, 
			String eventComponentName,
			final String container
	) {
		final EventCapturedObject captureEvent = new EventCapturedObject();
		captureEvent.eventLabel = event.getClass().getSimpleName() + ">";
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.componentType = component.getClass().getSimpleName();
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentName = eventComponentName;
		captureEvent.container = container;
		captureEvent.timeStamp = System.nanoTime();
		return captureEvent;
	}

	private EventCapturedObject buildFocusLostEvent(
			final AWTEvent event,
			final FocusEvent wEvent, 
			final String container
	) {
		final EventCapturedObject captureEvent = new EventCapturedObject();
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