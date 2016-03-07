package com.synaptix.toast.swing.agent.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.inspection.ISwingAutomationClient;
import com.synaptix.toast.swing.agent.constant.Resource;
import com.synaptix.toast.swing.agent.event.message.LoadingMessage;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.event.message.StatusMessage;
import com.synaptix.toast.swing.agent.event.message.StopLoadingMessage;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;

public class SwingInspectionFrame extends JFrame {

	private static final long serialVersionUID = -3089122099692525117L;

	private JDialog dialog;

	private JProgressBar progress;

	private final JPanel statusPanel;

	private JPanel container;

	private final JLabel statusMessageLabel;

	private final ProgressGlassPane glassPane;

	private String CONNECTED_TEXT = "Toast Automation Server - Connected";
	
	@Inject
	public SwingInspectionFrame(
		final ISwingAutomationClient serverClient,
		final HeaderPanel headerPanel,
		final CorpusPanel corpusPanel,
		final ProgressGlassPane progressGlassPane,
		final @StudioEventBus EventBus eventBus) {
		super("Toast Tk - Studio");
		setGlassPane(glassPane = progressGlassPane);
		eventBus.register(this);
		try {
			showSplashScreen();
			launchProgressBar();
		}
		catch(Exception e1) {
			e1.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(Resource.ICON_IMG);
		setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
		container = new JPanel();
		container.setLayout(new BorderLayout());
		container.add(headerPanel);

		statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusMessageLabel = new JLabel(serverClient.isConnected() ? CONNECTED_TEXT : "Offline");
		statusMessageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		statusPanel.add(statusMessageLabel);
		
		container.add(statusPanel, BorderLayout.SOUTH);
		setContentPane(container);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(
				WindowEvent e) {
				//serverClient.killServer();
				e.getWindow().dispose();
			}
		});
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		double width = defaultToolkit.getScreenSize().getWidth() / 2;
		double height = defaultToolkit.getScreenSize().getHeight() - 140;
		setMinimumSize(new Dimension(Double.valueOf(width).intValue(), Double.valueOf(height).intValue()));
	}

	private void launchProgressBar() {
		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
			@Override
			protected Void doInBackground()
				throws Exception {
				for(int i = 0; i < 100; i++) {
					Thread.sleep(100);// Simulate loading
					publish(i);// Notify progress
				}
				return Void.TYPE.newInstance();
			}

			@Override
			protected void process(
				List<Integer> chunks) {
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

	protected void showSplashScreen()
		throws MalformedURLException {
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

	@Subscribe
	public void handleServerConnexionStatus(
		SeverStatusMessage startUpMessage) {
		switch(startUpMessage.state) {
			case CONNECTED :
				statusMessageLabel.setText(CONNECTED_TEXT);
				stopLoading(new StopLoadingMessage(CONNECTED_TEXT));
				break;
			default :
				statusMessageLabel.setText("Offline");
				break;
		}
	}

	@Subscribe
	public void updateStatusMessage(
		StatusMessage status) {
		statusMessageLabel.setText(status.msg);
	}

	@Subscribe
	public void startLoading(
		final LoadingMessage lMsg) {
		if(!glassPane.isVisible() && lMsg.progress < 100) {
			glassPane.setVisible(true);
		}
		glassPane.setMessage(lMsg.msg);
		glassPane.setProgress(lMsg.progress);
		statusMessageLabel.setText(lMsg.msg);
	}

	@Subscribe
	public void stopLoading(
		final StopLoadingMessage lMsg) {
		glassPane.setMessage(lMsg.msg);
		statusMessageLabel.setText(lMsg.msg);
		glassPane.setVisible(false);
	}
}
