package io.toast.tk.agent.ui;

import io.toast.tk.agent.ui.utils.PanelHelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Waiter panel
 */
@SuppressWarnings("serial")
public class WaiterPanel extends JFrame {

	private long firstTime;
	private long scriptTime;
	private int scriptNumber = 0;
	private boolean interupted = false;
	private boolean done = false;
	private int iteration = 0, iTemp = 0;
	
	private JPanel leftPanel, rightPanel, panIcon;
	private JLabel timeLabel, scriptNameLabel, scriptNumberLabel, secondMainPanel;
	
	public WaiterPanel() throws IOException {
		super();
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
		
		panIcon.repaint();
		panIcon.revalidate();
		
		updateLabel(timeLabel, PanelHelper.secToHms(scriptTime));
		
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
	
	public void stop() {
		secondMainPanel.setText("Done.");
		interupted = true;
		done = true;
		secondMainPanel.repaint();
		secondMainPanel.revalidate();
	    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);   
	}
	
	private void buildContentPanel() throws IOException {
        JPanel mainPane = PanelHelper.createBasicPanel();
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
		Dimension prefDim = new Dimension(300, 25);
		Dimension moyDim = new Dimension(250, 25);
		
		JLabel firstMainPanel = buildLeftPanelLabel("Script runner", prefDim, moyDim);	    
	    secondMainPanel = buildLeftPanelLabel("In progress...", prefDim, moyDim);

		Image toastLogo = PanelHelper.createImage(this,"toast-loading.gif");
		panIcon = PanelHelper.createBasicPanel();
		panIcon.add(new JLabel(new ImageIcon(toastLogo)));
        panIcon.setLayout(new FlowLayout(FlowLayout.LEFT));

        leftPanel = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
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
	    JPanel scriptPanel = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
	    scriptPanel.add(scriptNumberPanel);
	    scriptPanel.add(scriptNamePanel);

	    timeLabel = PanelHelper.createBasicJLabel(PanelHelper.secToHms(scriptTime));
	    JPanel timePanel = buildRightlPanelPanel("Progress time : ", timeLabel, 300, prefHeight, 250, moyHeight);
	    
        rightPanel = PanelHelper.createBasicPanel(BoxLayout.Y_AXIS);
        rightPanel.add(scriptPanel);
        rightPanel.add(timePanel);
        rightPanel.add(buildRightPanelButton());
        rightPanel.setPreferredSize(new Dimension(300, prefHeight * 2 + 70));
        rightPanel.setMaximumSize(new Dimension(250, moyHeight * 2 + 60));
	}
	
	private JPanel buildRightlPanelPanel(String title, JLabel label, int prefHeightX, int prefHeightY, int moyHeightX, int moyHeightY) {
		JPanel scriptNumberPanel = PanelHelper.createBasicPanel(title, PanelHelper.FONT_TEXT_BOLD);
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
	
	private JButton buildIconButton(String str, Image image) {
		JButton interputButton = new JButton(new ImageIcon(image));
	    interputButton.setToolTipText(str);
	    interputButton.setBorder(null);
	    interputButton.setMargin(new Insets(0, 0, 0, 0));
	    interputButton.setBackground(Color.WHITE);
	    interputButton.setPreferredSize(new Dimension(70, 70));
	    interputButton.setMinimumSize(new Dimension(60, 60));
	    interputButton.setAlignmentX(CENTER_ALIGNMENT);
	    return interputButton;
	}
}