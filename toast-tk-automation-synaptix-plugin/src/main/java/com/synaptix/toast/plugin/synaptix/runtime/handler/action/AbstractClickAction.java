package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Component;
import java.awt.Point;

import org.fest.swing.core.MouseButton;

import com.synaptix.toast.fixture.utils.FestRobotInstance;

public abstract class AbstractClickAction<C extends Component> implements Runnable {

	protected Point pointToClick;
	
	protected final C component;
	
	public AbstractClickAction(
			final C component,
			final Point pointToClick
	) {
		this.component = component;
		this.pointToClick = pointToClick;
	}
	
	public void doOpenMenu() {
		FestRobotInstance.getRobot().click(component, pointToClick, MouseButton.RIGHT_BUTTON, 1);
	}
	
	public void doSimpleClick() {
		FestRobotInstance.getRobot().click(component, pointToClick, MouseButton.LEFT_BUTTON, 1);
	}
	
	public void doDoubleClick() {
		FestRobotInstance.getRobot().click(component, pointToClick, MouseButton.LEFT_BUTTON, 2);
	}
}