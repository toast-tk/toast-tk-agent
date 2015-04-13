package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Point;

import org.fest.swing.core.MouseButton;

import com.synaptix.toast.fixture.utils.FestRobotInstance;

public abstract class AbstractClickAction implements Runnable {

	protected final Point pointToClick;
	
	public AbstractClickAction(final Point pointToClick) {
		this.pointToClick = pointToClick;
	}
	
	static void doOpenMenu(final Point pointToClick) {
		FestRobotInstance.getRobot().click(pointToClick, MouseButton.RIGHT_BUTTON, 1);
	}
	
	static void doSimpleClick(final Point pointToClick) {
		FestRobotInstance.getRobot().click(pointToClick, MouseButton.LEFT_BUTTON, 1);
	}
	
	static void doDoubleClick(final Point pointToClick) {
		FestRobotInstance.getRobot().click(pointToClick, MouseButton.LEFT_BUTTON, 2);
	}
	
}
