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
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JTable;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.record.IEventRecorder;

public class MouseEventRecorder extends AbstractEventRecorder {

	public MouseEventRecorder(
			final InputState state, 
			final IEventRecorder recorder
	) {
		super(state, recorder);
	}

	@Override
	public void processEvent(final AWTEvent event) {
		if(isReleasedMouseEvent(event)) {
			final MouseEvent mEvent = (MouseEvent) event;
			final AWTEventCapturedObject captureEvent = buildMouseEventCapturedObject(event);
			if(isCapturedEventUninteresting(captureEvent)) {
				return;
			}
			
			final String classTypeSimpleName = findClassName(mEvent);
			captureEvent.componentType = classTypeSimpleName;
			captureEvent.timeStamp = System.nanoTime();
			appendEventRecord(captureEvent);
		}
	}

	private static String findClassName(final MouseEvent mEvent) {
		final Component component = mEvent.getComponent();
		final Class<? extends Component> componentClass = component.getClass();
		final String classTypeSimpleName = componentClass.getSimpleName();
		if (classTypeSimpleName == null || "".equals(classTypeSimpleName)) {
			if (component instanceof JTable) {
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

	private static boolean isReleasedMouseEvent(final AWTEvent event) {
		return event.getID() == MouseEvent.MOUSE_RELEASED;
	}

	private AWTEventCapturedObject buildMouseEventCapturedObject(final AWTEvent event) {
		final AWTEventCapturedObject captureEvent = new AWTEventCapturedObject();
		captureEvent.eventLabel = event.getClass().getSimpleName();
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentName =  getEventComponentLabel(event);
		captureEvent.container = getEventComponentContainer(event);
		return captureEvent;
	}

	@Override
	public long getEventMask() {
		return AWTEvent.MOUSE_EVENT_MASK;
	}
}