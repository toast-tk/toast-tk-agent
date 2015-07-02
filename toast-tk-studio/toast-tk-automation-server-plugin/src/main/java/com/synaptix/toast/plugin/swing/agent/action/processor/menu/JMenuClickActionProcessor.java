package com.synaptix.toast.plugin.swing.agent.action.processor.menu;

import javax.swing.JMenu;

import org.fest.swing.core.Robot;

import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.report.TestResult.ResultKind;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessor;


/**
 * Click on a JMenu 
 * Select a sub menu item from there.
 *
 */
class JMenuClickActionProcessor implements ActionProcessor<JMenu>{

	@Override
	public String processCommandOnComponent(CommandRequest command, JMenu target) {
		Robot robot = FestRobotInstance.getRobot();
		robot.click(target);
		return ResultKind.SUCCESS.name();
	}
	
}
