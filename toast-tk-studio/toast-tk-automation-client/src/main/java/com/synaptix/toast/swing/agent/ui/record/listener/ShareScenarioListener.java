package com.synaptix.toast.swing.agent.ui.record.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.swing.agent.config.Config;


public class ShareScenarioListener implements ActionListener{

	private final JButton saveScenarioButton;

	private ISwingAutomationClient recorder;

	private final Config config;

	private final JTextArea interpretedOutputArea;
	
	public ShareScenarioListener(ISwingAutomationClient recorder, Config config, JTextArea interpretedOutputArea, JButton saveScenarioButton){
		this.recorder = recorder;
		this.config = config;
		this.interpretedOutputArea = interpretedOutputArea;
		this.saveScenarioButton = saveScenarioButton;
	}

	@Override
	public void actionPerformed(
		ActionEvent e) {
		saveScenarioButton.setEnabled(false);
		if(recorder.saveObjectsToRepository()) {
			String scenarioName = JOptionPane.showInputDialog("Scenario name: ");
			if(scenarioName != null) {
				boolean saved = RestUtils.postScenario(
					scenarioName,
					config.getWebAppAddr(),
					config.getWebAppPort(),
					interpretedOutputArea.getText());
				if(saved) {
					JOptionPane.showMessageDialog(null,
						"Scenario succesfully saved !",
						"Save Scenario",
						JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(null,
						"Scenario not saved !",
						"Save Scenario",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else {
			JOptionPane.showMessageDialog(null,
				"Scenario can't be saved, repository not updated !",
				"Repository Update",
				JOptionPane.ERROR_MESSAGE);
		}
		saveScenarioButton.setEnabled(true);
	}
}
