package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

public final class CenterCellsPanelDoOpenMenuAction extends AbstractClickAction {

	public CenterCellsPanelDoOpenMenuAction(
			final Point pointToClick
	) {
		super(pointToClick);
	}

	@Override
	public void run() {
		doOpenMenu(pointToClick);
	}
}