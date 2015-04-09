package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.event.MouseEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;
import com.synaptix.toast.plugin.synaptix.runtime.interpreter.EventTransformer;
import com.synaptix.utils.MouseHelper;

public class CenterCellSelectionEventRecorder extends AbstractCenterCellEventRecorder {

	private static final Logger LOG = LogManager.getLogger(CenterCellSelectionEventRecorder.class);
	
	@Override
	public boolean isInterestedIn(final AWTEvent awtEvent) {
		if(isMouseEvent(awtEvent)) {
			final MouseEvent mouseEvent = (MouseEvent) awtEvent;
			final boolean simpleLeftClick = MouseHelper.isSimpleLeftClick(mouseEvent);
			LOG.info("simpleLeftClick {}", Boolean.valueOf(simpleLeftClick));
			if(simpleLeftClick) {
				final boolean isTimelineEvent = isCenterCellsPanelEvent(mouseEvent);
				LOG.info("isCenterCellsEventEvent {}", Boolean.valueOf(isTimelineEvent));
				if(isTimelineEvent) {
					final boolean isMouseReleased = MouseHelper.isMouseReleased(mouseEvent);
					LOG.info("isMouseReleased {}", Boolean.valueOf(isMouseReleased));
					return isMouseReleased;
				}
				return false;
			}
			return false;
		}
		return false;
	}
	
	@Override
	protected void makeRecord(final AWTEvent awtEvent) {
		final MouseEvent mouseEvent = (MouseEvent) awtEvent;
		final CenterCellsPanel centerCellsPanel = (CenterCellsPanel) mouseEvent.getSource();
		LOG.info("retrieveComponentFromEvent {}", centerCellsPanel);
		handleSelectionEventForCenterCellsPanel(centerCellsPanel, mouseEvent);
	}
	
	private void handleSelectionEventForCenterCellsPanel(
			final CenterCellsPanel centerCellsPanel,
			final AWTEvent event
	) {
		final Point findCurrentCell = findCurrentSelectedCell(centerCellsPanel);
		LOG.info("findCurrentCell {}", findCurrentCell);
		if(findCurrentCell != null) {
			final String eventData = buildEventData(centerCellsPanel, findCurrentCell, EventTransformer.CLIQUER_SUR);
			LOG.info("Selection Cell Event Data {}", eventData);
			cmdServer.publishInterpretedEvent(eventData);
		}
		else {
			LOG.info("no cells selected");
		}
	}
}