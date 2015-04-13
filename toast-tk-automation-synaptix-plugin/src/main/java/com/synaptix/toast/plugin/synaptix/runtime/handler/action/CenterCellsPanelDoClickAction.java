package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

public final class CenterCellsPanelDoClickAction extends AbstractClickAction {

	public CenterCellsPanelDoClickAction(
			final Point pointToClick
	) {
		super(pointToClick);
	}

	@Override
	public void run() {
		doSimpleClick(pointToClick);
	}
}