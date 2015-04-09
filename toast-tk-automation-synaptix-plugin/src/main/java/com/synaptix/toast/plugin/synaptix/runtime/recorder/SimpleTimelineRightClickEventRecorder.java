package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.toast.core.record.RecordedEvent;
import com.synaptix.toast.plugin.synaptix.runtime.model.TaskOnRessource;
import com.synaptix.utils.MouseHelper;

public class SimpleTimelineRightClickEventRecorder extends AbstractEventRecorder {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleTimelineRightClickEventRecorder.class);

	@Override
	public boolean isInterestedIn(final AWTEvent awtEvent) {
		if(isMouseEvent(awtEvent)) {
			final MouseEvent mouseEvent = (MouseEvent) awtEvent;
			final boolean rightClick = MouseHelper.isRightClick((MouseEvent) awtEvent);
			if(rightClick) {
				boolean isTimelineEvent = isTimelineEvent(mouseEvent);
				if(isTimelineEvent) {
					final boolean isMouseReleased = MouseHelper.isMouseReleased(mouseEvent);
					LOG.info("isMouseReleased {}", Boolean.valueOf(isMouseReleased));
					return isMouseReleased;
				}
				return false;
			}
			return false;
		}
		return false;
	}

	@Override
	protected void makeRecord(final AWTEvent awtEvent) {
		final MouseEvent mouseEvent = (MouseEvent) awtEvent;
		final Component component = retrieveComponentFromEvent(mouseEvent);
		final /*JSimpleDaysTimeline*/Object simpleDaysTimeline = retrieveSimpleDaysTimeline(component);
		handleEditionEventTimeline(simpleDaysTimeline, mouseEvent);
	}

	private void handleEditionEventTimeline(
			final /*JSimpleDaysTimeline*/Object simpleDaysTimeline,
			final AWTEvent event
	) {
		final TaskOnRessource findCurrentSelectedTask = findCurrentSelectedTask(simpleDaysTimeline);
		if(findCurrentSelectedTask != null) {
			final RecordedEvent buildRecordedEventFromTask = buildRecordedEventFromTask(findCurrentSelectedTask, simpleDaysTimeline, event);
			cmdServer.publishInterpretedEvent(buildRecordedEventFromTask.getEventData());
		}
		else {
			LOG.info("no task selected");
		}
	}
}