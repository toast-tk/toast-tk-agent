package io.toast.tk.agent.ui.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.Component;

/**
 * Configuration panel
 */
public class PanelHelper {
	
	private static String fontName = "Verdana";
	public static Font FONT_TEXT_BOLD = new Font("SansSerif",Font.BOLD + Font.ITALIC,14);
	public static Font FONT_TEXT_ITALIC = new Font(fontName,Font.ITALIC,14);
	public static Font FONT_TEXT = new Font(fontName,Font.PLAIN,14);
	public static Font FONT_TITLE_3 = new Font(fontName,Font.BOLD,16);
	public static Font FONT_TITLE_2 = new Font(fontName,Font.BOLD,18);
	public static Font FONT_TITLE_1 = new Font(fontName,Font.BOLD,22);

	public static JPanel createBasicPanel() {
		JPanel panel = new JPanel();
		panel.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		panel.setBackground(Color.white);
		return panel;
	} 
	public static JPanel createBasicPanel(int boxLayout) {
		JPanel panel = createBasicPanel();
		panel.add(Box.createHorizontalGlue());
		panel.setLayout(new BoxLayout(panel, boxLayout));
		return panel;
	}
	public static JPanel createBasicPanel(String strKey, int boxLayout) {
		return createBasicPanel(strKey, boxLayout, PanelHelper.FONT_TITLE_3);
	}
	public static JPanel createBasicPanel(String strKey, int boxLayout, Font font) {
		JPanel panel = createBasicPanel(strKey, font);
		panel.setLayout(new BoxLayout(panel, boxLayout));
		
		return panel;
	}
	public static JPanel createBasicPanel(String strKey, Font font) {
		JPanel panel = createBasicPanel();
		panel.add(Box.createHorizontalGlue());
		panel.setBorder(BorderFactory.createTitledBorder(panel.getBorder(),
	    		strKey,TitledBorder.ABOVE_TOP,TitledBorder.CENTER, font));
		
		return panel;
	} 
	
	public static JLabel createBasicJLabel() {
		return createBasicJLabel(" ");
	}
	public static JLabel createBasicJLabel(String str) {
		return createBasicJLabel(str, PanelHelper.FONT_TITLE_3);
	}
	public static JLabel createBasicJLabel(String str, Font font) {
		JLabel label = new JLabel(str);
		label.setFont(font);
		return label;
	}

	public static ImageIcon createImageIcon(JFrame frame, String str) {
		URL url = frame.getClass().getClassLoader().getResource(str);
		return new ImageIcon(url);
	}
	
	public static Image createImage(Object frame, String str) throws IOException {
		InputStream stream = frame.getClass().getClassLoader().getResourceAsStream(str);
		return ImageIO.read(stream);
	}	
	
	public static String secToHms(long nb) {
		long min = nb/60;
		long rsec = nb%60;
		long hours = min/60;
		long rmin = min%60;
		return hours + " h " + rmin + " m " + rsec + " s";
	}
	
	public static String numbToStr(int nb) {
		if(nb < 10) {
			return "0" + nb;
		}
		else {
			return String.valueOf(nb);
		}
	}
	
	public static String addPoint(String state, int iteration) {
		String res = state;
		for(int i = 0; i < iteration % 4; i++) {
			res = res + ".";
		}
		return res;
	}
	
}