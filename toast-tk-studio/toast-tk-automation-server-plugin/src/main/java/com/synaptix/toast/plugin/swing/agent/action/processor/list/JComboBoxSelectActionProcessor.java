package com.synaptix.toast.plugin.swing.agent.action.processor.list;

import javax.swing.JComboBox;

import org.apache.commons.lang3.StringUtils;
import org.fest.swing.fixture.JComboBoxFixture;

import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.report.TestResult;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessor;

class JComboBoxSelectActionProcessor implements ActionProcessor<JComboBox> {

	@Override
	public String processCommandOnComponent(
		CommandRequest command,
		JComboBox target) {
		JComboBoxFixture fixture = new JComboBoxFixture(FestRobotInstance.getRobot(), target);
		if(StringUtils.isNumeric(command.value)) {
			int indexToSelect = Integer.parseInt(command.value);
			if(indexToSelect >= 0 && indexToSelect < fixture.component().getItemCount()) {
				fixture.selectItem(indexToSelect);
				return TestResult.ResultKind.SUCCESS.name();
			}
			else {
				return TestResult.ResultKind.ERROR.name();
			}
		}
		else {
			fixture.selectItem(command.value);
			int selectedIndex = fixture.component().getSelectedIndex();
			if(command.value.equalsIgnoreCase(fixture.valueAt(selectedIndex))) {
				return TestResult.ResultKind.SUCCESS.name();
			}
			else {
				return TestResult.ResultKind.ERROR.name();
			}
		}
	}
}
