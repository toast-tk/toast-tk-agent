package io.toast.tk.agent.ui.panels;

import io.toast.tk.agent.run.TestPageRunner;
import io.toast.tk.agent.run.TestRunner;
import io.toast.tk.agent.ui.utils.PanelHelper;
import io.toast.tk.runtime.AbstractScenarioRunner;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Waiter panel
 */
public class WaiterPanel extends AbstractFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7445278086983713584L;

	private static final Logger LOG = LogManager.getLogger(WaiterPanel.class);
	
	private TestRunner testrunner;
	
	private long firstTime;
	private long scriptTime;
	private int scriptNumber = 0;
	private boolean interupted = false;
	private boolean done = false;
	private int iteration = 0, iTemp = 0;
	
	private int successNumber = 0;
	private int failureNumber = 0;
	
	private JPanel leftPanel, rightPanel, panIcon;
	private JLabel timeLabel, successLabel, errorLabel;
	private JLabel scriptNameLabel, scriptNumberLabel, secondMainPanel;
	private JLabel toastImageLabel;
	
	private List<ImageIcon> toastImage;
	private long lastCurrentTime = System.currentTimeMillis();
	private int imageIteration = 0;
	
	public WaiterPanel(TestRunner testrunner) throws IOException {
		super();
		this.testrunner = testrunner;
		
		buildContentPanel();

		this.firstTime= System.currentTimeMillis();
		this.scriptTime= System.currentTimeMillis() - firstTime;
	    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);        
         
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
	}
	
	public boolean isInterupted() {
		return this.interupted;
	}
	
	public void setScript(String name, String state) {
		updateScriptValue(name);
		
		updateLabel(scriptNumberLabel, PanelHelper.numbToStr(scriptNumber));
		
		updateImage();
		
		panIcon.repaint();
		panIcon.revalidate();

		setSuccesNumber();
		updateLabel(timeLabel, PanelHelper.secToHms(scriptTime));
		updateLabel(successLabel, String.valueOf(successNumber));
		updateLabel(errorLabel, String.valueOf(failureNumber));
				
		updateLabel(secondMainPanel, PanelHelper.addPoint(state,iteration));
	}
	
	public void updateScriptValue(String name) {
		scriptTime = (System.currentTimeMillis() - firstTime)/1000;
		
		if(name != null) {
			if(scriptNameLabel.getText() != null) {
				if(!scriptNameLabel.getText().contains(name)) {
					updateLabel(scriptNameLabel, name);
					scriptNumber ++;
				}			
			}
			else {
				scriptNumber ++;
				scriptNameLabel.setText(name);
			}
		}
		
		iTemp++;
		if(iTemp % 20 == 0) {
			iteration++;
		}
	}

	public void updateLabel(JLabel label, String name) {
		label.setText(name);
		label.repaint();
		label.revalidate();
	}

	private void updateImage() {
		toastImageLabel.setIcon(this.getToastIcon());
		toastImageLabel.repaint();
		toastImageLabel.revalidate();
	}
	
	private void setSuccesNumber() {
		TestPageRunner scenarioRunner = testrunner.getTestPageRunner();
		if(scenarioRunner instanceof AbstractScenarioRunner) {
			io.toast.tk.runtime.TestRunner runner = ((AbstractScenarioRunner) scenarioRunner).getTestRunner();
			if(runner != null) {
				successNumber = runner.getSuccessNumber();
				failureNumber = runner.getFailureNumber();
			}
		}
	}
	
	public void stop() {
		secondMainPanel.setText("Done.");
		interupted = true;
		done = true;
		secondMainPanel.repaint();
		secondMainPanel.revalidate();
	    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);  
	    sleepAndDispose();
	}
	
	private void sleepAndDispose() {
		int threeMinute = 3*60*1000;
		try {
			Thread.sleep(threeMinute);
		} catch (InterruptedException e) {
			LOG.debug(e.getMessage());
		}
		this.dispose();
	}
	
	private void buildContentPanel() throws IOException {
        JPanel mainPane = PanelHelper.createBasicJPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
        buildLeftPanel();
        buildRightPanel();
        mainPane.add(leftPanel);
        mainPane.add(rightPanel);
        
		Image toastLogo = PanelHelper.createImage(this,"ToastLogo.png");
        
        // add the panel to this frame        
		this.setContentPane(mainPane);
		this.setIconImage(toastLogo);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void buildLeftPanel() throws IOException {
		buildImages();
		
		Dimension prefDim = new Dimension(300, 25);
		Dimension moyDim = new Dimension(250, 25);
		
		JLabel firstMainPanel = buildLeftPanelLabel("Script runner", prefDim, moyDim);	    
	    secondMainPanel = buildLeftPanelLabel("In progress...", prefDim, moyDim);

	    toastImageLabel = new JLabel(this.getToastIcon());
	    
		panIcon = PanelHelper.createBasicJPanel();
		panIcon.add(toastImageLabel);
        panIcon.setLayout(new FlowLayout(FlowLayout.LEFT));

        leftPanel = PanelHelper.createBasicJPanel(BoxLayout.PAGE_AXIS);
        leftPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        leftPanel.add(firstMainPanel);
        leftPanel.add(panIcon);
        leftPanel.add(secondMainPanel);

        leftPanel.setPreferredSize(new Dimension(300, 350));
        leftPanel.setMinimumSize(new Dimension(250, 300));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}
		
	private JLabel buildLeftPanelLabel(String str, Dimension prefDim, Dimension moyDim) {
		JLabel firstMainPanel = PanelHelper.createBasicJLabel(str,PanelHelper.FONT_TITLE_2);
        firstMainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    firstMainPanel.setPreferredSize(prefDim);
	    firstMainPanel.setMinimumSize(moyDim);
	    return firstMainPanel;
	}
	
	private void buildRightPanel() throws IOException {
		int prefHeight = 75;
		int moyHeight = 50;
		
	    scriptNameLabel = PanelHelper.createBasicJLabel();
	    JPanel scriptNamePanel = buildRightlPanelPanel("Script in progress : ", scriptNameLabel, 250, prefHeight, 210, moyHeight);
	    scriptNumberLabel = PanelHelper.createBasicJLabel();
	    JPanel scriptNumberPanel = buildRightlPanelPanel("N°", scriptNumberLabel, 50, prefHeight, 40, moyHeight);
	    JPanel scriptPanel = PanelHelper.createBasicJPanel(BoxLayout.LINE_AXIS);
	    scriptPanel.add(scriptNumberPanel);
	    scriptPanel.add(scriptNamePanel);

	    timeLabel = PanelHelper.createBasicJLabel(PanelHelper.secToHms(scriptTime));
	    JPanel timePanel = buildRightlPanelPanel("Progress time : ", timeLabel, 300, prefHeight, 250, moyHeight);
	    
	    successLabel = PanelHelper.createBasicJLabel("0");
	    JPanel successPanel = buildRightlPanelPanel("Success number : ", successLabel, 300, prefHeight, 250, moyHeight);
	    errorLabel = PanelHelper.createBasicJLabel("0");
	    JPanel errorPanel = buildRightlPanelPanel("Error number : ", errorLabel, 300, prefHeight, 250, moyHeight);
	    
        rightPanel = PanelHelper.createBasicJPanel(BoxLayout.Y_AXIS);
        rightPanel.add(scriptPanel);
        rightPanel.add(timePanel);
        rightPanel.add(successPanel);
        rightPanel.add(errorPanel);
        rightPanel.add(buildRightPanelButton());
        rightPanel.setPreferredSize(new Dimension(300, prefHeight * 4 + 70));
        rightPanel.setMaximumSize(new Dimension(250, moyHeight * 4 + 60));
	}
	
	private JPanel buildRightlPanelPanel(String title, JLabel label, int prefHeightX, int prefHeightY, int moyHeightX, int moyHeightY) {
		JPanel scriptNumberPanel = PanelHelper.createBasicJPanel(title, PanelHelper.FONT_TEXT_BOLD);
	    scriptNumberPanel.setPreferredSize(new Dimension(prefHeightX, prefHeightY));
	    scriptNumberPanel.setMinimumSize(new Dimension(moyHeightX, moyHeightY));
	    scriptNumberPanel.add(label);
	    return scriptNumberPanel;
	}
	
	private JButton buildRightPanelButton() throws IOException {
		Image powerButton = PanelHelper.createImage(this,"power_button.png");
		JButton interputButton = buildIconButton("Strop script", powerButton);
	    interputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				interupt();
			}
		});
	    return interputButton;
	}
	
	private void interupt() {
		if(!interupted) {
			int rep = JOptionPane.showConfirmDialog(null,
							"Do you want to interrupt the script ?",
							"Interruption of the script",
							JOptionPane.YES_NO_OPTION);	
			if(rep == 0) {
				interupted = true;	
			}
		}
		else {
			if(done) {
				dispose();
			}
		}
	}
	
	private void buildImages() throws IOException {
		toastImage = new ArrayList<ImageIcon>();
		toastImage.add(PanelHelper.createImageIcon(this,"ToastLogo_1.png"));
		toastImage.add(PanelHelper.createImageIcon(this,"ToastLogo_2.png"));
		toastImage.add(PanelHelper.createImageIcon(this,"ToastLogo_3.png"));
		toastImage.add(PanelHelper.createImageIcon(this,"ToastLogo_4.png"));
		toastImage.add(PanelHelper.createImageIcon(this,"ToastLogo_5.png"));
		toastImage.add(PanelHelper.createImageIcon(this,"ToastLogo_6.png"));
	}
	
	private ImageIcon getToastIcon() {
		long time = System.currentTimeMillis();
		if(time - lastCurrentTime >= 1000) {
			lastCurrentTime = System.currentTimeMillis();
			if(imageIteration >= toastImage.size()-1) {
				imageIteration = 0;
			} else {
				imageIteration ++;
			}
		}
		return toastImage.get(imageIteration);
	}
}