package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;
import com.synaptix.toast.plugin.synaptix.runtime.helper.MouseHelper;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;

public class CenterCellDoubleClickEventRecorder extends AbstractCenterCellEventRecorder {

	private static final Logger LOG = LogManager.getLogger(CenterCellSelectionEventRecorder.class);

	@Override
	public boolean isInterestedIn(final AWTEvent awtEvent) {
		if(isMouseEvent(awtEvent)) {
			final MouseEvent mouseEvent = (MouseEvent) awtEvent;
			final boolean doubleClick = MouseHelper.isDoubleClick(mouseEvent);
			LOG.info("doubleClick {}", Boolean.valueOf(doubleClick));
			if(doubleClick) {
				final boolean isCenterCellsEvent = isCenterCellsPanelEvent(mouseEvent);
				LOG.info("isCenterCellsEvent {}", Boolean.valueOf(isCenterCellsEvent));
				if(isCenterCellsEvent) {
					final boolean isMouseReleased = MouseHelper.isMouseReleased(mouseEvent);
					LOG.info("isMouseReleased {}", Boolean.valueOf(isMouseReleased));
					return isMouseReleased;
				}
				return false;
			}
		}
		return false;
	}

	@Override
	protected void makeRecord(final AWTEvent awtEvent) {
		final MouseEvent mouseEvent = (MouseEvent) awtEvent;
		final CenterCellsPanel centerCellsPanel = (CenterCellsPanel) mouseEvent.getSource();
		LOG.info("centerCellsPanel {}", centerCellsPanel.getName());
		handleDoubleClickEventForCenterCellsPanel(centerCellsPanel, mouseEvent);
	}

	private void handleDoubleClickEventForCenterCellsPanel(
			final CenterCellsPanel centerCellsPanel,
			final AWTEvent event
	) {
		final Point findCurrentCell = findCurrentSelectedCell(centerCellsPanel);
		LOG.info("findCurrentCell {}", findCurrentCell);
		if(findCurrentCell != null) {
			final String eventData = buildEventData(centerCellsPanel, findCurrentCell, EventTransformer.DOUBLE_CLIQUER_SUR);
			LOG.info("Right Click Cell Event Data {}", eventData);
			cmdServer.publishInterpretedEvent(eventData);
		}
		else {
			LOG.info("no cells selected");
		}
	}
}