package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;
import java.util.Set;

import com.google.inject.Inject;
import com.synaptix.toast.core.record.RecordedEvent;

public final class EventRecorderDispatcher {

	@Inject
	private Set<AbstractEventRecorder> eventRecorders;

	public RecordedEvent eventDispatched(final AWTEvent awtEvent) {
		for(final AbstractEventRecorder eventRecorder : eventRecorders) {
			if(eventRecorder.isInterestedIn(awtEvent)) {
				eventRecorder.recorde(awtEvent);
			}
		}
		return RecordedEvent.getNoRecordEvent();
	}
}