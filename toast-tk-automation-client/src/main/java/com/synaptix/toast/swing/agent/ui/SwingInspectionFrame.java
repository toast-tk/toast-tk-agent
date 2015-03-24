package com.synaptix.toast.swing.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.automation.runner.SutRunnerAsExec;
import com.synaptix.toast.automation.utils.Resource;
import com.synaptix.toast.core.Property;
import com.synaptix.toast.core.inspection.ISwingInspectionClient;
import com.synaptix.toast.swing.agent.IToastClientApp;
import com.synaptix.toast.swing.agent.event.message.LoadingMessage;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.event.message.StatusMessage;
import com.synaptix.toast.swing.agent.event.message.StopLoadingMessage;

/**
 * Created by Sallah Kokaina on 12/11/2014.
 */
public class SwingInspectionFrame extends JFrame {

	private static final long serialVersionUID = -3089122099692525117L;
	private static final Logger LOG = LogManager.getLogger(SwingInspectionFrame.class);
	private final SwingInspectorPanel inspectorPanel;
	private JDialog dialog;
	private JProgressBar progress;
	private final JPanel statusPanel;
	private final JLabel statusMessageLabel;
	private final ProgressGlassPane glassPane;
	private JMenuItem initButton;
	private JMenuItem runtimePropertyButton;
	private final IToastClientApp app;
	private final SutRunnerAsExec runtime;
	
	@Inject
	public SwingInspectionFrame(
			final ISwingInspectionClient serverClient, 
			final SwingInspectorPanel swingInspectorPanel,
			final SwingInspectionRecorderPanel recorderPanel,
			final ProgressGlassPane progressGlassPane, 
			final EventBus eventBus,
			final SutRunnerAsExec runtime, 
			final IToastClientApp app
	) {
		super("Toast Tk - Studio");
		setGlassPane(glassPane = progressGlassPane);
		this.runtime = runtime;
		this.app = app;
		eventBus.register(this);
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			showSplashScreen();
			launchProgressBar();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Resource.ICON_IMG);
		setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
		this.inspectorPanel = swingInspectorPanel;

		getContentPane().setLayout(new BorderLayout());

		JTabbedPane tabPan = new JTabbedPane(JTabbedPane.TOP);
		tabPan.addTab("", new ImageIcon(Resource.ICON_CAMERA_IMG), recorderPanel, "Record your actions as a scenario");
		tabPan.addTab("", new ImageIcon(Resource.ICON_SEARCH_IMG), inspectorPanel, "Inspect the SUT interface widgets");
		getContentPane().add(tabPan);

		statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));

		statusMessageLabel = new JLabel("Offline");
		statusMessageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		statusPanel.add(statusMessageLabel);
		this.add(statusPanel, BorderLayout.SOUTH);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				serverClient.killServer();
				e.getWindow().dispose();
			}
		});

		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		double width = defaultToolkit.getScreenSize().getWidth() / 3;
		double height = defaultToolkit.getScreenSize().getHeight() - 40;
		setMinimumSize(new Dimension(Double.valueOf(width).intValue(), Double.valueOf(height).intValue()));
	
		createMenuBar();
		initActions();
	}

	
	private void initActions() {
		this.initButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						disableInitButton();
						app.initProperties();
						String runtimeType = app.getRuntimeType(); 
						String command = app.getRuntimeCommand(); 
						String agentType = app.getAgentType();
						try {
							publish();
							runtime.init(runtimeType, command, agentType, true);
							Desktop.getDesktop().open(new File(Property.TOAST_HOME_DIR));
							app.stopProgress("Done !");
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (SAXException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ParserConfigurationException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void process(List<Void> chunks) {
						super.process(chunks);
						app.startProgress("Starting SUT..");
					}
					
					
					
				};
				worker.execute();
			}
		});
		
		this.runtimePropertyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				app.openConfigDialog();
				
			}
		});
	}


	private void createMenuBar() {
        JMenuBar menubar = new JMenuBar();

        JMenu startMenu = new JMenu("Start");
        startMenu.setIcon(new ImageIcon(Resource.ICON_POWER_16PX_IMG));
        
        initButton = new JMenuItem("Download & Init SUT Bat");
        initButton.setMnemonic(KeyEvent.VK_F);
        initButton.setBackground(Color.green);
        initButton.setToolTipText("download the system under test, and open a bat to start it's inspection & recording..");
        initButton.setIcon(new ImageIcon(Resource.ICON_POWER_16PX_IMG));

        startMenu.add(initButton);
        
        runtimePropertyButton = new JMenuItem("Settings");
        runtimePropertyButton.setIcon(new ImageIcon(Resource.ICON_CONF_16PX_2_IMG));
        runtimePropertyButton.setToolTipText("Edit runtime properties..");
        runtimePropertyButton.setMnemonic(KeyEvent.VK_F);
        
        startMenu.add(runtimePropertyButton);
        menubar.add(startMenu);

        setJMenuBar(menubar);
    }

	
	private void launchProgressBar() {
		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				for (int i = 0; i < 100; i++) {
					Thread.sleep(100);// Simulate loading
					publish(i);// Notify progress
				}
				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				progress.setValue(chunks.get(chunks.size() - 1));
			}

			@Override
			protected void done() {
				showFrame();
				hideSplashScreen();
			}

		};
		worker.execute();
	}

	protected void hideSplashScreen() {
		dialog.setVisible(false);
		dialog.dispose();
	}

	private void showFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setExtendedState(Frame.MAXIMIZED_BOTH);
				setVisible(true);
			}
		});
	}

	protected void showSplashScreen() throws MalformedURLException {
		dialog = new JDialog((Frame) null);
		dialog.setModal(false);
		dialog.setUndecorated(true);
		URL resource = SwingInspectionFrame.class.getClassLoader().getResource("Spalsh.png");
		ImageIcon image = new ImageIcon(resource);
		Image scaledInstance = image.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
		JLabel background = new JLabel(new ImageIcon(scaledInstance));
		background.setLayout(new BorderLayout());
		dialog.add(background);
		progress = new JProgressBar();
		background.add(progress, BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public void addInspectComponent(String componentLocator) {
		inspectorPanel.addInspectComponent(componentLocator);
	}

	public void flush() {
		inspectorPanel.flush();
	}

	@Subscribe
	public void handleServerConnexionStatus(SeverStatusMessage startUpMessage) {
		switch (startUpMessage.state) {
		case CONNECTED:
			statusMessageLabel.setText("Toast Automation Server - Connected");
			stopLoading(new StopLoadingMessage("Toast Automation Server - Connected"));
			disableInitButton();
			break;
		default:
			statusMessageLabel.setText("Offline");
			enableInitButton();
			break;
		}
	}

	@Subscribe
	public void updateStatusMessage(StatusMessage status) {
		statusMessageLabel.setText(status.msg);
	}

	@Subscribe
	public void startLoading(final LoadingMessage lMsg) {
		if (!glassPane.isVisible()) {
			glassPane.setVisible(true);
		}
		glassPane.setMessage(lMsg.msg);
		glassPane.setProgress(lMsg.progress);
		statusMessageLabel.setText(lMsg.msg);
	}

	@Subscribe
	public void stopLoading(StopLoadingMessage lMsg) {
		glassPane.setMessage(lMsg.msg);
		statusMessageLabel.setText(lMsg.msg);
		glassPane.setVisible(false);
	}
	
	private void enableInitButton() {
		this.initButton.setBackground(Color.GREEN);
		this.initButton.setEnabled(true);
	}

	private void disableInitButton() {
		this.initButton.setBackground(Color.RED);
		this.initButton.setEnabled(false);
	}
}
