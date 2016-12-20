package io.toast.tk.agent.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;


/**
 * Configuration panel
 */
public class PanelHelper {

	public static JPanel createBasicPanel() {
		JPanel panel = new JPanel();
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
		return createBasicPanel(strKey, boxLayout, new Font("Arial",Font.BOLD,14));
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

	public static String secToHMS(long nb) {
		long m = nb/60;
		long rs = nb%60;
		long h = m/60;
		long rm = m %60;
		return h + " h " + rm + " m " + rs + " s";
	}
	
	public static String numbToStr(int nb) {
		if(nb < 10) {
			return "0" + nb;
		}
		else 
			return String.valueOf(nb);
	}
	
	public static String addPoint(String state, int iteration) {
		String res = state;
		for(int i = 0; i < iteration % 4; i++) {
			res = res + ".";
		}
		return res;
	}
	
}