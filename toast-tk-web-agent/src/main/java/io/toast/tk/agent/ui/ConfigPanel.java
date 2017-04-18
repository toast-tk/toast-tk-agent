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
	
	private BoxPanel chromePanel, webAppPanel, recorderPanel, apiKeyPanel, pluginPanel, scriptsPanel,
			proxyAdressPanel, proxyPortPanel, proxyUserNamePanel, proxyUserPswdPanel;
	
	private JButton tryButton;

	private JTabbedPane secondPane;

	public static JCheckBox proxyCheckBox;

	private final Properties properties;

	private HashMap<String, BoxPanel> boxFields;

	private final File propertyFile;
	
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

		JPanel mainPane = buildRightMainPanel();
		
		Image toastLogo = PanelHelper.createImage(this,"ToastLogo.png");
		this.setIconImage(toastLogo);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setContentPane(mainPane);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}
	
	private JPanel buildRightMainPanel() throws IOException {
		JPanel contentPanel = buildContentTabbedPanel();
		
		JPanel topMainPanel = buildTopMainPanel();
	    
		JPanel contentMainPanel = PanelHelper.createBasicPanel(BoxLayout.Y_AXIS);
		contentMainPanel.add(topMainPanel);
		contentMainPanel.add(contentPanel);
		
		JPanel mainPane = PanelHelper.createBasicPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		mainPane.add(buildBackgrounIconPanel());
		mainPane.add(contentMainPanel);
		
		return mainPane;
	}
	
	private JPanel buildTopMainPanel() throws IOException {
		JPanel topMainPanel = PanelHelper.createBasicPanel();
	    JLabel topMainLabel = new JLabel("Agent setting");
	    topMainLabel.setFont(PanelHelper.FONT_TITLE_1);
	    topMainLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
		JPanel topIcon = PanelHelper.createBasicPanel();
		topIcon.setLayout(new BorderLayout());
		Image topIconImage = PanelHelper.createImage(this,"AgentSetting_icon.png");
		topIcon.add(new JLabel(new ImageIcon(topIconImage)));
		topMainPanel.add(topMainLabel);
		topMainPanel.add(topIcon);
		return topMainPanel;
	}
	
	private JPanel buildBackgrounIconPanel() throws IOException {
		JPanel panIcon = PanelHelper.createBasicPanel();
		panIcon.setLayout(new BorderLayout());
		Image backGroundImage = PanelHelper.createImage(this,"AgentParamBackGround.png");
		panIcon.add(new JLabel(new ImageIcon(backGroundImage)));
		panIcon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		return panIcon;
	}

	private JPanel buildContentTabbedPanel() throws IOException {
		this.secondPane = new JTabbedPane();
		secondPane.setFont(PanelHelper.FONT_TITLE_2);
		buildFields();
		
		secondPane.setBackground(Color.white);

		ImageIcon generalLogo = PanelHelper.createImageIcon(this, "general_icon.png");
		secondPane.addTab("General", generalLogo, buildGeneralPanel());

		ImageIcon recordLogo = PanelHelper.createImageIcon(this,"recorder_icon.png");
		secondPane.addTab("Recording", recordLogo, buildRecorderPanel());

		ImageIcon proxyLogo = PanelHelper.createImageIcon(this, "proxy_icon.png");
		secondPane.addTab("Proxy", proxyLogo, buildProxyPanel());

		JPanel contentPanel = PanelHelper.createBasicPanel();
		contentPanel.add(secondPane);
		contentPanel.add(buildFullButtonPanel());
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		return contentPanel;
	}

	private void saveAndClose() {
		for (Entry<String, BoxPanel> entry : boxFields.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue().getTextValue());
		}
		properties.setProperty(AgentConfigProvider.TOAST_PROXY_ACTIVATE, Boolean.toString(proxyCheckBox.isSelected()));
		
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
	
	private JPanel buildGeneralPanel() {
		JPanel generalParameters = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
		generalParameters.add(webAppPanel);
		generalParameters.add(apiKeyPanel);
		generalParameters.add(pluginPanel);
		generalParameters.add(scriptsPanel);
		return generalParameters;
	}
	
	private JPanel buildRecorderPanel() {
		JPanel recorderParameters = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
		recorderParameters.add(chromePanel);
		recorderParameters.add(recorderPanel);
		recorderParameters.setLayout(new BoxLayout(recorderParameters, BoxLayout.Y_AXIS));
		return recorderParameters;
	}
	
	private JPanel buildProxyPanel() {
		JPanel proxyPanel = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
	    
	    proxyPanel.add(proxyCheckBox);
	    proxyPanel.add(proxyAdressPanel);
	    proxyPanel.add(proxyPortPanel);
	    proxyPanel.add(proxyUserNamePanel);
	    proxyPanel.add(proxyUserPswdPanel);
	    return proxyPanel;
	}

	private JPanel buildFullButtonPanel() {
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
				test();
			}
	
		});
		return tryButton;
	}
	
	private void test() {
		for (Object key : EnumerationUtils.toList(properties.propertyNames())) {
			String strKey = (String) key;
			if(!strKey.equals(AgentConfigProvider.TOAST_PROXY_ACTIVATE)) {
				BoxPanel box = boxFields.get(strKey);
				try {
					box.testIconValid(false);
					secondPane.repaint();
					secondPane.revalidate();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			} 
		}
		NotificationManager.showMessage("The parameters have been tested !");
	}
	
	private void buildFields() throws IOException{		
		//%% API PANEL %%
		String strKey = AgentConfigProvider.TOAST_API_KEY;
		apiKeyPanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, apiKeyPanel);
		
		//%% PLUGIN PANEL %%
		strKey = AgentConfigProvider.TOAST_PLUGIN_DIR;
		pluginPanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, pluginPanel);
			
		//%% SCRIPTS PANEL %%
		strKey = AgentConfigProvider.TOAST_SCRIPTS_DIR;
		scriptsPanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, scriptsPanel);
		
		//%% CHROME PANEL %%
		strKey = AgentConfigProvider.TOAST_CHROMEDRIVER_PATH;
		chromePanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, chromePanel);

		//%% PROXY CHECK BOX %%
		strKey = AgentConfigProvider.TOAST_PROXY_ACTIVATE;
		proxyCheckBox = new JCheckBox("Activation");
		proxyCheckBox.setBackground(Color.white);
		String proxyValue = properties.getProperty(strKey);
		if("true".equals(proxyValue)) {
			proxyCheckBox.setSelected(true);
		}
				
		//%% PROXY ADRESS PANEL %%
		strKey = AgentConfigProvider.TOAST_PROXY_ADRESS;
		proxyAdressPanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, proxyAdressPanel);
		
		//%% PROXY PORT PANEL %%
		strKey = AgentConfigProvider.TOAST_PROXY_PORT;
		proxyPortPanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, proxyPortPanel);

		//%% PROXY USER NAME PANEL %%
		strKey = AgentConfigProvider.TOAST_PROXY_USER_NAME;
		proxyUserNamePanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, proxyUserNamePanel);
		
		//%% PROXY USER PSWD PANEL %%
		strKey = AgentConfigProvider.TOAST_PROXY_USER_PSWD;
		proxyUserPswdPanel = new BoxPanel(properties, strKey);
		boxFields.put(strKey, proxyUserPswdPanel);
		
		//%% WEBAPP PANEL %%
		strKey = AgentConfigProvider.TOAST_TEST_WEB_APP_URL;
		webAppPanel = new BoxPanel(properties, strKey, 
				proxyAdressPanel, proxyPortPanel,
				proxyUserNamePanel, proxyUserPswdPanel, proxyCheckBox);
		boxFields.put(strKey, webAppPanel);
				
		//%% RECORDER PANEL %%
		strKey = AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL;
		recorderPanel= new BoxPanel(properties, strKey, 
						proxyAdressPanel, proxyPortPanel,
						proxyUserNamePanel, proxyUserPswdPanel, proxyCheckBox);
		boxFields.put(strKey, recorderPanel);
	}
}