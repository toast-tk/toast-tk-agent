package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

public final class CenterCellsPanelDoDoubleClickAction extends AbstractClickAction {

	private final Point pointToClick;

	public CenterCellsPanelDoDoubleClickAction(
			final Point pointToClick
	) {
		this.pointToClick = pointToClick;
	}

	@Override
	public void run() {
		doDoubleClick(pointToClick);
	}
}