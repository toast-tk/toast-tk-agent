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
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import io.toast.tk.agent.ui.i18n.MainAppMessages;
import io.toast.tk.agent.ui.verify.IPropertyVerifier;
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
	private Map<String, IPropertyVerifier> verifier;
	
	private boolean connectedToWebApp = false;
	private boolean listenerStarted = false;

	@Inject
	public MainApp(AgentConfigProvider webConfig,
				   BrowserManager browserManager, IAgentServer agentServer,
				   Map<String, IPropertyVerifier> verifier) throws Exception{
		this.webConfigProvider = webConfig;
		this.browserManager = browserManager;
		this.webProperties = new Properties();
		this.agentServer= agentServer;
		this.verifier = verifier;
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

	/**
	 * Initialise systray if supported and append the agent contextual menu
	 */
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
				 trayIcon.setImageAutoSize(true);
				 tray.add(trayIcon);
			} catch (IOException|AWTException e1) {
				LOG.error(e1);
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
	
	//TODO: Switch implementation to websocket and have a connection watcher
	private void connect() {
		try {
			if(isWebAppListening()) {
				if(agentServer.register(webConfigProvider.get().getApiKey())) {
					makeAgentOnline();
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

	private void makeAgentOnline() {
		trayIcon.setImage(onlineImage);
		connectedToWebApp = true;
	}

	private boolean isWebAppListening() throws IOException {
		return assertProperty(AgentConfigProvider.TOAST_TEST_WEB_APP_URL);
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
			if(hasPluginDirectory() && hasScriptsDirectory()) {
				NotificationManager.showMessage(MainAppMessages.SCRIPTS_EXECUTION).showNotification();
				Thread thread = new Thread(new WaiterThread(webConfigProvider));
				thread.start();
			}
		} catch (IOException e1) {
			LOG.error(e1.getMessage(), e1);
		}
	}

	private boolean hasPluginDirectory() throws IOException {
		return assertProperty(AgentConfigProvider.TOAST_PLUGIN_DIR);
	}

	private boolean hasScriptsDirectory() throws IOException {
		return assertProperty(AgentConfigProvider.TOAST_SCRIPTS_DIR);
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
				if(hasValidRecordingEnvironment()) {
					listenerStarted = true;
					browserManager.startRecording();
				}
				else {
					NotificationManager.showMessage(MainAppMessages.UNABLE_START_RECORDER).showNotification();
				}
    		}
    		else {
				NotificationManager.showMessage(MainAppMessages.UNABLE_CONNECT_WEBAPP).showNotification();
    		}
		} catch (IOException e1) {
			LOG.error(e1.getMessage(), e1);
		}
	}

	private boolean hasValidRecordingEnvironment() throws IOException {
		return assertProperty(AgentConfigProvider.TOAST_CHROMEDRIVER_PATH) &&
				assertProperty(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL) &&
				assertProperty(AgentConfigProvider.TOAST_TEST_WEB_APP_URL);
	}

	private ActionListener getStopListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	if(listenerStarted) {
		        	browserManager.closeBrowser();
	        	}
	        	else{
	        		NotificationManager.showMessage(MainAppMessages.RECORDER_NOT_STARTED).showNotification();
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

	private boolean assertProperty(String property) throws IOException{
		return this.verifier.get(property).validate();
	}
}
