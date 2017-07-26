package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.ui.i18n.UIMessages;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;
import io.toast.tk.agent.ui.utils.PanelHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.util.Properties;


public class SmtpPanel extends AbstractPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3708487609506872946L;
	private final String label;
    private final EnumError errorType;
    
	private static final Logger LOG = LogManager.getLogger(RecorderPanel.class);

    public SmtpPanel(Properties properties, String strkey, String label, EnumError errorType) throws IOException {
        super(properties,strkey);
        this.label = label;
        this.errorType = errorType;
        super.setBasicProperties(strkey);
    }
    
    private boolean testIconValidMail(boolean runTryValue) {
        return ConfigTesterHelper.testUserMail(textField.getText(),runTryValue);
    }

    public void testIconValid(boolean runTryValue) throws IOException {
    	boolean isNotKo = testIconValidMail(runTryValue);
        String errorMess = UIMessages.ERROR_MAIL_NOT_CONFORM;
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

    protected JComponent buildPanel() throws IOException {
        this.errorLabel = buildErrorLabel(iconPanel, errorType);
        this.setBorder(BorderFactory.createTitledBorder(null, label, TitledBorder.LEFT, TitledBorder.TOP, PanelHelper.FONT_TITLE_3, Color.BLACK));
        return null;
    }
}
