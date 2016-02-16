package com.synaptix.toast.plugin.swing.agent.record;


import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.junit.Assert;
import org.junit.Test;


public class EventRecorderTest {
	
	@Test
	public void computeComponentNameTest(){
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		fileMenu.add(saveMenuItem);
		String componentName = AbstractEventRecorder.getComponentName(saveMenuItem);
		Assert.assertEquals("File / Save", componentName);
	}
	
	@Test
	public void computeSubComponentNameTest(){
		JMenu fileMenu = new JMenu("File");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem saveSubMenuItem = new JMenuItem("OK");
		fileMenu.add(saveMenuItem);
		saveMenuItem.add(saveSubMenuItem);
		String componentName = AbstractEventRecorder.getComponentName(saveSubMenuItem);
		Assert.assertEquals("File / Save / OK", componentName);
	}
	
}
