package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

public final class CenterCellsPanelDoClickAction extends AbstractClickAction {

	private final Point pointToClick;

	public CenterCellsPanelDoClickAction(
			final Point pointToClick
	) {
		this.pointToClick = pointToClick;
	}

	@Override
	public void run() {
		doSimpleClick(pointToClick);
	}
}