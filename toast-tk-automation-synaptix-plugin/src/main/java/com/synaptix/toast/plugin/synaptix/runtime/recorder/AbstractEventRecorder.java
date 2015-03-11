package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.toast.core.inspection.ISwingInspectionServer;
import com.synaptix.toast.core.record.RecordedEvent;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.TimelineDataEvent;
import com.synaptix.toast.plugin.synaptix.runtime.model.TaskOnRessource;
import com.synaptix.toast.plugin.synaptix.runtime.split.Split;

public abstract class AbstractEventRecorder implements EventRecorder {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractEventRecorder.class);

	protected abstract void makeRecord(final AWTEvent awtEvent);

	@Inject
	protected ISwingInspectionServer cmdServer;

	@Override
	public boolean isInterestedIn(final AWTEvent awtEvent) {
		return false;
	}

	@Override
	public final void recorde(final AWTEvent awtEvent) {
		try {
			makeRecord(awtEvent);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected static boolean isMouseEvent(final AWTEvent awtEvent) {
		return awtEvent instanceof MouseEvent;
	}

	protected static Component retrieveComponentFromEvent(final MouseEvent mouseEvent) {
		return mouseEvent.getComponent();
	}

	protected static boolean isTimelineEvent(final MouseEvent mouseEvent) {
		final Component retrieveComponentFromEvent = retrieveComponentFromEvent(mouseEvent);
		final boolean timelineInstance = "com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineCenter".equals(retrieveComponentFromEvent.getClass().getName());
		return timelineInstance;
	}

	protected static Object retrieveSimpleDaysTimeline(final Component component) {
		try {
			final Method method = component.getClass().getMethod("getSimpleDaysTimeline", null);
			final Object invoke = method.invoke(component, null);
			return invoke;
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	protected static TimelineDataEvent buildTimelineDataEvent(
			final TaskOnRessource findCurrentSelectedTask,
			final /*JSimpleDaysTimeline*/Object simpleDaysTimeline,
			final AWTEvent event
	) {
		try {
			final MouseEvent mouseEvent = (MouseEvent) event;
			final Field field = findCurrentSelectedTask.getClass().getField("simpleDaysTask");
			final /*SimpleDaysTask*/Object simpleDaysTask = field.get(findCurrentSelectedTask);
			final Class<? extends Object> classSimpleDaysTask = simpleDaysTask.getClass();
			final Method getDayDateMinMethod = classSimpleDaysTask.getMethod("getDayDateMin", null);
			final Method getDayDateMaxMethod = classSimpleDaysTask.getMethod("getDayDateMax", null);

			final /*DayDate*/Object dayDateMin = getDayDateMinMethod.invoke(simpleDaysTask, null);
			final /*DayDate*/Object dayDateMax = getDayDateMaxMethod.invoke(simpleDaysTask, null);
			final Class<? extends Object> dayDateClass = dayDateMin.getClass();
			final Method getDayMethod = dayDateClass.getMethod("getDay", null);
			final Method getHourMethod = dayDateClass.getMethod("getHour", null);
			final Method getMinuteMethod = dayDateClass.getMethod("getMinute", null);

			final int mouseEventID = mouseEvent.getID();
			final int clickCount = mouseEvent.getClickCount();
			final int modifiers = mouseEvent.getModifiers();

			final String classTaskName = classSimpleDaysTask.getName();

			final int dayMin = (Integer) getDayMethod.invoke(dayDateMin, null);
			final int hourMin = (Integer) getHourMethod.invoke(dayDateMin, null);
			final int minMin = (Integer) getMinuteMethod.invoke(dayDateMin, null);

			final int dayMax = (Integer) getDayMethod.invoke(dayDateMax, null);
			final int hourMax = (Integer) getHourMethod.invoke(dayDateMax, null);
			final int minMax = (Integer) getMinuteMethod.invoke(dayDateMax, null);

			final Method getOrdreMethod = classSimpleDaysTask.getMethod("getOrdre", null);
			final int ordre = (Integer) getOrdreMethod.invoke(simpleDaysTask, null);

			final int ressource = findCurrentSelectedTask.ressource;

			final Method method = simpleDaysTimeline.getClass().getMethod("getName", null);
			final Object name = method.invoke(simpleDaysTimeline, null);
			final String timelineName = (String) (name != null ? name : JSimpleDaysTimeline.class.getName());
			final String ressourceName = findCurrentSelectedTask.identifier;

			return new TimelineDataEvent(
					mouseEventID,
					clickCount,
					modifiers,
					classTaskName,
					dayMin,
					hourMin,
					minMin,
					dayMax,
					hourMax,
					minMax,
					ordre,
					ressource,
					timelineName,
					ressourceName
			);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	protected static RecordedEvent buildRecordedEventFromTask(
			final TaskOnRessource findCurrentSelectedTask,
			final /*JSimpleDaysTimeline*/Object simpleDaysTimeline,
			final AWTEvent event
	) {
		try {
			final MouseEvent mouseEvent = (MouseEvent) event;
			final StringBuilder sb = new StringBuilder(64);
			final Field field = findCurrentSelectedTask.getClass().getField("simpleDaysTask");
			final /*SimpleDaysTask*/Object simpleDaysTask = field.get(findCurrentSelectedTask);
			final Class<? extends Object> classSimpleDaysTask = simpleDaysTask.getClass();
			final Method getDayDateMinMethod = classSimpleDaysTask.getMethod("getDayDateMin", null);
			final Method getDayDateMaxMethod = classSimpleDaysTask.getMethod("getDayDateMax", null);

			final /*DayDate*/Object dayDateMin = getDayDateMinMethod.invoke(simpleDaysTask, null);
			final /*DayDate*/Object dayDateMax = getDayDateMaxMethod.invoke(simpleDaysTask, null);
			appendMouseEventInfo(sb, mouseEvent);
			appendTaskInfo(sb, simpleDaysTask);
			appendDayDate(sb, dayDateMin);
			appendDayDate(sb, dayDateMax);
			appendOrdre(sb, simpleDaysTask);
			appendRessource(sb, findCurrentSelectedTask);
			appendTimelineName(sb, simpleDaysTimeline);
			appendIdentifier(sb, findCurrentSelectedTask);
			Split.clean(sb);
			return new RecordedEvent(sb.toString());
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	protected static void appendTaskInfo(
			final StringBuilder sb,
			final Object simpleDaysTask
	) {
		Split.addWithSeparator(sb, simpleDaysTask.getClass().getName());
	}

	protected static void appendOrdre(
			final StringBuilder sb,
			final /*SimpleDaysTask*/Object simpleDaysTask
	) {
		try {
			final Method method = simpleDaysTask.getClass().getMethod("getOrdre", null);
			Split.addWithSeparator(sb, method.invoke(simpleDaysTask, null));
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected static void appendRessource(
			final StringBuilder sb,
			final TaskOnRessource findCurrentSelectedTask
	) {
		Split.addWithSeparator(sb, findCurrentSelectedTask.ressource);
	}

	protected static void appendDayDate(
			final StringBuilder sb,
			final /*DayDate*/Object dayDate
	) {
		try {
			final Class<? extends Object> dayDateClass = dayDate.getClass();
			final Method getDayMethod = dayDateClass.getMethod("getDay", null);
			final Method getHourMethod = dayDateClass.getMethod("getHour", null);
			final Method getMinuteMethod = dayDateClass.getMethod("getMinute", null);
			Split.addWithSeparator(sb, getDayMethod.invoke(dayDate, null));
			Split.addWithSeparator(sb, getHourMethod.invoke(dayDate, null));
			Split.addWithSeparator(sb, getMinuteMethod.invoke(dayDate, null));
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected static void appendMouseEventInfo(
			final StringBuilder sb,
			final MouseEvent mouseEvent
	) {
		Split.addWithSeparator(sb, mouseEvent.getID());
		Split.addWithSeparator(sb, mouseEvent.getClickCount());
		Split.addWithSeparator(sb, mouseEvent.getModifiers());
	}

	protected static void appendTimelineName(
			final StringBuilder sb,
			final /*JSimpleDaysTimeline*/Object simpleDaysTimeline
	) {
		try {
			final Method method = simpleDaysTimeline.getClass().getMethod("getName", null);
			final Object name = method.invoke(simpleDaysTimeline, null);
			Split.addWithSeparator(sb, name != null ? name : JSimpleDaysTimeline.class.getName());
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected static void appendIdentifier(
			final StringBuilder sb,
			final TaskOnRessource findCurrentSelectedTask
	) {
		Split.addWithSeparator(sb, findCurrentSelectedTask.identifier.replace(":", ""));
	}

	protected static TaskOnRessource findCurrentSelectedTask(
			final /*JSimpleDaysTimeline*/Object simpleDaysTimeline
	) {
		final /*SimpleDaysTimelineSelectionModel*/Object selectionModel = getSimpleDaysTimelineSelectionModel(simpleDaysTimeline);
		final int nbSelectedTask = getSelectionTaskCount(selectionModel);
		if(nbSelectedTask == 1) {
			final int ressource = assertOneRessourceIsSelected(selectionModel);
			if(ressource != -1) {
				final /*SimpleDaysTask*/Object curSelectedTask = getSelectedTask(selectionModel, ressource);
				if(curSelectedTask != null) {
					final /*SimpleDaysTimelineResourcesModel*/Object resourcesModel = getResourcesModel(simpleDaysTimeline);
					return buildTaskOnRessource(resourcesModel, curSelectedTask, ressource);
				}
			}
		}
		return null;
	}

	protected static Object getResourcesModel(final /*JSimpleDaysTimeline*/Object simpleDaysTimeline) {
		try {
			final Method method = simpleDaysTimeline.getClass().getMethod("getResourcesModel", null);
			return method.invoke(simpleDaysTimeline, null);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	protected static Object getSelectedTask(final Object selectionModel, final int ressource) {
		try {
			final Method method = selectionModel.getClass().getMethod("getSelectionTasks", int.class);
			final Object invoke = method.invoke(selectionModel, ressource);
			return Array.get(invoke, 0);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	protected static int getSelectionTaskCount(final Object selectionModel) {
		try {
			final Method method = selectionModel.getClass().getMethod("getSelectionTaskCount", null);
			return (Integer) method.invoke(selectionModel, null);
		}
		catch(final Exception e) {
			return 0;
		}

	}

	protected static Object getSimpleDaysTimelineSelectionModel(final Object simpleDaysTimeline) {
		try {
			final Method method = simpleDaysTimeline.getClass().getMethod("getSelectionModel", null);
			return method.invoke(simpleDaysTimeline, null);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	protected static int assertOneRessourceIsSelected(final /*SimpleDaysTimelineSelectionModel*/Object selectionModel) {
		try {
			final Class<? extends Object> classSelectionModel = selectionModel.getClass();
			final Method minSelectionIndexRessourceMethod = classSelectionModel.getMethod("getMinSelectionIndexResource", null);
			final Method maxSelectionIndexRessourceMethod = classSelectionModel.getMethod("getMaxSelectionIndexResource", null);
			final int minSelectionIndexResource = (Integer) minSelectionIndexRessourceMethod.invoke(selectionModel, null);
			final int maxSelectionIndexResource = (Integer) maxSelectionIndexRessourceMethod.invoke(selectionModel, null);
			return minSelectionIndexResource == maxSelectionIndexResource ? minSelectionIndexResource : -1;
		}
		catch(final Exception e) {
			return -1;
		}
	}

	protected static TaskOnRessource buildTaskOnRessource(
			final /*SimpleDaysTimelineResourcesModel*/Object resourcesModel,
			final /*SimpleDaysTask*/Object curSelectedTask,
			final int ressource
	) {
		try {
			final Method getRessourceMethod = resourcesModel.getClass().getMethod("getResource", int.class);
			final /*SimpleDaysTimelineResource*/Object resource = getRessourceMethod.invoke(resourcesModel, ressource);
			final Method getNameMethod = resource.getClass().getMethod("getName", null);
			final Object resourceName = getNameMethod.invoke(resource, null);
			return new TaskOnRessource(curSelectedTask, ressource, String.valueOf(resourceName));
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
}