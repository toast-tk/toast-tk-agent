package com.synaptix.toast.plugin.synaptix.runtime.interpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.toast.core.interpret.InterpretedEvent;
import com.synaptix.toast.core.record.RecordedEvent;
import com.synaptix.toast.plugin.synaptix.runtime.split.Split;

public final class BaseEventInterpreter implements EventInterpreter {
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseEventInterpreter.class);

	public BaseEventInterpreter() {

	}

	@Override
	public InterpretedEvent interpreteEvent(final RecordedEvent recordedEvent) {
		final StringBuilder sb = new StringBuilder(128);
		Split.add(sb, "timeline ");
		final String eventData = recordedEvent.getEventData();
		final String[] split = Split.split(eventData);
		final String mouseEvent = split[0];
		LOG.info("mouseEvent = {}", mouseEvent);
		final String nbClick = split[1];
		LOG.info("nbClick = {}", mouseEvent);
		final String modifiers = split[2];
		LOG.info("modifiers = {}", modifiers);
		final String eventToAction = EventTransformer.eventToAction(
				Integer.parseInt(mouseEvent),
				Integer.parseInt(nbClick),
				Integer.parseInt(modifiers)
		);
		LOG.info("eventToAction = {}", eventToAction);
		Split.add(sb, eventToAction);
		Split.add(sb, " ");
		LOG.info("split[3] = {}", split[3]);
		Split.add(sb, split[3]);
		Split.add(sb, " du ");
		Split.add(sb, " (");
		LOG.info("split[4] = {}", split[4]);
		Split.add(sb, split[4]);
		Split.add(sb, " ,");
		LOG.info("split[5] = {}", split[5]);
		Split.add(sb, split[5]);
		Split.add(sb, " ,");
		LOG.info("split[6] = {}", split[6]);
		Split.add(sb, split[6]);
		Split.add(sb, ") / ");
		Split.add(sb, " (");
		LOG.info("split[7] = {}", split[7]);
		Split.add(sb, split[7]);
		Split.add(sb, " ,");
		LOG.info("split[8] = {}", split[8]);
		Split.add(sb, split[8]);
		Split.add(sb, " ,");
		LOG.info("split[9] = {}", split[9]);
		Split.add(sb, split[9]);
		Split.add(sb, ")");
		Split.add(sb, " de ");
		LOG.info("split[11] = {}", split[11]);
		LOG.info("split[12] = {}", split[12]);
		Split.add(sb, split[12]);
		Split.add(sb, " du ");
		LOG.info("split[13] = {}", split[13]);
		Split.add(sb, split[13]);
		return new InterpretedEvent(sb.toString(), Long.valueOf(0L));
	}
}