package com.synaptix.toast.swing.agent.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;
import com.synaptix.toast.swing.agent.ui.record.SwingInspectionRecorderPanel;
import com.synaptix.toast.swing.agent.ui.record.listener.ShareScenarioListener;
import com.synaptix.toast.swing.agent.ui.record.listener.StartStopRecordListener;

import io.toast.tk.core.agent.inspection.ISwingAutomationClient;

public class HeaderPanel extends JPanel {

	private static final long serialVersionUID = -8096917642917989626L;

	private final JButton connectButton;

	private final JButton startStopRecordButton;

	private final JButton saveScenarioButton;

	private final JButton homeButton;

	private final JButton advancedSettingsButton;

	private JButton killServerButton;

	private static final String startRecordingLabel = "Start";

	private static final ImageIcon startRecordingIcon = new ImageIcon(Resource.ICON_RUN_16PX_IMG);
	
	private static final ImageIcon connectedIcon = new ImageIcon(Resource.ICON_PRISE_16PX_IMG);
	
	private static final ImageIcon disconnectedIcon = new ImageIcon(Resource.ICON_ARRET_16PX_IMG);

	private ISwingAutomationClient recorder;
	
	private final Config config;
	
	private CorpusPanel corpusPanel;
	
	private AdvancedSettingsPanel advancedSettingsPanel;

	public static CardLayout corpusLayout = new CardLayout();

	private StartStopRecordListener startStopRecordListener;

	String[] listPanel = {"Home", "Recorder", "Inspection", "AdvancedSettings"};

	@Inject
	public HeaderPanel(
		final HomePanel homePanel,
		final SwingInspectorPanel inspectorPane,
		final SwingInspectionRecorderPanel recorderPane,
		final AdvancedSettingsPanel advancedSettingsPane,
		CorpusPanel corpusPane,
		Config config,
		final ISwingAutomationClient recorder,
		@StudioEventBus EventBus eventBus) {
		super();
		this.corpusPanel = corpusPane;
		this.advancedSettingsPanel = advancedSettingsPane;
		this.recorder = recorder;
		this.connectButton = new JButton("Connect", connectedIcon);
		this.connectButton.setToolTipText("Connect to Toast Recording Agent");
		this.startStopRecordButton = new JButton(startRecordingLabel, startRecordingIcon);
		this.startStopRecordButton.setToolTipText("Start/Stop recording your actions in a scenario");
		this.saveScenarioButton = new JButton("Share Scenario", new ImageIcon(Resource.ICON_SHARE_16PX_IMG));
		this.saveScenarioButton.setToolTipText("Publish the scenario on Toast Tk Webapp !");
		this.homeButton = new JButton("Home", new ImageIcon(Resource.ICON_HOME_16PX_IMG));
		this.homeButton.setToolTipText("Accueil");
		this.advancedSettingsButton = new JButton("Advanced Settings", new ImageIcon(Resource.ICON_SETTINGS_16PX_IMG));
		this.advancedSettingsButton.setToolTipText("Paramètres avancés");
		this.killServerButton = new JButton("Kill", new ImageIcon(Resource.ICON_KILL_POISON_16PX_IMG));
		this.killServerButton.setToolTipText("kill the server");
		this.config = config;
		
		final JPanel commandPanel = buildCommandPanel();
		add(commandPanel, BorderLayout.NORTH);
		
		corpusPane.setLayout(corpusLayout);
		corpusPane.add(homePanel, listPanel[0]);
		corpusPane.add(recorderPane, listPanel[1]);
		corpusPane.add(inspectorPane, listPanel[2]);
		corpusPane.add(advancedSettingsPane, listPanel[3]);
		add(corpusPane, BorderLayout.CENTER);
		initActions();
	}

	private JPanel buildCommandPanel() {
		final JPanel commandPane = new JPanel();
		commandPane.add(this.homeButton, BorderLayout.BEFORE_LINE_BEGINS);
		final JPanel shortcutsPane = new JPanel();
		shortcutsPane.add(this.connectButton);
		shortcutsPane.add(this.startStopRecordButton);
		shortcutsPane.add(this.saveScenarioButton);
		shortcutsPane.add(this.killServerButton);
		commandPane.add(shortcutsPane, BorderLayout.CENTER);
		commandPane.add(this.advancedSettingsButton, BorderLayout.AFTER_LINE_ENDS);
		return commandPane;
	}

	private void enableRecording() {
		this.startStopRecordButton.setEnabled(true);
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
		
		homeButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				corpusLayout.show(corpusPanel, "Home");
			}
	    });

		startStopRecordListener = new StartStopRecordListener(recorder, startStopRecordButton);
		this.startStopRecordButton.addActionListener(startStopRecordListener);
		this.saveScenarioButton.addActionListener(
				new ShareScenarioListener(recorder, config, advancedSettingsPanel.getInterpretedOutputArea(), saveScenarioButton));
		
		this.connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (recorder.isConnected()) {
					recorder.disconnect();
					resetConnectButton();
				} else {
					recorder.connect();
					if (recorder.isConnected()) {
						connectButton.setToolTipText("Disconnect from SUT");
						connectButton.setText("Disconnect");
						connectButton.setIcon(disconnectedIcon);
						enableRecording();
					}
				}
			}
		});

		advancedSettingsButton.addActionListener(
				new ActionListener(){
			public void actionPerformed(ActionEvent event){
				corpusLayout.show(corpusPanel, "AdvancedSettings");
			}
	    });

		killServerButton.addActionListener(
				new ActionListener(){
			public void actionPerformed(ActionEvent event){
				resetConnectButton();
				startStopRecordListener.stopRecording();
				recorder.killServer();
			}
	    });
	}

	private void resetConnectButton() {
		connectButton.setToolTipText("Connect from SUT");
		connectButton.setText("Connect");
		connectButton.setIcon(connectedIcon);
		disableRecording();
		killServerButton.setEnabled(false);
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

	public void switchAdvancedSettings() {
		corpusLayout.show(corpusPanel, "AdvancedSettings");
	}
}
