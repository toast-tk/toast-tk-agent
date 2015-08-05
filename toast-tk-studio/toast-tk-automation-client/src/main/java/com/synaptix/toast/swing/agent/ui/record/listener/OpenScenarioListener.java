package com.synaptix.toast.swing.agent.ui.record.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.synaptix.toast.core.rest.ImportedScenario;
import com.synaptix.toast.core.rest.ImportedScenarioDescriptor;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.swing.agent.ui.ListPanel;


public class OpenScenarioListener implements ActionListener {
	
	private JTextArea interpretedOutputArea;

	public OpenScenarioListener(JTextArea interpretedOutputArea){
		this.interpretedOutputArea= interpretedOutputArea;
	}

	@Override
	public void actionPerformed(
		ActionEvent e) {
		List<ImportedScenario> listOfScenario = new ArrayList<ImportedScenario>(RestUtils.getListOfScenario());
		String[] scenarioItems = new String[listOfScenario.size()];
		for(int i = 0; i < listOfScenario.size(); i++) {
			scenarioItems[i] = listOfScenario.get(i).getName();
		}
		final ListPanel elp = new ListPanel(scenarioItems);
		JOptionPane.showMessageDialog(null, elp);
		int selectedIndex = elp.getSelectedIndex();
		if(selectedIndex > -1) {
			ImportedScenario importedScenario = listOfScenario.get(selectedIndex);
			ImportedScenarioDescriptor scenarioDescriptor = RestUtils.getScenario(importedScenario);
			if(scenarioDescriptor != null) {
				interpretedOutputArea.setText("");
				interpretedOutputArea.setText(scenarioDescriptor.getRows());
				interpretedOutputArea.setCaretPosition(interpretedOutputArea.getDocument().getLength());
			}
			else {
				JOptionPane.showMessageDialog(null, "Scenario couldn't be loaded !");
			}
		}		
	}
}
