package com.synaptix.toast.plugin.synaptix.runtime.listener;

import java.awt.AWTEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.synaptix.toast.core.guice.FilteredAWTEventListener;
import com.synaptix.toast.plugin.synaptix.runtime.recorder.EventRecorderDispatcher;

public class TimelineFilteredAWTEventListener implements FilteredAWTEventListener {

	protected static final Logger LOG = LoggerFactory.getLogger(TimelineFilteredAWTEventListener.class);

	@Inject
	private EventRecorderDispatcher eventRecorderDispatcher;

	@Override
	public void eventDispatched(final AWTEvent event) {
		LOG.info("{}", event);
		eventRecorderDispatcher.eventDispatched(event);
	}

	@Override
	public long getEventMask() {
		return AWTEvent.MOUSE_EVENT_MASK;
	}
}