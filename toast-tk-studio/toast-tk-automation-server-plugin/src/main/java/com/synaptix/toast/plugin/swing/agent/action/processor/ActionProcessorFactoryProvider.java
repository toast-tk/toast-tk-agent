package com.synaptix.toast.plugin.swing.agent.action.processor;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JMenu;

import com.synaptix.toast.plugin.swing.agent.action.processor.list.JComboBoxActionProcessorFactory;
import com.synaptix.toast.plugin.swing.agent.action.processor.menu.JMenuActionProcessorFactory;

public class ActionProcessorFactoryProvider {
	static Map<String, ActionProcessorFactory> providers = new HashMap<String, ActionProcessorFactory>();
	
	static{
		providers.put("list", new JComboBoxActionProcessorFactory());
		providers.put("menu", new JMenuActionProcessorFactory());
	}
	
	public static ActionProcessorFactory getFactory(Component component) {
		if(component instanceof JComboBox){
			return providers.get("list");
		}
		if(component instanceof JMenu){
			return providers.get("menu");
		}
		return null;
	}

}
