package io.toast.tk.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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
	public String proxyActivate = AgentConfigProvider.TOAST_PROXY_ACTIVATE;
	public String proxyAdress = AgentConfigProvider.TOAST_PROXY_ADRESS;
	public String proxyPort = AgentConfigProvider.TOAST_PROXY_PORT;
	public String proxyUser = AgentConfigProvider.TOAST_PROXY_USER_NAME;
	public String proxyPswd = AgentConfigProvider.TOAST_PROXY_USER_PSWD;

	private static final Logger LOG = LogManager.getLogger(ConfigPanel.class);

	private JPanel mainPane;
	
	private BoxPanel chromePanel, webAppPanel, recorderPanel, apiKeyPanel, pluginPanel, scriptsPanel,
			proxyAdressPanel, proxyPortPanel, proxyUserNamePanel, proxyUserPswdPanel;
	
	private JButton tryButton;

	private JTabbedPane secondPane;

	public static JCheckBox proxyCheckBox;

	private final Properties properties;

	private HashMap<String, BoxPanel> boxFields;

	private final File propertyFile;

	private Image backGround_image;
	
	public ConfigPanel(Properties propertiesConfiguration, File propertyFile) throws IOException {
		super();
		this.properties = propertiesConfiguration;
		this.propertyFile = propertyFile;
		
		buildContentPanel();
		
		tryButton.doClick();
		
		this.setVisible(true);
	}
	
	
	private void buildContentPanel() throws IOException {
		
		this.boxFields = new HashMap<String, BoxPanel>();
		this.secondPane = new JTabbedPane();
		secondPane.setFont(PanelHelper.FONT_TITLE_2);
		buildFields();
		
		secondPane.setBackground(Color.white);

		ImageIcon general_logo = PanelHelper.createImageIcon(this, "general_icon.png");
		secondPane.addTab("General parameters", general_logo, createGeneralPanel());

		ImageIcon record_logo = PanelHelper.createImageIcon(this,"recorder_icon.png");
		secondPane.addTab("Recording", record_logo, createRecorderPanel());

		ImageIcon proxy_logo = PanelHelper.createImageIcon(this, "proxy_icon.png");
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
		
	    JLabel firstMainPanel = new JLabel("Agent setting");
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
	}

	private void saveAndClose() {
		for (Entry<String, BoxPanel> entry : boxFields.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue().getTextValue());
		}
		properties.setProperty(proxyActivate, Boolean.toString(proxyCheckBox.isSelected()));
		
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
		tryButton = new JButton("Test");
		tryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	
				for (Object key : EnumerationUtils.toList(properties.propertyNames())) {
					String strKey = (String) key;
					if(strKey != proxyActivate) {
						BoxPanel box = boxFields.get(strKey);
						try {
							box.testIconValid(strKey, false);
							secondPane.repaint();
							secondPane.revalidate();
						} catch (IOException e) {
							LOG.error(e.getMessage(), e);
						}
					} 
				}
				NotificationManager.showMessage("The parameters have been tested !");
	
			}
	
		});
		return tryButton;
	}
	
	private void buildFields() throws IOException{	
		
		//%% API PANEL %%
		String strKey = apiKeyName;
		apiKeyPanel = new BoxPanel( properties, strKey);
		boxFields.put(strKey, apiKeyPanel);
		
		//%% PLUGIN PANEL %%
		strKey = pluginName;
		pluginPanel = new BoxPanel( properties, strKey);
		boxFields.put(strKey, pluginPanel);
			
		//%% SCRIPTS PANEL %%
		strKey = scriptsName;
		scriptsPanel = new BoxPanel( properties, strKey);
		boxFields.put(strKey, scriptsPanel);
		
		//%% CHROME PANEL %%
		strKey = chromeDriverName;
		chromePanel = new BoxPanel( properties, strKey);
		boxFields.put(strKey, chromePanel);

		//%% PROXY CHECK BOX %%
		strKey = proxyActivate;
		proxyCheckBox = new JCheckBox("Activation");
		proxyCheckBox.setBackground(Color.white);
		String proxyValue = properties.getProperty(strKey);
		if(proxyValue == "true") {
			proxyCheckBox.setSelected(true);
		}
				
		//%% PROXY ADRESS PANEL %%
		strKey = proxyAdress;
		proxyAdressPanel = new BoxPanel( properties, strKey);
		boxFields.put(strKey, proxyAdressPanel);
		
		//%% PROXY PORT PANEL %%
		strKey = proxyPort;
		proxyPortPanel = new BoxPanel( properties, strKey);
		chromeDriverName = "chromedriver";
		boxFields.put(strKey, proxyPortPanel);

		//%% PROXY USER NAME PANEL %%
		strKey = proxyUser;
		proxyUserNamePanel = new BoxPanel( properties, strKey);
		boxFields.put(strKey, proxyUserNamePanel);
		
		//%% PROXY USER PSWD PANEL %%
		strKey = proxyPswd;
		proxyUserPswdPanel = new BoxPanel( properties, strKey);
		boxFields.put(strKey, proxyUserPswdPanel);
		
		//%% WEBAPP PANEL %%
		strKey = webAppName;
		webAppPanel = new BoxPanel( properties, strKey, 
				proxyAdressPanel, proxyPortPanel,
				proxyUserNamePanel, proxyUserPswdPanel, proxyCheckBox);
		boxFields.put(strKey, webAppPanel);
				

		//%% RECORDER PANEL %%
		strKey = recorderName;
		recorderPanel= new BoxPanel( properties, strKey, 
						proxyAdressPanel, proxyPortPanel,
						proxyUserNamePanel, proxyUserPswdPanel, proxyCheckBox);
		boxFields.put(strKey, recorderPanel);
	}
	

}