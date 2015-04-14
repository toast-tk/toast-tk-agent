package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;

public final class CenterCellsPanelDoOpenMenuAction extends AbstractClickAction {

	public CenterCellsPanelDoOpenMenuAction(
			final CenterCellsPanel centerCellsPanel,
			final Point pointToClick
	) {
		super(centerCellsPanel, pointToClick);
	}

	@Override
	public void run() {
		doOpenMenu();
	}
}