package com.synaptix.toast.swing.agent.ui.record.listener;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.google.inject.Inject;
import com.synaptix.toast.swing.agent.ui.CorpusPanel;

public class AdvancedSettingsListener implements ActionListener {

	CorpusPanel corpusPanel;
	CardLayout cl;
	
	@Inject
	public AdvancedSettingsListener(CorpusPanel corpusPanel,
			CardLayout cl){
		this.corpusPanel = corpusPanel;
		this.cl = cl;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		cl.show(corpusPanel, "Home");
	}

}
