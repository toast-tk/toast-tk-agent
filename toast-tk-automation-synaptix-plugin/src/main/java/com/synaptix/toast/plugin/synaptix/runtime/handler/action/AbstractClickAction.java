package com.synaptix.toast.plugin.synaptix.runtime.handler.action;

import java.awt.Component;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.SwingUtilities;

import org.fest.swing.core.MouseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.toast.fixture.utils.FestRobotInstance;
import com.synaptix.toast.plugin.synaptix.runtime.handler.CenterCellsHandler;

public abstract class AbstractClickAction<C extends Component> implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractClickAction.class);

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
		try {
			SwingUtilities.convertPointToScreen(pointToClick, component);
			LOG.info("doOpenMenu to {} on {}", pointToClick, component);
			FestRobotInstance.getRobot().click(pointToClick, MouseButton.RIGHT_BUTTON, 1);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void doSimpleClick() {
		try {
			SwingUtilities.convertPointToScreen(pointToClick, component);
			LOG.info("doSimpleClick to {} on {}", pointToClick, component);
			FestRobotInstance.getRobot().click(component, pointToClick, MouseButton.LEFT_BUTTON, 1);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public void doDoubleClick() {
		try {
			SwingUtilities.convertPointToScreen(pointToClick, component);
			LOG.info("doDoubleClick to {} on {}", pointToClick, component);
			FestRobotInstance.getRobot().click(component, pointToClick, MouseButton.LEFT_BUTTON, 2);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}