package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;

public final class CenterCellsPanelDoClickAction extends AbstractClickAction {

	public CenterCellsPanelDoClickAction(
			final CenterCellsPanel centerCellsPanel,
			final Point pointToClick
	) {
		super(centerCellsPanel, pointToClick);
	}

	@Override
	public void run() {
		doSimpleClick();
	}
}