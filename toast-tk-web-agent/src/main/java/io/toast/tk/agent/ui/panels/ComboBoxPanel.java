package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.config.DriverFactory;
import io.toast.tk.agent.config.DriverFactory.DRIVER;
import io.toast.tk.agent.ui.i18n.UIMessages;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;
import io.toast.tk.agent.ui.utils.PanelHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;


public class ComboBoxPanel extends AbstractPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 928467488922109794L;
	private static final Logger LOG = LogManager.getLogger(ComboBoxPanel.class);
	private final String label;
    private final EnumError errorType;
    private JComboBox<DriverFactory.DRIVER> comboBox;

    public ComboBoxPanel(Properties properties, String strkey, String label, EnumError errorType) throws IOException {
        super(properties, strkey);
        this.label = label;
        this.errorType = errorType;
        super.setBasicProperties(DriverFactory.getDriverValue());
    }

	public String getTextValue() {
		return comboBox.getSelectedItem().toString();
	}

	public String getValue() {
		return this.textField.getText();
	}
	
    public void testIconValid(boolean runTry) throws IOException {
        boolean isNotKo = testIconValidDirectory(runTry);
        String errorMess = UIMessages.ERROR_FILE_NOT_EXIST;
        JLabel icon = iconNotValid;
        if(isNotKo) {
            LOG.info("Status of " + textField.getText() + " : OK");
            icon = iconValid;
            errorMess = " ";
        } else {
            LOG.info("Status of " + textField.getText() + " : KO");
        }
        iconPanel.removeAll();
        iconPanel.add(icon);
        errorLabel.setText(errorMess);
        this.repaint();
        this.revalidate();
    }

    public boolean testIconValidDirectory(boolean runTryValue) throws IOException {
        return ConfigTesterHelper.testWebAppDirectory(textField.getText(),runTryValue, true);
    }

    protected JComponent buildPanel() throws IOException {
    	this.comboBox = buildComboBoxPanel();
        this.errorLabel = buildErrorLabel(iconPanel, errorType);
        this.setBorder(BorderFactory.createTitledBorder(null, label, TitledBorder.LEFT, TitledBorder.TOP, PanelHelper.FONT_TITLE_3, Color.BLACK));
        return comboBox;
    }

	
	protected JComboBox<DriverFactory.DRIVER> buildComboBoxPanel() {
		JComboBox<DriverFactory.DRIVER> comboBox = new JComboBox<DriverFactory.DRIVER>();
		comboBox.addItem(DRIVER.CHROME_32);
		comboBox.addItem(DRIVER.CHROME_64);
		comboBox.addItem(DRIVER.FIREFOX_32);
		comboBox.addItem(DRIVER.FIREFOX_64);
		comboBox.addItem(DRIVER.IE_32);
		comboBox.addItem(DRIVER.IE_64);
		
		comboBox.setSelectedItem(DriverFactory.getSelected());
		
		comboBox.addActionListener(new ActionListener () {
		    public void actionPerformed(ActionEvent e) {
		    	Object selectedObject = comboBox.getSelectedItem();
		    	DriverFactory.setSelected((DRIVER) selectedObject);
		    	textField.setText(properties.getProperty(DriverFactory.getDriverValue(selectedObject.toString())));
		    }
		});
		
		comboBox.setSize(new Dimension(comboBox.getSize().width, 40));
		comboBox.setMaximumSize(new Dimension(comboBox.getMaximumSize().width, 40));
		comboBox.setBackground(Color.gray);
		PanelHelper.setBasicLayout(comboBox, BoxLayout.LINE_AXIS);
		
		comboBox.setAlignmentX(RIGHT_ALIGNMENT);
		JTextField editor = 
			    (JTextField) comboBox.getEditor().getEditorComponent(); 
		editor.setHorizontalAlignment(JTextField.LEFT);
		
		return comboBox;
	}
}
