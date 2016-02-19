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
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;
import com.synaptix.toast.swing.agent.ui.record.SwingInspectionRecorderPanel;
import com.synaptix.toast.swing.agent.ui.record.listener.ShareScenarioListener;
import com.synaptix.toast.swing.agent.ui.record.listener.StartStopRecordListener;

public class HeaderPanel extends JPanel {

	private static final long serialVersionUID = -8096917642917989626L;

	private final JButton connectButton;

	private final JButton startStopRecordButton;

	private final JButton saveScenarioButton;

	private final JButton homeButton;

	private final JButton advancedSettingsButton;

	private static final String startRecordingLabel = "Start";

	private static final ImageIcon startRecordingIcon = new ImageIcon(Resource.ICON_RUN_16PX_IMG);
	
	private static final ImageIcon connectedIcon = new ImageIcon(Resource.ICON_PRISE_16PX_IMG);
	
	private static final ImageIcon disconnectedIcon = new ImageIcon(Resource.ICON_ARRET_16PX_IMG);

	private ISwingAutomationClient recorder;
	
	private final Config config;
	
	private ISwingAutomationClient serverClient;

	private CorpusPanel corpusPanel;
	
	private AdvancedSettingsPanel advancedSettingsPanel;

	public static CardLayout cl = new CardLayout();
	
	String[] listPanel = {"Home", "Recorder", "Inspection", "AdvancedSettings"};

	@Inject
	public HeaderPanel(
		final HomePanel homePanel,
		final SwingInspectorPanel inspectorPanel,
		final SwingInspectionRecorderPanel recorderPanel,
		final AdvancedSettingsPanel advancedSettingsPanel,
		CorpusPanel corpusPanel,
		Config config,
		ISwingAutomationClient recorder,
		final ISwingAutomationClient serverClient,
		@StudioEventBus EventBus eventBus) {
		super();
		this.corpusPanel = corpusPanel;
		this.advancedSettingsPanel = advancedSettingsPanel;
		this.serverClient = serverClient;
		this.recorder = recorder;
		this.connectButton = new JButton("Connect", connectedIcon);
		this.connectButton.setToolTipText("Connect to the SUT");
		this.startStopRecordButton = new JButton(startRecordingLabel, startRecordingIcon);
		this.startStopRecordButton.setToolTipText("Start/Stop recording your actions in a scenario");
		this.saveScenarioButton = new JButton("Share Scenario", new ImageIcon(Resource.ICON_SHARE_16PX_IMG));
		this.saveScenarioButton.setToolTipText("Publish the scenario on Toast Tk Webapp !");
		this.homeButton = new JButton("Home", new ImageIcon(Resource.ICON_HOME_16PX_IMG));
		this.homeButton.setToolTipText("Accueil");
		this.advancedSettingsButton = new JButton("Advanced Settings", new ImageIcon(Resource.ICON_SETTINGS_16PX_IMG));
		this.advancedSettingsButton.setToolTipText("Paramètres avancés");
		this.config = config;
		
		final JPanel commandPanel = buildCommandPanel();
		add(commandPanel, BorderLayout.NORTH);
		
		corpusPanel.setLayout(cl);
		corpusPanel.add(homePanel, listPanel[0]);
		corpusPanel.add(recorderPanel, listPanel[1]);
		corpusPanel.add(inspectorPanel, listPanel[2]);
		corpusPanel.add(advancedSettingsPanel, listPanel[3]);
		add(corpusPanel, BorderLayout.CENTER);
		initActions();
	}

	private JPanel buildCommandPanel() {
		final JPanel commandPanel = new JPanel();
		commandPanel.add(this.homeButton, BorderLayout.BEFORE_LINE_BEGINS);
		final JPanel shortcutsPanel = new JPanel();
		shortcutsPanel.add(this.connectButton, BorderLayout.CENTER);
		shortcutsPanel.add(this.startStopRecordButton, BorderLayout.CENTER);
		shortcutsPanel.add(this.saveScenarioButton,BorderLayout.CENTER);
		commandPanel.add(shortcutsPanel, BorderLayout.CENTER);
		commandPanel.add(this.advancedSettingsButton, BorderLayout.AFTER_LINE_ENDS);
		return commandPanel;
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
				cl.show(corpusPanel, "Home");
			}
	    });

		this.startStopRecordButton.addActionListener(new StartStopRecordListener(recorder, startStopRecordButton));
		this.saveScenarioButton.addActionListener(
				new ShareScenarioListener(recorder, config, advancedSettingsPanel.getInterpretedOutputArea(), saveScenarioButton));
		
		this.connectButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (serverClient.isConnected()) {
					serverClient.disconnect();
					connectButton.setToolTipText("Connect from SUT");
					connectButton.setText("Connect");
					connectButton.setIcon(connectedIcon);
					disableRecording();
				} else {
					serverClient.connect();
					if (serverClient.isConnected()) {
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
				cl.show(corpusPanel, "AdvancedSettings");
			}
	    });
		
//		randomButton.addActionListener(new ActionListener(){
//			public void actionPerformed(ActionEvent event){
//				cl.next(corpusPanel);
//			}
//	    });

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
		cl.show(corpusPanel, "AdvancedSettings");
	}
}
