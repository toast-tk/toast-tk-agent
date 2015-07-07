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
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.CollectionUtils;
import org.fit.cssbox.swingbox.BrowserPane;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.core.agent.interpret.InterpretedEvent;
import com.synaptix.toast.core.rest.ImportedScenario;
import com.synaptix.toast.core.rest.ImportedScenarioDescriptor;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.core.runtime.ITestManager;
import com.synaptix.toast.runtime.core.runtime.DefaultScriptRunner;
import com.synaptix.toast.runtime.core.runtime.IReportUpdateCallBack;
import com.synaptix.toast.swing.agent.AgentBoot;
import com.synaptix.toast.swing.agent.config.Config;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;

public class SwingInspectionRecorderPanel extends JPanel {

	private static final long serialVersionUID = -8096917642917989626L;

	private final JTextArea interpretedOutputArea;

	private final JButton openScenarioButton;

	private final JButton startStopRecordButton;

	private final JButton saveScenarioButton;

	private final JButton runButton;

	private final Config config;

	private ITestManager testEnvManager;

	private final JComboBox comboBox;

	private final static long WAIT_THRESHOLD = 15; // in sec, TODO: link with
// fixture exist timeout

	private static final String stopRecordingLabel = "Stop recording";

	private static final String startRecordingLabel = "Start recording";

	private static final ImageIcon stopRecordingIcon = new ImageIcon(Resource.ICON_STOP_16PX_IMG);

	private static final ImageIcon startRecordingIcon = new ImageIcon(Resource.ICON_RUN_16PX_IMG);

	private ISwingAutomationClient recorder;

	private DefaultScriptRunner runner;

	private Long previousTimeStamp;

	private boolean recordingActive;

	private final MongoRepositoryCacheWrapper mongoRepoManager;

	@Inject
	public SwingInspectionRecorderPanel(
		ISwingAutomationClient recorder,
		EventBus eventBus,
		Config config,
		ITestManager testEnvManager,
		final MongoRepositoryCacheWrapper mongoRepoManager) {
		super(new BorderLayout());
		eventBus.register(this);
		this.recorder = recorder;
		this.config = config;
		this.testEnvManager = testEnvManager;
		this.interpretedOutputArea = new JTextArea();
		this.mongoRepoManager = mongoRepoManager;
		this.comboBox = new JComboBox(new String[]{
			"RedPlay"
		});
		this.startStopRecordButton = new JButton(startRecordingLabel, startRecordingIcon);
		this.startStopRecordButton.setToolTipText("Start/Stop recording your actions in a scenario");
		this.saveScenarioButton = new JButton("Share Scenario", new ImageIcon(Resource.ICON_SHARE_16PX_IMG));
		this.saveScenarioButton.setToolTipText("Publish the scenario on Toast Tk Webapp !");
		this.openScenarioButton = new JButton("Open Scenario");
		this.runButton = new JButton("Run Test", new ImageIcon(Resource.ICON_RUN_16PX_IMG));
		this.runButton.setToolTipText("Execute current scenario..");
		eventBus.register(this);
		interpretedOutputArea.setText("");
		JScrollPane scrollPanelRight = new JScrollPane(interpretedOutputArea);
		final JPanel commandPanel = new JPanel();
		commandPanel.add(openScenarioButton);
		commandPanel.add(startStopRecordButton);
		commandPanel.add(saveScenarioButton);
		commandPanel.add(runButton);
		// commandPanel.add(comboBox);
		add(commandPanel, BorderLayout.PAGE_START);
		add(scrollPanelRight, BorderLayout.CENTER);
		initActions();
	}

	private void enableRecording() {
		this.startStopRecordButton.setEnabled(true);
	}

	private void disableRecording() {
		this.startStopRecordButton.setEnabled(false);
	}

	private void initActions() {
		if(recorder.isConnected()) {
			enableRecording();
		}
		else {
			disableRecording();
		}
		openScenarioButton.addActionListener(new ActionListener() {

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
		});
		startStopRecordButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(
				ActionEvent e) {
				try {
					if(recorder.isConnected()) {
						if(!recordingActive) {
							recordingActive = true;
							startStopRecordButton.setText(stopRecordingLabel);
							startStopRecordButton.setIcon(stopRecordingIcon);
							recorder.startRecording();
						}
						else {
							recordingActive = false;
							previousTimeStamp = null;
							startStopRecordButton.setText(startRecordingLabel);
							startStopRecordButton.setIcon(startRecordingIcon);
							recorder.stopRecording();
						}
					}
					else {
						startStopRecordButton.setText(startRecordingLabel);
						startStopRecordButton.setIcon(startRecordingIcon);
						recordingActive = false;
						previousTimeStamp = null;
					}
				}
				catch(Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		comboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(
				ItemEvent e) {
				recorder.setMode(0);
			}
		});
		saveScenarioButton.addActionListener(new ActionListener() {

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
							JOptionPane.showMessageDialog(
								SwingInspectionRecorderPanel.this,
								"Scenario succesfully saved !",
								"Save Scenario",
								JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							JOptionPane.showMessageDialog(
								SwingInspectionRecorderPanel.this,
								"Scenario not saved !",
								"Save Scenario",
								JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				else {
					JOptionPane.showMessageDialog(
						SwingInspectionRecorderPanel.this,
						"Scenario can't be saved, repository not updated !",
						"Repository Update",
						JOptionPane.ERROR_MESSAGE);
				}
				saveScenarioButton.setEnabled(true);
			}
		});
		this.runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(
				ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						final String test = interpretedOutputArea.getText();
						if(!recorder.isConnected()) {
							JOptionPane.showMessageDialog(
								null,
								"Automation agent offline, please launch the System Under Test with an active agent!");
						}
						else if(test != null && !test.isEmpty()) {
							if(runner == null) {
								runner = new DefaultScriptRunner(AgentBoot.injector);
							}
							final String wikiScenario = toWikiScenario(test);
							final BrowserPane swingbox = new BrowserPane();
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									JDialog dialog = new JDialog();
									dialog.setSize(500, 300);
									dialog.setTitle("Execution report..");
									dialog.setLayout(new BorderLayout());
									dialog.setModalityType(ModalityType.APPLICATION_MODAL);
									dialog.add(swingbox);
									dialog.setVisible(true);
								}
							});
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									try {
										runner.runLocalScript(
											wikiScenario,
											mongoRepoManager.getWikiFiedRepo(),
											new IReportUpdateCallBack() {

												@Override
												public void onUpdate(
													final String report) {
													swingbox.setText(report);
													swingbox.revalidate();
												}

												@Override
												public void onFatalStepError(
													String message) {
													JOptionPane.showMessageDialog(null, message);
												}
											});
									}
									catch(IllegalAccessException e) {
										e.printStackTrace();
									}
									catch(ClassNotFoundException e) {
										e.printStackTrace();
									}
									catch(IOException e) {
										e.printStackTrace();
									}
								}
							});
						}
						else {
							JOptionPane.showMessageDialog(null, "Script Text Area is Empty !");
						}
					}
				});
			}

			private String toWikiScenario(
				final String test) {
				final StringBuilder sb = new StringBuilder(1024);
				sb.append("|| scenario || swing ||\n"); // TODO: bind type to
// selected descriptor
				final String[] lines = test.split("\n");
				for(final String line : lines) {
					sb.append('|').append(line).append('|').append('\n');
				}
				return sb.toString();
			}
		});
	}

	@Subscribe
	public synchronized void handleInterpretedEvent(
		final InterpretedEvent event) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				interpretedOutputArea.append(event.getEventData() + "\n");
				interpretedOutputArea.setCaretPosition(interpretedOutputArea.getDocument().getLength());
				String waitInstruction = appendWait(event.getTimeStamp());
				if(waitInstruction != null) {
					interpretedOutputArea.append(waitInstruction + "\n");
					interpretedOutputArea.setCaretPosition(interpretedOutputArea.getDocument().getLength());
				}
			}
		});
	}

	private String appendWait(
		Long newTimeStamp) {
		if(newTimeStamp == null) {
			return null;
		}
		if(previousTimeStamp == null) {
			previousTimeStamp = newTimeStamp;
		}
		long delta = ((newTimeStamp - previousTimeStamp) / 1000000000);
		String res = delta > WAIT_THRESHOLD ? "wait " + delta + "s" : null;
		previousTimeStamp = newTimeStamp;
		return res;
	}

	@Subscribe
	public void handleServerConnexionStatus(
		SeverStatusMessage startUpMessage) {
		switch(startUpMessage.state) {
			case CONNECTED :
				enableRecording();
				break;
			default :
				disableRecording();
				break;
		}
	}

	public static void main(
		String[] args) {
		String style = "body{line-height: 1.6em;font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", \"Sans-Serif\";font-size: 12px;padding-left: 1em;}table{font-size: 12px;text-align: left;color: black;border: 3px solid darkgray;margin-top: 8px;margin-bottom: 12px;}th{font-size: 14px;font-weight: normal;padding: 6px 4px;border: 1px solid darkgray;}td{border: 1px solid darkgray;padding: 4px 4px;}h3{margin-top: 24pt;}div{margin: 0px;}.summary {text-align: justify;letter-spacing: 1px;padding: 2em;background-color: darkgray;color: white;} .resultSuccess {background-color:green;} .resultFailure {background-color:red;} .resultInfo {background-color:lightblue;} .resultError {background-color: orange;}.noResult {background-color: white;}.message {font-weight: bold;}";
		String html = "<html><head>" +
			"<style>" + style + "</style>" +
			"</head><body><div class='summary'>Test</div></body></html>";
		final BrowserPane swingbox = new BrowserPane();
		swingbox.setText(html);
		JDialog dialog = new JDialog();
		dialog.setSize(500, 300);
		dialog.setTitle("Execution report..");
		dialog.setLayout(new BorderLayout());
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		JScrollPane panel = new JScrollPane();
		panel.getViewport().add(swingbox);
		dialog.add(panel);
		dialog.setVisible(true);
	}
}
