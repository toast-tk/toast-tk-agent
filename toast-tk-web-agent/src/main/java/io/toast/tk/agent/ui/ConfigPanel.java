package io.toast.tk.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.toast.tk.agent.config.AgentConfigProvider;

/**
 * Configuration panel
 */
public class ConfigPanel extends JDialog {

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
	private static int timeout = 500; // in milliseconds

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
	private Image toast_logo;
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
		
		initialize();
	}
	
	/**
	 * TODO: refactor and create a JComponent with a FieldFactory ! This method
	 * initializes this
	 * 
	 * @return void
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		
		this.textFields = new HashMap<String, JTextField>();
		this.secondPane = new JTabbedPane();
		
		InputStream notvalid_imageAsStream = this.getClass().getClassLoader()
				.getResourceAsStream("picto-non-valide.png");
		this.notvalid_image = ImageIO.read(notvalid_imageAsStream);

		InputStream valid_imageAsStream = this.getClass().getClassLoader().getResourceAsStream("picto-valide.png");
		this.valid_image = ImageIO.read(valid_imageAsStream);

		InputStream toast_logoAsStream = this.getClass().getClassLoader().getResourceAsStream("ToastLogo_24.png");
		this.toast_logo = ImageIO.read(toast_logoAsStream);
		
		buildFields();
		
		secondPane.setBackground(Color.white);
		secondPane.addTab("General parameters", createGeneralPanel());
		secondPane.addTab("Recording", createRecorderPanel());
		secondPane.addTab("Proxy", createProxyPanel());

		JPanel contentPanel = createBasicPanel();
		contentPanel.add(secondPane);
		contentPanel.add(createFullButtonPanel());
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

		

		InputStream AgentParamBackGroundAsStream = this.getClass().getClassLoader()
				.getResourceAsStream("AgentParamBackGround.png");
		this.backGround_image = ImageIO.read(AgentParamBackGroundAsStream);

		JPanel panIcon = createBasicPanel();
		panIcon.setLayout(new BorderLayout());
		panIcon.add(new JLabel(new ImageIcon(this.backGround_image)));
		panIcon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		
		//"Arial",Font.BOLD,14));
	    JLabel firstMainPanel = new JLabel("AGENT SETTING");
	    firstMainPanel.setFont(new Font("Verdana",Font.BOLD,22));
		JPanel secondMainPanel = createBasicPanel(BoxLayout.X_AXIS);
		secondMainPanel.add(panIcon);
		secondMainPanel.add(contentPanel);
		
		mainPane = createBasicPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		mainPane.add(firstMainPanel);
		mainPane.add(secondMainPanel);

		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setContentPane(mainPane);
		setResizable(false);
		setAlwaysOnTop(true);
		pack();
		centerWindow(); // setLocationRelativeTo(null) does not work
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
		setAlwaysOnTop(false);
		this.close();
	}

	private void close() {
		this.setVisible(false);
		this.dispose();
	}

	private void chooseFile(JTextField textField, String strKey) {	
		setAlwaysOnTop(false);

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
		setAlwaysOnTop(true);
		
	}

	private void chooseDirectory(JTextField textField, String strKey) {
		setAlwaysOnTop(false);

		JFileChooser dialogue = new JFileChooser();
		dialogue.setDialogTitle("Select directory");
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
		setAlwaysOnTop(true);
	}

	private void testIconValid(String strKey, boolean runTryValue) throws IOException {
		if(strKey.contains(webAppName)) {
			boolean test = false;
			if(proxyCheckBox.isSelected()) {
				test = testWebAppURL(textFieldWebApp.getText(),runTryValue, 
						textFieldProxyAdress.getText(), textFieldProxyPort.getText(), 
						textFieldProxyUserName.getText(), textFieldProxyUserPswd.getText());
			}
			else {
				test = testWebAppURL(textFieldWebApp.getText(),runTryValue);
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
				test = testWebAppURL(textFieldRecorder.getText(),runTryValue, 
						textFieldProxyAdress.getText(), textFieldProxyPort.getText(), 
						textFieldProxyUserName.getText(), textFieldProxyUserPswd.getText());
			}
			else {
				test = testWebAppURL(textFieldRecorder.getText(),runTryValue);
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
			if(!testWebAppDirectory(textFieldChrome.getText(),runTryValue, true))
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
			if(!testWebAppDirectory(textFieldPlugin.getText(),runTryValue, false))
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
			if(!testWebAppDirectory(textFieldScripts.getText(),runTryValue, false))
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

	public static boolean testWebAppDirectory(String directory, boolean runTryValue, boolean fileOrDirectory)
			throws IOException {
		String fileName = directory.split("/")[directory.split("/").length - 1];
		if (directory.contains(" ")) {
			if (runTryValue) {
				NotificationManager.showMessage(directory + " has spaces in its name.").showNotification();
			}
			LOG.info("Status of " + directory + " : KO");
			return false;
		}

		if (fileOrDirectory) {
			if (testFileDirectory(directory, runTryValue, fileName)) {
				LOG.info("Status of " + directory + " : OK");
				return true;
			} else {
				LOG.info("Status of " + directory + " : KO");
				return false;
			}
		} else {
			if (testDirectory(directory, runTryValue, fileName)) {
				LOG.info("Status of " + directory + " : OK");
				return true;
			} else {
				LOG.info("Status of " + directory + " : KO");
				return false;
			}
		}
	}

	public static boolean testDirectory(String directory, boolean runTryValue, String fileName) {
		File myFile = new File(directory);
		if (myFile.exists()) {
			if (myFile.isDirectory()) {
				return true;
			} else {
				if (runTryValue) {
					NotificationManager.showMessage("You did not select a directory.").showNotification();
				}
				return false;
			}
		} else {
			if (runTryValue) {
				NotificationManager.showMessage("The directory : " + directory.split(fileName)[0] + " does not exist !")
						.showNotification();
			}
			return false;
		}
	}

	public static boolean testFileDirectory(String directory, boolean runTryValue, String fileName) {
		File myFile = new File(directory);
		if (myFile.exists()) {
			if (myFile.isFile()) {
				return true;
			} else {
				if (runTryValue) {
					NotificationManager.showMessage("You did not select a file.").showNotification();
				}
				return false;
			}
		} else {
			if (runTryValue) {
				NotificationManager
						.showMessage(fileName + " does not exist in the directory : " + directory.split(fileName)[0])
						.showNotification();
			}
			return false;
		}
	}

	public static boolean testWebAppURL(String URL, boolean runTryValue) throws IOException {
		return testWebAppURL(URL, runTryValue, null, null, null, null);
	}

	public static boolean testWebAppURL(String URL, boolean runTryValue, String proxyAdress, String proxyPort,
			String proxyUserName, String proxyUserPswd) throws IOException {
		if (URL.contains(" ")) {
			if (runTryValue) {
				NotificationManager.showMessage(URL + " has spaces in its name.").showNotification();
			}
			LOG.info("Status of " + URL + " : KO");
			return false;
		}

		if (getStatus(URL)) {
			LOG.info("Status of " + URL + " : OK");
			return true;
		} else {
			if (runTryValue) {
				NotificationManager.showMessage(URL + " does not answer.").showNotification();
			}
			LOG.info("Status of " + URL + " : KO");
			return false;
		}
	}

	public static boolean getStatus(String url) throws IOException {
		boolean result = false;
		try {
			URL siteURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();

			int code = connection.getResponseCode();
			if (code == 200) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public boolean isChecked() {
		return proxyCheckBox.isSelected();
	}

	public static boolean getStatus(String url, String proxyAdress, String proxyPort, String proxyUserName,
			String proxyUserPswd) throws IOException {
		boolean result = false;
		try {
			URL siteURL = new URL(url);
			HttpURLConnection connection = null;
			if (proxyUserName != null && proxyUserPswd != null) {
				Authenticator authenticator = new Authenticator() {

					public PasswordAuthentication getPasswordAuthentication() {
						return (new PasswordAuthentication(proxyUserName, proxyUserPswd.toCharArray()));
					}
				};
				Authenticator.setDefault(authenticator);
			}

			if (proxyAdress != null && proxyUserName != null) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP,
						new InetSocketAddress(proxyAdress, Integer.parseInt(proxyPort)));
				connection = (HttpURLConnection) siteURL.openConnection(proxy);
			} else
				connection = (HttpURLConnection) siteURL.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(timeout);
			connection.connect();

			int code = connection.getResponseCode();
			if (code == 200) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	private JPanel createFullButtonPanel() {
		JPanel buttonPanel = createBasicPanel();
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
	
	private JPanel createGeneralPanel() {
		JPanel generalParameters = createBasicPanel(BoxLayout.LINE_AXIS);
	    JPanel generalParameters1 = createBasicPanel(BoxLayout.PAGE_AXIS);
	    JPanel generalParameters2 = createBasicPanel(BoxLayout.PAGE_AXIS);
		generalParameters1.add(webAppPanel);
		generalParameters1.add(apiKeyPanel);
		generalParameters2.add(pluginPanel);
		generalParameters2.add(scriptsPanel);
		generalParameters.add(generalParameters1);
		generalParameters.add(generalParameters2);
		return generalParameters;
	}
	
	private JPanel createRecorderPanel() {
		JPanel recorderParameters = createBasicPanel(BoxLayout.PAGE_AXIS);
		recorderParameters.add(chromePanel);
		recorderParameters.add(recorderPanel);
		recorderParameters.setLayout(new BoxLayout(recorderParameters, BoxLayout.Y_AXIS));
		return recorderParameters;
	}
	
	private JPanel createProxyPanel() {
		JPanel proxyPanel = createBasicPanel(BoxLayout.PAGE_AXIS);
	    JPanel proxyPanel1 = createBasicPanel(BoxLayout.LINE_AXIS);
	    JPanel proxyPanel2 = createBasicPanel(BoxLayout.LINE_AXIS);
	    JPanel proxyPanel3 = createBasicPanel(BoxLayout.LINE_AXIS);
		proxyCheckBox = new JCheckBox("Activation");
		proxyCheckBox.setBackground(Color.white);
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


	private JPanel createBasicPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		return panel;
	} 
	private JPanel createBasicPanel(int boxLayout) {
		JPanel panel = createBasicPanel();
		panel.add(Box.createHorizontalGlue());
		panel.setLayout(new BoxLayout(panel, boxLayout));
		
		return panel;
	}
	private JPanel createBasicPanel(String strKey, Font font) {
		JPanel panel = createBasicPanel();
		panel.add(Box.createHorizontalGlue());
		panel.setBorder(BorderFactory.createTitledBorder(panel.getBorder(),
	    		strKey,TitledBorder.ABOVE_TOP,TitledBorder.CENTER, font));
		
		return panel;
	} 
	private JPanel createBasicPanel(String strKey, int boxLayout) {
		return createBasicPanel(strKey, boxLayout, new Font("Arial",Font.BOLD,14));
	}
	private JPanel createBasicPanel(String strKey, int boxLayout, Font font) {
		JPanel panel = createBasicPanel(strKey, font);
		panel.setLayout(new BoxLayout(panel, boxLayout));
		
		return panel;
	}
	
	private JTextField createBasicTextPanel (String strKey) {

		JTextField textField = new JTextField(render(properties.getProperty(strKey)));
		textField.setPreferredSize(new Dimension(200, 30));
		textField.setLayout(new BorderLayout());
		textField.setColumns(30);
		textField.setAlignmentX(LEFT_ALIGNMENT);
		textFields.put(strKey, textField);
		
		
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent a) {
				if (a.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						testIconValid(strKey, false);
						secondPane.repaint();
						secondPane.revalidate();

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
		iconValid.setBackground(Color.white);
		return iconValid;
	}
	
	public JLabel createIconNotValid() {
		JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalid_image));
		iconNotValid.setBackground(Color.white);
		return iconNotValid;
	}
	
	private void buildFields() throws IOException{		
		//%% WEBAPP PANEL %%
		String strKey = webAppName;
		textFieldWebApp = createBasicTextPanel(strKey);
		iconPanelWebApp = createBasicPanel();
		iconValidWebApp = createIconValid();
		iconNotValidWebApp = createIconNotValid();
		textButtonPanelWebApp = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelWebApp.add(textFieldWebApp);
		textButtonPanelWebApp.add(iconPanelWebApp);
		errorLabelWebApp = buildErrorLabel(strKey, iconPanelWebApp, textFieldWebApp, 3);
		webAppPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		webAppPanel.add(textButtonPanelWebApp);
		webAppPanel.add(errorLabelWebApp);

		//%% API PANEL %%
		strKey = apiKeyName;
		textFieldApiKey = createBasicTextPanel(strKey);
		iconPanelApiKey = createBasicPanel();
		textButtonPanelApiKey = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelApiKey.add(textFieldApiKey);
		textButtonPanelApiKey.add(iconPanelApiKey);
		errorLabelApiKey = buildErrorLabel(strKey, iconPanelApiKey, textFieldApiKey, 4);
		apiKeyPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		apiKeyPanel.add(textButtonPanelApiKey);
		apiKeyPanel.add(errorLabelApiKey);

		//%% PLUGIN PANEL %%
		strKey = pluginName;
		textFieldPlugin = createBasicTextPanel(strKey);
		iconPanelPlugin = createBasicPanel();
		iconValidPlugin = createIconValid();
		iconNotValidPlugin = createIconNotValid();
		fileSearchPlugin = createBasicFileSearch(textFieldPlugin, strKey,false);
		textButtonPanelPlugin = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelPlugin.add(fileSearchPlugin);
		textButtonPanelPlugin.add(textFieldPlugin);
		textButtonPanelPlugin.add(iconPanelPlugin);
		errorLabelPlugin = buildErrorLabel(strKey, iconPanelPlugin, textFieldPlugin, 2);
		pluginPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		pluginPanel.add(textButtonPanelPlugin);
		pluginPanel.add(errorLabelPlugin);
			
		//%% SCRIPTS PANEL %%
		strKey = scriptsName;
		textFieldScripts = createBasicTextPanel(strKey);
		iconPanelScripts = createBasicPanel();
		iconValidScripts = createIconValid();
		iconNotValidScripts = createIconNotValid();
		fileSearchScripts = createBasicFileSearch(textFieldScripts, strKey, false);
		textButtonPanelScripts = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelScripts.add(fileSearchScripts);
		textButtonPanelScripts.add(textFieldScripts);
		textButtonPanelScripts.add(iconPanelScripts);
		errorLabelScripts = buildErrorLabel(strKey, iconPanelScripts, textFieldScripts, 2);
		scriptsPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		scriptsPanel.add(textButtonPanelScripts);
		scriptsPanel.add(errorLabelScripts);
		
		//%% CHROME PANEL %%
		strKey = chromeDriverName;
		textFieldChrome = createBasicTextPanel(strKey);
		textFieldChrome.setSize(textFieldWebApp.getSize());
		iconPanelChrome = createBasicPanel();
		iconValidChrome = createIconValid();
		iconNotValidChrome = createIconNotValid();
		fileSearchChrome = createBasicFileSearch(textFieldChrome, strKey);
		textButtonPanelChrome = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelChrome.add(fileSearchChrome);
		textButtonPanelChrome.add(textFieldChrome);
		textButtonPanelChrome.add(iconPanelChrome);
		errorLabelChrome = buildErrorLabel(strKey, iconPanelChrome, textFieldChrome, 1);;
		chromePanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		chromePanel.add(textButtonPanelChrome);
		chromePanel.add(errorLabelChrome);
		
		//%% RECORDER PANEL %%
		strKey = recorderName;
		textFieldRecorder = createBasicTextPanel(strKey);
		textFieldRecorder.setSize(textFieldWebApp.getSize());
		iconPanelRecorder = createBasicPanel();
		iconValidRecorder = createIconValid();
		iconNotValidRecorder = createIconNotValid();
		textButtonPanelRecorder = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelRecorder.add(textFieldRecorder);
		textButtonPanelRecorder.add(iconPanelRecorder);
		errorLabelRecorder = buildErrorLabel(strKey, iconPanelRecorder, textFieldRecorder, 3);
		recorderPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		recorderPanel.add(textButtonPanelRecorder);
		recorderPanel.add(errorLabelRecorder);		
		
		//%% PROXY ADRESS PANEL %%
		strKey = proxyAdress;
		textFieldProxyAdress = createBasicTextPanel(strKey);
		iconPanelProxyAdress = createBasicPanel();
		textButtonPanelProxyAdress = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyAdress.add(textFieldProxyAdress);
		textButtonPanelProxyAdress.add(iconPanelProxyAdress);
		errorLabelProxyAdress = buildErrorLabel(strKey, iconPanelProxyAdress, textFieldProxyAdress, 0);
		proxyAdressPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		proxyAdressPanel.add(textButtonPanelProxyAdress);
		proxyAdressPanel.add(errorLabelProxyAdress);
		
		//%% PROXY PORT PANEL %%
		strKey = proxyPort;
		textFieldProxyPort = createBasicTextPanel(strKey);
		iconPanelProxyPort = createBasicPanel();
		textButtonPanelProxyPort = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyPort.add(textFieldProxyPort);
		textButtonPanelProxyPort.add(iconPanelProxyPort);
		errorLabelProxyPort = buildErrorLabel(strKey, iconPanelProxyPort, textFieldProxyPort, 0);
		proxyPortPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		proxyPortPanel.add(textButtonPanelProxyPort);
		proxyPortPanel.add(errorLabelProxyPort);
		chromeDriverName = "chromedriver";

		//%% PROXY USER NAME PANEL %%
		strKey = proxyUser;
		textFieldProxyUserName = createBasicTextPanel(strKey);
		iconPanelProxyUserName = createBasicPanel();
		textButtonPanelProxyUserName = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyUserName.add(textFieldProxyUserName);
		textButtonPanelProxyUserName.add(iconPanelProxyUserName);
		errorLabelProxyUserName = buildErrorLabel(strKey, iconPanelProxyUserName, textFieldProxyUserName, 0);
		proxyUserNamePanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
		proxyUserNamePanel.add(textButtonPanelProxyUserName);
		proxyUserNamePanel.add(errorLabelProxyUserName);
		
		//%% PROXY USER PSWD PANEL %%
		strKey = proxyPswd;
		textFieldProxyUserPswd = createBasicTextPanel(strKey);
		iconPanelProxyUserPswd = createBasicPanel();
		textButtonPanelProxyUserPswd = createBasicPanel(BoxLayout.LINE_AXIS);
		textButtonPanelProxyUserPswd.add(textFieldProxyUserPswd);
		textButtonPanelProxyUserPswd.add(iconPanelProxyUserPswd);
		errorLabelProxyUserPswd = buildErrorLabel(strKey, iconPanelProxyUserPswd, textFieldProxyUserPswd, 0);
		proxyUserPswdPanel = createBasicPanel(strKey, BoxLayout.PAGE_AXIS);
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
			if( testWebAppDirectory(textField.getText(),false, true) ) {
				iconPanel.add(iconValid);
				return new JLabel(" ");
			}
			else {
				iconPanel.add(iconNotValid);
				return new JLabel(errorMessage);
			}
		}
		else if(testValue == 2) {
			errorMessage = errorMessageSelectFile;
			if( testWebAppDirectory(textField.getText(),false, false) ) {
				iconPanel.add(iconValid);
				return new JLabel(" ");
			} else {
				iconPanel.add(iconNotValid);
				return new JLabel(errorMessage);
			}
		} else if(testValue == 3) {
			errorMessage = errorMessageSelectURL;
			if (testWebAppURL(textField.getText(), false)) {
				iconPanel.add(iconValid);
				return new JLabel(" ");
			} else {
				iconPanel.add(iconNotValid);
				return new JLabel(errorMessage);
			}
		} else if(testValue == 4) { // apiKey does not have verification
				JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
				toastLogo.setBackground(Color.white);
				iconPanel.add(toastLogo);
				errorMessage = errorMessageApiKey;
				return new JLabel(errorMessage);
		} else { // Proxy tests will be tested through the webApp URL
			JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
			toastLogo.setBackground(Color.white);
			iconPanel.add(toastLogo);
			
			errorMessage = " ";
			return new JLabel(errorMessage);
		} 
	}
	
	private void centerWindow() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point middle = new Point(screenSize.width / 2, screenSize.height / 2);
		Point newLocation = new Point(middle.x - (this.getWidth() / 2), 
		                              middle.y - (this.getHeight() / 2));
		this.setLocation(newLocation);
	}
}