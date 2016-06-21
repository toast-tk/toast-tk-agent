package com.synaptix.toast.agent.ui;

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
import com.synaptix.toast.agent.web.BrowserManager;
import com.synaptix.toast.agent.web.IAgentServer;
import com.synaptix.toast.agent.web.RestRecorderService;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.config.ConfigProvider;
import com.synaptix.toast.core.agent.config.WebConfig;
import com.synaptix.toast.core.agent.config.WebConfigProvider;

public class MainApp {

	private static final Logger LOG = LogManager.getLogger(MainApp.class);
	final static String PROPERTY_FILE = "/toast.web.properties";
	private BrowserManager browserManager;
	private File toastWebPropertiesFile;
	private ConfigProvider configProvider;
	private WebConfigProvider webConfigProvider;
	private final Properties webProperties;
	private TrayIcon trayIcon;
	private Image online_image;
	private Image offline_image;
	private IAgentServer agentServer;
	
	@Inject
	public MainApp(ConfigProvider config, WebConfigProvider webConfig, 
			BrowserManager browserManager, IAgentServer agentServer){
		this.configProvider = config;
		this.webConfigProvider = webConfig;
		this.browserManager = browserManager;
		this.webProperties = new Properties();
		this.agentServer= agentServer;
		initWorkspace();
		init();
	}

	private void initWorkspace() {
		try {
			Config config = configProvider.get();
			WebConfig webConfig = webConfigProvider.get();
			final String workSpaceDir = config.getToastHome();
			LOG.info("creating workspace directory at: " + workSpaceDir );
			createHomeDirectories(workSpaceDir);
			this.toastWebPropertiesFile = new File(workSpaceDir + PROPERTY_FILE);
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

	private void initAndStoreProperties(final WebConfig webConfig) throws IOException {
		Properties p = new Properties();
		p.setProperty(Property.TOAST_TEST_WEB_INIT_RECORDING_URL, webConfig.getWebInitRecordingUrl());
		p.setProperty(Property.TOAST_CHROMEDRIVER_PATH, webConfig.getChromeDriverPath());
		p.setProperty(Property.TOAST_TEST_WEB_APP_URL, webConfig.getWebAppUrl());
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
			    
			    MenuItem killItem = new MenuItem("Kill agent");
			    MenuItem connectItem = new MenuItem("Connect to WebApp");
			    MenuItem startRecordingItem = new MenuItem("Start Recording");
			    MenuItem stopRecordingItem = new MenuItem("Stop Recording");
			    MenuItem settingsItem = new MenuItem("Settings");
			    
			    killItem.addActionListener(getKillListener());
			    connectItem.addActionListener(getConnectListener());
			    stopRecordingItem.addActionListener(getStopListener());
			    startRecordingItem.addActionListener(getStartListener());
			    settingsItem.addActionListener(getSettingsListener());
			    
			    popup.add(connectItem);
			    popup.addSeparator();
			    popup.add(startRecordingItem);
			    popup.add(stopRecordingItem);
			    popup.addSeparator();
			    popup.add(killItem);
			    popup.addSeparator();
			    popup.add(settingsItem); 
			    
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
	        	agentServer.register();
	        	trayIcon.setImage(online_image);
	        	NotificationManager.showMessage("Web Agent - Connected to Webapp !").showNotification();
	        }
	    };
	    return listener;
	}
	
	ActionListener getKillListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	System.exit(-1);
	        }
	    };
	    return listener;
	}
	
	ActionListener getStartListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	browserManager.startRecording();
	        }
	    };
	    return listener;
	}
	
	ActionListener getStopListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	browserManager.closeBrowser();
	        }
	    };
	    return listener;
	}
	
	
	ActionListener getSettingsListener(){
	    ActionListener listener = new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	new ConfigPanel(webProperties, toastWebPropertiesFile);
	        }
	    };
	    return listener;
	}
	
	public WebConfig getWebConfig() {
		return webConfigProvider.get();
	}

	public Config getConfig() {
		return configProvider.get();
	}
	

}
