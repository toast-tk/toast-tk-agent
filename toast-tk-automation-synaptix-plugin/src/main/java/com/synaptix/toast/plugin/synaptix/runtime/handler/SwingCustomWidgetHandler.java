package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JViewport;

import org.fest.swing.core.MouseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.SimpleDaysTimelineModel;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineCenter;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.fixture.utils.FestRobotInstance;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.TimelineHandler;
import com.synaptix.toast.plugin.synaptix.runtime.command.TimelineCommandRequest;
import com.synaptix.toast.plugin.synaptix.runtime.handler.sentence.SentenceFinder;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;

@TimelineHandler
public class SwingCustomWidgetHandler extends AbstractCustomFixtureHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SwingCustomWidgetHandler.class);

	public SwingCustomWidgetHandler() {
		super();
	}

	@Override
	public String makeHanldeFixtureCall(final Component component, final IIdRequest request) {
		if (component instanceof JSimpleDaysTimelineCenter) {
			handleTimeline((JSimpleDaysTimelineCenter) component, request);
		}
		return null;
	}

	private void handleTimeline(final JSimpleDaysTimelineCenter timeline, final IIdRequest command) {
		try {
			if(command instanceof TimelineCommandRequest) {
				final TimelineCommandRequest timelineCommandRequest = (TimelineCommandRequest) command;
				if ("service".equals(timelineCommandRequest.itemType)) {
					handleCommandTask(timelineCommandRequest, timeline);
				}
				else {
					throw new IllegalAccessError("Custom command not supported: " + timelineCommandRequest.customCommand);
				}
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void handleCommandTask(
			final TimelineCommandRequest timelineCommandRequest, 
			final JSimpleDaysTimeline simpleDaysTimeline
	) {
		final SentenceFinder sentenceFinder = new SentenceFinder(timelineCommandRequest.value);
		if(sentenceFinder.isAValidSentence()) {
			final SimpleDaysTask findedTask = findTaskToClick(simpleDaysTimeline, sentenceFinder.actionTimelineInfo);
			if (findedTask != null) {
				moveToPointAndDoClick(simpleDaysTimeline, sentenceFinder.actionTimelineInfo, findedTask);
			}
			else {
				// send not found message to server
			}
		}
		else {
			LOG.info("sentence invalid {}", sentenceFinder);
		}
	}

	private void handleCommandTask(final TimelineCommandRequest timelineCommandRequest, final JSimpleDaysTimelineCenter timeline) {
		final JSimpleDaysTimeline simpleDaysTimeline = timeline.getSimpleDaysTimeline();
		handleCommandTask(timelineCommandRequest, simpleDaysTimeline);
	}

	private void moveToPointAndDoClick(final JSimpleDaysTimeline simpleDaysTimeline, final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask taskToClick) {
		runAction(new Runnable() {
			@Override
			public void run() {
				movetoTo(simpleDaysTimeline, actionTimelineInfo, taskToClick);
				final Point pointToClick = findPointToClick(simpleDaysTimeline, actionTimelineInfo, taskToClick);
				if(EventTransformer.CLIQUER_SUR.equals(actionTimelineInfo.action)) {
					doDoubleClick(pointToClick);
				}
			}
		});
	}

	static void doDoubleClick(final Point pointToClick) {
		FestRobotInstance.getRobot().click(pointToClick, MouseButton.LEFT_BUTTON, 2);
	}

	static Point findPointToClick(final JSimpleDaysTimeline simpleDaysTimeline, final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask findedTask) {
		final Point computeMiddleTaskPoint = computeMiddleTaskPoint(simpleDaysTimeline, actionTimelineInfo, findedTask);
		final Point locationOnScreen = getLocationOnScreen(simpleDaysTimeline);
		return new Point(locationOnScreen.x + computeMiddleTaskPoint.x, locationOnScreen.y + computeMiddleTaskPoint.y);
	}

	private static Point getLocationOnScreen(final JSimpleDaysTimeline simpleDaysTimeline) {
		final JViewport internalTimelineViewport = simpleDaysTimeline.getInternalTimelineViewport();
		final Point locationOnScreen = internalTimelineViewport.getLocationOnScreen();
		final Point viewPosition = internalTimelineViewport.getViewPosition();
		return new Point(locationOnScreen.x - viewPosition.x, locationOnScreen.y - viewPosition.y);
	}

	private static Point computeMiddleTaskPoint(final JSimpleDaysTimeline simpleDaysTimeline, final ActionTimelineInfo actionTimelineInfo,
			final SimpleDaysTask findedTask) {
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

	private static int findRessource(
			final JSimpleDaysTimeline simpleDaysTimeline,
			final ActionTimelineInfo actionTimelineInfo
	) {
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

	private static SimpleDaysTask findTaskToClick(
			final JSimpleDaysTimeline simpleDaysTimeline,
			final ActionTimelineInfo actionTimelineInfo
	) {
		final int findedRessource = findRessource(simpleDaysTimeline, actionTimelineInfo);
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
			if(!taskType.equals(next.getClass().getName())) {
				tasksIterator.remove();
			}
		}
	}

	static void movetoTo(final JSimpleDaysTimeline timeline, final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask taskToGoTo) {
		try {
			final int p = timeline.pointAtDayDate(actionTimelineInfo.dayDateMin);
			timeline.setHorizontalScrollBarValue(p);
			final int re = timeline.convertResourceIndexToView(actionTimelineInfo.findedRessource);
			timeline.getSelectionModel().addSelectionIndexResource(re, taskToGoTo.getDayDateMin(), taskToGoTo);
			timeline.scrollRectToVisible(timeline.getSimpleDayTaskRect(re, taskToGoTo));
		} 
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public Component locateComponentTarget(final String item, final String itemType, final Component value) {
		return null;
	}

	@Override
	public String processCustomCall(final CommandRequest commandRequest) {
		try {
			if (isTaskCustomCommand(commandRequest)) {
				if(isTimelineCommandRequest(commandRequest)) {
					final TimelineCommandRequest timelineCommandRequest = (TimelineCommandRequest) commandRequest;
					final JSimpleDaysTimeline timeline = findTimeline(commandRequest.value);
					handleCommandTask(timelineCommandRequest, timeline);
				}
			}
			else {
				throw new IllegalAccessError("Custom command not supported: " + commandRequest.customCommand);
			}
		} 
		catch (final Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}

	private boolean isTimelineCommandRequest(final CommandRequest commandRequest) {
		return commandRequest instanceof TimelineCommandRequest;
	}

	private boolean isTaskCustomCommand(final CommandRequest commandRequest) {
		//return "task".equals(commandRequest.customCommand);
		return "timeline".equals(commandRequest.customCommand);
	}

	public static ActionTimelineInfo constructActionTimelineInfo(final String value) {
		final String action = EventTransformer.CLIQUER_SUR;
		if(value.startsWith(action)) {
			int currentLenght = action.length();
			final String actionFinded = value.substring(0, currentLenght);
			final int indexDu = value.indexOf(" du");
			if(indexDu != -1) {
				final String taskType = value.substring(currentLenght + 1, currentLenght = indexDu);
				final int indexSlash = value.indexOf('/');
				if(indexSlash != -1) {
					final String dayDateOne = value.substring(" du".length() + currentLenght + 1, currentLenght = indexSlash).trim();
					final DayDate recognizeDayDateMin = recognizeDayDate(dayDateOne);

					final int indexOrdre = value.indexOf("ordre");
					if(indexOrdre != -1) {
						final String dayDateTwo = value.substring(currentLenght + 1, currentLenght = indexOrdre);
						final DayDate recognizeDayDateMax = recognizeDayDate(dayDateTwo);

						final int indexDe = value.indexOf("de ", indexOrdre);
						if(indexDe != -1) {
							final int indexDudu = value.indexOf("du ", indexDe);
							if(indexDudu != 1) {
								final String name = value.substring(indexDe + "de ".length(), indexDudu - 1);
								final String ressourceName = value.substring(indexDudu + "du ".length());
								return new ActionTimelineInfo(actionFinded, taskType, recognizeDayDateMin, recognizeDayDateMax, name, ressourceName, "");
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static DayDate recognizeDayDate(final String dayDate) {
		final String[] split = dayDate.replace("(", "").replace(")", "").split(",");
		final DayDate realDayDate = new DayDate();
		realDayDate.setDay(Integer.parseInt(split[0].trim()));
		realDayDate.setHour(Integer.parseInt(split[1].trim()));
		realDayDate.setMinute(Integer.parseInt(split[2].trim()));
		return realDayDate;
	}

	private static JSimpleDaysTimeline findTimeline(final String value) {
		final ActionTimelineInfo contructActionTimelineInfo = constructActionTimelineInfo(value);
		final Collection<Container> containers = getWindows(contructActionTimelineInfo.container);
		for(final Container container : containers) {
			if(container instanceof JSimpleDaysTimeline) {
				return (JSimpleDaysTimeline) container;
			}
		}
		return null;
	}

	private static Collection<Container> getWindows(
			final String name
	) {
		final Window[] allWindows = Window.getWindows();
		final Collection<Container> containers = new ArrayList<Container>();
		for(final Window w : allWindows) {
			if(acceptableContainer(w, name)) {
				containers.add(w);
			}
		}
		return containers;
	}

	private static boolean acceptableContainer(
			final Window w,
			final String name
	) {
		return name.equals(w.getName()) || name.equals(w.getClass().getName());
	}


	@Override
	public String getName() {
		return "STX-PLUGIN-SwingCustomWidgetHandler";
	}

	@Override
	public boolean isInterestedIn(Component component) {
		return false;
	}

	@Override
	public List<Class<? extends CommandRequest>> getCommandRequestWhiteList() {
		// TODO Auto-generated method stub
		return null;
	}
}