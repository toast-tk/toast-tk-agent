package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;
import com.synaptix.core.dock.IViewDockable;
import com.synaptix.core.view.CoreView;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.SimpleDaysTimelineModel;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineCenter;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.ServiceCallHandler;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.TimelineHandler;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.CenterCellsHandler;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.CenterCellsPanelDoClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.CenterCellsPanelDoDoubleClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.CenterCellsPanelDoOpenMenuAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.TimelineMoveToPointAndDoClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.TimelineMoveToPointAndDoDoubleClickAction;
import com.synaptix.toast.plugin.synaptix.runtime.handler.action.TimelineMoveToPointAndOpenMenuAction;
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
				final ActionTimelineInfo actionTimelineInfo = sentenceFinder.actionTimelineInfo;
				if(EventTransformer.DOUBLE_CLIQUER_SUR.equals(actionTimelineInfo.action)) {
					moveToPointAndDoDoubleClick(simpleDaysTimeline, sentenceFinder.actionTimelineInfo, findedTask);
				}
				else if(EventTransformer.CLIQUER_SUR.equals(actionTimelineInfo.action)) {
					moveToPointAndDoClick(simpleDaysTimeline, sentenceFinder.actionTimelineInfo, findedTask);
				}
				else if(EventTransformer.OUVRIR_LE_MENU_SUR.equals(actionTimelineInfo.action)) {
					moveToPointAndDoOpenMenu(simpleDaysTimeline, sentenceFinder.actionTimelineInfo, findedTask);
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
			final JSimpleDaysTimeline simpleDaysTimeline, 
			final ActionTimelineInfo actionTimelineInfo, 
			final SimpleDaysTask taskToClick
	) {
		runAction(new TimelineMoveToPointAndDoClickAction(simpleDaysTimeline, actionTimelineInfo, taskToClick));
	}
	
	private void moveToPointAndDoDoubleClick(
			final JSimpleDaysTimeline simpleDaysTimeline, 
			final ActionTimelineInfo actionTimelineInfo, 
			final SimpleDaysTask taskToClick
	) {
		runAction(new TimelineMoveToPointAndDoDoubleClickAction(simpleDaysTimeline, actionTimelineInfo, taskToClick));
	}

	private void moveToPointAndDoOpenMenu(
			final JSimpleDaysTimeline simpleDaysTimeline, final 
			ActionTimelineInfo actionTimelineInfo, 
			final SimpleDaysTask taskToClick
	) {
		runAction(new TimelineMoveToPointAndOpenMenuAction(simpleDaysTimeline, actionTimelineInfo, taskToClick));
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
					final Point cell = extractCoordinates(command);
					final String[] extractCenterCellsPanelInfo = extractCenterCellsPanelInfo(command);
					LOG.info("cliquer sur ({}:{}) {}|{}", extractCenterCellsPanelInfo[0], extractCenterCellsPanelInfo[1], Integer.valueOf(cell.x), Integer.valueOf(cell.y));
					final CenterCellsPanel centerCellsPanel = findCenterCells(extractCenterCellsPanelInfo[0]);
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
			final Point convertedPoint = centerCellsPanelMoveTo(command, centerCellSPanel);
			centerCellsPanelDoClick(convertedPoint);
		}
		else if(command.startsWith(EventTransformer.DOUBLE_CLIQUER_SUR)) {
			final Point convertedPoint = centerCellsPanelMoveTo(command, centerCellSPanel);
			centerCellsPanelDoDoubleClick(convertedPoint);
		}
		else if(command.startsWith(EventTransformer.OUVRIR_LE_MENU_SUR)) {
			final Point convertedPoint = centerCellsPanelMoveTo(command, centerCellSPanel);
			centerCellsPanelDoOpenMenu(convertedPoint);
		}
		else if(command.startsWith(EventTransformer.GET)) {
			final Point cell = extractCoordinates(command);
			final String[] extractCenterCellsPanelInfo = extractCenterCellsPanelInfo(command);
			LOG.info("get ({}:{}) {}|{}", extractCenterCellsPanelInfo[0], extractCenterCellsPanelInfo[1], Integer.valueOf(cell.x), Integer.valueOf(cell.y));
			final int actifValue = centerCellSPanel.getActifValue(cell.x, cell.y);
			LOG.info("finded actifValue {}", Integer.valueOf(actifValue));
			//TODO
		}
		else if(command.startsWith(EventTransformer.SET)) {
			final String[] extractCenterCellsPanelInfo = extractCenterCellsPanelInfo(command);
			final Point cell = extractCoordinates(command);
			centerCellSPanel.setActifValue(cell.x, extractCenterCellsPanelInfo[1], 1);
		}
		else {
			throw new IllegalAccessError("unknown action for CenterCellsPanel");
		}
	}

	private static Point centerCellsPanelMoveTo(
			final String command,
			final CenterCellsPanel centerCellSPanel
	) {
		final Point cell = extractCoordinates(command);
		final String[] extractCenterCellsPanelInfo = extractCenterCellsPanelInfo(command);
		LOG.info("cliquer sur ({}:{}) {}|{}", extractCenterCellsPanelInfo[0], extractCenterCellsPanelInfo[1], Integer.valueOf(cell.x), Integer.valueOf(cell.y));
		centerCellSPanel.changeSelection(extractCenterCellsPanelInfo[1], cell.x, false, false);
		final int pointAtJour = CenterCellsPanel.pointAtJour(cell.x);
		final int pointAtRow = CenterCellsPanel.pointAtRow(cell.y);
		final Point convertedPoint = new Point(pointAtJour, pointAtRow);
		final Dimension cellDimension = new Dimension(CenterCellsPanel.getCellWidth(), CenterCellsPanel.getCellHeight());
		final Rectangle rectangle = new Rectangle(convertedPoint, cellDimension);
		centerCellSPanel.scrollRectToVisible(rectangle);
		return convertedPoint;
	}

	private static Point extractCoordinates(final String command) {
		final int indexEndParenthesis = command.indexOf(')');
		if(indexEndParenthesis != -1) {
			final String lastPart = command.substring(indexEndParenthesis + 1).trim();
			final String[] split = lastPart.split(":");
			final int x = Integer.parseInt(split[0]);
			final int y = Integer.parseInt(split[1]);
			return new Point(x, y);
		}
		return null;
	}
	
	private static String[] extractCenterCellsPanelInfo(final String command) {
		final int indexBeginParenthesis = command.indexOf('(');
		if(indexBeginParenthesis != -1) {
			final int indexEndParenthesis = command.indexOf(')');
			if(indexEndParenthesis != -1) {
				final String infoCordonnees = command.substring(indexBeginParenthesis, indexEndParenthesis + 1);
				final String[] splits = infoCordonnees.split(":");
				for(final String split : splits) {
					
				}
				return splits;
			}
		}
		return null;
	}
	
	private void centerCellsPanelDoDoubleClick(final Point pointToClick) {
		runAction(new CenterCellsPanelDoDoubleClickAction(pointToClick));
	}
	
	private void centerCellsPanelDoClick(final Point pointToClick) {
		runAction(new CenterCellsPanelDoClickAction(pointToClick));
	}
	
	private void centerCellsPanelDoOpenMenu(final Point pointToClick) {
		runAction(new CenterCellsPanelDoOpenMenuAction(pointToClick));
	}
}