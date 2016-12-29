package io.toast.tk.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.toast.tk.agent.config.AgentConfigProvider;

/**
 * Box panel
 */
public class BoxPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static final Logger LOG = LogManager.getLogger(ConfigPanel.class);


	private final Properties properties;

	private static Dimension dim = null;
	
	private String strkey;
	private JTextField textField;
	private JPanel textButtonPanel, iconPanel;
	private JLabel errorLabel;
	private JButton fileSearch;

	private JLabel iconValid, iconNotValid;

	private Image toast_logo;
	private Image notvalid_image;
	private Image valid_image;

	private String errorMessageSelectFile = "The file that you have selected do not exist.";
	private String errorMessageSelectURL = "The URL does not anwser.";
	private String errorMessageApiKey = "The Api Key have to match with the WebApp";

	private JCheckBox proxyCheckBox = null;
	private BoxPanel proxyAdressPanel = null;
	private BoxPanel proxyPortPanel = null;
	private BoxPanel proxyUserPanel = null;
	private BoxPanel proxyPswdPanel = null;
	
	private String chromeDriverName = AgentConfigProvider.TOAST_CHROMEDRIVER_PATH;
	private String webAppName = AgentConfigProvider.TOAST_TEST_WEB_APP_URL;
	private String recorderName = AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL;
	private String apiKeyName = AgentConfigProvider.TOAST_API_KEY;
	private String pluginName = AgentConfigProvider.TOAST_PLUGIN_DIR;
	private String scriptsName = AgentConfigProvider.TOAST_SCRIPTS_DIR;
	private String proxyAdress = AgentConfigProvider.TOAST_PROXY_ADRESS;
	private String proxyPort = AgentConfigProvider.TOAST_PROXY_PORT;
	private String proxyUser = AgentConfigProvider.TOAST_PROXY_USER_NAME;
	private String proxyPswd = AgentConfigProvider.TOAST_PROXY_USER_PSWD;
	
	public BoxPanel(Properties properties, String strkey) throws IOException {
		this.properties = properties;
		this.strkey = strkey;	
		this.proxyAdressPanel = null;
		this.proxyPortPanel = null;
		this.proxyUserPanel = null;
		this.proxyPswdPanel = null;
		
		this.notvalid_image = PanelHelper.createImage(this,"picto-non-valide.png");
		this.valid_image = PanelHelper.createImage(this,"picto-valide.png");
		this.toast_logo = PanelHelper.createImage(this,"ToastLogo_24.png");
				
		this.buildContentPanel();
		
		if(dim == null) {
			dim = getPreferredSize();
		}
		else 
			setPreferredSize(dim);
	}
	public BoxPanel(Properties properties, String strkey, 
			BoxPanel proxyAdress, BoxPanel proxyPort, 
			BoxPanel proxyUser, BoxPanel proxyPswd, JCheckBox proxyCheckBox ) throws IOException {
		this.properties = properties;
		this.strkey = strkey;
		this.proxyAdressPanel = proxyAdress;
		this.proxyPortPanel = proxyPort;
		this.proxyUserPanel = proxyUser;
		this.proxyPswdPanel = proxyPswd;
		this.proxyCheckBox = proxyCheckBox;
	
		this.notvalid_image = PanelHelper.createImage(this,"picto-non-valide.png");
		this.valid_image = PanelHelper.createImage(this,"picto-valide.png");
		this.toast_logo = PanelHelper.createImage(this,"ToastLogo_24.png");
		
		this.buildContentPanel();
	}
	
	public String getTextValue() {
		return this.textField.getText();
	}

	private void chooseFile(JTextField textField, String strKey) {	
		JFileChooser dialogue = new JFileChooser();
		dialogue.setDialogTitle("Select file");
		dialogue.showOpenDialog(null);
		dialogue.setMaximumSize(getMaximumSize());

		if (dialogue.getSelectedFile() != null) {
			if (dialogue.getSelectedFile().isFile()) {
				LOG.info("File selected : " + dialogue.getSelectedFile().getAbsolutePath());
				textField.setText(dialogue.getSelectedFile().getAbsolutePath());
				try {
					testIconValid(strKey, false);
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}		
	}

	private void chooseDirectory(JTextField textField, String strKey) {
		JFileChooser dialogue = new JFileChooser(textField.getText());
		dialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		dialogue.setDialogTitle("Select directory for " + strKey);
		dialogue.showOpenDialog(null);

		if (dialogue.getSelectedFile() != null) {
			if (dialogue.getSelectedFile().isDirectory()) {
				LOG.info("File selected : " + dialogue.getSelectedFile().getAbsolutePath());
				textField.setText(dialogue.getSelectedFile().getAbsolutePath());
				try {
					testIconValid(strKey, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void testIconValid(String strKey, boolean runTryValue) throws IOException {
		if(strKey.contains(webAppName) || strKey.contains(recorderName)) {
			boolean test = false;
			if(proxyCheckBox.isSelected()) {
				test = ConfigTesterHelper.testWebAppURL(this.getTextValue(),runTryValue, 
						proxyAdressPanel.getTextValue(), proxyPortPanel.getTextValue(), 
						proxyUserPanel.getTextValue(), proxyPswdPanel.getTextValue());
			}
			else {
				test = ConfigTesterHelper.testWebAppURL(textField.getText(),runTryValue);
			}
			if(!test){
				iconPanel.removeAll();
				iconPanel.add(iconNotValid);
				errorLabel.setText(errorMessageSelectURL);
			} else {
				iconPanel.removeAll();
				iconPanel.add(iconValid);
				errorLabel.setText(" ");
			}
		} else if(strKey.contains(chromeDriverName) ||
				strKey.contains(pluginName) ||
				strKey.contains(scriptsName)) {
			boolean FileOrDirectory = false;
			if(strKey.contains(chromeDriverName)) FileOrDirectory = true;
				
			if(!ConfigTesterHelper.testWebAppDirectory(textField.getText(),runTryValue, FileOrDirectory))
			{
				iconPanel.removeAll();
				iconPanel.add(iconNotValid);
				errorLabel.setText(errorMessageSelectFile);
			} else {
				iconPanel.removeAll();
				iconPanel.add(iconValid);
				errorLabel.setText(" ");
			}
		} 
		this.repaint();
		this.revalidate();
	}

	private JTextField createBasicTextPanel (String strKey) {

		JTextField textField = new JTextField(render(properties.getProperty(strKey)));

		Dimension pD = new Dimension(textField.getPreferredSize().width, 30);
		Dimension mD = new Dimension(textField.getMaximumSize().width, 40);
		textField.setPreferredSize(pD);
        textField.setMaximumSize(mD);
        
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
						testIconValid(strKey, false);

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
	
	public JButton createBasicFileSearch(JTextField textField, String strKey) {
		return createBasicFileSearch(textField, strKey, true);
	}
	public JButton createBasicFileSearch(JTextField textField, String strKey, Boolean fileOrDir) {	
		JButton fileSearch = new JButton();
		fileSearch.setText("...");
		if (fileOrDir) {
			fileSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					chooseFile(textField, strKey);
				}
			});
		} 	else {
			fileSearch.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					chooseDirectory(textField, strKey);
				}
			});
		}
	
		return fileSearch;
	}
	
		
	
	public JLabel createIconValid() {
		JLabel iconValid = new JLabel(new ImageIcon(this.valid_image));
		return iconValid;
	}
	
	public JLabel createIconNotValid() {
		JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalid_image));
		return iconNotValid;
	}
	
	private void buildContentPanel() throws IOException{	
		this.add(Box.createHorizontalGlue());
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.setAlignmentY(Component.CENTER_ALIGNMENT);
		this.setBackground(Color.white);
		
		textField = createBasicTextPanel(strkey);
		iconPanel = PanelHelper.createBasicPanel();
		textButtonPanel = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		iconValid = createIconValid();
		iconNotValid = createIconNotValid();
		
		//%% WEBAPP PANEL %%
		if(strkey == webAppName) {
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 3);
			this.setBorder(BorderFactory.createTitledBorder(null, "Toast WebApp URL",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}
		
		//%% API PANEL %%
		if(strkey == apiKeyName) {
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 4);
			this.setBorder(BorderFactory.createTitledBorder(null, "User API key",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}

		//%% PLUGIN PANEL %%
		if(strkey == pluginName) {
			fileSearch = createBasicFileSearch(textField, strkey);
			textButtonPanel.add(fileSearch);
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 2);
			this.setBorder(BorderFactory.createTitledBorder(null, "Plugin directory",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}	
			
		//%% SCRIPTS PANEL %%
		if(strkey == scriptsName) {
			fileSearch = createBasicFileSearch(textField, strkey);
			textButtonPanel.add(fileSearch);
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 2);
			this.setBorder(BorderFactory.createTitledBorder(null, "Script directory",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}
		
		//%% CHROME PANEL %%
		if(strkey == chromeDriverName) {
			fileSearch = createBasicFileSearch(textField, strkey);
			textButtonPanel.add(fileSearch);
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 1);;
			this.setBorder(BorderFactory.createTitledBorder(null, "ChromeDriver path directory",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}
		
		//%% RECORDER PANEL %%
		if(strkey == recorderName) {
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 3);
			this.setBorder(BorderFactory.createTitledBorder(null, "Recorded WebApp URL",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}
		
		//%% PROXY ADRESS PANEL %%
		if(strkey == proxyAdress) {
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 0);
			this.setBorder(BorderFactory.createTitledBorder(null, "Proxy adress",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}
		
		//%% PROXY PORT PANEL %%
		if(strkey == proxyPort) {
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 0);
			this.setBorder(BorderFactory.createTitledBorder(null, "Proxy port",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
			chromeDriverName = "chromedriver";
		}

		//%% PROXY USER NAME PANEL %%
		if(strkey == proxyUser) {
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 0);
			this.setBorder(BorderFactory.createTitledBorder(null, "Proxy user name",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}
		
		//%% PROXY USER PSWD PANEL %%
		if(strkey == proxyPswd) {
			errorLabel = buildErrorLabel(strkey, iconPanel, textField, 0);
			this.setBorder(BorderFactory.createTitledBorder(null, "Proxy user password",TitledBorder.LEFT, TitledBorder.TOP,PanelHelper.FONT_TITLE_3,Color.BLACK));
		}
		
		textButtonPanel.add(textField);
		textButtonPanel.add(iconPanel);
		this.add(textButtonPanel);
		this.add(errorLabel);
	}
	

	private JLabel buildErrorLabel(String strKey, JPanel iconPanel, JTextField textField, int testValue) throws IOException{
		// testValue : 
		// 0 for nothing
		// 1 for File
		// 2 for Directory
		// 3 for URL
		// 4 for ApiKey message
		JLabel iconValid = new JLabel(new ImageIcon(this.valid_image));
		JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalid_image));
		iconValid.setBackground(Color.white);
		iconNotValid.setBackground(Color.white);
		if(testValue == 1 || testValue == 2 ||testValue == 3) {
			iconPanel.add(iconValid);		
		} else { // apiKey does not have verification & Proxy tests will be tested through the webApp URL
			JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
			toastLogo.setBackground(Color.white);
			iconPanel.add(toastLogo);
		} 
		return PanelHelper.createBasicJLabel(" ", PanelHelper.FONT_TEXT_BOLD);
	}

	public static String render(Object object) {
		if (object instanceof List) {
			String raw = object.toString();
			return raw.substring(1, raw.length() - 1);
		}
		return object.toString();
	}

}