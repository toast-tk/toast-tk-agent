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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JViewport;

import org.apache.commons.lang3.StringUtils;
import org.fest.swing.core.MouseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;
import com.synaptix.core.dock.IViewDockable;
import com.synaptix.core.view.CoreView;
import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.SimpleDaysTimelineModel;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineCenter;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.fixture.utils.FestRobotInstance;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.TimelineHandler;
import com.synaptix.toast.plugin.synaptix.runtime.handler.sentence.SentenceFinder;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;

@TimelineHandler
public class SwingCustomWidgetHandler extends AbstractCustomFixtureHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SwingCustomWidgetHandler.class);

	private final List<String> whiteList;
	
	public SwingCustomWidgetHandler() {
		super();
		this.whiteList = new ArrayList<String>(1);
		initWhiteList();
	}

	private void initWhiteList() {
		whiteList.add("timeline");
		whiteList.add("centerCells");
	}
	
	@Override
	public String makeHanldeFixtureCall(final Component component, final IIdRequest request) {
		if (component instanceof JSimpleDaysTimelineCenter) {
			handleTimeline((JSimpleDaysTimelineCenter) component, request);
		}
		else if(component instanceof CenterCellsPanel) {
			handleCenterCellPanel((CenterCellsPanel) component, request);
		}
		return null;
	}

	private void handleTimeline(final JSimpleDaysTimelineCenter timeline, final IIdRequest command) {
		try {
			if(command instanceof CommandRequest) {
				final CommandRequest commandRequest = (CommandRequest) command;
				if(isCustomCommand(commandRequest)) {
					if (isTimelineCustomCommand(commandRequest)) {
						handleTimelineCommandTask(commandRequest.value, timeline.getSimpleDaysTimeline());
					}
					else {
						throw new IllegalAccessError("Custom command not supported: " + commandRequest.customCommand);
					}
				}
			}
		} 
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void handleTimelineCommandTask(
			final String command, 
			final JSimpleDaysTimeline simpleDaysTimeline
	) {
		final SentenceFinder sentenceFinder = new SentenceFinder(command);
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

	private void moveToPointAndDoClick(final JSimpleDaysTimeline simpleDaysTimeline, final ActionTimelineInfo actionTimelineInfo, final SimpleDaysTask taskToClick) {
		runAction(new MoveToPointAndDoClickAction(simpleDaysTimeline, actionTimelineInfo, taskToClick));
	}

	static void doOpenMenu(final Point pointToClick) {
		FestRobotInstance.getRobot().click(pointToClick, MouseButton.RIGHT_BUTTON, 1);
	}
	
	static void doSimpleClick(final Point pointToClick) {
		FestRobotInstance.getRobot().click(pointToClick, MouseButton.LEFT_BUTTON, 1);
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
			if(isCustomCommand(commandRequest)) {
				final String command = commandRequest.value;
				LOG.info("processing command : {}", command);
				if(isTimelineCustomCommand(commandRequest)) {
					final JSimpleDaysTimeline timeline = findTimeline(command);
					handleTimelineCommandTask(command, timeline);
				}
				else if(isCellCenterCustomCommand(commandRequest)) {
					final CenterCellsPanel centerCellsPanel = findCenterCells(command);
					handleCommandCenterCellsPanel(command, centerCellsPanel);
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

	private static boolean isCustomCommand(final CommandRequest commandRequest) {
		return isTimelineCustomCommand(commandRequest) || isCellCenterCustomCommand(commandRequest);
	}

	private static boolean isCellCenterCustomCommand(
			final CommandRequest commandRequest
	) {
		return "centerCells".equals(commandRequest.itemType);
	}

	private static boolean isTimelineCustomCommand(
			final CommandRequest commandRequest
	) {
		return "timeline".equals(commandRequest.itemType);
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
		final SentenceFinder sentenceFinder = new SentenceFinder(value);
		if(sentenceFinder.isAValidSentence()) {
			final ActionTimelineInfo contructActionTimelineInfo =  sentenceFinder.actionTimelineInfo;
			return getWindows(contructActionTimelineInfo.container);
		}
		return null;
	}

	private static CenterCellsPanel findCenterCells(final String value) {
		final Window[] allWindows = Window.getWindows();
		for(final Window window : allWindows) {
			LOG.info("window {}", window);
			if(window instanceof JFrame) {
				final JFrame frame = (JFrame) window;
				final Container contentPane = frame.getContentPane();
				final int componentCount = contentPane.getComponentCount();
				for(int index = 0; index < componentCount; ++index) {
					final Component component = contentPane.getComponent(index);
					if(component instanceof CenterCellsPanel) {
						final String extractName = extractName(value);
						if(extractName != null && extractName.equals(component.getName())) {
							return (CenterCellsPanel) component;
						}
					}
				}
			}
		}
		return null;
	}
	
	private static String extractName(final String value) {
		LOG.info("extractName from {}", value);
		try {
			final int indexOpenParenthesis = value.indexOf('(');
			if(indexOpenParenthesis != -1) {
				final int indexCloseParenthesis = value.indexOf(')');
				if(indexCloseParenthesis != -1) {
					final String extractedName = value.substring(indexOpenParenthesis, indexCloseParenthesis + 1); 
					LOG.info("extractedName = {}", extractedName);
					return extractedName;
				}
			}
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);

		}
		return null;
	}
	
	private static JSimpleDaysTimeline getWindows(
			final String name
	) {
		final Window[] allWindows = Window.getWindows();
		final JSimpleDaysTimeline timeline = findGoodWindows(allWindows, name);
		return timeline;
	}

	private static JSimpleDaysTimeline findGoodWindows(
			final Window[] windows,
			final String name
	) {
		for(final Window window : windows) {
			LOG.info("window {}", window);
			if(window instanceof CoreView) {
				final CoreView coreView = (CoreView) window;
				final Collection<IViewDockable> viewDockables = coreView.getViewDockables();
				for(final IViewDockable viewDockable : viewDockables) {
					final String viewName = viewDockable.getName();
					if(viewName != null) {
						final String normalisedViewName = StringUtils.stripAccents(viewName).toLowerCase().replaceAll("\\s","");
						if(name.equals(normalisedViewName)) {
							final JComponent view = viewDockable.getView();
							final List<JSimpleDaysTimeline> findTimelines = findTimelines(view);
							for(final JSimpleDaysTimeline timeline : findTimelines) {
								final String normalizedTimelineName = StringUtils.stripAccents(timeline.getName()).toLowerCase().replaceAll("\\s","");
								if(normalizedTimelineName.equals(name)) {
									return timeline;
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private static List<JSimpleDaysTimeline> findTimelines(final Container c) {
	    final List<JSimpleDaysTimeline> timelines = new ArrayList<JSimpleDaysTimeline>();
	    findTimelines(c, timelines);
	    return timelines;
	}
	
	private static void findTimelines(
			final Container c, 
			final List<JSimpleDaysTimeline> timelines
	) {
	    final Component[] components = c.getComponents();
	    for(final Component com : components) {
	        if(com instanceof JSimpleDaysTimeline) {
	            timelines.add((JSimpleDaysTimeline) com);
	        } 
	        else if(com instanceof Container) {
	        	findTimelines((Container) com, timelines);
	        }
	    }
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
	public List<String> getCommandRequestWhiteList() {
		return whiteList;
	}
	
	private final class MoveToPointAndDoClickAction implements Runnable {
		
		private final JSimpleDaysTimeline simpleDaysTimeline;

		private final ActionTimelineInfo actionTimelineInfo;

		private final SimpleDaysTask taskToClick;

		private MoveToPointAndDoClickAction(
				final JSimpleDaysTimeline simpleDaysTimeline,
				final ActionTimelineInfo actionTimelineInfo,
				final SimpleDaysTask taskToClick
		) {
			this.simpleDaysTimeline = simpleDaysTimeline;
			this.actionTimelineInfo = actionTimelineInfo;
			this.taskToClick = taskToClick;
		}

		@Override
		public void run() {
			movetoTo(simpleDaysTimeline, actionTimelineInfo, taskToClick);
			final Point pointToClick = findPointToClick(simpleDaysTimeline, actionTimelineInfo, taskToClick);
			if(EventTransformer.CLIQUER_SUR.equals(actionTimelineInfo.action)) {
				doDoubleClick(pointToClick);
			}
		}
	}
	
	private void handleCenterCellPanel(final CenterCellsPanel centerCellSPanel, final IIdRequest request) {
		try {
			if(request instanceof CommandRequest) {
				final CommandRequest commandRequest = (CommandRequest) request;
				if(isCustomCommand(commandRequest)) {
					if (isCellCenterCustomCommand(commandRequest)) {
						handleCommandCenterCellsPanel(commandRequest.value, centerCellSPanel);
					}
					else {
						throw new IllegalAccessError("Custom command not supported: " + commandRequest.customCommand);
					}
				}
			}
		} 
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	private void handleCommandCenterCellsPanel(
			final String command,
			final CenterCellsPanel centerCellSPanel
	) {
		if(command.startsWith(EventTransformer.CLIQUER_SUR)) {
			final Point cell = extractCoordinates(command);
			LOG.info("cliquer sur {}/{}", Integer.valueOf(cell.x), Integer.valueOf(cell.y));
		}
		else if(command.startsWith(EventTransformer.GET)) {
			final Point cell = extractCoordinates(command);
			LOG.info("cliquer sur {}/{}", Integer.valueOf(cell.x), Integer.valueOf(cell.y));
			final int actifValue = centerCellSPanel.getActifValue(cell.x, cell.y);
		}
	}

	private Point extractCoordinates(final String command) {
		final int indexBeginParenthesis = command.indexOf('(');
		if(indexBeginParenthesis != -1) {
			final int indexEndParenthesis = command.indexOf(')');
			if(indexEndParenthesis != -1) {
				final String infoCordonnees = command.substring(indexBeginParenthesis, indexEndParenthesis + 1);
				final String[] split = infoCordonnees.split(":");
				final int x = Integer.parseInt(split[0]);
				final int y = Integer.parseInt(split[1]);
				return new Point(x, y);
			}
		}
		return null;
	}
}