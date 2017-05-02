package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.ui.i18n.UIMessages;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;
import io.toast.tk.agent.ui.utils.PanelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;


public class WebAppPanel extends AbstractPanel {

    private static final Logger LOG = LogManager.getLogger(WebAppPanel.class);

    private JCheckBox proxyCheckBox = null;
    private AbstractPanel proxyAdressPanel = null;
    private AbstractPanel proxyPortPanel = null;
    private AbstractPanel proxyUserPanel = null;
    private AbstractPanel proxyPswdPanel = null;

    public WebAppPanel(Properties properties, String strkey, AbstractPanel proxyAdress, AbstractPanel proxyPort,
                       AbstractPanel proxyUser, AbstractPanel proxyPswd, JCheckBox proxyCheckBox) throws IOException {
        super(properties,strkey);
        this.proxyAdressPanel = proxyAdress;
        this.proxyPortPanel = proxyPort;
        this.proxyUserPanel = proxyUser;
        this.proxyPswdPanel = proxyPswd;
        this.proxyCheckBox = proxyCheckBox;
        super.setBasicProperties(strkey);
    }

    public void testIconValid(boolean runTryValue) throws IOException {
        boolean isNotKo = testIconValidUrl(runTryValue);
        String errorMess = errorMessageSelectUrl;
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

    private boolean testIconValidUrl(boolean runTryValue) throws IOException {
        if(proxyCheckBox.isSelected()) {
            return ConfigTesterHelper.testWebAppUrl(this.getTextValue(),runTryValue,
                    proxyAdressPanel.getTextValue(), proxyPortPanel.getTextValue(),
                    proxyUserPanel.getTextValue(), proxyPswdPanel.getTextValue());
        }
        else {
            return ConfigTesterHelper.testWebAppUrl(textField.getText(),runTryValue);
        }
    }

    protected JComponent buildPanel() throws IOException {
        errorLabel = buildErrorLabel(iconPanel, EnumError.URL);
        this.setBorder(BorderFactory.createTitledBorder(null,
                UIMessages.URL_TOAST,
                TitledBorder.LEFT, TitledBorder.TOP,
                PanelHelper.FONT_TITLE_3, Color.BLACK));
        return null;
    }
}
