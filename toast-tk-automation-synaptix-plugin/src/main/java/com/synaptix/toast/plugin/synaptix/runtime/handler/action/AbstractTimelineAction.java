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

public abstract class AbstractTimelineAction extends AbstractClickAction<JSimpleDaysTimeline> {

	private static final Logger LOG = LogManager.getLogger(AbstractTimelineAction.class);

	public AbstractTimelineAction(
			final JSimpleDaysTimeline timeline,
			final Point pointToClick
	) {
		super(timeline, pointToClick);
	}

	public void movetoTo(final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask taskToGoTo) {
		try {
			setHorizontalScrollValue(actionTimelineInfo);
			final int re = addSelectionIndex(actionTimelineInfo, taskToGoTo);
			component.scrollRectToVisible(component.getSimpleDayTaskRect(re, taskToGoTo));
		} 
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private int addSelectionIndex(
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask taskToGoTo
	) {
		final int re = component.convertResourceIndexToView(actionTimelineInfo.findedRessource);
		component.getSelectionModel().addSelectionIndexResource(re, taskToGoTo.getDayDateMin(), taskToGoTo);
		return re;
	}

	private void setHorizontalScrollValue(
			final ActionTimelineInfo actionTimelineInfo
	) {
		final int p = component.pointAtDayDate(actionTimelineInfo.dayDateMin);
		component.setHorizontalScrollBarValue(p);
	}
	
	public Point findTimelinePointToClick(final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask findedTask) {
		final Point computeMiddleTaskPoint = computeMiddleTaskPoint(actionTimelineInfo, findedTask);
		final Point locationOnScreen = getTimelineLocationOnScreen();
		return new Point(locationOnScreen.x + computeMiddleTaskPoint.x, locationOnScreen.y + computeMiddleTaskPoint.y);
	}

	private Point getTimelineLocationOnScreen() {
		final JViewport internalTimelineViewport = component.getInternalTimelineViewport();
		final Point locationOnScreen = internalTimelineViewport.getLocationOnScreen();
		final Point viewPosition = internalTimelineViewport.getViewPosition();
		return new Point(locationOnScreen.x - viewPosition.x, locationOnScreen.y - viewPosition.y);
	}

	private Point computeMiddleTaskPoint(
			final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask findedTask
	) {
		final int middleAbscissTask = computeMiddleAbscissTask(findedTask);
		final int middleOrdinateTask = computeMiddleOrdinateTask(actionTimelineInfo);
		return new Point(middleAbscissTask, middleOrdinateTask);
	}

	private int computeMiddleAbscissTask(final SimpleDaysTask findedTask) {
		final int pointAtDayMin = component.pointAtDayDate(normalizedDayDateMin(findedTask.getDayDateMin()));
		final int pointAtDayMax = component.pointAtDayDate(normalizedDayDateMax(findedTask.getDayDateMax()));
		return (pointAtDayMin + pointAtDayMax) / 2;
	}

	private static DayDate normalizedDayDateMin(final DayDate dayDateMin) {
		final DayDate groundZero = new DayDate(0);
		return dayDateMin.before(groundZero) ? groundZero : dayDateMin;
	}

	private DayDate normalizedDayDateMax(final DayDate dayDateMax) {
		final int nbDays = component.getNbDays();
		final DayDate groundInfinite = new DayDate(nbDays + 1);
		return dayDateMax.after(groundInfinite) ? groundInfinite : dayDateMax;
	}

	private int computeMiddleOrdinateTask(final ActionTimelineInfo actionTimelineInfo) {
		final Rectangle resourceRect = component.getResourceRect(actionTimelineInfo.findedRessource);
		return resourceRect.y + resourceRect.height / 2;
	}
}