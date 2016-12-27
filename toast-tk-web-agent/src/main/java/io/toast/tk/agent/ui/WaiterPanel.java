package io.toast.tk.agent.ui;

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

	private JPanel mainPane;
	private long firstTime;
	private long scriptTime;
	private int scriptNumber = 0;
	private boolean interupted = false;
	private boolean done = false;
	private int iteration = 0, i = 0;
	
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
		scriptTime = (System.currentTimeMillis() - firstTime)/1000;
		
		if(name != null) {
			if(scriptNameLabel.getText() != null) {
				if(!scriptNameLabel.getText().contains(name)) {
					scriptNameLabel.setText(name);
					scriptNumber ++;
				}			
			}
			else {
				scriptNumber ++;
				scriptNameLabel.setText(name);
			}
		}
		
		scriptNumberLabel.setText(PanelHelper.numbToStr(scriptNumber));
		scriptNumberLabel.repaint();
		scriptNumberLabel.revalidate();
		scriptNameLabel.repaint();
		scriptNameLabel.revalidate();
		
		panIcon.repaint();
		panIcon.revalidate();
		
		timeLabel.setText(PanelHelper.secToHMS(scriptTime));
		timeLabel.repaint();
		timeLabel.revalidate();
		
		i++;
		if(i % 20 == 0) {
			iteration++;
		}
		secondMainPanel.setText(PanelHelper.addPoint(state,iteration));
		secondMainPanel.repaint();
		secondMainPanel.revalidate();
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

        mainPane = PanelHelper.createBasicPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.X_AXIS));
        buildLeftPanel();
        buildRightPanel();
        mainPane.add(leftPanel);
        mainPane.add(rightPanel);
        

		Image toast_logo = PanelHelper.createImage(this,"ToastLogo.png");
        
        // add the panel to this frame        
		this.setContentPane(mainPane);
		this.setIconImage(toast_logo);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private void buildLeftPanel() throws IOException {
		Dimension pD = new Dimension(300, 25);
		Dimension mD = new Dimension(250, 25);
		
		JLabel firstMainPanel = PanelHelper.createBasicJLabel("Script runner",PanelHelper.FONT_TITLE_2);
        firstMainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	    firstMainPanel.setPreferredSize(pD);
	    firstMainPanel.setMinimumSize(mD);
	    
	    secondMainPanel = PanelHelper.createBasicJLabel("In progress...",PanelHelper.FONT_TITLE_2);
	    secondMainPanel.setPreferredSize(pD);
	    secondMainPanel.setMinimumSize(mD);

		Image toast_logo = PanelHelper.createImage(this,"toast-loading.gif");
		panIcon = PanelHelper.createBasicPanel();
		panIcon.add(new JLabel(new ImageIcon(toast_logo)));
        panIcon.setLayout(new FlowLayout(FlowLayout.LEFT));

        leftPanel = PanelHelper.createBasicPanel(BoxLayout.PAGE_AXIS);
        leftPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        leftPanel.add(firstMainPanel);
        leftPanel.add(panIcon);
        leftPanel.add(secondMainPanel);

		pD = new Dimension(300, 350);
		mD = new Dimension(250, 300);
        leftPanel.setPreferredSize(pD);
        leftPanel.setMinimumSize(mD);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}
	
	private void buildRightPanel() throws IOException {
		int pHeight = 75;
		int mHeight = 50;
		
	    JPanel scriptPanel = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		
	    JPanel scriptNamePanel = PanelHelper.createBasicPanel("Script in progress : ", PanelHelper.FONT_TEXT_BOLD);
	    scriptNameLabel = PanelHelper.createBasicJLabel();
	    scriptNamePanel.setPreferredSize(new Dimension(250, pHeight));
	    scriptNamePanel.setMinimumSize(new Dimension(210, mHeight));
	    scriptNamePanel.add(scriptNameLabel);
	    
	    JPanel scriptNumberPanel = PanelHelper.createBasicPanel("N°", PanelHelper.FONT_TEXT_BOLD);
	    scriptNumberLabel = PanelHelper.createBasicJLabel(PanelHelper.numbToStr(scriptNumber));
	    scriptNumberPanel.setPreferredSize(new Dimension(50, pHeight));
	    scriptNumberPanel.setMinimumSize(new Dimension(40, mHeight));
	    scriptNumberPanel.add(scriptNumberLabel);

	    scriptPanel.add(scriptNumberPanel);
	    scriptPanel.add(scriptNamePanel);

	    JPanel timePanel = PanelHelper.createBasicPanel("Progress time : ", PanelHelper.FONT_TEXT_BOLD);
	    timeLabel = PanelHelper.createBasicJLabel(PanelHelper.secToHMS(scriptTime));
	    timePanel.setPreferredSize(new Dimension(300, pHeight));
	    timePanel.setMinimumSize(new Dimension(250, mHeight));
	    timePanel.add(timeLabel);
	    
		Image power_button = PanelHelper.createImage(this,"power_button.png");
	    JButton interputButton = new JButton(new ImageIcon(power_button));
	    interputButton.setToolTipText("Interruption of the script");
	    interputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!interupted) {
					int rep = JOptionPane.showConfirmDialog(null,
									"Do you want to interupt the script ?",
									"Interruption of the script",
									JOptionPane.YES_NO_OPTION);	
					if(rep == 0)	
						interupted = true;	
				}
				else {
					if(done) {
						dispose();
					}
				}
					
			}
		});
	    interputButton.setBorder(null);
	    interputButton.setMargin(new Insets(0, 0, 0, 0));
	    interputButton.setBackground(Color.WHITE);
	    interputButton.setPreferredSize(new Dimension(70, 70));
	    interputButton.setMinimumSize(new Dimension(60, 60));
	    interputButton.setAlignmentX(CENTER_ALIGNMENT);

        rightPanel = PanelHelper.createBasicPanel(BoxLayout.Y_AXIS);
        rightPanel.add(scriptPanel);
        rightPanel.add(timePanel);
        rightPanel.add(interputButton);

        rightPanel.setPreferredSize(new Dimension(300, pHeight * 2 + 70));
        rightPanel.setMaximumSize(new Dimension(250, mHeight * 2 + 60));
	}
	
	

}