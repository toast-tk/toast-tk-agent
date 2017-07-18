package io.toast.tk.agent.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public abstract class AbstractFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected JButton buildIconButton(String str, Image image) {
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
