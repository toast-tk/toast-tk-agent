package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ActionTimelineInfo;

public final class TimelineMoveToPointAndOpenMenuAction extends AbstractTimelineMoveToAction {
	
	public TimelineMoveToPointAndOpenMenuAction(
			final JSimpleDaysTimeline simpleDaysTimeline,
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask taskToClick
	) {
		super(simpleDaysTimeline, actionTimelineInfo, taskToClick);
	}

	@Override
	public void doAction() {
		doOpenMenu();
	}
}
