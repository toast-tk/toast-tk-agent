package com.synaptix.toast.adapter.swing.handler;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class SwingWidgetActionHandlerFactory {
	
	private static SwingWidgetActionHandlerFactory instance;
	
	private static Map<Class<? extends Component>, ISwingwidgetActionHandler> map;
	
	static{
		map = new HashMap<Class<? extends Component>, ISwingwidgetActionHandler>();
		map.put(JLabel.class, new JLabelActionHandler());
		map.put(JTextField.class, new JTextFieldActionHandler());
		map.put(JPasswordField.class, new JPasswordFieldActionHandler());
		map.put(JButton.class, new JButtonActionHandler());
		map.put(JCheckBox.class, new JCheckBoxActionHandler());
		map.put(JTextArea.class, new JTextAreaActionHandler());
		map.put(JTable.class, new JTableActionHandler());
	}
	
	private SwingWidgetActionHandlerFactory(){
		
	}
	
	public static SwingWidgetActionHandlerFactory getInstance(){
		if(instance == null){
			instance = new SwingWidgetActionHandlerFactory();
		}
		return instance;
	}

	public ISwingwidgetActionHandler getHandler(
		Component component) {
		if(map.get(component.getClass()) == null){
			for(Class<? extends Component> c: map.keySet()){
				if(c.isInstance(component)){
					return map.get(c);
				}
			}
		}
		return map.get(component.getClass());
	}

	public boolean hasHandlerFor(
		Class<? extends Component> component) {
		return map.containsKey(component.getClass());
	}
	
}
