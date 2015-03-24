package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.synaptix.toast.core.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.interpret.InterpretedEvent;
import com.synaptix.toast.core.record.RecordedEvent;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventInterpreter;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.TimelineDataEvent;
import com.synaptix.toast.plugin.synaptix.runtime.model.TaskOnRessource;
import com.synaptix.utils.MouseHelper;

public class SimpleTimelineSelectionEventRecorder extends AbstractEventRecorder {

	private static final Logger LOG = LoggerFactory.getLogger(SimpleTimelineSelectionEventRecorder.class);

	@Inject
	protected ISwingInspectionServer cmdServer;

	@Inject
	protected EventInterpreter eventInterpreter;

	@Override
	public boolean isInterestedIn(final AWTEvent awtEvent) {
		if(isMouseEvent(awtEvent)) {
			final MouseEvent mouseEvent = (MouseEvent) awtEvent;
			final boolean simpleLeftClick = MouseHelper.isSimpleLeftClick((MouseEvent) awtEvent);
			LOG.info("simpleLeftClick {}", Boolean.valueOf(simpleLeftClick));
			final boolean isTimelineEvent = isTimelineEvent(mouseEvent);
			LOG.info("isTimelineEvent {}", Boolean.valueOf(isTimelineEvent));
			return simpleLeftClick && isTimelineEvent;
		}
		return false;
	}

	@Override
	public void makeRecord(final AWTEvent awtEvent) {
		final MouseEvent mouseEvent = (MouseEvent) awtEvent;
		final Component component = retrieveComponentFromEvent(mouseEvent);
		LOG.info("retrieveComponentFromEvent {}", component);
		final /*JSimpleDaysTimeline*/Object simpleDaysTimeline = retrieveSimpleDaysTimeline(component);
		LOG.info("simpleDaysTimeline {}", simpleDaysTimeline);
		handleSelectionEventForTimeline(simpleDaysTimeline, mouseEvent);
	}
	
	private void handleSelectionEventForTimeline(
			final /*JSimpleDaysTimeline*/Object simpleDaysTimeline,
			final AWTEvent event
	) {
		final TaskOnRessource findCurrentSelectedTask = findCurrentSelectedTask(simpleDaysTimeline);
		if(findCurrentSelectedTask != null) {
			LOG.info("findCurrentSelectedTask {}", findCurrentSelectedTask);
			final RecordedEvent buildRecordedEventFromTask = buildRecordedEventFromTask(findCurrentSelectedTask, simpleDaysTimeline, event);
			LOG.info("buildRecordedEventFromTask.getEventData() {}", buildRecordedEventFromTask.getEventData());
			final InterpretedEvent interpreteEvent = eventInterpreter.interpreteEvent(buildRecordedEventFromTask);
			String eventData = interpreteEvent.getEventData();
			LOG.info("eventData {}", eventData);
			if(EventTransformer.isInterestingEvent(eventData)) {
				cmdServer.publishInterpretedEvent(eventData);
			}
			final TimelineDataEvent timelineDataEvent = buildTimelineDataEvent(findCurrentSelectedTask, simpleDaysTimeline, event);
			LOG.info("timelineDataEvent {}", timelineDataEvent);
		}
		else {
			LOG.info("no task selected");
		}
	}
}