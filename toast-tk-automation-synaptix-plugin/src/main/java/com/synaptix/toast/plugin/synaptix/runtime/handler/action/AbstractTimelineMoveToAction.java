package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ActionTimelineInfo;

public abstract class AbstractTimelineMoveToAction extends AbstractTimelineAction {

	private final JSimpleDaysTimeline simpleDaysTimeline;

	private final ActionTimelineInfo actionTimelineInfo;

	private final SimpleDaysTask taskToClick;

	protected abstract void doAction(final Point pointToClick);
	
	public AbstractTimelineMoveToAction(
			final JSimpleDaysTimeline simpleDaysTimeline,
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask taskToClick
	) {
		super(null);
		this.simpleDaysTimeline = simpleDaysTimeline;
		this.actionTimelineInfo = actionTimelineInfo;
		this.taskToClick = taskToClick;
	}
	
	@Override
	public void run() {
		movetoTo(simpleDaysTimeline, actionTimelineInfo, taskToClick);
		final Point pointToClick = findTimelinePointToClick(simpleDaysTimeline, actionTimelineInfo, taskToClick);
		doAction(pointToClick);
	}
}