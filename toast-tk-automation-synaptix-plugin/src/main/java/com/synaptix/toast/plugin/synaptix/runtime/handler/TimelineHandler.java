package com.synaptix.toast.plugin.synaptix.runtime.handler;

import com.synaptix.swing.JSimpleDaysTimeline;

public final class TimelineHandler {

	private final String commandRequestValue;
	
	private final JSimpleDaysTimeline simpleDaysTimeline;
	
	public TimelineHandler(
			final String commandRequestValue,
			final JSimpleDaysTimeline simpleDaysTimeline
	) {
		this.commandRequestValue = commandRequestValue;
		this.simpleDaysTimeline = simpleDaysTimeline;
	}
}