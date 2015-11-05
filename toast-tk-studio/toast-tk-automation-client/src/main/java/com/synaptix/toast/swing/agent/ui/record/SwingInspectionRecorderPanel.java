package com.synaptix.toast.swing.agent.ui.record;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fit.cssbox.swingbox.BrowserPane;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.core.agent.interpret.InterpretedEvent;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;
import com.synaptix.toast.swing.agent.runtime.StudioScriptRunner;
import com.synaptix.toast.swing.agent.runtime.SutRunnerAsExec;
import com.synaptix.toast.swing.agent.ui.record.listener.OpenScenarioListener;
import com.synaptix.toast.swing.agent.ui.record.listener.RunScriptActionListener;
import com.synaptix.toast.swing.agent.ui.record.listener.ShareScenarioListener;
import com.synaptix.toast.swing.agent.ui.record.listener.StartStopRecordListener;

public class SwingInspectionRecorderPanel extends JPanel {

	private static final Logger LOG = LogManager.getLogger(SwingInspectionRecorderPanel.class);

	private static final long serialVersionUID = -8096917642917989626L;

	private final JButton openScenarioButton;

	private final JButton startStopRecordButton;

	private final JButton saveScenarioButton;

	private final JButton runButton;
	
	private final JButton openJar;

	private final Config config;

	private final JTextArea interpretedOutputArea;

	private final JComboBox recordTypeComboBox;

	private static final long WAIT_THRESHOLD = 15; // in sec, TODO: link with fixture exist timeout

	private static final String startRecordingLabel = "Start";

	private static final ImageIcon startRecordingIcon = new ImageIcon(Resource.ICON_RUN_16PX_IMG);

	private ISwingAutomationClient recorder;

	private StudioScriptRunner runner;

	private Long previousTimeStamp;
	
	private final MongoRepositoryCacheWrapper mongoRepoManager;

	@Inject
	public SwingInspectionRecorderPanel(
		ISwingAutomationClient recorder,
		EventBus eventBus,
		Config config,
		final MongoRepositoryCacheWrapper mongoRepoManager) {
		super(new BorderLayout());
		eventBus.register(this);
		this.recorder = recorder;
		this.config = config;
		this.interpretedOutputArea = new JTextArea();
		this.mongoRepoManager = mongoRepoManager;
		this.recordTypeComboBox = new JComboBox(new String[]{
			"Web", "Swing" 
		});
		this.startStopRecordButton = new JButton(startRecordingLabel, startRecordingIcon);
		this.startStopRecordButton.setToolTipText("Start/Stop recording your actions in a scenario");
		this.saveScenarioButton = new JButton("Share Scenario", new ImageIcon(Resource.ICON_SHARE_16PX_IMG));
		this.saveScenarioButton.setToolTipText("Publish the scenario on Toast Tk Webapp !");
		this.openScenarioButton = new JButton("Open Scenario");
		this.runButton = new JButton("Run Test", new ImageIcon(Resource.ICON_RUN_16PX_IMG));
		this.runButton.setToolTipText("Execute current scenario..");
		this.openJar = new JButton("Open Jar");
		this.interpretedOutputArea.setText("");
		JScrollPane scrollPanelRight = new JScrollPane(this.interpretedOutputArea);
		final JPanel commandPanel = buildCommandPanel();
		add(commandPanel, BorderLayout.PAGE_START);
		add(scrollPanelRight, BorderLayout.CENTER);
		initActions();
	}

	private JPanel buildCommandPanel() {
		final JPanel commandPanel = new JPanel();
		commandPanel.add(this.openJar);
		commandPanel.add(this.openScenarioButton);
		commandPanel.add(this.startStopRecordButton);
		commandPanel.add(this.saveScenarioButton);
		commandPanel.add(this.runButton);
		commandPanel.add(this.recordTypeComboBox);
		return commandPanel;
	}

	private void enableRecording() {
		this.startStopRecordButton.setEnabled(true);
		this.startStopRecordButton.setText("Stop");
	}

	private void disableRecording() {
		this.startStopRecordButton.setEnabled(false);
		this.startStopRecordButton.setText("Start");
	}

	private void initActions() {
		if(this.recorder.isConnected()) {
			enableRecording();
		}
		else {
			disableRecording();
		}
		this.openScenarioButton.addActionListener(new OpenScenarioListener(interpretedOutputArea));
		this.startStopRecordButton.addActionListener(new StartStopRecordListener(recorder, startStopRecordButton));
		this.saveScenarioButton.addActionListener(new  ShareScenarioListener(recorder, config, interpretedOutputArea, saveScenarioButton));
		this.runButton.addActionListener(new RunScriptActionListener(recorder, interpretedOutputArea, runner, mongoRepoManager));
		this.recordTypeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(
				ItemEvent e) {
				final String item = (String)recordTypeComboBox.getSelectedItem();
				if(item.equals("Swing")){
					recorder.switchToSwingRecordingMode();
				}else{
					recorder.switchToWebRecordingMode();
				}
			}
		});
		this.openJar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(SwingInspectionRecorderPanel.this);
				final File selectedFile = chooser.getSelectedFile();
				new Thread(new Runnable() {
					@Override
					public void run() {
						SutRunnerAsExec runner = SutRunnerAsExec.FromLocalConfiguration(config);
						try {
							runner.launchJarInspection(selectedFile);
						} catch (IllegalAccessException e) {
							LOG.error(e.getMessage(), e);
						}
					}
				}).start();
				
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
		if(this.previousTimeStamp == null) {
			this.previousTimeStamp = newTimeStamp;
		}
		long delta = ((newTimeStamp - previousTimeStamp) / 1000000000);
		String res = delta > WAIT_THRESHOLD ? "wait " + delta + "s" : null;
		this.previousTimeStamp = newTimeStamp;
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
