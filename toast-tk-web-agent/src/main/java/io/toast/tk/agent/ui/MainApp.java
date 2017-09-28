package io.toast.tk.agent.ui;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import io.toast.tk.agent.ui.i18n.CommonMessages;
import io.toast.tk.agent.ui.i18n.MainAppMessages;
import io.toast.tk.agent.ui.panels.DropPanel;
import io.toast.tk.agent.ui.panels.MailHelpPanel;
import io.toast.tk.agent.ui.provider.ConfigPanelProvider;
import io.toast.tk.agent.ui.provider.DropPanelProvider;
import io.toast.tk.agent.ui.provider.HelpPanelProvider;
import io.toast.tk.agent.ui.provider.PropertiesProvider;
import io.toast.tk.agent.ui.utils.PanelHelper;
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
	private final HelpPanelProvider helpPanelProvider;
	private BrowserManager browserManager;
	private AgentConfigProvider webConfigProvider;
	private final PropertiesProvider propertiesProvider;
	private TrayIcon trayIcon;
	private IAgentServer agentServer;
	private Map<String, IPropertyVerifier> verifier;
	
	private boolean connectedToWebApp = false;
	private boolean listenerStarted = false;
	private Image onlineImage, offlineImage;
	
	private final String TOAST_VERSION = "Lesotho"; //Swaziland Brunei
	private final String TOAST_VERSION_URL = "https://fr.wikipedia.org/wiki/Lesotho";

	@Inject
	public MainApp(AgentConfigProvider webConfig,
				   BrowserManager browserManager,
				   IAgentServer agentServer,
				   Map<String, IPropertyVerifier> verifier,
				   PropertiesProvider propertiesProvider,
				   ConfigPanelProvider configPanelProvider,
				   DropPanelProvider dropPanelProvider,
				   HelpPanelProvider helpPanelProvider) {
		this.webConfigProvider = webConfig;
		this.browserManager = browserManager;
		this.propertiesProvider = propertiesProvider;
		this.agentServer = agentServer;
		this.configPanelProvider = configPanelProvider;
		this.dropPanelProvider = dropPanelProvider;
		this.helpPanelProvider = helpPanelProvider;
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
				InputStream onlineImageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_on.png");
			 	this.onlineImage = ImageIO.read(onlineImageAsStream);
			    this.trayIcon = initTrayIcon();
			    tray.add(this.trayIcon);
			    
			    dropPanelProvider.get();
			} catch (IOException|AWTException e1) {
				LOG.error(e1);
			}
		} else {
			LOG.error("The system Tray is not supported !");
		}
	}
	
	private TrayIcon initTrayIcon() throws IOException {
		InputStream onlineImageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_on.png");
	 	this.onlineImage = ImageIO.read(onlineImageAsStream);
    	InputStream offlineImageAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("ToastLogo_off.png");
		this.offlineImage = ImageIO.read(offlineImageAsStream);

		JPopupMenu jpopup = initMenuItem();
		
		TrayIcon trayIcon = new TrayIcon(offlineImage, "Toast TK - Web Agent");
		trayIcon.addMouseListener(new MouseAdapter() {
	        public void mouseReleased(MouseEvent e) {
	            if (e.isPopupTrigger()) {
	                jpopup.setLocation(e.getX(), e.getY()-200);
	                jpopup.setInvoker(jpopup);
	                jpopup.setVisible(true);
	            }
	        }
	    });
	    trayIcon.setImageAutoSize(true);
		return trayIcon;
	}
	
	private JPopupMenu initMenuItem() {
		JPopupMenu popup = new JPopupMenu();

	    JMenuItem connectItem = new JMenuItem("Connect WebApp", 
	    		PanelHelper.createImageIcon(this, "connect_icon_30.png"));
	    JMenuItem helpItem = new JMenuItem("Ask for help!", 
	    		PanelHelper.createImageIcon(this, "lifbuoy_icon_30.png"));
		JMenuItem closeItem = new JMenuItem("Close", 
	    		PanelHelper.createImageIcon(this, "close_icon_30.png"));
	    
	    connectItem.addActionListener(this::connectListener);
	    helpItem.addActionListener(this::helpingListener);

	    popup.add(initToastMenu());
	    popup.addSeparator();
	    popup.add(connectItem);
	    popup.addSeparator();
	    popup.add(initRecordMenu());
	    popup.add(initExecuteMenu());
	    popup.addSeparator();
	    popup.add(helpItem);
	    popup.addSeparator();
	    popup.add(closeItem); 
	    try {
		    popup.addSeparator();
			popup.add(initVersionMenu());
		} catch (URISyntaxException | IOException e) {
			LOG.error(e.getMessage());
		} 
	    
	    return popup;
	}
	
	private JMenu initToastMenu() {
		JMenu toastMenu = new JMenu("TOAST AGENT");
		ImageIcon toastIcon = PanelHelper.createImageIcon(this, "ToastLogo_24.png");
		toastMenu.setIcon(toastIcon);
	    JMenuItem settingsItem = new JMenuItem("Settings", 
	    		PanelHelper.createImageIcon(this, "AgentSetting_icon_30.png"));
		JMenuItem quitItem = new JMenuItem("Quit agent", 
	    		PanelHelper.createImageIcon(this, "power_button_30.png"));
	    settingsItem.addActionListener(this::settingsListener);
	    quitItem.addActionListener(this::killListener);
		toastMenu.add(settingsItem);
		toastMenu.add(quitItem);
	    return toastMenu;
	}
	
	private JMenu initRecordMenu() {
		JMenu recordMenu = new JMenu("Recording");
		ImageIcon recordIcon = PanelHelper.createImageIcon(this, "record_icon_30.png");
		recordMenu.setIcon(recordIcon);
	    JMenuItem startRecordingItem = new JMenuItem("Start Recording", 
	    		PanelHelper.createImageIcon(this, "record_start_icon_30.png"));
	    JMenuItem stopRecordingItem = new JMenuItem("Stop Recording", 
	    		PanelHelper.createImageIcon(this, "record_stop_icon_30.png"));
	    stopRecordingItem.addActionListener(this::stopListener);
	    startRecordingItem.addActionListener(this::start);
	    recordMenu.add(startRecordingItem);
	    recordMenu.add(stopRecordingItem);
	    return recordMenu;
	}
		
	private JMenu initExecuteMenu() {
		JMenu executeMenu = new JMenu("Execute");
		ImageIcon executeIcon = PanelHelper.createImageIcon(this, "execute_icon_30.png");
		executeMenu.setIcon(executeIcon);
	    JMenuItem executeItem = new JMenuItem("Execute Scripts", 
	    		PanelHelper.createImageIcon(this, "executeFile_icon_30.png"));
	    JMenuItem dropItem = new JMenuItem("Drag & Drop", 
	    		PanelHelper.createImageIcon(this, "dragAndDrop_icon_30.png"));
	    executeItem.addActionListener(this::executeListener);
	    dropItem.addActionListener(this::dropListener);
	    executeMenu.add(executeItem);
	    executeMenu.add(dropItem);
	    return executeMenu;
	}
	
	private JPanel initVersionMenu() throws URISyntaxException, IOException {
		JPanel infoPanel = new JPanel();
		JLabel infoLabel = new JLabel("Version " + TOAST_VERSION);
		
		ImageIcon infoButtonImg = PanelHelper.createImageIcon(this,"info_icon_15.png");
		final URI uri = new URI(TOAST_VERSION_URL);
		JButton infoButton = new JButton(infoButtonImg);
		infoButton.setToolTipText(TOAST_VERSION_URL);
		infoButton.setBorder(null);
		infoButton.setOpaque(false);
		infoButton.setContentAreaFilled(false);
		infoButton.setBorderPainted(false);
		infoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				open(uri);
	      }
		});

		infoPanel.add(infoButton);
		infoPanel.add(infoLabel);
		return infoPanel;
	}

	private static void open(URI uri) {
		if (Desktop.isDesktopSupported()) {
			try {
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) { LOG.warn(e.getMessage()); }
		} else { LOG.warn(CommonMessages.DESKTOP_NOT_SUPPORTED); }
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
		return assertProperty(DriverFactory.getDriverValue()) &&
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

	private void helpingListener(ActionEvent e){
		MailHelpPanel p = helpPanelProvider.get();
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
