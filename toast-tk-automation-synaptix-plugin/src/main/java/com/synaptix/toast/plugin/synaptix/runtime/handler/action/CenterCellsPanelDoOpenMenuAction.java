package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

public final class CenterCellsPanelDoOpenMenuAction extends AbstractClickAction {

	private final Point pointToClick;

	public CenterCellsPanelDoOpenMenuAction(
			final Point pointToClick
	) {
		this.pointToClick = pointToClick;
	}

	@Override
	public void run() {
		doOpenMenu(pointToClick);
	}
}