package com.synaptix.toast.fixture.swing;

import com.synaptix.toast.core.AutoSwingType;
import com.synaptix.toast.core.ISwingElement;

/**
 * return a web automation element based on its type
 * 
 * @author skokaina
 * 
 */
public class ElementFactory {

	public static SwingAutoElement getElement(ISwingElement e) {
		switch (e.getType()) {
		case button:
			return new SwingButtonElement(e);
		case input:
			return new SwingInputElement(e);
		case menu:
			return new SwingMenuElement(e);
		case menuitem:
			return new SwingMenuItemElement(e);
		case table:
			return new SwingTableElement(e);
		case date:
			return new SwingDateElement(e);
		case timeline:
			return new SwingSynTimeLineElement(e);
		case list:
			return new SwingListElement(e);
		case checkbox:
			return new SwingCheckBoxElement(e);
		default:
			return new DefaultSwingAutoElement(e);
		}

	}

	public static Class<? extends SwingAutoElement> getTypeClass(AutoSwingType e) {
		switch (e) {
		case button:
			return SwingButtonElement.class;
		case input:
			return SwingInputElement.class;
		case timeline:
			return SwingSynTimeLineElement.class;
		case menu:
			return SwingMenuElement.class;
		case menuitem:
			return SwingMenuItemElement.class;
		case table:
			return SwingTableElement.class;
		case list:
			return SwingListElement.class;
		case date:
			return SwingDateElement.class;
		case checkbox:
			return SwingCheckBoxElement.class;
		default:
			return DefaultSwingAutoElement.class;
		}

	}

	public static AutoSwingType getClassAutoType(Class<?> e) {
		if (e.equals(SwingButtonElement.class)) {
			return AutoSwingType.button;
		} else if (e.equals(SwingInputElement.class)){
			return AutoSwingType.input;
		} else if (e.equals(SwingSynTimeLineElement.class)){
			return AutoSwingType.timeline;
		}else if (e.equals(SwingMenuElement.class)){
			return AutoSwingType.menu;
		}else if (e.equals(SwingMenuItemElement.class)){
			return AutoSwingType.menuitem;
		}else if (e.equals(SwingTableElement.class)){
			return AutoSwingType.table;
		} else if (e.equals(SwingListElement.class)){
			return AutoSwingType.list;
		} else if (e.equals(SwingDateElement.class)){
			return AutoSwingType.date;
		} else if (e.equals(SwingCheckBoxElement.class)){
			return AutoSwingType.checkbox;
		}
		else{
			return AutoSwingType.other;
		}

	}
}
