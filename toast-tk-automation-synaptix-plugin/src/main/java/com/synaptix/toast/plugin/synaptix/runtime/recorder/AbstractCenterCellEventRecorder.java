package com.synaptix.toast.plugin.synaptix.runtime.recorder;

import java.awt.Point;
import java.awt.event.MouseEvent;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;

public abstract class AbstractCenterCellEventRecorder extends AbstractEventRecorder {

	protected static boolean isCenterCellsPanelEvent(final MouseEvent mouseEvent) {
		return mouseEvent.getSource().getClass().equals(CenterCellsPanel.class);
	}
	
	protected static Point findCurrentSelectedCell(final CenterCellsPanel centerCellsPanel) {
		final int selectedColumn = centerCellsPanel.getSelectedColumn();
		if(selectedColumn != -1) {
			final int selectedRow = centerCellsPanel.getSelectedRow();
			if(selectedRow != -1) {
				return new Point(selectedColumn, selectedRow);
			}
		}
		return null;
	}
	
	protected static String buildEventData(
			final CenterCellsPanel centerCellsPanel,
			final Point findCurrentCell,
			final String action
	) {
		final StringBuilder sb = new StringBuilder(action);
		sb.append('(').append(centerCellsPanel.getName()).append(')').append(" ").append(findCurrentCell.x).append('|').append(findCurrentCell.y);
		return sb.toString();
	}
}