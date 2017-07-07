package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.config.DriverFactory;
import io.toast.tk.agent.ui.i18n.UIMessages;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;
import io.toast.tk.agent.ui.utils.PanelHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Color;
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
        super.setBasicProperties(DriverFactory.getDriver());
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
}
