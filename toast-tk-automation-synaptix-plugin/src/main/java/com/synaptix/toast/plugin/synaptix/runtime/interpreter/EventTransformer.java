package com.synaptix.toast.plugin.synaptix.runtime.interpreter;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import org.apache.commons.codec.StringEncoderComparator;
import org.apache.commons.codec.language.Soundex;

public final class EventTransformer {

	public static final String NO_ACTION = "Aucune action definie sur";

	public static final String OUVRIR_LE_MENU_SUR = "Ouvrir le menu sur";

	public static final String OUVRIR_LE_MENU_SUR_SOUNDEX = new Soundex().encode(OUVRIR_LE_MENU_SUR);

	public static final String DOUBLE_CLIQUER_SUR = "Double-cliquer sur";

	public static final String DOUBLE_CLIQUER_SUR_SOUNDEX = new Soundex().encode(DOUBLE_CLIQUER_SUR);

	public static final String CLIQUER_SUR = "Cliquer sur";
	
	public static final String GET = "get";
	
	public static final String SET = "set";

	public static final String CLIQUER_SUR_SOUNDEX = new Soundex().encode(CLIQUER_SUR);

	public static final String[] DICTIONARY = new String[] {OUVRIR_LE_MENU_SUR, DOUBLE_CLIQUER_SUR, CLIQUER_SUR};

	public static final String[] DICTIONARY_SOUNDEX = new String[] {
		OUVRIR_LE_MENU_SUR_SOUNDEX,
		DOUBLE_CLIQUER_SUR_SOUNDEX,
		CLIQUER_SUR_SOUNDEX
	};

	public static void main(final String[] args) {
		System.out.println("1 "+OUVRIR_LE_MENU_SUR);
		System.out.println("2 "+OUVRIR_LE_MENU_SUR_SOUNDEX);
		System.out.println("3 "+OUVRIR_LE_MENU_SUR.equals(OUVRIR_LE_MENU_SUR_SOUNDEX));

		final StringEncoderComparator sCompare = new StringEncoderComparator(new Soundex());

		System.out.println("1 test "+sCompare.compare( CLIQUER_SUR, "AbstractOperationLocomotive dans le planning des locomotives sur la locomotive BB7823" ));
		System.out.println("2 test "+sCompare.compare( CLIQUER_SUR, "Cliquer sur AbstractOperationLocomotive dans le planning des locomotives sur la locomotive BB7823" ));
		System.out.println("3 test "+sCompare.compare( CLIQUER_SUR, "AbstractOperationLocomotive dans le planning des locomotives sur la locomotive BB7823 Cliquer sur" ));
		System.out.println("4 test "+sCompare.compare( CLIQUER_SUR, "AbstractOperationLocomotive Cliquer sur dans le planning des locomotives sur la locomotive BB7823"));
		System.out.println("5 test "+sCompare.compare( "BB7823", "AbstractOperationLocomotive Cliquer sur dans le planning des locomotives sur la locomotive BB7823"));
		System.out.println("6 test "+sCompare.compare( "BB7823", "BB7823 AbstractOperationLocomotive Cliquer sur dans le planning des locomotives sur la locomotive"));
		System.out.println("7 test "+sCompare.compare( "BB7823", "AbstractOperationLocomotive Cliquer sur BB7823 dans le planning des locomotives sur la locomotive"));
	}

	public static boolean isInterestingEvent(final String eventData) {
		return eventData != null && !eventData.contains(NO_ACTION);
	}
	
	private EventTransformer() {

	}

	public static String eventToAction(final MouseEvent event) {
		return eventToAction(event.getID(), event.getClickCount(), event.getModifiers());
	}

	public static String eventToAction(
			final int id,
			final int clickCount,
			final int modifiers
	) {
		if(isMouseReleasedEvent(id) && clickCount == 1) {
			return CLIQUER_SUR;
		}
		else if(isMouseReleasedEvent(id) && clickCount == 2) {
			return DOUBLE_CLIQUER_SUR;
		}
		else if(isMouseReleasedEvent(id) && clickCount == 1 && (modifiers & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
			return OUVRIR_LE_MENU_SUR;
		}
		return NO_ACTION;
	}

	private static boolean isMouseReleasedEvent(final int id) {
		return id == MouseEvent.MOUSE_RELEASED;
	}

	public static int actionToEventId(final String action) {
		if(IsCliquerSur(action)) {
			return MouseEvent.MOUSE_RELEASED;
		}
		else if(isDoubleCliquer(action)) {
			return MouseEvent.MOUSE_RELEASED;
		}
		else if(isOuvrirMenu(action)) {
			return MouseEvent.MOUSE_RELEASED;
		}
		return -1;
	}

	public static int actionToEventClickCount(final String action) {
		if(IsCliquerSur(action)) {
			return 1;
		}
		else if(isDoubleCliquer(action)) {
			return 2;
		}
		else if(isOuvrirMenu(action)) {
			return 1;
		}
		return -1;
	}

	private static boolean isOuvrirMenu(final String action) {
		return OUVRIR_LE_MENU_SUR.equals(action);
	}

	private static boolean isDoubleCliquer(final String action) {
		return DOUBLE_CLIQUER_SUR.equals(action);
	}

	private static boolean IsCliquerSur(final String action) {
		return CLIQUER_SUR.equals(action);
	}

	public static int actionToEventModifiers(final String action) {
		if(IsCliquerSur(action)) {
			return InputEvent.BUTTON1_MASK;
		}
		else if(isDoubleCliquer(action)) {
			return InputEvent.BUTTON1_MASK;
		}
		else if(isOuvrirMenu(action)) {
			return InputEvent.BUTTON3_MASK;
		}
		return -1;
	}
}