package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.SimpleDaysTimelineModel;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.TimelineMoveToPointAndDoClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.TimelineMoveToPointAndDoDoubleClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.TimelineMoveToPointAndOpenMenuAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.sentence.SentenceFinder;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;

public final class JSimpleDaysTimelineHandler extends AbstractSwingCustomWidgetHandler {

	private static final Logger LOG = LoggerFactory.getLogger(JSimpleDaysTimelineHandler.class);
	
	private final String commandRequestValue;
	
	private final JSimpleDaysTimeline simpleDaysTimeline;
	
	public JSimpleDaysTimelineHandler(
			final String commandRequestValue,
			final JSimpleDaysTimeline simpleDaysTimeline
	) {
		this.commandRequestValue = commandRequestValue;
		this.simpleDaysTimeline = simpleDaysTimeline;
	}
	
	@Override
	public String handleCommand() {
		try {
			handleTimelineCommandTask();
		} 
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private void handleTimelineCommandTask(
	) {
		final SentenceFinder sentenceFinder = new SentenceFinder(commandRequestValue);
		if(sentenceFinder.isAValidSentence()) {
			final SimpleDaysTask findedTask = findTaskToClick(sentenceFinder.actionTimelineInfo);
			if (findedTask != null) {
				final ActionTimelineInfo actionTimelineInfo = sentenceFinder.actionTimelineInfo;
				if(EventTransformer.DOUBLE_CLIQUER_SUR.equals(actionTimelineInfo.action)) {
					moveToPointAndDoDoubleClick(sentenceFinder.actionTimelineInfo, findedTask);
				}
				else if(EventTransformer.CLIQUER_SUR.equals(actionTimelineInfo.action)) {
					moveToPointAndDoClick(sentenceFinder.actionTimelineInfo, findedTask);
				}
				else if(EventTransformer.OUVRIR_LE_MENU_SUR.equals(actionTimelineInfo.action)) {
					moveToPointAndDoOpenMenu(sentenceFinder.actionTimelineInfo, findedTask);
				}
				else {
					throw new IllegalAccessError("unknown action for timeline");
				}
			}
			else {
				// send not found message to server
			}
		}
		else {
			LOG.info("sentence invalid {}", sentenceFinder);
		}
	}

	private void moveToPointAndDoClick(
			final ActionTimelineInfo actionTimelineInfo, 
			final SimpleDaysTask taskToClick
	) {
		runAction(new TimelineMoveToPointAndDoClickAction(simpleDaysTimeline, actionTimelineInfo, taskToClick));
	}
	
	private void moveToPointAndDoDoubleClick(
			final ActionTimelineInfo actionTimelineInfo, 
			final SimpleDaysTask taskToClick
	) {
		runAction(new TimelineMoveToPointAndDoDoubleClickAction(simpleDaysTimeline, actionTimelineInfo, taskToClick));
	}

	private void moveToPointAndDoOpenMenu(
			final ActionTimelineInfo actionTimelineInfo, 
			final SimpleDaysTask taskToClick
	) {
		runAction(new TimelineMoveToPointAndOpenMenuAction(simpleDaysTimeline, actionTimelineInfo, taskToClick));
	}

	private int findRessource(final ActionTimelineInfo actionTimelineInfo) {
		final SimpleDaysTimelineModel model = simpleDaysTimeline.getModel();
		final int resourceCount = model.getResourceCount();
		for(int index = 0; index < resourceCount; ++index) {
			final String simpleName = model.getSimpleName(index);
			if(actionTimelineInfo.ressourceName.contains(simpleName)) {
				return index;
			}
		}
		return -1;
	}

	private SimpleDaysTask findTaskToClick(final ActionTimelineInfo actionTimelineInfo) {
		final int findedRessource = findRessource(actionTimelineInfo);
		if(findedRessource != -1) {
			actionTimelineInfo.findedRessource = findedRessource;
			final List<? extends SimpleDaysTask> tasks = simpleDaysTimeline.getModel().getTasks(
					findedRessource,
					actionTimelineInfo.dayDateMin,
					actionTimelineInfo.dayDateMax
			);
			filterByHint(tasks, actionTimelineInfo);
			return tasks != null && tasks.size() > 0 ? tasks.get(0) : null;
		}
		LOG.info("No Task Finded for {} in {}", actionTimelineInfo.ressourceName, actionTimelineInfo.container);
		return null;
	}

	private static void filterByHint(
			final List<? extends SimpleDaysTask> tasks,
			final ActionTimelineInfo actionTimelineInfo
	) {
		final String taskType = actionTimelineInfo.taskType;
		final Iterator<? extends SimpleDaysTask> tasksIterator = tasks.iterator();
		while(tasksIterator.hasNext()) {
			final SimpleDaysTask next = tasksIterator.next();
			if(isUninterestingTaskClass(taskType, next)) {
				tasksIterator.remove();
			}
		}
	}

	private static boolean isUninterestingTaskClass(
			final String taskType,
			final SimpleDaysTask next
	) {
		return !taskType.equals(next.getClass().getName());
	}
}