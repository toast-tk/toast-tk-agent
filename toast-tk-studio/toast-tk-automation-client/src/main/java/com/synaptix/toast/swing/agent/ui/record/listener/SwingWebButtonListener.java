package com.synaptix.toast.swing.agent.ui.record.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import io.toast.tk.core.agent.inspection.ISwingAutomationClient;

public class SwingWebButtonListener implements ActionListener {

	private ISwingAutomationClient recorder;

	public SwingWebButtonListener(ISwingAutomationClient recorder) {
		this.recorder = recorder;
	}

	@Override
	public void actionPerformed(
		ActionEvent e) {
		if (recorder.isWebMode()) {
			
		} else {
			
		}
	}
	
}
