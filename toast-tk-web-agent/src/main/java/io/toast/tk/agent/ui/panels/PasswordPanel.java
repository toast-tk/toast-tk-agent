package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.ui.utils.PanelHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.IOException;
import java.util.Properties;


public class PasswordPanel extends AbstractPanel {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9219045926752739314L;
	private final String label;
    private final EnumError errorType;

    public PasswordPanel(Properties properties, String strkey, String label, EnumError errorType) throws IOException {
        super(properties,strkey);
        this.label = label;
        this.errorType = errorType;
        super.setBasicProperties(strkey);
    }

    public void testIconValid(boolean runTryValue) throws IOException {
        //NO-OP
    }
    
    protected JPasswordField  createBasicTextPanel() {
    	String passwordCrypted = render(properties.getProperty(strkey));
    	JPasswordField  textField = new JPasswordField(passwordCrypted);
		Dimension prefDim = new Dimension(textField.getPreferredSize().width, 30);
		Dimension moyDim = new Dimension(textField.getMaximumSize().width, 40);
		textField.setPreferredSize(prefDim);
        textField.setMaximumSize(moyDim);
        textField.setFont(PanelHelper.FONT_TEXT);
		textField.setLayout(new BorderLayout());
		textField.setColumns(30);
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);	
		textField.setAlignmentY(Component.CENTER_ALIGNMENT);
		textField.setEchoChar('*');
		
		return textField;
	}

	public String getTextValue() {
		return String.valueOf(((JPasswordField) this.textField).getPassword());
	}
	
    protected JComponent buildPanel() throws IOException {
        this.errorLabel = buildErrorLabel(iconPanel, errorType);
        this.setBorder(BorderFactory.createTitledBorder(null, label, TitledBorder.LEFT, TitledBorder.TOP, PanelHelper.FONT_TITLE_3, Color.BLACK));
        return null;
    }

}
