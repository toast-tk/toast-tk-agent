package com.synaptix.toast.swing.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.IStudioApplication;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.core.agent.interpret.InterpretedEvent;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;
import com.synaptix.toast.swing.agent.runtime.SutRunnerAsExec;
import com.synaptix.toast.swing.agent.ui.record.SwingInspectionRecorderPanel;

public class HomePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;

	private JTextArea configuration;

	private Config config;

	private JButton configButton;

	private JButton initButton;
	
	private JPanel jPanelButton;
	
	private JSwitchBox webSwingSlider;
	
	private final IStudioApplication app;

	private final SutRunnerAsExec runtime;

	private ISwingAutomationClient recorder;

	private static final ImageIcon ampouleIcon = new ImageIcon(Resource.ICON_AMPOULE_16PX_IMG);
	
	private SwingInspectionRecorderPanel recorderPanel;
	
	@Inject
	public HomePanel(Config config,
			final SutRunnerAsExec runtime,
			final IStudioApplication app,
			SwingInspectionRecorderPanel recorderPanel,
			ISwingAutomationClient recorder,
			@StudioEventBus EventBus eventBus) {
		this.config = config;
		this.recorder = recorder;
		this.recorderPanel = recorderPanel;
		this.configuration = new JTextArea();
		this.configuration.setEditable(false);
		this.configuration.setBorder(null);
		this.configuration.setBackground(Color.white);
		this.runtime = runtime;
		this.app = app;
		
		add(buildPanel());
		initText();
		initActions();
	}

	private JPanel buildPanel() {
		JPanel panel = new JPanel();
		JScrollPane scrollPanel = new JScrollPane(recorderPanel.getInterpretedOutputArea());
		scrollPanel.setPreferredSize(new Dimension(1000, 500));
		
	    Border outline = BorderFactory.createLineBorder(Color.black);
	    panel.setBorder(outline);
	    BoxLayout bxl = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
	    panel.setLayout(bxl);
	    panel.add(buildHomePanel());
	    panel.add(scrollPanel);
	    return panel;
	}

	private JPanel buildHomePanel() {
		final JPanel homePanel = new JPanel();
		homePanel.setBackground(Color.white);
		homePanel.setPreferredSize(new Dimension(1000, 50));
		JScrollPane scrollPanelRight = new JScrollPane(this.configuration);
		scrollPanelRight.setBorder(null);
		
		JLabel image = new JLabel(ampouleIcon);
		homePanel.add(image);
		homePanel.add(scrollPanelRight, BorderLayout.CENTER);
		
		jPanelButton = new JPanel();
		jPanelButton.setBackground(Color.white);
		initButton = new JButton("Initialiser");
		initButton.setMnemonic(KeyEvent.VK_F);
		initButton.setBackground(Color.green);
		initButton.setToolTipText("Download the system under test, and open a bat to start it's inspection & recording...");
		initButton.setIcon(new ImageIcon(Resource.ICON_POWER_16PX_IMG));
		webSwingSlider = new JSwitchBox("Web", "Swing");
		configButton = new JButton("Ok");
		
		jPanelButton.add(initButton);
		jPanelButton.add(configButton);
		jPanelButton.add(webSwingSlider);
		homePanel.add(jPanelButton, BorderLayout.PAGE_END);
		
		return homePanel;
	}
	
	private void initActions() {
		this.initButton.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(
				ActionEvent e) {
				initButton.setVisible(false);
				String configText = config.getRuntimeCommand() == null ? "" : config.getRuntimeCommand() ;
				String message = "SUT : " + configText +
						"" +
						"\n--> Aller dans les paramètres avancés pour changer de programme";
				configuration.setText(message);
				
				
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground()
						throws Exception {
						String runtimeType = app.getRuntimeType();
						try {
							publish();
							runtime.init(runtimeType, true);
							Desktop.getDesktop().open(new File(Config.TOAST_HOME_DIR));
							app.stopProgress("Done !");
							int response = JOptionPane.showConfirmDialog(
								HomePanel.this,
								"SUT initialized, do you want to start it ?");
							if(response == 1) {
								runtime.executeSutBat();
							}
						}
						catch(IllegalAccessException e) {
							e.printStackTrace();
						}
						catch(SAXException e) {
							e.printStackTrace();
						}
						catch(IOException e) {
							e.printStackTrace();
						}
						catch(ParserConfigurationException e) {
							e.printStackTrace();
						}
						return Void.TYPE.newInstance();
					}

					@Override
					protected void process(
						List<Void> chunks) {
						super.process(chunks);
						app.startProgress("Starting SUT..");
					}
				};
				worker.execute();
			}
		});
		
		this.configButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(
				ActionEvent e) {
				configButton.setVisible(false);
				configuration.setText("SUT non initialisé");
				initButton.setVisible(true);
			}
		});
		
		this.webSwingSlider.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(
				ItemEvent e) {
				final String item = webSwingSlider.getText();
				if(item.equals("Swing")){
					recorder.switchToSwingRecordingMode();
				} else {
					recorder.switchToWebRecordingMode();
				}
			}
		});
	}
	
	@Subscribe
	public synchronized void handleInterpretedEvent(
		final InterpretedEvent event) {
		recorderPanel.handleInterpretedEvent(event);
	}

	private void initText() {
		String sut = config.getRuntimeCommand();
		configButton.setVisible(false);
		initButton.setVisible(false);
		if ("".equals(sut)) {
			String text = "Il n'y a pas de projet configuré \n"
					+ "\nAller dans les paramètres avancés pour configurer";
			configuration.setText(text);
			configButton.setVisible(true);
		}  else {
			configuration.setText("SUT non initialisé");
			initButton.setVisible(true);
		}
	}
}
