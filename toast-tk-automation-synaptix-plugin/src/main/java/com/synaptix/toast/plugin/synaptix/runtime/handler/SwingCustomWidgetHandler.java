package com.synaptix.toast.plugin.synaptix.runtime.handler;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
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
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineCenter;
import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.plugin.synaptix.runtime.annotation.TimelineHandler;
import com.synaptix.toast.plugin.synaptix.runtime.handler.sentence.SentenceFinder;

@TimelineHandler
public class SwingCustomWidgetHandler extends AbstractCustomFixtureHandler {

	private static final Logger LOG = LoggerFactory.getLogger(SwingCustomWidgetHandler.class);

	private final List<String> whiteList;
	
	public SwingCustomWidgetHandler() {
		super();
		this.whiteList = buildWhiteList();
	}

	private static List<String> buildWhiteList() {
		final List<String> whiteList = new ArrayList<String>(2); 
		whiteList.add("timeline");
		whiteList.add("centerCells");
		return whiteList;
	}
	
	@Override
	public String makeHandleFixtureCall(
			final Component component, 
			final IIdRequest command
	) {
		if(command instanceof CommandRequest) {
			final CommandRequest commandRequest = (CommandRequest) command;
			if(isCustomCommand(commandRequest)) {
				if (isJSimpleDaysTimelineCenter(component)) {
					final JSimpleDaysTimeline simpleDaysTimeline = ((JSimpleDaysTimelineCenter) component).getSimpleDaysTimeline();
					final JSimpleDaysTimelineHandler timelineHandler =  new JSimpleDaysTimelineHandler(commandRequest.value, simpleDaysTimeline);
					timelineHandler.handleCommand();
				}
				else if(isCenterCellsPanel(component)) {
					final CenterCellsPanel centerCellsPanel = (CenterCellsPanel) component;
					final CenterCellsHandler centerCellsHandler = new CenterCellsHandler(centerCellsPanel, commandRequest);
					return centerCellsHandler.handleCommand();
				}
			}
		}
		return null;
	}

	private static boolean isCenterCellsPanel(final Component component) {
		return component instanceof CenterCellsPanel;
	}

	private static boolean isJSimpleDaysTimelineCenter(final Component component) {
		return component instanceof JSimpleDaysTimelineCenter;
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
				LOG.info("processing custom command : {}", command);
				if(isTimelineCustomCommand(commandRequest)) {
					final JSimpleDaysTimeline simpleDaysTimeline = findTimeline(command);
					final JSimpleDaysTimelineHandler timelineHandler =  new JSimpleDaysTimelineHandler(commandRequest.value, simpleDaysTimeline);
					timelineHandler.handleCommand();
				}
				else if(isCellCenterCustomCommand(commandRequest)) {
					final ActionCenterCellsInfo actionCenterCellsInfo = new ActionCenterCellsInfo(commandRequest.value);
					final CenterCellsPanel centerCellsPanel = findCenterCells(actionCenterCellsInfo.centerCellsPanelName);
					final CenterCellsHandler centerCellHandler = new CenterCellsHandler(centerCellsPanel, commandRequest);
					return centerCellHandler.handleCommand();
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

	private static CenterCellsPanel findCenterCellsPanel(
			final Container c,
			final String name
	) {
	    final List<CenterCellsPanel> centerCellsPanels = new ArrayList<CenterCellsPanel>();
	    findCenterCellsPanel(c, centerCellsPanels);
	    for(final CenterCellsPanel centerCellsPanel : centerCellsPanels) {
	    	if(name.equals(centerCellsPanel.getName())) {
	    		return centerCellsPanel;
	    	}
	    }
	    return null;
	}
	
	private static void findCenterCellsPanel(
			final Container c,
			final List<CenterCellsPanel> centerCellsPanels
	) {
	    final Component[] components = c.getComponents();
	    for(final Component com : components) {
	        if(isCenterCellsPanel(com)) {
	            centerCellsPanels.add((CenterCellsPanel) com);
	        } 
	        else if(com instanceof Container) {
	        	findCenterCellsPanel((Container) com, centerCellsPanels);
	        }
	    }
	}
	
	private static CenterCellsPanel findCenterCells(final String value) {
		final Window[] allWindows = Window.getWindows();
		for(final Window window : allWindows) {
			LOG.info("window {}", window);
			if(window instanceof JFrame) {
				final JFrame frame = (JFrame) window;
				final Container contentPane = frame.getContentPane();
				final CenterCellsPanel findCenterCellsPanel = findCenterCellsPanel(contentPane, value);
				if(findCenterCellsPanel != null) {
					LOG.info("finded = {}", findCenterCellsPanel.getName());
					return findCenterCellsPanel;
				}
			}
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
}