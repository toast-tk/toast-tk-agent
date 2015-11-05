package com.synaptix.toast.adapter.swing.handler.list;

import javax.swing.JComboBox;

import org.fest.swing.fixture.JComboBoxFixture;

import com.synaptix.toast.adapter.swing.handler.ActionProcessor;
import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.core.net.request.CommandRequest;

@FixMe(todo = "fix cause it will throw an enum exception on SwingActionRequestListener side !")
class JComboBoxGetActionProcessor implements ActionProcessor<JComboBox> {

	@Override
	public String processCommandOnComponent(
		CommandRequest command,
		JComboBox target) {
		JComboBoxFixture fixture = new JComboBoxFixture(FestRobotInstance.getRobot(), target);
		int selectedIndex = fixture.component().getSelectedIndex();
		return fixture.selectItem(selectedIndex).toString();
	}
}
