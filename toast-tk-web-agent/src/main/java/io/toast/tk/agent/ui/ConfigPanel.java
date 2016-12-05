package io.toast.tk.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
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
import javax.swing.JFrame;
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

	private String chromeDriverName = "chromedriver";
	private String webAppName = "webapp";
	private String recorderName = "recording";
	private String apiKeyName = "api";
	private String pluginName = "plugin";
	private String scriptsName = "scripts";
	private String proxyAdress = "adress";
	private String proxyPort = "port";
	private String proxyUser = "username";
	private String proxyPswd = "userpswd";
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

	private JLabel errorLabelChrome, errorLabelWebApp, errorLabelRecorder, errorLabelApiKey, errorLabelPlugin,
			errorLabelProxyAdress, errorLabelProxyPort, errorLabelProxyUserName, errorLabelProxyUserPswd, errorScript;

	private JButton fileSearchChrome, fileSearchPlugin, fileSearchScripts;

	private JLabel iconValidChrome, iconValidWebApp, iconValidRecorder, iconValidPlugin, iconValidProxyAdress,
			iconValidScripts, iconNotValidChrome, iconNotValidWebApp, iconNotValidRecorder, iconNotValidPlugin,
			iconNotValidProxyAdress, iconNotValidScripts;


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
	
	private JLabel buildErrorLabel(String strKey, JPanel iconPanel, JTextField textField) throws IOException{
		JLabel iconValid = new JLabel(new ImageIcon(this.valid_image));
		JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalid_image));
		iconValid.setBackground(Color.white);
		iconNotValid.setBackground(Color.white);
		String errorMessage = null;
		if (strKey.contains(chromeDriverName) || strKey.contains(pluginName)) {
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
		else if(strKey.contains(pluginName)){
			errorMessage = errorMessageSelectFile;
			if( testWebAppDirectory(textField.getText(),false, false) ) {
				iconPanel.add(iconValid);
				return new JLabel(" ");
			} else {
				iconPanel.add(iconNotValid);
				return new JLabel(errorMessage);
			}
		} else if (strKey.contains(apiKeyName)) { // apiKey does not have verification
				JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
				toastLogo.setBackground(Color.white);
				iconPanel.add(toastLogo);
				errorMessage = errorMessageApiKey;
				return new JLabel(errorMessage);
		} 
		else if (strKey.contains(proxyUser) 
					|| strKey.contains(proxyPswd) 
					|| strKey.contains(proxyPort)) { 
			// Proxy tests will only be for the proxy adress
			JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
			toastLogo.setBackground(Color.white);
			iconPanel.add(toastLogo);

			errorMessage = "";
			return new JLabel(errorMessage);
		}
		else if(strKey.contains(proxyAdress) ||
					strKey.contains(proxyUser) ||
					strKey.contains(proxyPswd) ||
					strKey.contains(proxyPort)){ // Proxy tests will be tested through the webApp URL
			JLabel toastLogo = new JLabel(new ImageIcon(this.toast_logo));
			toastLogo.setBackground(Color.white);
			iconPanel.add(toastLogo);
			
			errorMessage = " ";
			return new JLabel(errorMessage);
		}
		else {
			errorMessage = errorMessageSelectURL;
			if (testWebAppURL(textField.getText(), false)) {
				iconPanel.add(iconValid);
				return new JLabel(" ");
			} else {
				iconPanel.add(iconNotValid);
				return new JLabel(errorMessage);
			}
		}
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
		
		JButton okButton = new JButton("Ok");
	
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAndClose();				
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

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
		
		Font fontTitle = new Font("Arial",Font.BOLD,16);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(LEFT_ALIGNMENT);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		buttonPanel.setBackground(Color.white);
		buttonPanel.add(tryButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(okButton);

		JPanel generalParameters = new JPanel();
		generalParameters.setBackground(Color.white);
		generalParameters.setBorder(BorderFactory.createTitledBorder("General Parameters"));
		generalParameters.add(webAppPanel);
		generalParameters.add(apiKeyPanel);
		generalParameters.add(pluginPanel);
		generalParameters.add(scriptsPanel);
		generalParameters.setLayout(new BoxLayout(generalParameters, BoxLayout.PAGE_AXIS));

		JPanel recorderParameters = new JPanel(new GridLayout());
		recorderParameters.setBackground(Color.white);
		recorderParameters.setBorder(BorderFactory.createTitledBorder("Recorder Parameters"));
		recorderParameters.add(chromePanel);
		recorderParameters.add(recorderPanel);
		recorderParameters.setLayout(new BoxLayout(recorderParameters, BoxLayout.Y_AXIS));

		JPanel proxyPanel = createPanel();
	    JPanel proxyPanel1 = createPanel(BoxLayout.LINE_AXIS);
	    JPanel proxyPanel2 = createPanel(BoxLayout.LINE_AXIS);
	    JPanel proxyPanel3 = createPanel(BoxLayout.LINE_AXIS);
		proxyCheckBox = new JCheckBox("Activation");
		proxyCheckBox.setBackground(Color.white);
	    proxyPanel.setBackground(Color.white);
	    proxyPanel.setBorder(BorderFactory.createTitledBorder(proxyPanel.getBorder(),
	    		"Proxy Parameters",TitledBorder.ABOVE_TOP,TitledBorder.CENTER, fontTitle));
	    proxyPanel1.add(proxyCheckBox);
	    proxyPanel2.add(proxyAdressPanel);
	    proxyPanel2.add(proxyPortPanel);
	    proxyPanel3.add(proxyUserNamePanel);
	    proxyPanel3.add(proxyUserPswdPanel);
	    proxyPanel.add(proxyPanel1);
	    proxyPanel.add(proxyPanel2);
	    proxyPanel.add(proxyPanel3);


		secondPane.setBackground(Color.white);
		secondPane.addTab("General", generalParameters);
		secondPane.addTab("Recording", recorderParameters);
		secondPane.addTab("Proxy", proxyPanel);

		JPanel contentPanel = new JPanel();
		contentPanel.add(secondPane);
		contentPanel.add(buttonPanel);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

		mainPane = new JPanel();
		mainPane.setBackground(Color.white);
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JPanel configEntry = new JPanel();
		configEntry.setAlignmentX(Component.LEFT_ALIGNMENT);
		configEntry.setLayout(new BoxLayout(configEntry, BoxLayout.PAGE_AXIS));

		InputStream AgentParamBackGroundAsStream = this.getClass().getClassLoader()
				.getResourceAsStream("AgentParamBackGround.jpg");
		this.backGround_image = ImageIO.read(AgentParamBackGroundAsStream);

		JPanel panIcon = new JPanel();
		panIcon.setBackground(Color.white);
		panIcon.setLayout(new BorderLayout());
		panIcon.add(new JLabel(new ImageIcon(this.backGround_image)));
		mainPane.add(panIcon);


		mainPane.add(contentPanel);

		setTitle("AGENT SETTINGS");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(500, 500);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setContentPane(mainPane);
		setResizable(false);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		pack();
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

	private void chooseFile(String strKey) {
		try {
			setAlwaysOnTop(false);

			JFileChooser dialogue = new JFileChooser();
			dialogue.setDialogTitle("Select file");
			dialogue.showOpenDialog(null);
			dialogue.setMaximumSize(getMaximumSize());

			if (dialogue.getSelectedFile() != null) {

				if (dialogue.getSelectedFile().isFile()) {
					LOG.info("File selected : " + dialogue.getSelectedFile().getAbsolutePath());
					textFieldChrome.setText(dialogue.getSelectedFile().getAbsolutePath());
					testIconValid(strKey, false);
				}
			}
			setAlwaysOnTop(true);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void chooseDirectory(String strKey) {
		setAlwaysOnTop(false);

		JFileChooser dialogue = new JFileChooser();
		dialogue.setDialogTitle("Select directory");
		dialogue.showOpenDialog(null);
		dialogue.setMaximumSize(getMaximumSize());

		if (dialogue.getSelectedFile() != null) {
			if (dialogue.getSelectedFile().isDirectory()) {
				NotificationManager.showMessage("Wrong path selected, this is not a directory.").showNotification();
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

	public static JPanel createPanel() {
		return createPanel(BoxLayout.PAGE_AXIS);
	}

	public static JPanel createPanel(int boxLayout) {
		JPanel panel = new JPanel();
		panel.setAlignmentX(LEFT_ALIGNMENT);
		panel.setLayout(new BoxLayout(panel, boxLayout));
		panel.setBackground(Color.white);

		return panel;
	}
	
	private void buildFields() throws IOException{
		for (Object key : EnumerationUtils.toList(properties.propertyNames())) {
			String strKey = (String) key;
			JPanel panel = createPanel();
			panel.add(Box.createHorizontalGlue());
			panel.setBackground(Color.white);
			panel.setBorder(BorderFactory.createTitledBorder(strKey));

			JTextField textField = new JTextField(render(properties.getProperty(strKey)));
			textField.setColumns(30);
			textField.setAlignmentX(LEFT_ALIGNMENT);
			textFields.put(strKey, textField);
	
			JPanel textButtonPanel = createPanel(BoxLayout.LINE_AXIS);
			textButtonPanel.add(Box.createHorizontalGlue());

			JPanel iconPanel = new JPanel();
			iconPanel.setBackground(Color.white);
		

			JLabel iconValid = new JLabel(new ImageIcon(this.valid_image));
			JLabel iconNotValid = new JLabel(new ImageIcon(this.notvalid_image));
			iconValid.setBackground(Color.white);
			iconNotValid.setBackground(Color.white);
			
			JLabel errorLabel = buildErrorLabel(strKey, iconPanel, textField);

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

			JButton fileSearch = new JButton();
			fileSearch.setText("...");
			if (strKey.contains(chromeDriverName)) {
				fileSearch.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						chooseFile(strKey);
					}
				});
			}
			else if (strKey.contains(scriptsName) || strKey.contains(pluginName)) {
				fileSearch.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						chooseDirectory(strKey);
					}
				});
			}

			if (strKey.equals(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH)) {
				textFieldChrome = textField;
				iconPanelChrome = iconPanel;
				iconValidChrome = iconValid;
				iconNotValidChrome = iconNotValid;
				fileSearchChrome = fileSearch;
				textButtonPanelChrome = textButtonPanel;
				textButtonPanelChrome.add(fileSearchChrome);
				textButtonPanelChrome.add(textFieldChrome);
				textButtonPanelChrome.add(iconPanelChrome);
				errorLabelChrome = errorLabel;
				chromePanel = panel;
				chromePanel.add(textButtonPanelChrome);
				chromePanel.add(errorLabelChrome);
			} 
			else if (strKey.contains(webAppName)) {
				textFieldWebApp = textField;
				iconPanelWebApp = iconPanel;
				iconValidWebApp = iconValid;
				iconNotValidWebApp = iconNotValid;
				textButtonPanelWebApp = textButtonPanel;
				textButtonPanelWebApp.add(textFieldWebApp);
				textButtonPanelWebApp.add(iconPanelWebApp);
				errorLabelWebApp = errorLabel;
				webAppPanel = panel;
				webAppPanel.add(textButtonPanelWebApp);
				webAppPanel.add(errorLabelWebApp);
			}
			else if (strKey.contains(recorderName)) {
				textFieldRecorder = textField;
				iconPanelRecorder = iconPanel;
				iconValidRecorder = iconValid;
				iconNotValidRecorder = iconNotValid;
				textButtonPanelRecorder = textButtonPanel;
				textButtonPanelRecorder.add(textFieldRecorder);
				textButtonPanelRecorder.add(iconPanelRecorder);
				errorLabelRecorder = errorLabel;
				recorderPanel = panel;
				recorderPanel.add(textButtonPanelRecorder);
				recorderPanel.add(errorLabelRecorder);
			}
			else if (strKey.contains(apiKeyName)) {
				textFieldApiKey = textField;
				iconPanelApiKey = iconPanel;
				textButtonPanelApiKey = textButtonPanel;
				textButtonPanelApiKey.add(textFieldApiKey);
				textButtonPanelApiKey.add(iconPanelApiKey);
				errorLabelApiKey = errorLabel;
				apiKeyPanel = panel;
				apiKeyPanel.add(textButtonPanelApiKey);
				apiKeyPanel.add(errorLabelApiKey);
			} 
			else if (strKey.contains(pluginName)) {
				textFieldPlugin = textField;
				iconPanelPlugin = iconPanel;
				iconValidPlugin = iconValid;
				iconNotValidPlugin = iconNotValid;
				fileSearchPlugin = fileSearch;
				textButtonPanelPlugin = textButtonPanel;
				textButtonPanelPlugin.add(fileSearchPlugin);
				textButtonPanelPlugin.add(textFieldPlugin);
				errorLabelPlugin = errorLabel;
				pluginPanel = panel;
				pluginPanel.add(textButtonPanelPlugin);
				pluginPanel.add(errorLabelPlugin);
			} 
			else if (strKey.contains(scriptsName)) {
				textFieldScripts = textField;
				iconPanelScripts = iconPanel;
				iconValidScripts = iconValid;
				iconNotValidScripts = iconNotValid;
				fileSearchScripts = fileSearch;
				textButtonPanelScripts = textButtonPanel;
				textButtonPanelScripts.add(fileSearchScripts);
				textButtonPanelScripts.add(textFieldScripts);
				errorLabelPlugin = errorLabel;
				scriptsPanel = panel;
				scriptsPanel.add(textButtonPanelScripts);
				scriptsPanel.add(errorLabelPlugin);
			} 
			else if (strKey.contains(proxyAdress)) {
				textFieldProxyAdress = textField;
				iconPanelProxyAdress = iconPanel;
				textButtonPanelProxyAdress = textButtonPanel;
				textButtonPanelProxyAdress.add(textFieldProxyAdress);
				textButtonPanelProxyAdress.add(iconPanelProxyAdress);
				errorLabelProxyAdress = errorLabel;
				proxyAdressPanel = panel;
				proxyAdressPanel.add(textButtonPanelProxyAdress);
				proxyAdressPanel.add(errorLabelProxyAdress);
			} 
			else if (strKey.contains(proxyPort)) {
				textFieldProxyPort = textField;
				iconPanelProxyPort = iconPanel;
				textButtonPanelProxyPort = textButtonPanel;
				textButtonPanelProxyPort.add(textFieldProxyPort);
				textButtonPanelProxyPort.add(iconPanelProxyPort);
				errorLabelProxyPort = errorLabel;
				proxyPortPanel = panel;
				proxyPortPanel.add(textButtonPanelProxyPort);
				proxyPortPanel.add(errorLabelProxyPort);
			} 
			else if (strKey.contains(proxyUser)) {
				textFieldProxyUserName = textField;
				iconPanelProxyUserName = iconPanel;
				textButtonPanelProxyUserName = textButtonPanel;
				textButtonPanelProxyUserName.add(textFieldProxyUserName);
				textButtonPanelProxyUserName.add(iconPanelProxyUserName);
				errorLabelProxyUserName = errorLabel;
				proxyUserNamePanel = panel;
				proxyUserNamePanel.add(textButtonPanelProxyUserName);
				proxyUserNamePanel.add(errorLabelProxyUserName);
			}
			else if (strKey.contains(proxyPswd)) {
				textFieldProxyUserPswd = textField;
				iconPanelProxyUserPswd = iconPanel;
				textButtonPanelProxyUserPswd = textButtonPanel;
				textButtonPanelProxyUserPswd.add(textFieldProxyUserPswd);
				textButtonPanelProxyUserPswd.add(iconPanelProxyUserPswd);
				errorLabelProxyUserPswd = errorLabel;
				proxyUserPswdPanel = panel;
				proxyUserPswdPanel.add(textButtonPanelProxyUserPswd);
				proxyUserPswdPanel.add(errorLabelProxyUserPswd);
			}
		}
	}
}