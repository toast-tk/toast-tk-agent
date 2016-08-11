package io.toast.tk.agent.ui;

import java.awt.Image;

import javax.swing.ImageIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.swingfx.twinkle.NotificationBuilder;
import ch.swingfx.twinkle.event.NotificationEvent;
import ch.swingfx.twinkle.event.NotificationEventAdapter;
import ch.swingfx.twinkle.style.INotificationStyle;
import ch.swingfx.twinkle.style.theme.DarkDefaultNotification;
import ch.swingfx.twinkle.window.Positions;
import io.toast.tk.agent.web.RestRecorderService;

public class NotificationManager {

	private static final Logger LOG = LogManager.getLogger(NotificationManager.class);
	
	public static NotificationBuilder showMessage(String message) {
		ImageIcon icon = new ImageIcon(RestRecorderService.class.getClassLoader().getResource("ToastLogo.png"));
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(48, 48,  java.awt.Image.SCALE_SMOOTH);
		INotificationStyle style = new DarkDefaultNotification().withWidth(400);
		return new NotificationBuilder().withStyle(style) 
				.withTitle("Toast Web Agent") 
				.withMessage(message)
				.withIcon(new ImageIcon(newimg)) 
				.withDisplayTime(3000) 
				.withPosition(Positions.SOUTH_EAST) 
				.withListener(new NotificationEventAdapter() { 
					public void closed(NotificationEvent event) {
						LOG.info("closed notification with UUID "
										+ event.getId());
					}
					public void clicked(NotificationEvent event) {
						LOG.info("clicked notification with UUID "
										+ event.getId());
					}
				});
	}

}
