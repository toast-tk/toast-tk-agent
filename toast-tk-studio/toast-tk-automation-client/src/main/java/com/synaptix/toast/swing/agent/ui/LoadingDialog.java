package com.synaptix.toast.swing.agent.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LoadingDialog {

	public static JPanel buildLoadingDialog() {
		JPanel p1 = new JPanel(new GridBagLayout());
		p1.setOpaque(false);
		InputStream resourceAsStream = LoadingDialog.class.getClassLoader().getResourceAsStream("spiffygif.gif");
		try {
			p1.add(new JLabel(new ImageIcon(ImageIO.read(resourceAsStream))), new GridBagConstraints());
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		p1.setSize(100, 100);
		return p1;
	}
}