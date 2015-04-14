package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ActionTimelineInfo;

public final class TimelineMoveToPointAndDoClickAction extends AbstractTimelineMoveToAction {

	public TimelineMoveToPointAndDoClickAction(
			final JSimpleDaysTimeline simpleDaysTimeline,
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask taskToClick
	) {
		super(simpleDaysTimeline, actionTimelineInfo, taskToClick);
	}

	@Override
	public void doAction() {
		doSimpleClick();
	}
}