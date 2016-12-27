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
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.toast.tk.agent.config.AgentConfigProvider;

/**
 * Configuration panel
 */
public class ConfigPanel extends JFrame {

	private static final long serialVersionUID = 1L;

	public String chromeDriverName = AgentConfigProvider.TOAST_CHROMEDRIVER_PATH;
	public String webAppName = AgentConfigProvider.TOAST_TEST_WEB_APP_URL;
	public String recorderName = AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL;
	public String apiKeyName = AgentConfigProvider.TOAST_API_KEY;
	public String pluginName = AgentConfigProvider.TOAST_PLUGIN_DIR;
	public String scriptsName = AgentConfigProvider.TOAST_SCRIPTS_DIR;
	public String proxyAdress = AgentConfigProvider.TOAST_PROXY_ADRESS;
	public String proxyPort = AgentConfigProvider.TOAST_PROXY_PORT;
	public String proxyUser = AgentConfigProvider.TOAST_PROXY_USER_NAME;
	public String proxyPswd = AgentConfigProvider.TOAST_PROXY_USER_PSWD;

	private static final Logger LOG = LogManager.getLogger(ConfigPanel.class);

	private JPanel mainPane, chromePanel, webAppPanel, recorderPanel, apiKeyPanel, pluginPanel, scriptsPanel,
			proxyAdressPanel, proxyPortPanel, proxyUserNamePanel, proxyUserPswdPanel;

	private JTabbedPane secondPane;

	private JTextField textFieldChrome, textFieldWebApp, textFieldRecorder, textFieldApiKey, textFieldPlugin,
			textFieldProxyAdress, textFieldProxyPort, textFieldProxyUserName, textFieldProxyUserPswd, textFieldScripts;

	private JPanel textButtonPanelChrome, textButtonPanelWebApp, textButtonPanelRecorder, textButtonPanelApiKey,
			textButtonPanelPlugin, textButtonPanelScripts, textButtonPanelProxyAdress, textButtonPanelProxyPort,
			textButtonPanelProxyUserName, textButtonPanelProxyUserPswd, iconPanelChrome, iconPanelWebApp,
			iconPanelRecorder, iconPanelApiKey, iconPanelPlugin, iconPanelProxyAdress, iconPanelProxyPort,
			iconPanelProxyUserName, iconPanelProxyUserPswd, iconPanelScripts;

	private JLabel errorLabelChrome, errorLabelWebApp, errorLabelRecorder, errorLabelApiKey, errorLabelPlugin, errorLabelScripts,
			errorLabelProxyAdress, errorLabelProxyPort, errorLabelProxyUserName, errorLabelProxyUserPswd;

	private JButton fileSearchChrome, fileSearchPlugin, fileSearchScripts;

	private JLabel iconValidChrome, iconValidWebApp, iconValidRecorder, iconValidPlugin,
			iconValidScripts, iconNotValidChrome, iconNotValidWebApp, iconNotValidRecorder, iconNotValidPlugin, iconNotValidScripts;


	public static JCheckBox proxyCheckBox;

	private final Properties properties;

	private HashMap<String, JTextField> textFields;

	private final File propertyFile;

	private Image notvalid_image;
	private Image valid_image;
	private Image backGround_image;

	private String errorMessageSelectFile = "The file that you have selected do not exist.";
	private String errorMessageSelectURL = "The URL does not anwser.";
	private String errorMessageApiKey = "The Api Key have to match with the WebApp";

	/**
	 * This is the default constructor
	 * 
	 * @param propertiesConfiguration
	 * @throws IOException
	 */
	public ConfigPanel(Properties propertiesConfiguration, File propertyFile) throws IOException {
		super();
		this.properties = propertiesConfiguration;
		this.propertyFile = propertyFile;
		
		buildContentPanel();
	}
	
	/**
	 * TODO: refactor and create a JComponent with a FieldFactory ! This method
	 * initializes this
	 * 
	 * @return void
	 * @throws IOException
	 */
	private void buildContentPanel() throws IOException {
		
		this.textFields = new HashMap<String, JTextField>();
		this.secondPane = new JTabbedPane();
		secondPane.setFont(PanelHelper.FONT_TITLE_2);
		buildFields();
		
		secondPane.setBackground(Color.white);

		ImageIcon general_logo = PanelHelper.createImageIcon(this, "ToastLogo_24.png");
		secondPane.addTab("General parameters", general_logo, createGeneralPanel());

		ImageIcon record_logo = PanelHelper.createImageIcon(this,"ToastLogo_24.png");
		secondPane.addTab("Recording", record_logo, createRecorderPanel());

		ImageIcon proxy_logo = PanelHelper.createImageIcon(this, "ToastLogo_24.png");
		secondPane.addTab("Proxy", proxy_logo, createProxyPanel());

		JPanel contentPanel = PanelHelper.createBasicPanel();
		contentPanel.add(secondPane);
		contentPanel.add(createFullButtonPanel());
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

		this.backGround_image = PanelHelper.createImage(this,"AgentParamBackGround.png");

		JPanel panIcon = PanelHelper.createBasicPanel();
		panIcon.setLayout(new BorderLayout());
		panIcon.add(new JLabel(new ImageIcon(this.backGround_image)));
		panIcon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
	    JLabel firstMainPanel = new JLabel("AGENT SETTING");
	    firstMainPanel.setFont(PanelHelper.FONT_TITLE_1);
	    firstMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
	    
		JPanel secondMainPanel = PanelHelper.createBasicPanel(BoxLayout.X_AXIS);
		secondMainPanel.add(panIcon);
		secondMainPanel.add(contentPanel);
		
		mainPane = PanelHelper.createBasicPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		mainPane.add(firstMainPanel);
		mainPane.add(secondMainPanel);


		Image toast_logo = PanelHelper.createImage(this,"ToastLogo.png");
		this.setIconImage(toast_logo);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setContentPane(mainPane);
		setResizable(false);
		pack();
		PanelHelper.centerWindow(this); // setLocationRelativeTo(null) does not work
		setVisible(true);

	}

	private String render(Object object) {
		if (object instanceof List) {
			String raw = object.toString();
			return raw.substring(1, raw.length() - 1);
		}
		return object.toString();
	}

	private void saveAndClose() {
		for (Entry<String, JTextField> entry : textFields.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue().getText());
		}
		try {
			properties.store(FileUtils.openOutputStream(propertyFile), "Saved !");
		} catch (IOException e) {
			LOG.warn("Could not save properties", e);
		}
		this.close();
	}

	private void close() {
		this.setVisible(false);
		this.dispose();
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
		dialogue.setMaximumSize(getMaximumSize());

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

	private void testIconValid(String strKey, boolean runTryValue) throws IOException {
		if(strKey.contains(webAppName)) {
			boolean test = false;
			if(proxyCheckBox.isSelected()) {
				test = ConfigTesterHelper.testWebAppURL(textFieldWebApp.getText(),runTryValue, 
						textFieldProxyAdress.getText(), textFieldProxyPort.getText(), 
						textFieldProxyUserName.getText(), textFieldProxyUserPswd.getText());
			}
			else {
				test = ConfigTesterHelper.testWebAppURL(textFieldWebApp.getText(),runTryValue);
			}
			if(!test){
				iconPanelWebApp.removeAll();
				iconPanelWebApp.add(iconNotValidWebApp);
				errorLabelWebApp.setText(errorMessageSelectURL);
			} else {
				iconPanelWebApp.removeAll();
				iconPanelWebApp.add(iconValidWebApp);
				errorLabelWebApp.setText(" ");
			}
		}
		else if (strKey.contains(recorderName)) {
			boolean test = false;
			if(proxyCheckBox.isSelected()) {
				test = ConfigTesterHelper.testWebAppURL(textFieldRecorder.getText(),runTryValue, 
						textFieldProxyAdress.getText(), textFieldProxyPort.getText(), 
						textFieldProxyUserName.getText(), textFieldProxyUserPswd.getText());
			}
			else {
				test = ConfigTesterHelper.testWebAppURL(textFieldRecorder.getText(),runTryValue);
			}
			
			if(!test)
			{
				iconPanelRecorder.removeAll();
				iconPanelRecorder.add(iconNotValidRecorder);
				errorLabelRecorder.setText(errorMessageSelectURL);
			} else {
				iconPanelRecorder.removeAll();
				iconPanelRecorder.add(iconValidRecorder);
				errorLabelRecorder.setText(" ");
			}

		} else if(strKey.contains(chromeDriverName)) {
			if(!ConfigTesterHelper.testWebAppDirectory(textFieldChrome.getText(),runTryValue, true))
			{
				iconPanelChrome.removeAll();
				iconPanelChrome.add(iconNotValidChrome);
				errorLabelChrome.setText(errorMessageSelectFile);
			} else {
				iconPanelChrome.removeAll();
				iconPanelChrome.add(iconValidChrome);
				errorLabelChrome.setText(" ");
			}
		} else if(strKey.contains(pluginName)) {
			if(!ConfigTesterHelper.testWebAppDirectory(textFieldPlugin.getText(),runTryValue, false))
			{
				iconPanelPlugin.removeAll();
				iconPanelPlugin.add(iconNotValidPlugin);
				errorLabelPlugin.setText(errorMessageSelectFile);
			} else {
				iconPanelPlugin.removeAll();
				iconPanelPlugin.add(iconValidPlugin);
				errorLabelPlugin.setText(" ");
			}
		} else if(strKey.contains(scriptsName)) {
			if(!ConfigTesterHelper.testWebAppDirectory(textFieldScripts.getText(),runTryValue, false))
			{
				iconPanelScripts.removeAll();
				iconPanelScripts.add(iconNotValidScripts);
				errorLabelScripts.setText(errorMessageSelectFile);
			} else {
				iconPanelScripts.removeAll();
				iconPanelScripts.add(iconValidScripts);
				errorLabelScripts.setText(" ");
			}
		} 
	}

	private JPanel createGeneralPanel() {
		JPanel generalParameters = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
	    JPanel generalParameters1 = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
	    JPanel generalParameters2 = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
		generalParameters1.add(webAppPanel);
		generalParameters1.add(apiKeyPanel);
		generalParameters2.add(pluginPanel);
		generalParameters2.add(scriptsPanel);
		generalParameters.add(generalParameters1);
		generalParameters.add(generalParameters2);
		return generalParameters;
	}
	
	private JPanel createRecorderPanel() {
		JPanel recorderParameters = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
		recorderParameters.add(chromePanel);
		recorderParameters.add(recorderPanel);
		recorderParameters.setLayout(new BoxLayout(recorderParameters, BoxLayout.Y_AXIS));
		return recorderParameters;
	}
	
	private JPanel createProxyPanel() {
		JPanel proxyPanel = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
	    JPanel proxyPanel1 = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
	    JPanel proxyPanel2 = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
	    JPanel proxyPanel3 = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		proxyCheckBox = new JCheckBox("Activation");
		proxyCheckBox.setBackground(Color.white);
		proxyCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(proxyCheckBox.isSelected()) {
					ConfigTesterHelper.proxy = true;
				} else {
					ConfigTesterHelper.proxy = false;
				}
			}
		});
	    proxyPanel1.add(proxyCheckBox);
	    proxyPanel2.add(proxyAdressPanel);
	    proxyPanel2.add(proxyPortPanel);
	    proxyPanel3.add(proxyUserNamePanel);
	    proxyPanel3.add(proxyUserPswdPanel);
	    proxyPanel.add(proxyPanel1);
	    proxyPanel.add(proxyPanel2);
	    proxyPanel.add(proxyPanel3);
	    return proxyPanel;
	}

	private JPanel createFullButtonPanel() {
		JPanel buttonPanel = PanelHelper.createBasicPanel();
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		buttonPanel.setBackground(Color.white);
		buttonPanel.add(createBasicTestButton());
		buttonPanel.add(createBasicCancelButton());
		buttonPanel.add(createBasicOkButton());
		return buttonPanel;
	}
	
	private JButton createBasicOkButton() {
		JButton okButton = new JButton("Ok");
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAndClose();				
			}
		});
		return okButton;
	}
	
	private JButton createBasicCancelButton() {
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		return cancelButton;
	}
	
	private JButton createBasicTestButton() {
		JButton tryButton = new JButton("Test");
		tryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	
				for (Object key : EnumerationUtils.toList(properties.propertyNames())) {
					String strKey = (String) key;
					try {
						testIconValid(strKey, false);
						secondPane.repaint();
						secondPane.revalidate();
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
				NotificationManager.showMessage("The parameters have been tested !");
	
			}
	
		});
		return tryButton;
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
		textField.setAlignmentX(LEFT_ALIGNMENT);	
		textField.setAlignmentY(Component.CENTER_ALIGNMENT);	
		
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent a) {
				if (a.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						testIconValid(strKey, false);
						repaint();
						revalidate();

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
		
		textFields.put(strKey, textField);
		
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
	
	private void buildFields() throws IOException{		
		//%% WEBAPP PANEL %%
		String strKey = webAppName;
		textFieldWebApp = createBasicTextPanel(strKey);
		iconPanelWebApp = PanelHelper.createBasicPanel();
		iconValidWebApp = createIconValid();
		iconNotValidWebApp = createIconNotValid();
		textButtonPanelWebApp = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelWebApp.add(textFieldWebApp);
		textButtonPanelWebApp.add(iconPanelWebApp);
		errorLabelWebApp = buildErrorLabel(strKey, iconPanelWebApp, textFieldWebApp, 3);
		webAppPanel = PanelHelper.createBasicPanel("Toast WebApp URL", BoxLayout.PAGE_AXIS);
		webAppPanel.add(textButtonPanelWebApp);
		webAppPanel.add(errorLabelWebApp);

		//%% API PANEL %%
		strKey = apiKeyName;
		textFieldApiKey = createBasicTextPanel(strKey);
		iconPanelApiKey = PanelHelper.createBasicPanel();
		textButtonPanelApiKey = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelApiKey.add(textFieldApiKey);
		textButtonPanelApiKey.add(iconPanelApiKey);
		errorLabelApiKey = buildErrorLabel(strKey, iconPanelApiKey, textFieldApiKey, 4);
		apiKeyPanel = PanelHelper.createBasicPanel("User API key", BoxLayout.PAGE_AXIS);
		apiKeyPanel.add(textButtonPanelApiKey);
		apiKeyPanel.add(errorLabelApiKey);

		//%% PLUGIN PANEL %%
		strKey = pluginName;
		textFieldPlugin = createBasicTextPanel(strKey);
		iconPanelPlugin = PanelHelper.createBasicPanel();
		iconValidPlugin = createIconValid();
		iconNotValidPlugin = createIconNotValid();
		fileSearchPlugin = createBasicFileSearch(textFieldPlugin, strKey,false);
		textButtonPanelPlugin = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelPlugin.add(fileSearchPlugin);
		textButtonPanelPlugin.add(textFieldPlugin);
		textButtonPanelPlugin.add(iconPanelPlugin);
		errorLabelPlugin = buildErrorLabel(strKey, iconPanelPlugin, textFieldPlugin, 2);
		pluginPanel = PanelHelper.createBasicPanel("Plugin directory", BoxLayout.PAGE_AXIS);
		pluginPanel.add(textButtonPanelPlugin);
		pluginPanel.add(errorLabelPlugin);
			
		//%% SCRIPTS PANEL %%
		strKey = scriptsName;
		textFieldScripts = createBasicTextPanel(strKey);
		iconPanelScripts = PanelHelper.createBasicPanel();
		iconValidScripts = createIconValid();
		iconNotValidScripts = createIconNotValid();
		fileSearchScripts = createBasicFileSearch(textFieldScripts, strKey, false);
		textButtonPanelScripts = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelScripts.add(fileSearchScripts);
		textButtonPanelScripts.add(textFieldScripts);
		textButtonPanelScripts.add(iconPanelScripts);
		errorLabelScripts = buildErrorLabel(strKey, iconPanelScripts, textFieldScripts, 2);
		scriptsPanel = PanelHelper.createBasicPanel("Script directory", BoxLayout.PAGE_AXIS);
		scriptsPanel.add(textButtonPanelScripts);
		scriptsPanel.add(errorLabelScripts);
		
		//%% CHROME PANEL %%
		strKey = chromeDriverName;
		textFieldChrome = createBasicTextPanel(strKey);
		textFieldChrome.setSize(textFieldWebApp.getSize());
		iconPanelChrome = PanelHelper.createBasicPanel();
		iconValidChrome = createIconValid();
		iconNotValidChrome = createIconNotValid();
		fileSearchChrome = createBasicFileSearch(textFieldChrome, strKey);
		textButtonPanelChrome = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelChrome.add(fileSearchChrome);
		textButtonPanelChrome.add(textFieldChrome);
		textButtonPanelChrome.add(iconPanelChrome);
		errorLabelChrome = buildErrorLabel(strKey, iconPanelChrome, textFieldChrome, 1);;
		chromePanel = PanelHelper.createBasicPanel("ChromeDriver path directory", BoxLayout.PAGE_AXIS);
		chromePanel.add(textButtonPanelChrome);
		chromePanel.add(errorLabelChrome);
		
		//%% RECORDER PANEL %%
		strKey = recorderName;
		textFieldRecorder = createBasicTextPanel(strKey);
		textFieldRecorder.setSize(textFieldWebApp.getSize());
		iconPanelRecorder = PanelHelper.createBasicPanel();
		iconValidRecorder = createIconValid();
		iconNotValidRecorder = createIconNotValid();
		textButtonPanelRecorder = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelRecorder.add(textFieldRecorder);
		textButtonPanelRecorder.add(iconPanelRecorder);
		errorLabelRecorder = buildErrorLabel(strKey, iconPanelRecorder, textFieldRecorder, 3);
		recorderPanel = PanelHelper.createBasicPanel("Recorded WebApp URL", BoxLayout.PAGE_AXIS);
		recorderPanel.add(textButtonPanelRecorder);
		recorderPanel.add(errorLabelRecorder);		
		
		//%% PROXY ADRESS PANEL %%
		strKey = proxyAdress;
		textFieldProxyAdress = createBasicTextPanel(strKey);
		iconPanelProxyAdress = PanelHelper.createBasicPanel();
		textButtonPanelProxyAdress = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyAdress.add(textFieldProxyAdress);
		textButtonPanelProxyAdress.add(iconPanelProxyAdress);
		errorLabelProxyAdress = buildErrorLabel(strKey, iconPanelProxyAdress, textFieldProxyAdress, 0);
		proxyAdressPanel = PanelHelper.createBasicPanel("Proxy adress", BoxLayout.PAGE_AXIS);
		proxyAdressPanel.add(textButtonPanelProxyAdress);
		proxyAdressPanel.add(errorLabelProxyAdress);
		
		//%% PROXY PORT PANEL %%
		strKey = proxyPort;
		textFieldProxyPort = createBasicTextPanel(strKey);
		iconPanelProxyPort = PanelHelper.createBasicPanel();
		textButtonPanelProxyPort = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyPort.add(textFieldProxyPort);
		textButtonPanelProxyPort.add(iconPanelProxyPort);
		errorLabelProxyPort = buildErrorLabel(strKey, iconPanelProxyPort, textFieldProxyPort, 0);
		proxyPortPanel = PanelHelper.createBasicPanel("Proxy port", BoxLayout.PAGE_AXIS);
		proxyPortPanel.add(textButtonPanelProxyPort);
		proxyPortPanel.add(errorLabelProxyPort);
		chromeDriverName = "chromedriver";

		//%% PROXY USER NAME PANEL %%
		strKey = proxyUser;
		textFieldProxyUserName = createBasicTextPanel(strKey);
		iconPanelProxyUserName = PanelHelper.createBasicPanel();
		textButtonPanelProxyUserName = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyUserName.add(textFieldProxyUserName);
		textButtonPanelProxyUserName.add(iconPanelProxyUserName);
		errorLabelProxyUserName = buildErrorLabel(strKey, iconPanelProxyUserName, textFieldProxyUserName, 0);
		proxyUserNamePanel = PanelHelper.createBasicPanel("Proxy user name", BoxLayout.PAGE_AXIS);
		proxyUserNamePanel.add(textButtonPanelProxyUserName);
		proxyUserNamePanel.add(errorLabelProxyUserName);
		
		//%% PROXY USER PSWD PANEL %%
		strKey = proxyPswd;
		textFieldProxyUserPswd = createBasicTextPanel(strKey);
		iconPanelProxyUserPswd = PanelHelper.createBasicPanel();
		textButtonPanelProxyUserPswd = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyUserPswd.add(textFieldProxyUserPswd);
		textButtonPanelProxyUserPswd.add(iconPanelProxyUserPswd);
		errorLabelProxyUserPswd = buildErrorLabel(strKey, iconPanelProxyUserPswd, textFieldProxyUserPswd, 0);
		proxyUserPswdPanel = PanelHelper.createBasicPanel("Proxy user password", BoxLayout.PAGE_AXIS);
		proxyUserPswdPanel.add(textButtonPanelProxyUserPswd);
		proxyUserPswdPanel.add(errorLabelProxyUserPswd);
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
		String errorMessage = null;
		if(testValue == 1) {
			errorMessage = errorMessageSelectFile;				
			if(ConfigTesterHelper.testWebAppDirectory(textField.getText(),false, true) ) {
				iconPanel.add(iconValid);
				return PanelHelper.createBasicJLabel(" ", PanelHelper.FONT_TEXT_BOLD);
			}
			else {
				iconPanel.add(iconNotValid);
				return PanelHelper.createBasicJLabel(errorMessage, PanelHelper.FONT_TEXT_BOLD);
			}
		}
		else if(testValue == 2) {
			errorMessage = errorMessageSelectFile;
			if( ConfigTesterHelper.testWebAppDirectory(textField.getText(),false, false) ) {
				iconPanel.add(iconValid);
				return PanelHelper.createBasicJLabel(" ", PanelHelper.FONT_TEXT_BOLD);
			} else {
				iconPanel.add(iconNotValid);
				return PanelHelper.createBasicJLabel(errorMessage, PanelHelper.FONT_TEXT_BOLD);
			}
		} else if(testValue == 3) {
			errorMessage = errorMessageSelectURL;
			if (ConfigTesterHelper.testWebAppURL(textField.getText(), false)) {
				iconPanel.add(iconValid);
				return PanelHelper.createBasicJLabel(" ", PanelHelper.FONT_TEXT_BOLD);
			} else {
				iconPanel.add(iconNotValid);
				return PanelHelper.createBasicJLabel(errorMessage, PanelHelper.FONT_TEXT_BOLD);
			}
		} else if(testValue == 4) { // apiKey does not have verification
				JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
				toastLogo.setBackground(Color.white);
				iconPanel.add(toastLogo);
				errorMessage = errorMessageApiKey;
				return PanelHelper.createBasicJLabel(errorMessage, PanelHelper.FONT_TEXT_BOLD);
		} else { // Proxy tests will be tested through the webApp URL
			JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
			toastLogo.setBackground(Color.white);
			iconPanel.add(toastLogo);
			
			errorMessage = " ";
			return PanelHelper.createBasicJLabel(errorMessage, PanelHelper.FONT_TEXT_BOLD);
		} 
	}
}