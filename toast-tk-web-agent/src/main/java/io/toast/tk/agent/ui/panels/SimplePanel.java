package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.ui.utils.PanelHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;


public class SimplePanel extends AbstractPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9219045926752739314L;
	private final String label;
    private final EnumError errorType;

    public SimplePanel(Properties properties, String strkey, String label, EnumError errorType) throws IOException {
        super(properties,strkey);
        this.label = label;
        this.errorType = errorType;
        super.setBasicProperties(strkey);
    }

    public void testIconValid(boolean runTryValue) throws IOException {
        //NO-OP
    }

    protected JComponent buildPanel() throws IOException {
        this.errorLabel = buildErrorLabel(iconPanel, errorType);
        this.setBorder(BorderFactory.createTitledBorder(null, label, TitledBorder.LEFT, TitledBorder.TOP, PanelHelper.FONT_TITLE_3, Color.BLACK));
        return null;
    }
}
