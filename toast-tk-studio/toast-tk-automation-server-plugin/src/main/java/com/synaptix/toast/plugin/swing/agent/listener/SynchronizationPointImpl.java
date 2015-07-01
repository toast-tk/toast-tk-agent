
package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JFrame;

import com.synaptix.toast.core.annotation.craft.FixMe;


/**
 * RUS specific synchronization point 
 *
 */

@FixMe(todo = "move in synaptix plugin")
public class SynchronizationPointImpl implements ISynchronizationPoint {

	JFrame superFrame = null;

	@Override
	public boolean hasToWait() {
		JFrame currentFrame = getFrame();
		if(currentFrame == null){
			return false;
		}
		String context = currentFrame.getAccessibleContext().getAccessibleDescription();
		if (isLoadingContext(context)) {
			if (isThereAFocusedDialog()) {
				return false;
			}
			return true;
		}
		return false;
	}

	public JFrame getFrame() {
		if (superFrame == null || !superFrame.isVisible()) {
			Window[] windows = Window.getWindows();
			for (Window window : windows) {
				if (window instanceof JFrame) {
					JFrame foundFrame = (JFrame) window;
					if (foundFrame.isVisible()) {
						this.superFrame = foundFrame;
						break;
					}
				}
			}
		}
		return superFrame;
	}

	private boolean isThereAFocusedDialog() {
		Window[] windows = Window.getWindows();
		for (Window window : windows) {
			if (window instanceof JDialog) {
				JDialog dialog = (JDialog) window;
				if (dialog.isFocused()) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isLoadingContext(String accessibleDescription) {
		return "babyProtection".equalsIgnoreCase(accessibleDescription);
	}
}
