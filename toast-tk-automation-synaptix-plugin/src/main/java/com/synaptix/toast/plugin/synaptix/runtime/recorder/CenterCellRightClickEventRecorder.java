package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;
import com.synaptix.utils.MouseHelper;

public class CenterCellRightClickEventRecorder extends AbstractCenterCellEventRecorder {

	private static final Logger LOG = LogManager.getLogger(CenterCellSelectionEventRecorder.class);

	@Override
	public boolean isInterestedIn(final AWTEvent awtEvent) {
		if(isMouseEvent(awtEvent)) {
			final MouseEvent mouseEvent = (MouseEvent) awtEvent;
			final boolean rightClick = MouseHelper.isRightClick(mouseEvent);
			LOG.info("rightClick {}", Boolean.valueOf(rightClick));
			final boolean isCenterCellsEvent = isCenterCellsPanelEvent(mouseEvent);
			LOG.info("isCenterCellsEvent {}", Boolean.valueOf(isCenterCellsEvent));
			return rightClick && isCenterCellsEvent;
		}
		return false;
	}

	@Override
	protected void makeRecord(final AWTEvent awtEvent) {
		final MouseEvent mouseEvent = (MouseEvent) awtEvent;
		final CenterCellsPanel centerCellsPanel = (CenterCellsPanel) mouseEvent.getSource();
		LOG.info("centerCellsPanel {}", centerCellsPanel.getName());
		handleRightClickEventForCenterCellsPanel(centerCellsPanel, mouseEvent);
	}

	private void handleRightClickEventForCenterCellsPanel(
			final CenterCellsPanel centerCellsPanel,
			final AWTEvent event
	) {
		final Point findCurrentCell = findCurrentSelectedCell(centerCellsPanel);
		LOG.info("findCurrentCell {}", findCurrentCell);
		if(findCurrentCell != null) {
			final String eventData = buildEventData(centerCellsPanel, findCurrentCell, EventTransformer.OUVRIR_LE_MENU_SUR);
			LOG.info("Right Click Cell Event Data {}", eventData);
			cmdServer.publishInterpretedEvent(eventData);
		}
		else {
			LOG.info("no cells selected");
		}
	}
}