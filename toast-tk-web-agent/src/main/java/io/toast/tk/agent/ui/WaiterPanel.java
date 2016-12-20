package io.toast.tk.agent.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;


/**
 * Runner panel
 */
public class WaiterPanel extends JFrame {

	private JPanel mainPane;
	private long firstTime;
	private long scriptTime;
	private int scriptNumber = 0;
	private boolean interupted = false;
	private int iteration = 0;
	
	private JPanel leftPanel, rightPanel;
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
		iteration++;
		scriptTime = (System.currentTimeMillis() - firstTime)/1000;
		
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
		
		scriptNumberLabel.setText(PanelHelper.numbToStr(scriptNumber));
		scriptNumberLabel.repaint();
		scriptNumberLabel.revalidate();
		scriptNameLabel.repaint();
		scriptNameLabel.revalidate();
		
		timeLabel.setText(PanelHelper.secToHMS(scriptTime));
		timeLabel.repaint();
		timeLabel.revalidate();

		secondMainPanel.setText(PanelHelper.addPoint(state,iteration));
		secondMainPanel.repaint();
		secondMainPanel.revalidate();
	}
	
	public void stop() {
		secondMainPanel.setText("Done.");
		interupted = true;
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
        

		InputStream toast_logoInput = this.getClass().getClassLoader().getResourceAsStream("ToastLogo.png");
		Image toast_logo = ImageIO.read(toast_logoInput);
        
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
		
		JLabel firstMainPanel = new JLabel("Script runner");
	    firstMainPanel.setFont(new Font("Verdana",Font.BOLD,22));
	    firstMainPanel.setPreferredSize(pD);
	    firstMainPanel.setMinimumSize(mD);
	    
	    secondMainPanel = new JLabel("In progress...");
	    secondMainPanel.setFont(new Font("Verdana",Font.BOLD,22));
	    secondMainPanel.setPreferredSize(pD);
	    secondMainPanel.setMinimumSize(mD);

		InputStream toast_logoInput = this.getClass().getClassLoader().getResourceAsStream("toast-loading.png");
		Image toast_logo = ImageIO.read(toast_logoInput);
		JPanel panIcon = PanelHelper.createBasicPanel();
		panIcon.add(new JLabel(new ImageIcon(toast_logo)));

        leftPanel = PanelHelper.createBasicPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));

        firstMainPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panIcon.setLayout(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        leftPanel.add(firstMainPanel);
        leftPanel.add(panIcon);
        leftPanel.add(secondMainPanel);

		pD = new Dimension(300, 300);
		mD = new Dimension(250, 250);
        leftPanel.setPreferredSize(pD);
        leftPanel.setMinimumSize(mD);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}
	
	private void buildRightPanel() throws IOException {
		int pHeight = 75;
		int mHeight = 50;
		
	    JPanel scriptPanel = PanelHelper.createBasicPanel(BoxLayout.LINE_AXIS);
		
	    JPanel scriptNamePanel = PanelHelper.createBasicPanel(
	    		"Script in progress : ", new Font("Verdana",Font.PLAIN,15));
	    scriptNameLabel = new JLabel(" ");
	    scriptNameLabel.setFont(new Font("Verdana",Font.PLAIN,12));
	    scriptNamePanel.setPreferredSize(new Dimension(250, pHeight));
	    scriptNamePanel.setMinimumSize(new Dimension(210, mHeight));
	    scriptNamePanel.add(scriptNameLabel);
	    
	    JPanel scriptNumberPanel = PanelHelper.createBasicPanel(
	    		"N°", new Font("Verdana",Font.PLAIN,15));
	    scriptNumberLabel = new JLabel(PanelHelper.numbToStr(scriptNumber));
	    scriptNumberLabel.setFont(new Font("Verdana",Font.PLAIN,12));
	    scriptNumberPanel.setPreferredSize(new Dimension(50, pHeight));
	    scriptNumberPanel.setMinimumSize(new Dimension(40, mHeight));
	    scriptNumberPanel.add(scriptNumberLabel);

	    scriptPanel.add(scriptNumberPanel);
	    scriptPanel.add(scriptNamePanel);

	    JPanel timePanel = PanelHelper.createBasicPanel(
	    		"Progress time : ", new Font("Verdana",Font.PLAIN,15));
	    timeLabel = new JLabel(PanelHelper.secToHMS(scriptTime));
	    timeLabel.setFont(new Font("Verdana",Font.PLAIN,12));
	    timePanel.setPreferredSize(new Dimension(300, pHeight));
	    timePanel.setMinimumSize(new Dimension(250, mHeight));
	    timePanel.add(timeLabel);
	    
	    InputStream power_buttonInput = this.getClass().getClassLoader().getResourceAsStream("power_button.png");
		Image power_button = ImageIO.read(power_buttonInput);
	    JButton interputButton = new JButton(new ImageIcon(power_button));
	    interputButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!interupted) {
					int rep = JOptionPane.showConfirmDialog(null,
							"Do you want to interupt the script ?",null,JOptionPane.YES_NO_OPTION);	
					if(rep == 0)	
						interupted = true;	
				}
			}
		});
	    interputButton.setBorder(null);
	    interputButton.setMargin(new Insets(0, 0, 0, 0));
	    interputButton.setBackground(Color.WHITE);
	    interputButton.setPreferredSize(new Dimension(70, 70));
	    interputButton.setMinimumSize(new Dimension(60, 60));

        rightPanel = PanelHelper.createBasicPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(scriptPanel);
        rightPanel.add(timePanel);
        rightPanel.add(interputButton);

        rightPanel.setPreferredSize(new Dimension(300, pHeight * 2 + 70));
        rightPanel.setMaximumSize(new Dimension(250, mHeight * 2 + 60));
	}
	
	

}