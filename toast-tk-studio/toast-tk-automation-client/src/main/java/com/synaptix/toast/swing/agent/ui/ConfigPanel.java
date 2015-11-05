package com.synaptix.toast.swing.agent.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.synaptix.toast.constant.Property;


/**
 * Stub for displaying Configuration item.
 * 
 * @author andre
 * 
 */
public class ConfigPanel extends JDialog {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(ConfigPanel.class);

	private JPanel mainPane;

	private final Properties properties;

	private HashMap<String, JTextField> textFields;

	private final File propertyFile;

	/**
	 * This is the default constructor
	 * 
	 * @param propertiesConfiguration
	 */
	public ConfigPanel(
		Properties propertiesConfiguration,
		File propertyFile) {
		super();
		this.properties = propertiesConfiguration;
		this.propertyFile = propertyFile;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.PAGE_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		this.textFields = new HashMap<String, JTextField>();
		JPanel configEntry = new JPanel();
		configEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
		configEntry.setLayout(new BoxLayout(configEntry, BoxLayout.PAGE_AXIS));
		for(Object key : EnumerationUtils.toList(properties.propertyNames())) {
			String strKey = (String) key;
			JLabel label = new JLabel(label(strKey));
			label.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
			label.setAlignmentX(LEFT_ALIGNMENT);
			mainPane.add(label);
			JTextField textField = new JTextField(render(properties.getProperty(strKey)));
			textField.setColumns(30);
			textField.setAlignmentX(LEFT_ALIGNMENT);
			textFields.put(strKey, textField);
			mainPane.add(textField);
		}
		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			public void actionPerformed(
				ActionEvent e) {
				saveAndClose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(
				ActionEvent e) {
				close();
			}
		});
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);
		mainPane.add(buttonPanel);
		setSize(500, 500);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setContentPane(mainPane);
		setResizable(false);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
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

	private void saveAndClose() {
		for(Entry<String, JTextField> entry : textFields.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue().getText());
		}
		try {
			properties.store(FileUtils.openOutputStream(propertyFile), "Saving !");
		}
		catch(IOException e) {
			LOG.warn("Could not save properties", e);
		}
		close();
	}

	private void close() {
		this.setVisible(false);
		this.dispose();
	}
}