package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;

public final class CenterCellsPanelDoDoubleClickAction extends AbstractClickAction<CenterCellsPanel> {

	public CenterCellsPanelDoDoubleClickAction(
			final CenterCellsPanel centerCellsPanel,
			final Point pointToClick
	) {
		super(centerCellsPanel, pointToClick);
	}

	@Override
	public void run() {
		doDoubleClick();
	}
}