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
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.record.IEventRecorder;

public class WindowEventRecorder extends AbstractEventRecorder {

	public WindowEventRecorder(
			final InputState state, 
			final IEventRecorder eventRecorder
	) {
		super(state, eventRecorder);
	}

	@Override
	public void processEvent(final AWTEvent event) {
		if(isGainedFocusWindowEvent(event)) {
			eventRecorder.scanUi(true);
			String eventComponentName = null;
			final WindowEvent wEvent = (WindowEvent) event;
			final Window w = wEvent.getWindow();
			if (w instanceof Dialog) {
				eventComponentName = ((Dialog) w).getTitle();
			}
			if (eventComponentName != null) {
				final AWTEventCapturedObject captureEvent = buildWindowsEventCapturedObject(event, eventComponentName, wEvent);
				appendEventRecord(captureEvent);
			}
		}		
	}

	private AWTEventCapturedObject buildWindowsEventCapturedObject(
			final AWTEvent event, 
			final String eventComponentName, 
			final WindowEvent wEvent
	) {
		AWTEventCapturedObject captureEvent = new AWTEventCapturedObject();
		captureEvent.eventLabel = event.getClass().getSimpleName();
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.componentType = wEvent.getComponent().getClass().getSimpleName();
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentName = eventComponentName;
		captureEvent.container = getEventComponentContainer(event);
		captureEvent.timeStamp = System.nanoTime();
		return captureEvent;
	}

	private static boolean isGainedFocusWindowEvent(final AWTEvent event) {
		return event.getID() == WindowEvent.WINDOW_GAINED_FOCUS;
	}

	@Override
	public long getEventMask() {
		return AWTEvent.WINDOW_EVENT_MASK;
	}
}