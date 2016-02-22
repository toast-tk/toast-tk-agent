package com.synaptix.toast.swing.agent.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.swing.agent.IWorkspaceBuilder;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;
import com.synaptix.toast.swing.agent.runtime.StudioScriptRunner;
import com.synaptix.toast.swing.agent.runtime.SutRunnerAsExec;
import com.synaptix.toast.swing.agent.ui.record.SwingInspectionRecorderPanel;
import com.synaptix.toast.swing.agent.ui.record.listener.OpenScenarioListener;
import com.synaptix.toast.swing.agent.ui.record.listener.RunScriptActionListener;

public class AdvancedSettingsPanel extends JPanel {
	
	private static final Logger LOG = LogManager.getLogger(AdvancedSettingsPanel.class);

	private static final long serialVersionUID = 1L;

	private final JComboBox<String> recordTypeComboBox;
	
	private final JButton settingsButton;
	
	private final JButton openJar;

	private final JButton openScenarioButton;

	private final JButton runButton;

	private ISwingAutomationClient recorder;

	private IWorkspaceBuilder workspaceBuilder;
	
	private StudioScriptRunner runner;
	
	private final MongoRepositoryCacheWrapper mongoRepoManager;
	
	private final Config config;
	
	private SwingInspectionRecorderPanel recorderPanel;

	private HashMap<String, JTextField> textFields;

	@Inject
	public AdvancedSettingsPanel(
		Config config,
		final IWorkspaceBuilder workspaceBuilder,
		ISwingAutomationClient recorder,
		final MongoRepositoryCacheWrapper mongoRepoManager,
		final SwingInspectionRecorderPanel recorderPanel) {
		super();
		this.config = config;
		this.recorder = recorder;
		this.workspaceBuilder = workspaceBuilder;
		this.mongoRepoManager = mongoRepoManager;
		this.recorderPanel = recorderPanel;
		this.openScenarioButton = new JButton("Open Scenario");
		this.openJar = new JButton("Open Jar");
		this.runButton = new JButton("Run Test", new ImageIcon(Resource.ICON_RUN_16PX_IMG));
		this.runButton.setToolTipText("Execute current scenario..");
		this.settingsButton = new JButton("Other Settings");
		settingsButton.setIcon(new ImageIcon(Resource.ICON_CONF_16PX_2_IMG));
		settingsButton.setToolTipText("Edit runtime properties..");
		settingsButton.setMnemonic(KeyEvent.VK_F);
		this.recordTypeComboBox = new JComboBox<String>(new String[]{
				"Web", "Swing" 
		});
		add(buildMainPanel());
		initActions();
	}
	
	private JPanel buildHiddenButton() {
		final JPanel settingsPanel = new JPanel();
//		settingsPanel.add(this.recordTypeComboBox);
//		
//		settingsPanel.add(this.openJar);
//		settingsPanel.add(this.openScenarioButton);
//		settingsPanel.add(this.runButton);
//		settingsPanel.add(this.settingsButton);
		return settingsPanel;
	}

	private void initActions() {
		this.settingsButton.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(
				ActionEvent e) {
				workspaceBuilder.openConfigDialog();
			}
		});
		
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
		
		this.openScenarioButton.addActionListener(new OpenScenarioListener(getInterpretedOutputArea()));
		
		this.openJar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(AdvancedSettingsPanel.this);
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
		
		this.runButton.addActionListener(new RunScriptActionListener(recorder, getInterpretedOutputArea(), runner, mongoRepoManager));
	}

	public JTextArea getInterpretedOutputArea() {
		return recorderPanel.getInterpretedOutputArea();
	}

	private JPanel buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		mainPanel.add(buildHiddenButton());
		this.textFields = new HashMap<String, JTextField>();
		JPanel configEntry = new JPanel();
		configEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
		configEntry.setLayout(new BoxLayout(configEntry, BoxLayout.PAGE_AXIS));
		for(Object key : EnumerationUtils.toList(workspaceBuilder.getProperties().propertyNames())) {
			String strKey = (String) key;
			JLabel label = new JLabel(label(strKey));
			label.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			label.setAlignmentX(LEFT_ALIGNMENT);
			mainPanel.add(label);
			JTextField textField = new JTextField(render(workspaceBuilder.getProperties().getProperty(strKey)));
			textField.setColumns(30);
			textField.setAlignmentX(LEFT_ALIGNMENT);
			textFields.put(strKey, textField);
			mainPanel.add(textField);
		}
		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		JButton okButton = new JButton("Save settings");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(
				ActionEvent e) {
				save();
			}
		});
		buttonPanel.add(okButton);
		mainPanel.add(buttonPanel);
		return(mainPanel);
	}

	private String label(
		String strKey) {
		if(Property.TOAST_RUNTIME_TYPE.equals(strKey)) {
			return "Runtime Type (JNLP|MVN|JAR):";
		}
		if(Property.TOAST_RUNTIME_AGENT.equals(strKey)) {
			return "Agent Path:";
		}
		if(Property.TOAST_RUNTIME_CMD.equals(strKey)) {
			return "SUT Launch Command:";
		}
		return strKey + ":";
	}

	private String render(
		Object object) {
		if(object instanceof List) {
			String raw = object.toString();
			return raw.substring(1, raw.length() - 1);
		}
		return object.toString();
	}

	private void save() {
		for(Entry<String, JTextField> entry : textFields.entrySet()) {
			workspaceBuilder.getProperties().setProperty(entry.getKey(), entry.getValue().getText());
		}
		try {
			workspaceBuilder.getProperties().store(FileUtils.openOutputStream(workspaceBuilder.getToastPropertiesFile()), "Saving !");
		}
		catch(IOException e) {
			LOG.warn("Could not save properties", e);
		}
		close();
	}

	private void close() {
		this.setVisible(false);
	}
}