package com.synaptix.toast.swing.agent.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.synaptix.toast.swing.agent.web.RestRecorderService;

public class SysTrayHook {

	private static final Logger LOG = LogManager.getLogger(SysTrayHook.class);
	

	public static void init(){
		TrayIcon trayIcon = null;
		if (SystemTray.isSupported()) {
		    ActionListener listener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		        	System.exit(-1);
		        }
		    };
		    SystemTray tray = SystemTray.getSystemTray();
		    InputStream resourceAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("toast.png");
		    try {
				Image image = ImageIO.read(resourceAsStream);
			    PopupMenu popup = new PopupMenu();
			    MenuItem defaultItem = new MenuItem("kill agent");
			    defaultItem.addActionListener(listener);
			    popup.add(defaultItem);
			    trayIcon = new TrayIcon(image, "Toast Demo", popup);
			} catch (IOException e1) {
				LOG.info(e1);
			}
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(listener);
		    try {
		        tray.add(trayIcon);
		    } catch (AWTException e) {
		    	LOG.info(e);
		    }
		}
	}
}
