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
import java.awt.event.KeyEvent;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.agent.interpret.AWTEventCapturedObject;
import com.synaptix.toast.core.record.IEventRecorder;

import static com.synaptix.toast.plugin.swing.agent.listener.InspectionUtils.*;

public class KeyboardEventRecorder extends AbstractEventRecorder {

	public KeyboardEventRecorder(InputState state, IEventRecorder eventRecorder) {
		super(state, eventRecorder);
	}
	
	@Override
	public void processEvent(AWTEvent event) {
		if (isKeyReleasedEvent(event)) {
			final KeyEvent kEvent = (KeyEvent) event;
			final AWTEventCapturedObject captureEvent = buildKeyboardEventCapturedObject(event, kEvent);
			if(isCapturedEventUninteresting(captureEvent)) {
				return;
			}
			appendEventRecord(captureEvent);
		}
	}

	private static boolean isKeyReleasedEvent(final AWTEvent event) {
		return event.getID() == KeyEvent.KEY_RELEASED;
	}

	private AWTEventCapturedObject buildKeyboardEventCapturedObject(
			final AWTEvent event, 
			final KeyEvent kEvent
	) {
		final AWTEventCapturedObject captureEvent = new AWTEventCapturedObject();
		captureEvent.businessValue = getEventValue(event);
		captureEvent.componentLocator = getEventComponentLocator(event);
		captureEvent.componentName = getEventComponentLabel(event);
		captureEvent.container = getEventComponentContainer(event);
		captureEvent.componentType = kEvent.getComponent().getClass().getSimpleName();
		captureEvent.eventLabel = event.getClass().getSimpleName();
		captureEvent.timeStamp = System.nanoTime();
		return captureEvent;
	}
	
	@Override
	public long getEventMask() {
		return AWTEvent.KEY_EVENT_MASK;
	}
}