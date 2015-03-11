package com.synaptix.toast.plugin.synaptix.runtime.helper;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public final class MouseHelper {

	private MouseHelper() {

	}

	public static boolean isLeftClick(final MouseEvent e) {
		return SwingUtilities.isLeftMouseButton(e);
	}

	public static boolean isRightClick(final MouseEvent e) {
		return SwingUtilities.isRightMouseButton(e);
	}

	public static boolean isMiddleClick(final MouseEvent e) {
		return SwingUtilities.isMiddleMouseButton(e);
	}

	public static boolean isSimpleClick(final MouseEvent e) {
		return isNbClick(e, 1);
	}

	public static boolean isDoubleClick(final MouseEvent e) {
		return isNbClick(e, 2);
	}

	private static boolean isNbClick(final MouseEvent e, final int nbClick) {
		return nbClick == e.getClickCount();
	}

	public static boolean isLeftDoubleClick(final MouseEvent e) {
		return SwingUtilities.isLeftMouseButton(e) && isDoubleClick(e);
	}

	public static boolean isRightDoubleClick(final MouseEvent e) {
		return SwingUtilities.isRightMouseButton(e) && isDoubleClick(e);
	}

	public static boolean isMiddleDoubleClick(final MouseEvent e) {
		return SwingUtilities.isMiddleMouseButton(e) && isDoubleClick(e);
	}

	public static boolean isSimpleLeftClick(final MouseEvent e) {
		return SwingUtilities.isLeftMouseButton(e) && isSimpleClick(e);
	}

	public static boolean isSimpleRightClick(final MouseEvent e) {
		return SwingUtilities.isRightMouseButton(e) && isSimpleClick(e);
	}

	public static boolean isSimpleMiddleClick(final MouseEvent e) {
		return SwingUtilities.isMiddleMouseButton(e) && isSimpleClick(e);
	}

	public static boolean isMousePressedEvent(final MouseEvent e) {
		return e.getID() == MouseEvent.MOUSE_PRESSED;
	}

	public static boolean isLeftAltClick(final MouseEvent e) {
		return SwingUtilities.isLeftMouseButton(e) && e.isAltDown();
	}

	public static boolean isLeftShiftClick(final MouseEvent e) {
		return SwingUtilities.isLeftMouseButton(e) && e.isShiftDown();
	}

	public static boolean isLeftDoubleClickAndNotAltDown(final MouseEvent e) {
		return isLeftDoubleClick(e) && !e.isAltDown();
	}
}