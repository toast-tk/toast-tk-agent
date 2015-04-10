package com.synaptix.toast.plugin.synaptix.runtime.handler;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;
import com.synaptix.swing.JSimpleDaysTimeline;

public final class CenterCellsPanelHandler {

	private final String commandRequestValue;
	
	private final CenterCellsPanel centerCellsPanel;
	
	public CenterCellsPanelHandler(
			final String commandRequestValue,
			final CenterCellsPanel centerCellsPanel
	) {
		this.commandRequestValue = commandRequestValue;
		this.centerCellsPanel = centerCellsPanel;
	}
}