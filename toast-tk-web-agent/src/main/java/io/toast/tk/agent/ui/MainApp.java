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
	private Image onlineImage;
	private Image offlineImage;
	private IAgentServer agentServer;
	
	private boolean connectedToWebApp = false;
	private boolean listenerStarted = false;

	
	@Inject
	public MainApp(AgentConfigProvider webConfig, 
			BrowserManager browserManager, IAgentServer agentServer) throws Exception{
		this.webConfigProvider = webConfig;
		this.browserManager = browserManager;
		this.webProperties = new Properties();
		this.agentServer= agentServer;
		initWorkspace();
		init();
	}

	private void initWorkspace() throws Exception {
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
			throw new Exception();
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
		p.setProperty(AgentConfigProvider.TOAST_PROXY_ACTIVATE, webConfig.getProxyActivate());
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
		    	InputStream offlineImageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_off.png");   
				this.offlineImage = ImageIO.read(offlineImageAsStream);
				
				InputStream onlineImageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_on.png");   
				this.onlineImage = ImageIO.read(onlineImageAsStream);

			    PopupMenu popup = initMenuItem();
			    
			    this.trayIcon = new TrayIcon(offlineImage, "Toast TK - Web Agent", popup);
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
	
	private PopupMenu initMenuItem() {
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
	    
	    return popup;
	}
	
	private ActionListener getConnectListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	connect();
	        }
	    };
	    return listener;
	}
	
	private void connect() {	
		try {
			if(verificationWebApp(AgentConfigProvider.TOAST_TEST_WEB_APP_URL)) {
				if(agentServer.register(webConfigProvider.get().getApiKey())) {
					trayIcon.setImage(onlineImage);
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
	
	private ActionListener getExecuteListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	execute();
	        }
	    };
	    return listener;
	}
	
	private void execute() {
		try {
			if(verificationWebApp(AgentConfigProvider.TOAST_PLUGIN_DIR) && 
					verificationWebApp(AgentConfigProvider.TOAST_SCRIPTS_DIR)) {
				NotificationManager.showMessage("The scripts are executed !").showNotification();
				Thread thread = new Thread(new WaiterThread(webConfigProvider));
				thread.start();
				
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private ActionListener getKillListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	agentServer.unRegister();
	        	System.exit(0);
	        }
	    };
	    return listener;
	}
	
	private ActionListener getStartListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	start();
	        }
	    };
	    return listener;
	}
	
	private void start() {
		try {
    		if(connectedToWebApp) {
    			boolean flag = false;
				if(verificationWebApp(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH) &&
						verificationWebApp(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL) && 
							verificationWebApp(AgentConfigProvider.TOAST_TEST_WEB_APP_URL)) {
					flag = true;
					listenerStarted = true;
					browserManager.startRecording();
					
				}
				if(!flag) {
					NotificationManager.showMessage("Unable to start recorder, please check recoder parameters !").showNotification();
				}
    		}
    		else {
				NotificationManager.showMessage("You have to be connected to the WebApp !").showNotification();
    		}
		} catch (IOException e1) {
			LOG.error(e1.getMessage(), e1);
		}
	}
	
	private ActionListener getStopListener(){
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
	
	private ActionListener getSettingsListener(){
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
			if(webConfigProvider.get().getProxyActivate().equals("true")) {
				return ConfigTesterHelper.testWebAppUrl(webConfigProvider.get().getWebAppUrl(), true,
						webConfigProvider.get().getProxyAdress(),
						webConfigProvider.get().getProxyPort(),
						webConfigProvider.get().getProxyUserName(),
						webConfigProvider.get().getProxyUserPswd());
			}
			else 
				return ConfigTesterHelper.testWebAppUrl(webConfigProvider.get().getWebAppUrl(), true);
		}
		if(property.equals(AgentConfigProvider.TOAST_PLUGIN_DIR)) {
			return ConfigTesterHelper.testWebAppDirectory(webConfigProvider.get().getPluginDir(), true, false);
		}
		if(property.equals(AgentConfigProvider.TOAST_SCRIPTS_DIR)) {
			return ConfigTesterHelper.testWebAppDirectory(webConfigProvider.get().getScriptsDir(), true, false);
		}
		if(property.equals(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL)) {
			if(webConfigProvider.get().getProxyActivate().equals("true")) {
				return ConfigTesterHelper.testWebAppUrl(webConfigProvider.get().getWebInitRecordingUrl(), true,
						webConfigProvider.get().getProxyAdress(),
						webConfigProvider.get().getProxyPort(),
						webConfigProvider.get().getProxyUserName(),
						webConfigProvider.get().getProxyUserPswd());
			}
			else 
				return ConfigTesterHelper.testWebAppUrl(webConfigProvider.get().getWebInitRecordingUrl(), true);
		}
		if(property.equals(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH)) {
			return ConfigTesterHelper.testWebAppDirectory(webConfigProvider.get().getChromeDriverPath(), true, true);
		}
		return false;
	}
}
