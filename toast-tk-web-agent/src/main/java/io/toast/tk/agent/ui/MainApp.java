package io.toast.tk.agent.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Map;

import javax.imageio.ImageIO;

import io.toast.tk.agent.ui.i18n.CommonMessages;
import io.toast.tk.agent.ui.i18n.MainAppMessages;
import io.toast.tk.agent.ui.panels.ConfigPanel;
import io.toast.tk.agent.ui.panels.DropPanel;
import io.toast.tk.agent.ui.provider.ConfigPanelProvider;
import io.toast.tk.agent.ui.provider.DropPanelProvider;
import io.toast.tk.agent.ui.provider.PropertiesProvider;
import io.toast.tk.agent.ui.verify.IPropertyVerifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;

import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.config.DriverFactory;
import io.toast.tk.agent.web.BrowserManager;
import io.toast.tk.agent.web.IAgentServer;
import io.toast.tk.agent.web.RestRecorderService;

public class MainApp implements IAgentApp {

	private static final Logger LOG = LogManager.getLogger(MainApp.class);

	private final ConfigPanelProvider configPanelProvider;
	private final DropPanelProvider dropPanelProvider;
	private BrowserManager browserManager;
	private AgentConfigProvider webConfigProvider;
	private final PropertiesProvider propertiesProvider;
	private TrayIcon trayIcon;
	private IAgentServer agentServer;
	private Map<String, IPropertyVerifier> verifier;
	
	private boolean connectedToWebApp = false;
	private boolean listenerStarted = false;
	private Image onlineImage;

	@Inject
	public MainApp(AgentConfigProvider webConfig,
				   BrowserManager browserManager,
				   IAgentServer agentServer,
				   Map<String, IPropertyVerifier> verifier,
				   PropertiesProvider propertiesProvider,
				   ConfigPanelProvider configPanelProvider,
				   DropPanelProvider dropPanelProvider) {
		this.webConfigProvider = webConfig;
		this.browserManager = browserManager;
		this.propertiesProvider = propertiesProvider;
		this.agentServer = agentServer;
		this.configPanelProvider = configPanelProvider;
		this.dropPanelProvider = dropPanelProvider;
		this.verifier = verifier;
		initWorkspace();
		init();
	}

	private void initWorkspace() {
		webConfigProvider.get();
		propertiesProvider.get();
	}


	/**
	 * Initialise systray if supported and append the agent contextual menu
	 */
	public void init(){
		if (SystemTray.isSupported()) {
		    SystemTray tray = SystemTray.getSystemTray();
		     try {
		    	InputStream offlineImageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_off.png");
				Image offlineImage = ImageIO.read(offlineImageAsStream);
				InputStream onlineImageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_on.png");
			 	this.onlineImage = ImageIO.read(onlineImageAsStream);
				PopupMenu popup = initMenuItem();
			    this.trayIcon = new TrayIcon(offlineImage, "Toast TK - Web Agent", popup);
			    this.trayIcon.setImageAutoSize(true);
			    tray.add(this.trayIcon);
			} catch (IOException|AWTException e1) {
				LOG.error(e1);
			}
		} else {
			LOG.error("The system Tray is not supported !");
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
	    MenuItem dropItem = new MenuItem("Drag & Drop");
	    
	    quitItem.addActionListener(this::killListener);
	    connectItem.addActionListener(this::connectListener);
	    executeItem.addActionListener(this::executeListener);
	    stopRecordingItem.addActionListener(this::stopListener);
	    startRecordingItem.addActionListener(this::start);
	    settingsItem.addActionListener(this::settingsListener);
	    dropItem.addActionListener(this::dropListener);
	    
	    popup.add(connectItem);
	    popup.addSeparator();
	    popup.add(startRecordingItem);
	    popup.add(stopRecordingItem);
	    popup.addSeparator();
	    popup.add(executeItem);
	    popup.addSeparator();
	    popup.add(dropItem);
	    popup.addSeparator();
	    popup.add(settingsItem);
	    popup.addSeparator();
	    popup.add(quitItem); 
	    
	    return popup;
	}
	
	private void connectListener(ActionEvent e){
		connect();
	}

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

	private void executeListener(ActionEvent e){
		execute();
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

	private void killListener(ActionEvent event){
		try {
			agentServer.unRegister();
		} catch (UnknownHostException exception) {
			LOG.error(exception.getMessage(), exception);
		}finally {
			System.exit(0);
		}
	}

	private void start(ActionEvent e) {
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
	}

	private boolean hasValidRecordingEnvironment() {
		return assertProperty(DriverFactory.getDriver()) &&
				assertProperty(AgentConfigProvider.TOAST_TEST_WEB_INIT_RECORDING_URL) &&
				assertProperty(AgentConfigProvider.TOAST_TEST_WEB_APP_URL);
	}

	private void stopListener(ActionEvent e){
		if(listenerStarted) {
			browserManager.closeBrowser();
		}
		else{
			NotificationManager.showMessage(MainAppMessages.RECORDER_NOT_STARTED).showNotification();
		}
	}
	
	private void settingsListener(ActionEvent e){
		ConfigPanel p = configPanelProvider.get();
		if (p == null) {
			NotificationManager.showMessage(CommonMessages.PROPERTIES_NOT_DISPLAYED);
		}
	}

	private void dropListener(ActionEvent e){
		DropPanel p = dropPanelProvider.get();
		if (p == null) {
			NotificationManager.showMessage(CommonMessages.PROPERTIES_NOT_DISPLAYED);
		}
	}
	
	public AgentConfig getConfig() {
		return webConfigProvider.get();
	}

	private boolean assertProperty(String property) {
		return this.verifier.get(property).validate();
	}
}
