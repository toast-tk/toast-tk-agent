package io.toast.tk.agent.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.*;

import io.toast.tk.agent.ui.utils.PanelHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class AbstractPanel extends JComponent {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(AbstractPanel.class);
	final Properties properties;
	private static Dimension dim;

	protected String strkey;
	protected JTextField textField;
	protected JPanel iconPanel;
	protected JLabel errorLabel;

	protected JLabel iconValid, iconNotValid;

	private Image toastLogo;
	private Image notvalidImage;
	private Image validImage;

	protected final String errorMessageSelectUrl = "The URL does not anwser.";
	private final String errorMessageApiKey = "The Api Key have to match with the WebApp";
	
	public AbstractPanel(Properties properties, String strkey) throws IOException {
		this.properties = properties;
		this.strkey = strkey;
	}
	
	protected void setBasicProperties(String strkey) throws IOException {
		this.strkey = strkey;
		
		this.notvalidImage = PanelHelper.createImage(this,"picto-non-valide.png");
		this.validImage = PanelHelper.createImage(this,"picto-valide.png");
		this.toastLogo = PanelHelper.createImage(this,"ToastLogo_24.png");
				
		this.buildContentPanel();
		
		if(dim == null) {
			dim = getPreferredSize();
		}
		else {
			setPreferredSize(dim);
		}		
	}
	
	public String getTextValue() {
		return this.textField.getText();
	}

	private JTextField createBasicTextPanel() {
		JTextField textField = new JTextField(render(properties.getProperty(strkey)));
		Dimension prefDim = new Dimension(textField.getPreferredSize().width, 30);
		Dimension moyDim = new Dimension(textField.getMaximumSize().width, 40);
		textField.setPreferredSize(prefDim);
        textField.setMaximumSize(moyDim);
        textField.setFont(PanelHelper.FONT_TEXT);
		textField.setLayout(new BorderLayout());
		textField.setColumns(30);
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);	
		textField.setAlignmentY(Component.CENTER_ALIGNMENT);
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent a) {
				if (a.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						testIconValid(false);
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
		});
		
		return textField;
	}

	public abstract void testIconValid(boolean retry) throws IOException;

	public JLabel createIconValid() {
		JLabel iconValid = new JLabel(new ImageIcon(this.validImage));
		return iconValid;
	}
	
	public JLabel createIconNotValid() {
		JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalidImage));
		return iconNotValid;
	}

	protected abstract JComponent buildPanel() throws IOException;

	private void buildContentPanel() throws IOException{	
		this.add(Box.createHorizontalGlue());
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.setAlignmentY(Component.CENTER_ALIGNMENT);
		this.setBackground(Color.white);
		
		JPanel textButtonPanel = PanelHelper.createBasicJPanel(BoxLayout.LINE_AXIS);

		textField = createBasicTextPanel();
		iconPanel = PanelHelper.createBasicJPanel();
		iconValid = createIconValid();
		iconNotValid = createIconNotValid();

		JComponent component = buildPanel();
		if(component != null){
			textButtonPanel.add(component);
		}

		textButtonPanel.add(textField);
		textButtonPanel.add(iconPanel);

		textButtonPanel.setSize(new Dimension(textButtonPanel.getSize().width, 50));
		textButtonPanel.setMaximumSize(new Dimension(textButtonPanel.getMaximumSize().width, 50));
		
		this.add(textButtonPanel);
		this.add(errorLabel);
	}
	
	protected JLabel buildErrorLabel(JPanel iconPanel, EnumError testValue) throws IOException{
		JLabel iconValid = new JLabel(new ImageIcon(this.validImage));
		JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalidImage));
		iconValid.setBackground(Color.white);
		iconNotValid.setBackground(Color.white);
		String errorMessage = " ";
		if(testValue == EnumError.FILE || testValue == EnumError.DIRECTORY ||testValue == EnumError.URL) {
			iconPanel.add(iconValid);		
		} else if(testValue == EnumError.APIKEY) { // apiKey does not have verification
			errorMessage = errorMessageApiKey;
			JLabel toastLogo = new JLabel(new ImageIcon(this.toastLogo));
			toastLogo.setBackground(Color.white);
			iconPanel.add(toastLogo);
		} else { // Proxy tests will be tested through the webApp URL
			JLabel toastLogo = new JLabel(new ImageIcon(this.toastLogo));
			toastLogo.setBackground(Color.white);
			iconPanel.add(toastLogo);
		} 
		return PanelHelper.createBasicJLabel(errorMessage, PanelHelper.FONT_TEXT_BOLD);
	}

	public static String render(Object object) {
		if (object instanceof List) {
			String raw = object.toString();
			return raw.substring(1, raw.length() - 1);
		}
		return object.toString();
	}

}