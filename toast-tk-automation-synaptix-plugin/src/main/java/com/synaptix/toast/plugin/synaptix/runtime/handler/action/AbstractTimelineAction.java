package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JViewport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.toast.plugin.synaptix.runtime.handler.ActionTimelineInfo;

public abstract class AbstractTimelineAction extends AbstractClickAction {

	private static final Logger LOG = LogManager.getLogger(AbstractTimelineAction.class);
	
	static void movetoTo(final JSimpleDaysTimeline timeline, final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask taskToGoTo) {
		try {
			setHorizontalScrollValue(timeline, actionTimelineInfo);
			final int re = addSelectionIndex(timeline, actionTimelineInfo, taskToGoTo);
			timeline.scrollRectToVisible(timeline.getSimpleDayTaskRect(re, taskToGoTo));
		} 
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static int addSelectionIndex(
			final JSimpleDaysTimeline timeline,
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask taskToGoTo
	) {
		final int re = timeline.convertResourceIndexToView(actionTimelineInfo.findedRessource);
		timeline.getSelectionModel().addSelectionIndexResource(re, taskToGoTo.getDayDateMin(), taskToGoTo);
		return re;
	}

	private static void setHorizontalScrollValue(
			final JSimpleDaysTimeline timeline,
			final ActionTimelineInfo actionTimelineInfo
	) {
		final int p = timeline.pointAtDayDate(actionTimelineInfo.dayDateMin);
		timeline.setHorizontalScrollBarValue(p);
	}
	
	static Point findTimelinePointToClick(final JSimpleDaysTimeline simpleDaysTimeline, final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask findedTask) {
		final Point computeMiddleTaskPoint = computeMiddleTaskPoint(simpleDaysTimeline, actionTimelineInfo, findedTask);
		final Point locationOnScreen = getTimelineLocationOnScreen(simpleDaysTimeline);
		return new Point(locationOnScreen.x + computeMiddleTaskPoint.x, locationOnScreen.y + computeMiddleTaskPoint.y);
	}

	private static Point getTimelineLocationOnScreen(final JSimpleDaysTimeline simpleDaysTimeline) {
		final JViewport internalTimelineViewport = simpleDaysTimeline.getInternalTimelineViewport();
		final Point locationOnScreen = internalTimelineViewport.getLocationOnScreen();
		final Point viewPosition = internalTimelineViewport.getViewPosition();
		return new Point(locationOnScreen.x - viewPosition.x, locationOnScreen.y - viewPosition.y);
	}

	private static Point computeMiddleTaskPoint(
			final JSimpleDaysTimeline simpleDaysTimeline, 
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask findedTask
	) {
		final int middleAbscissTask = computeMiddleAbscissTask(simpleDaysTimeline, findedTask);
		final int middleOrdinateTask = computeMiddleOrdinateTask(simpleDaysTimeline, actionTimelineInfo);
		return new Point(middleAbscissTask, middleOrdinateTask);
	}

	private static int computeMiddleAbscissTask(final JSimpleDaysTimeline simpleDaysTimeline, final SimpleDaysTask findedTask) {
		final int pointAtDayMin = simpleDaysTimeline.pointAtDayDate(normalizedDayDateMin(findedTask.getDayDateMin()));
		final int pointAtDayMax = simpleDaysTimeline.pointAtDayDate(normalizedDayDateMax(findedTask.getDayDateMax(), simpleDaysTimeline));
		return (pointAtDayMin + pointAtDayMax) / 2;
	}

	private static DayDate normalizedDayDateMin(final DayDate dayDateMin) {
		final DayDate groundZero = new DayDate(0);
		return dayDateMin.before(groundZero) ? groundZero : dayDateMin;
	}

	private static DayDate normalizedDayDateMax(final DayDate dayDateMax, final JSimpleDaysTimeline simpleDaysTimeline) {
		final int nbDays = simpleDaysTimeline.getNbDays();
		final DayDate groundInfinite = new DayDate(nbDays + 1);
		return dayDateMax.after(groundInfinite) ? groundInfinite : dayDateMax;
	}

	private static int computeMiddleOrdinateTask(final JSimpleDaysTimeline simpleDaysTimeline, final ActionTimelineInfo actionTimelineInfo) {
		final Rectangle resourceRect = simpleDaysTimeline.getResourceRect(actionTimelineInfo.findedRessource);
		return resourceRect.y + resourceRect.height / 2;
	}
}