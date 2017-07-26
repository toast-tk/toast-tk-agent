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

import com.google.inject.Inject;

import io.toast.tk.agent.ui.i18n.UIMessages;
import io.toast.tk.agent.ui.panels.AbstractPanel;
import io.toast.tk.agent.ui.panels.ComboBoxPanel;
import io.toast.tk.agent.ui.panels.EnumError;
import io.toast.tk.agent.ui.panels.FileChoosePanel;
import io.toast.tk.agent.ui.panels.SmtpPanel;
import io.toast.tk.agent.ui.panels.PasswordPanel;
import io.toast.tk.agent.ui.panels.RecorderPanel;
import io.toast.tk.agent.ui.panels.SimplePanel;
import io.toast.tk.agent.ui.panels.WebAppPanel;
import io.toast.tk.agent.ui.utils.PanelHelper;
import io.toast.tk.runtime.constant.Property;
import io.toast.tk.runtime.mail.SmtpConfigProvider;
import io.toast.tk.runtime.utils.EncryptHelper;

import org.apache.commons.collections4.EnumerationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.toast.tk.adapter.constant.AdaptersConfigProvider;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.config.DriverFactory;


public class ConfigPanel extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(ConfigPanel.class);
	
	private AbstractPanel driverSelectPanel, webAppPanel, recorderPanel, apiKeyPanel, pluginPanel, scriptsPanel,
			proxyAdressPanel, proxyPortPanel, proxyUserNamePanel, proxyUserPswdPanel, 
			mailHostPanel, mailPortPanel, mailUserPanel, mailUserPswdPanel;
	
	private JButton tryButton;

	private JTabbedPane secondPane;

	public static JCheckBox proxyCheckBox, mailCheckBox;

	private final Properties properties;

	private HashMap<String, AbstractPanel> boxFields;

	private final File propertyFile;

	@Inject
	public ConfigPanel(PropertiesHolder pHolder) throws IOException {
		super();
		this.properties = pHolder.getProperties();
		this.propertyFile = pHolder.getFile();
		
		buildContentPanel();
		
		tryButton.doClick();
		
		this.setVisible(true);
	}

	private void buildContentPanel() throws IOException {
		
		this.boxFields = new HashMap<String, AbstractPanel>();
		this.secondPane = new JTabbedPane();
		secondPane.setFont(PanelHelper.FONT_TITLE_2);
		
		buildFields();

		JPanel mainPane = buildRightMainPanel();
		
		Image toastLogo = PanelHelper.createImage(this,"ToastLogo.png");
		this.setIconImage(toastLogo);
		
		setDefaultCloseOperation(HIDE_ON_CLOSE);
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
	    
		JPanel contentMainPanel = PanelHelper.createBasicJPanel(BoxLayout.Y_AXIS);
		contentMainPanel.add(topMainPanel);
		contentMainPanel.add(contentPanel);
		
		JPanel mainPane = PanelHelper.createBasicJPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
		mainPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		mainPane.add(buildBackgrounIconPanel());
		mainPane.add(contentMainPanel);
		
		return mainPane;
	}
	
	private JPanel buildTopMainPanel() throws IOException {
		JPanel topMainPanel = PanelHelper.createBasicJPanel();
	    JLabel topMainLabel = new JLabel("Agent settings");
	    topMainLabel.setFont(PanelHelper.FONT_TITLE_1);
	    topMainLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
		JPanel topIcon = PanelHelper.createBasicJPanel();
		topIcon.setLayout(new BorderLayout());
		Image topIconImage = PanelHelper.createImage(this,"AgentSetting_icon.png");
		topIcon.add(new JLabel(new ImageIcon(topIconImage)));
		topMainPanel.add(topMainLabel);
		topMainPanel.add(topIcon);
		return topMainPanel;
	}
	
	private JPanel buildBackgrounIconPanel() throws IOException {
		JPanel panIcon = PanelHelper.createBasicJPanel();
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
		secondPane.addTab("Recorder", recordLogo, buildRecorderPanel());

		ImageIcon proxyLogo = PanelHelper.createImageIcon(this, "proxy_icon.png");
		secondPane.addTab("Proxy", proxyLogo, buildProxyPanel());

		// ADD THIS WHEN MAIL WORKS
		ImageIcon mailLogo = PanelHelper.createImageIcon(this, "mail_icon.png");
		secondPane.addTab("Mail", mailLogo, buildMailPanel());
		
		JPanel contentPanel = PanelHelper.createBasicJPanel();
		contentPanel.add(secondPane);
		contentPanel.add(buildFullButtonPanel());
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		return contentPanel;
	}

	private void saveAndClose() {
		for (Entry<String, AbstractPanel> entry : boxFields.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue().getTextValue());
		}
		properties.setProperty(DriverFactory.getDriverValue(), ((ComboBoxPanel) driverSelectPanel).getValue());
		properties.setProperty(AgentConfigProvider.TOAST_PROXY_ACTIVATE, Boolean.toString(proxyCheckBox.isSelected()));
		properties.setProperty(AgentConfigProvider.TOAST_SMTP_ACTIVATE, Boolean.toString(mailCheckBox.isSelected()));
		properties.setProperty(AgentConfigProvider.TOAST_PROXY_USER_PSWD, EncryptHelper.encrypt(proxyUserPswdPanel.getTextValue()));
		properties.setProperty(AgentConfigProvider.TOAST_SMTP_PSWD, EncryptHelper.encrypt(mailUserPswdPanel.getTextValue()));
		
		try {
			properties.store(FileUtils.openOutputStream(propertyFile), "Saved !");
		} catch (IOException e) {
			LOG.warn("Could not save properties", e);
		}
		
		savePropertyFile();
		saveSmtpPropertyFile();
		
		this.close();
	}
	
	private void savePropertyFile() {
		Properties p = new Properties();
		
		String driver = DriverFactory.getSelected().getType();
		
		p.setProperty(AdaptersConfigProvider.ADAPTER_WEB_DRIVER, driver);
		p.setProperty(AdaptersConfigProvider.ADAPTER_WEB_DRIVER_PATH, ((ComboBoxPanel) driverSelectPanel).getValue());

		p.setProperty(AdaptersConfigProvider.ADAPTER_MAIL_SEND, 
				properties.getProperty(AgentConfigProvider.TOAST_SMTP_ACTIVATE));
		p.setProperty(AdaptersConfigProvider.ADAPTER_MAIL_TO, 
				properties.getProperty(AgentConfigProvider.TOAST_SMTP_USER));

		try {
			p.store(FileUtils.openOutputStream(new File(Property.TOAST_PROPERTIES_FILE)), "Saved !");
		} catch (IOException e) {
			LOG.warn("Could not save properties", e);
		}
		
	}

	private void saveSmtpPropertyFile() {
		SmtpConfigProvider config = new SmtpConfigProvider();
		Properties p = config.get();

		p.setProperty(SmtpConfigProvider.SMTP_PROPERTIES_HOST , mailHostPanel.getTextValue());
		p.setProperty(SmtpConfigProvider.SMTP_PROPERTIES_PORT , mailPortPanel.getTextValue());
		p.setProperty(SmtpConfigProvider.SMTP_PROPERTIES_USER , mailUserPanel.getTextValue());
		p.setProperty(SmtpConfigProvider.SMTP_PROPERTIES_PASSWORD, EncryptHelper.encrypt(mailUserPswdPanel.getTextValue()));

		try {
			p.store(FileUtils.openOutputStream(new File(SmtpConfigProvider.SMTP_PROPERTIES_FILE_PATH)), "Saved !");
		} catch (IOException e) {
			LOG.warn("Could not save properties", e);
		}
		
	}

	private void close() {
		this.setVisible(false);
		this.dispose();
	}
	
	private JPanel buildGeneralPanel() {
		JPanel generalParameters = PanelHelper.createBasicJPanel(BoxLayout.PAGE_AXIS);
		generalParameters.add(webAppPanel);
		generalParameters.add(apiKeyPanel);
		generalParameters.add(pluginPanel);
		generalParameters.add(scriptsPanel);
		return generalParameters;
	}
	
	private JPanel buildRecorderPanel() {
		JPanel recorderParameters = PanelHelper.createBasicJPanel(BoxLayout.PAGE_AXIS);
		recorderParameters.add(driverSelectPanel);
		recorderParameters.add(recorderPanel);
		recorderParameters.setLayout(new BoxLayout(recorderParameters, BoxLayout.Y_AXIS));
		return recorderParameters;
	}
	
	private JPanel buildProxyPanel() {
		JPanel proxyPanel = PanelHelper.createBasicJPanel(BoxLayout.PAGE_AXIS);
	    proxyPanel.add(proxyCheckBox);
	    proxyPanel.add(proxyAdressPanel);
	    proxyPanel.add(proxyPortPanel);
	    proxyPanel.add(proxyUserNamePanel);
	    proxyPanel.add(proxyUserPswdPanel);
	    return proxyPanel;
	}

	private JPanel buildMailPanel() {
		JPanel mailPanel = PanelHelper.createBasicJPanel(BoxLayout.PAGE_AXIS);
		mailPanel.add(mailCheckBox);
		mailPanel.add(mailHostPanel);
		mailPanel.add(mailPortPanel);
		mailPanel.add(mailUserPanel);
		mailPanel.add(mailUserPswdPanel);
	    return mailPanel;
	}
	
	private JPanel buildFullButtonPanel() {
		JPanel buttonPanel = PanelHelper.createBasicJPanel();
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
			if(!strKey.equals(AgentConfigProvider.TOAST_PROXY_ACTIVATE) && 
					!strKey.equals(AgentConfigProvider.TOAST_SMTP_ACTIVATE) && 
					!strKey.equals(AgentConfigProvider.TOAST_CHROMEDRIVER_32_PATH) && 
					!strKey.equals(AgentConfigProvider.TOAST_CHROMEDRIVER_64_PATH) &&
					!strKey.equals(AgentConfigProvider.TOAST_FIREFOXDRIVER_32_PATH) && 
					!strKey.equals(AgentConfigProvider.TOAST_FIREFOXDRIVER_64_PATH) &&
					!strKey.equals(AgentConfigProvider.TOAST_IEDRIVER_32_PATH) && 
					!strKey.equals(AgentConfigProvider.TOAST_IEDRIVER_64_PATH)) {
				AbstractPanel box = boxFields.get(strKey);
				try {
					box.testIconValid(false);
					secondPane.repaint();
					secondPane.revalidate();
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			} 
		}
		NotificationManager.showMessage("The parameters have been tested !").showNotification();
	}
	
	private void buildFields() throws IOException{
		//%% API PANEL %%
		apiKeyPanel = new SimplePanel(properties, AgentConfigProvider.TOAST_API_KEY,
				UIMessages.USER_API_KEY, EnumError.APIKEY);
		boxFields.put(AgentConfigProvider.TOAST_API_KEY, apiKeyPanel);

		//%% PLUGIN PANEL %%
		pluginPanel = new FileChoosePanel(properties, AgentConfigProvider.TOAST_PLUGIN_DIR,
				UIMessages.PLUGIN_DIR, EnumError.DIRECTORY);
		boxFields.put(AgentConfigProvider.TOAST_PLUGIN_DIR, pluginPanel);

		//%% SCRIPTS PANEL %%
		scriptsPanel = new FileChoosePanel(properties, AgentConfigProvider.TOAST_SCRIPTS_DIR,
				UIMessages.SCRIPT_DIR, EnumError.DIRECTORY);
		boxFields.put( AgentConfigProvider.TOAST_SCRIPTS_DIR, scriptsPanel);

		//%% DRIVER SELECTION PANEL %%
		driverSelectPanel = new ComboBoxPanel(properties, AgentConfigProvider.TOAST_DRIVER_SELECT,
				UIMessages.DRIVER_SELECTED, EnumError.FILE);
		boxFields.put(AgentConfigProvider.TOAST_DRIVER_SELECT, driverSelectPanel);
		
		//%% PROXY CHECK BOX %%
		proxyCheckBox = new JCheckBox(UIMessages.ACTIVATE);
		proxyCheckBox.setBackground(Color.white);
		String proxyValue = properties.getProperty(AgentConfigProvider.TOAST_PROXY_ACTIVATE);
		if("true".equals(proxyValue)) {
			proxyCheckBox.setSelected(true);
		}

		//%% PROXY ADRESS PANEL %%
		proxyAdressPanel = new SimplePanel(properties, AgentConfigProvider.TOAST_PROXY_ADRESS,
				UIMessages.PROXY_ADDR, EnumError.NOTHING);
		boxFields.put(AgentConfigProvider.TOAST_PROXY_ADRESS, proxyAdressPanel);

		//%% PROXY PORT PANEL %%
		proxyPortPanel = new SimplePanel(properties, AgentConfigProvider.TOAST_PROXY_PORT,
				UIMessages.PROXY_PORT, EnumError.NOTHING);
		boxFields.put(AgentConfigProvider.TOAST_PROXY_PORT, proxyPortPanel);

		//%% PROXY USER NAME PANEL %%
		proxyUserNamePanel = new SimplePanel(properties, AgentConfigProvider.TOAST_PROXY_USER_NAME,
				UIMessages.PROXY_USER, EnumError.NOTHING);
		boxFields.put(AgentConfigProvider.TOAST_PROXY_USER_NAME, proxyUserNamePanel);

		//%% PROXY USER PSWD PANEL %%
		proxyUserPswdPanel = new PasswordPanel(properties,
				AgentConfigProvider.TOAST_PROXY_USER_PSWD, UIMessages.PROXY_PSENTENCE, EnumError.NOTHING);
		boxFields.put(AgentConfigProvider.TOAST_PROXY_USER_PSWD, proxyUserPswdPanel);

		//%% MAIL CHECK BOX %%
		mailCheckBox = new JCheckBox(UIMessages.ACTIVATE);
		mailCheckBox.setBackground(Color.white);
		String mailValue = properties.getProperty(AgentConfigProvider.TOAST_SMTP_ACTIVATE);
		if("true".equals(mailValue)) {
			mailCheckBox.setSelected(true);
		}

		//%% MAIL HOST PANEL %%
		mailHostPanel = new SimplePanel(properties,
				AgentConfigProvider.TOAST_SMTP_HOST, UIMessages.MAIL_HOST, EnumError.NOTHING);
		boxFields.put(AgentConfigProvider.TOAST_SMTP_HOST, mailHostPanel);

		//%% MAIL PORT PANEL %%
		mailPortPanel = new SimplePanel(properties,
				AgentConfigProvider.TOAST_SMTP_PORT, UIMessages.MAIL_PORT, EnumError.NOTHING);
		boxFields.put(AgentConfigProvider.TOAST_SMTP_PORT, mailPortPanel);

		//%% MAIL USER PANEL %%
		mailUserPanel = new SmtpPanel(properties,
				AgentConfigProvider.TOAST_SMTP_USER, UIMessages.MAIL_USER, EnumError.MAIL);
		boxFields.put(AgentConfigProvider.TOAST_SMTP_USER, mailUserPanel);

		//%% PROXY USER PSWD PANEL %%
		mailUserPswdPanel = new PasswordPanel(properties,
				AgentConfigProvider.TOAST_SMTP_PSWD, UIMessages.MAIL_PSENTENCE, EnumError.NOTHING);
		boxFields.put(AgentConfigProvider.TOAST_SMTP_PSWD, mailUserPswdPanel);
		
		//%% WEBAPP PANEL %%
		webAppPanel = new WebAppPanel(properties, AgentConfigProvider.TOAST_TEST_WEB_APP_URL,
				proxyAdressPanel, proxyPortPanel,
				proxyUserNamePanel, proxyUserPswdPanel, proxyCheckBox);
		boxFields.put(AgentConfigProvider.TOAST_TEST_WEB_APP_URL, webAppPanel);

		//%% RECORDER PANEL %%
		recorderPanel= new RecorderPanel(properties, AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL,
						proxyAdressPanel, proxyPortPanel,
						proxyUserNamePanel, proxyUserPswdPanel, proxyCheckBox);
		boxFields.put(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL, recorderPanel);
	}
}