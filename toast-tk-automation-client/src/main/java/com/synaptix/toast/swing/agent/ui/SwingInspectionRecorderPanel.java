/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 6 févr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.swing.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.automation.config.Config;
import com.synaptix.toast.automation.utils.Resource;
import com.synaptix.toast.core.inspection.ISwingInspectionClient;
import com.synaptix.toast.core.interpret.InterpretedEvent;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.swing.agent.AgentBoot;
import com.synpatix.toast.runtime.core.runtime.DefaultScriptRunner;

public class SwingInspectionRecorderPanel extends JPanel{
	private static final long serialVersionUID = -8096917642917989626L;
	private final static long WAIT_THRESHOLD = 10; //in sec, TODO: link with fixture exist timeout
	private final JTextArea interpretedOutputArea;
	private final JButton startRecordButton;
	private final JButton stopRecordButton;
    private final JButton saveScenarioButton; 
	private final JButton runButton;
	private final Config config;
    
	private final JComboBox comboBox = new JComboBox(new String[]{"RedPlay"});
	private DefaultScriptRunner runner;
    Long previousTimeStamp;
    
	private ISwingInspectionClient recorder;
	
	@Inject
	public SwingInspectionRecorderPanel(ISwingInspectionClient recorder, EventBus eventBus, Config config){
		super(new BorderLayout());
		this.recorder = recorder;
		this.config = config;
		this.interpretedOutputArea = new JTextArea();
		this.startRecordButton = new JButton("Start recording", new ImageIcon(Resource.ICON_RUN_16PX_IMG));
		this.startRecordButton.setToolTipText("Start recording your actions in a scenario");
		
		this.stopRecordButton = new JButton("Stop recording", new ImageIcon(Resource.ICON_STOP_16PX_IMG));
		this.startRecordButton.setToolTipText("Stop action recording");
		
		this.saveScenarioButton = new JButton("Share Scenario", new ImageIcon(Resource.ICON_SHARE_16PX_IMG));
		this.saveScenarioButton.setToolTipText("Publish the scenario on Toast Tk Webapp !");
		
		this.runButton = new JButton("Run Test", new ImageIcon(Resource.ICON_RUN_16PX_IMG));
		this.runButton.setToolTipText("Execute current scenario..");
		
		eventBus.register(this);
        interpretedOutputArea.setText("");
        
        JScrollPane scrollPanelRight = new JScrollPane(interpretedOutputArea);
        
		final JPanel commandPanel = new JPanel();
        commandPanel.add(startRecordButton);
        commandPanel.add(stopRecordButton);
        commandPanel.add(saveScenarioButton);
        commandPanel.add(runButton);
        //commandPanel.add(comboBox);
		add(commandPanel, BorderLayout.PAGE_START);
        add(scrollPanelRight, BorderLayout.CENTER);
        
        initActions();
	}

	private void initActions() {
		startRecordButton.setBackground(Color.GREEN);
		startRecordButton.setEnabled(true);
		stopRecordButton.setEnabled(false);
		startRecordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					startRecordButton.setEnabled(false);
					stopRecordButton.setBackground(Color.GREEN);
					stopRecordButton.setEnabled(true);
					recorder.startRecording();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
        stopRecordButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					startRecordButton.setBackground(Color.GREEN);
					startRecordButton.setEnabled(true);
					stopRecordButton.setEnabled(false);
					previousTimeStamp = null;
					recorder.stopRecording();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
        comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				recorder.setMode(0);
			}
		});
        
        saveScenarioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recorder.saveObjectsToRepository();
				saveScenarioButton.setEnabled(false);
				String scenarioName = JOptionPane.showInputDialog("Scenario name: ");
				boolean saved = RestUtils.postScenario(scenarioName, config.getWebAppAddr(), config.getWebAppPort(), interpretedOutputArea.getText());
				if(saved){
					JOptionPane.showMessageDialog(SwingInspectionRecorderPanel.this, "Scenario succesfully saved !", "Save Scenario", JOptionPane.INFORMATION_MESSAGE);
				}else {
					JOptionPane.showMessageDialog(SwingInspectionRecorderPanel.this, "Scenario not saved !", "Save Scenario", JOptionPane.ERROR_MESSAGE);
				}
				saveScenarioButton.setEnabled(true);
			}
		});
        
        this.runButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String test = interpretedOutputArea.getText();
						if (test != null) {
							// TODO: check if client is connected !
							if (runner == null) {
								runner = new DefaultScriptRunner(AgentBoot.injector);
							}
							String wikiScenario = toWikiScenario(test);
							runner.runRemoteScript(wikiScenario);
						} else {
							JOptionPane.showMessageDialog(null, "Script Text Area is Empty !");
						}
					}
				});
			}

			private String toWikiScenario(String test) {
				String output = "|| scenario || swing ||\n";
				String[] lines = test.split("\n");
				for (String line : lines) {
					output += "|" + line + "|\n";
				}
				return output;
			}
		});
		
	}
	
    @Subscribe
    public synchronized void handleInterpretedEvent(final InterpretedEvent event){
    	SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				interpretedOutputArea.append(event.getEventData() + "\n");
				String waitInstruction = appendWait(event.getTimeStamp());
				if(waitInstruction != null){
					interpretedOutputArea.append(waitInstruction + "\n");
				}
				interpretedOutputArea.setCaretPosition(interpretedOutputArea.getDocument().getLength());
			}
		});
    }
    
    private String appendWait(Long newTimeStamp){
    	if(newTimeStamp == null){
    		return null;
    	}
    	if(previousTimeStamp == null){
    		previousTimeStamp = newTimeStamp;
    	}
    	long delta = ((newTimeStamp - previousTimeStamp)/1000000000);
    	String res = delta > WAIT_THRESHOLD ? "wait " + delta + "s" : null;
    	previousTimeStamp = newTimeStamp; 
    	return res;
    }
    
}
