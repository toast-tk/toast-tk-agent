package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sncf.fret.swi.client.assemblage.view.swing.extension.panel.gestionprevisions.CenterCellsPanel;

public final class CenterCellsPanelDoOpenMenuAction extends AbstractClickAction<CenterCellsPanel> {

	private static final Logger LOG = LoggerFactory.getLogger(CenterCellsPanelDoOpenMenuAction.class);
	
	public CenterCellsPanelDoOpenMenuAction(
			final CenterCellsPanel centerCellsPanel,
			final Point pointToClick
	) {
		super(centerCellsPanel, pointToClick);
	}

	@Override
	public void run() {
		LOG.info("doingOpenMenu = {}", pointToClick);
		doOpenMenu();
		LOG.info("doneOpenMenu = {}", pointToClick);
	}
}