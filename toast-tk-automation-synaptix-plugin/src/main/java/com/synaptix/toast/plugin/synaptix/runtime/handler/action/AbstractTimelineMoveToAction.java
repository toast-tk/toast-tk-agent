package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ActionTimelineInfo;

public abstract class AbstractTimelineMoveToAction extends AbstractTimelineAction {

	private final ActionTimelineInfo actionTimelineInfo;

	private final SimpleDaysTask taskToClick;

	protected abstract void doAction();
	
	public AbstractTimelineMoveToAction(
			final JSimpleDaysTimeline simpleDaysTimeline,
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask taskToClick
	) {
		super(simpleDaysTimeline, null);
		this.actionTimelineInfo = actionTimelineInfo;
		this.taskToClick = taskToClick;
	}
	
	@Override
	public void run() {
		movetoTo(actionTimelineInfo, taskToClick);
		this.pointToClick = findTimelinePointToClick(actionTimelineInfo, taskToClick);
		doAction();
	}
}