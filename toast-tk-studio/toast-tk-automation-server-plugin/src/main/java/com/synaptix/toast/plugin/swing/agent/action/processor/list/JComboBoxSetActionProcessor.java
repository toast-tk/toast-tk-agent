package com.synaptix.toast.plugin.swing.agent.action.processor.list;

import javax.swing.JComboBox;

import org.fest.swing.fixture.JComboBoxFixture;

import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessor;

class JComboBoxSetActionProcessor implements ActionProcessor<JComboBox>{

	@Override
	public String processCommandOnComponent(CommandRequest command, JComboBox target) {
		JComboBoxFixture fixture = new JComboBoxFixture(FestRobotInstance.getRobot(), target);
		fixture.focus().enterText(command.value);
		int selectedIndex = fixture.component().getSelectedIndex();
		if(command.value.equalsIgnoreCase(fixture.valueAt(selectedIndex))){
			return TestResult.ResultKind.SUCCESS.name();
		}else{
			return TestResult.ResultKind.ERROR.name();
		}
	}

}
