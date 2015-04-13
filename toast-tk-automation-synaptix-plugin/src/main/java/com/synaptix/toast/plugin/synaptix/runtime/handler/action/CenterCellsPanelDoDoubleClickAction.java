package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

public final class CenterCellsPanelDoDoubleClickAction extends AbstractClickAction {

	public CenterCellsPanelDoDoubleClickAction(
			final Point pointToClick
	) {
		super(pointToClick);
	}

	@Override
	public void run() {
		doDoubleClick(pointToClick);
	}
}