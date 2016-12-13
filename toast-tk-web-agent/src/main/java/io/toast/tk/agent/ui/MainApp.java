package io.toast.tk.agent.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.run.TestRunner;
import io.toast.tk.agent.web.BrowserManager;
import io.toast.tk.agent.web.IAgentServer;
import io.toast.tk.agent.web.RestRecorderService;

public class MainApp implements IAgentApp {

	private static final Logger LOG = LogManager.getLogger(MainApp.class);
	
	private BrowserManager browserManager;
	private File toastWebPropertiesFile;
	private AgentConfigProvider webConfigProvider;
	private final Properties webProperties;
	private TrayIcon trayIcon;
	private Image online_image;
	private Image offline_image;
	private IAgentServer agentServer;
	
	private boolean connectedToWebApp = false;
	private boolean listenerStarted = false;

	private TestRunner runner;
	
	@Inject
	public MainApp(AgentConfigProvider webConfig, 
			BrowserManager browserManager, IAgentServer agentServer){
		this.webConfigProvider = webConfig;
		this.browserManager = browserManager;
		this.webProperties = new Properties();
		this.agentServer= agentServer;
		this.runner = new TestRunner(this.webConfigProvider);
		initWorkspace();
		init();
	}

	private void initWorkspace() {
		try {
			AgentConfig webConfig = webConfigProvider.get();
			final String workSpaceDir = AgentConfig.getToastHome();
			LOG.info("creating workspace directory at: " + workSpaceDir );
			createHomeDirectories(workSpaceDir);
			this.toastWebPropertiesFile = new File(AgentConfig.TOAST_PROPERTIES_FILE);
			if (!toastWebPropertiesFile.exists()) {
				toastWebPropertiesFile.createNewFile();
			}
			initAndStoreProperties(webConfig);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			throw new Error(e.getMessage());
		}
	}
	
	private void createHomeDirectories(String workSpaceDir) {
		new File(workSpaceDir).mkdir();
	}

	private void initAndStoreProperties(final AgentConfig webConfig) throws IOException {
		Properties p = new Properties();
		p.setProperty(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL, webConfig.getWebInitRecordingUrl());
		p.setProperty(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH, webConfig.getChromeDriverPath());
		p.setProperty(AgentConfigProvider.TOAST_TEST_WEB_APP_URL, webConfig.getWebAppUrl());
		p.setProperty(AgentConfigProvider.TOAST_API_KEY, webConfig.getApiKey());
		p.setProperty(AgentConfigProvider.TOAST_PLUGIN_DIR, webConfig.getPluginDir());
		p.setProperty(AgentConfigProvider.TOAST_SCRIPTS_DIR, webConfig.getScriptsDir());
		p.setProperty(AgentConfigProvider.TOAST_PROXY_ADRESS, webConfig.getProxyAdress());
		p.setProperty(AgentConfigProvider.TOAST_PROXY_PORT, webConfig.getProxyPort());
		p.setProperty(AgentConfigProvider.TOAST_PROXY_USER_NAME, webConfig.getProxyUserName());
		p.setProperty(AgentConfigProvider.TOAST_PROXY_USER_PSWD, webConfig.getProxyUserPswd());
		p.store(FileUtils.openOutputStream(this.toastWebPropertiesFile), null);
		this.webProperties.load(FileUtils.openInputStream(this.toastWebPropertiesFile));
	}
	
	public void init(){
		if (SystemTray.isSupported()) {
		    SystemTray tray = SystemTray.getSystemTray();
		     try {
		    	InputStream offline_imageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_off.png");   
				this.offline_image = ImageIO.read(offline_imageAsStream);
				
				InputStream online_imageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_on.png");   
				this.online_image = ImageIO.read(online_imageAsStream);

			    PopupMenu popup = new PopupMenu();
			    
			    MenuItem quitItem = new MenuItem("Quit");
			    MenuItem connectItem = new MenuItem("Connect");
			    MenuItem executeItem = new MenuItem("Execute Scripts");
			    MenuItem startRecordingItem = new MenuItem("Start Recording");
			    MenuItem stopRecordingItem = new MenuItem("Stop Recording");
			    MenuItem settingsItem = new MenuItem("Settings");
			    
			    quitItem.addActionListener(getKillListener());
			    connectItem.addActionListener(getConnectListener());
			    executeItem.addActionListener(getExecuteListener());
			    stopRecordingItem.addActionListener(getStopListener());
			    startRecordingItem.addActionListener(getStartListener());
			    settingsItem.addActionListener(getSettingsListener());
			    
			    popup.add(connectItem);
			    popup.addSeparator();
			    popup.add(startRecordingItem);
			    popup.add(stopRecordingItem);
			    popup.addSeparator();
			    popup.add(executeItem);
			    popup.addSeparator();
			    popup.add(settingsItem);
			    popup.addSeparator();
			    popup.add(quitItem); 
			    
			    this.trayIcon = new TrayIcon(offline_image, "Toast TK - Web Agent", popup);
			} catch (IOException e1) {
				LOG.info(e1);
			}
		    trayIcon.setImageAutoSize(true);
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		    	LOG.info(e);
		    }
		}
	}
	
	ActionListener getConnectListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	try {
					if(verificationWebApp(AgentConfigProvider.TOAST_TEST_WEB_APP_URL)) {
						if(agentServer.register(webConfigProvider.get().getApiKey())) {
							trayIcon.setImage(online_image);
							connectedToWebApp = true;
							NotificationManager.showMessage("Web Agent - Connected to Webapp !").showNotification();
						}
						else{
							NotificationManager.showMessage("The ApiKey does not match with the WebApp: \n" + webConfigProvider.get().getApiKey()).showNotification();
						}
					}
					else {
						NotificationManager.showMessage("The Web App does not anwser").showNotification();
					}
				} catch (IOException e1) {
					LOG.error(e1.getMessage(), e1);
				}
	        }
	    };
	    return listener;
	}
	
	ActionListener getExecuteListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	try {
					if(verificationWebApp(AgentConfigProvider.TOAST_PLUGIN_DIR)) {
						if(verificationWebApp(AgentConfigProvider.TOAST_SCRIPTS_DIR)) {
							NotificationManager.showMessage("The scripts are executed !").showNotification();
				        	runner.execute();
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
	    };
	    return listener;
	}
	
	ActionListener getKillListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	agentServer.unRegister();
	        	System.exit(-1);
	        }
	    };
	    return listener;
	}
	
	ActionListener getStartListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	try {
	        		if(connectedToWebApp) {
	        			boolean flag = false;
						if(verificationWebApp(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH)) {
							if(verificationWebApp(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL)) {
								if(verificationWebApp(AgentConfigProvider.TOAST_TEST_WEB_APP_URL)) {
									flag = true;
									listenerStarted = true;
									browserManager.startRecording();
								}
							}
						}
						if(!flag) {
							NotificationManager.showMessage("Unable to start recorder, please check recoder parameters !").showNotification();
						}
	        		}
	        		else 
						NotificationManager.showMessage("You have to be connected to the WebApp !").showNotification();
				} catch (IOException e1) {
					LOG.error(e1.getMessage(), e1);
				}
	        }
	    };
	    return listener;
	}
	
	ActionListener getStopListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	if(listenerStarted) {
		        	browserManager.closeBrowser();
	        	}
	        	else{
	        		NotificationManager.showMessage("The recorder has not been started yet !").showNotification();
	        	}
	        }
	    };
	    return listener;
	}
	
	ActionListener getSettingsListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	try {
					new ConfigPanel(webProperties, toastWebPropertiesFile);
				} catch (IOException e1) {
					LOG.error(e1.getMessage(), e1);
				}
	        }
	    };
	    return listener;
	}
	
	public AgentConfig getConfig() {
		return webConfigProvider.get();
	}

	public boolean verificationWebApp(String property) throws IOException{
		if(property.equals(AgentConfigProvider.TOAST_TEST_WEB_APP_URL)) {
			return ConfigPanel.testWebAppURL(webConfigProvider.get().getWebAppUrl(), true);
		}
		if(property.equals(AgentConfigProvider.TOAST_PLUGIN_DIR)) {
			return ConfigPanel.testWebAppDirectory(webConfigProvider.get().getPluginDir(), true, false);
		}
		if(property.equals(AgentConfigProvider.TOAST_SCRIPTS_DIR)) {
			return ConfigPanel.testWebAppDirectory(webConfigProvider.get().getScriptsDir(), true, false);
		}
		if(property.equals(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL)) {
			return ConfigPanel.testWebAppURL(webConfigProvider.get().getWebInitRecordingUrl(), true);
		}
		if(property.equals(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH)) {
			return ConfigPanel.testWebAppDirectory(webConfigProvider.get().getChromeDriverPath(), true, true);
		}
		return false;
	}
}
