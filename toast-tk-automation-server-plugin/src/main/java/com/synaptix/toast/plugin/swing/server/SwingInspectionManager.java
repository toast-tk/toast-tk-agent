package com.synaptix.toast.plugin.swing.server;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwingInspectionManager {
	private static SwingInspectionManager instance;
	private List<Container> containers;

	SwingInspectionManager() {
		containers = new ArrayList<Container>();
	}

	public synchronized Map<Object, String> getAllInstances() {
		Map<Object, String> items = new HashMap<Object, String>();
		for (Container container : containers) {
			synchronized (items) {
				try {
					items.putAll(getAllInstances(container));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return items;
	}

	public synchronized List<Component> getAllComponents() {
		List<Component> items = new ArrayList<Component>();
		for (Container container : containers) {
			synchronized (items) {
				try {
					items.addAll(getAllComponents(container));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return items;
	}

	/**
	 * Go through the gui hierarchy and return recursively the list of contained
	 * components
	 * 
	 * @param c
	 * @return
	 */
	public synchronized List<Component> getAllComponents(Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Container) {
				try {
					compList.addAll(getAllComponents((Container) comp));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return compList;
	}

	/**
	 * Go through the gui hierarchy at runtime to reverse the fields names
	 * mapped to related instance
	 * 
	 * TODO: restrict to a white list (swing.*, etc..)
	 * 
	 * @param c
	 * @return
	 */
	public synchronized Map<Object, String> getAllInstances(Container c) {
		Map<Object, String> compMap = new HashMap<Object, String>();
		for (Field field : c.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				Object propertyValue = field.get(c);
				try {
					compMap.put(propertyValue, c.getClass().getCanonicalName()
							+ ":" + field.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Component[] comps = c.getComponents();
		for (Component comp : comps) {
			for (Field field : comp.getClass().getDeclaredFields()) {
				try {
					field.setAccessible(true);
					Object propertyValue = field.get(comp);
					compMap.put(propertyValue,
							c.getName() + ":" + field.getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (comp instanceof Container) {
				compMap.putAll(getAllInstances((Container) comp));
			}
		}
		return compMap;
	}

	public synchronized static SwingInspectionManager getInstance() {
		if (instance == null) {
			instance = new SwingInspectionManager();
		}
		return instance;
	}

	public synchronized void addContainer(Object retVal) {
		if (isValidInstance(retVal)) {
			containers.add((Container) retVal);
		}
	}

	public synchronized boolean isValidInstance(Object retVal) {
		return retVal instanceof Container;
	}

	public synchronized void clearContainers() {
		containers.clear();
	}
}
