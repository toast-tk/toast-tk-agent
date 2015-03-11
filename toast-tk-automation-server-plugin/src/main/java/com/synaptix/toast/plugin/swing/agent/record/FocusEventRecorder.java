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
import java.awt.event.FocusEvent;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;

import org.fest.swing.input.InputState;

import com.synaptix.toast.core.interpret.EventCapturedObject;
import com.synaptix.toast.core.record.IEventRecorder;

public class FocusEventRecorder extends AbstractEventRecorder {

	public FocusEventRecorder(InputState state, IEventRecorder eventRecorder) {
		super(state, eventRecorder);
	}

	@Override
	public void processEvent(AWTEvent event) {
		FocusEvent wEvent = (FocusEvent) event;
		String eventComponentName = null;
		String container = getEventComponentContainer(event);
		if (event.getID() == FocusEvent.FOCUS_GAINED) {
			if (!(wEvent.getComponent() instanceof JTabbedPane) && !(wEvent.getComponent() instanceof JLayeredPane)
					&& !(wEvent.getComponent().getClass().equals(JFrame.class))) {
				return;
			}
			eventRecorder.scanUi(true);

			if (wEvent.getComponent() instanceof JLayeredPane) {
				JLayeredPane p = (JLayeredPane) wEvent.getComponent();
				eventComponentName = p.getToolTipText();
			} else if (wEvent.getComponent() instanceof JTabbedPane) {
				JTabbedPane panel = (JTabbedPane) wEvent.getComponent();
				eventComponentName = panel.getTitleAt(panel.getSelectedIndex());
			} else if ((wEvent.getComponent().getClass().equals(JFrame.class))) {
				JFrame panel = (JFrame) wEvent.getComponent();
				eventComponentName = panel.getTitle();
			} else {
				eventComponentName = wEvent.getComponent().getName();
			}

			EventCapturedObject captureEvent = new EventCapturedObject();
			captureEvent.eventLabel = event.getClass().getSimpleName() + ">";
			captureEvent.componentLocator = getEventComponentLocator(event);
			captureEvent.componentType = wEvent.getComponent().getClass().getSimpleName();
			captureEvent.businessValue = getEventValue(event);
			captureEvent.componentName = eventComponentName;
			captureEvent.container = container;
			captureEvent.timeStamp = System.nanoTime();
			
			appendEventRecord(captureEvent);
		} else if (event.getID() == FocusEvent.FOCUS_LOST) {

			EventCapturedObject captureEvent = new EventCapturedObject();
			captureEvent.eventLabel = event.getClass().getSimpleName() + "<";
			captureEvent.componentLocator = getEventComponentLocator(event);
			captureEvent.componentType = wEvent.getComponent().getClass().getSimpleName();
			captureEvent.businessValue = getEventValue(event);
			captureEvent.componentName = wEvent.getComponent().getName();
			captureEvent.container = container;
			
			appendEventRecord(captureEvent);
		}		
	}

	@Override
	public long getEventMask() {
		return AWTEvent.FOCUS_EVENT_MASK;
	}
}