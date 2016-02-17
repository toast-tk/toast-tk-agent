package com.synaptix.toast.test.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.MenuItem;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.event.MenuEvent;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.plugin.swing.server.SwingInspectionRecorder;
import com.synaptix.toast.test.TestSuiteHelper;

public class TestRecorder {

	SwingInspectionRecorder recorder;
	
	@BeforeClass
	public static void init() {
	}

	@Before
	public void initGuiRepository() {
		recorder = TestSuiteHelper.getInjector().getInstance(SwingInspectionRecorder.class);
	}
	
	@Test
	public void buttonClickRecordTest(){
		AWTCapturedEvent event = new AWTCapturedEvent("", "", "Connexion", JButton.class.getName(), null, 0);
		event.eventLabel = MouseEvent.class.getSimpleName();
		AWTCapturedEvent capturedEvent = recorder.liveExplore(Arrays.asList(event));
		assertEquals(capturedEvent.getEventType(), EventType.BUTTON_CLICK);
		assertEquals(capturedEvent.componentName, "Connexion");
	}
	
	@Test
	public void inputTypeRecordTest(){
		AWTCapturedEvent event = new AWTCapturedEvent("", "", "Login", JTextField.class.getName(), null, 0);
		event.eventLabel = KeyEvent.class.getSimpleName();
		
		AWTCapturedEvent closureEvent = new AWTCapturedEvent("", "", "Login", JTextField.class.getName(), "value", 0);
		closureEvent.eventLabel = "CausedFocusEvent<";
		
		AWTCapturedEvent unCapturedEvent = recorder.liveExplore(Arrays.asList(event));
		assertNull(unCapturedEvent);
		
		AWTCapturedEvent capturedEvent = recorder.liveExplore(Arrays.asList(closureEvent));
		assertEquals(capturedEvent.getEventType(), EventType.KEY_INPUT);
		assertEquals(capturedEvent.componentName, "Login");
		assertEquals(capturedEvent.businessValue, "value");
	}
	
	@Test
	public void inputTypeSequencedRecordTest(){
		AWTCapturedEvent event = new AWTCapturedEvent("", "", "Login", JTextField.class.getName(), null, 0);
		event.eventLabel = KeyEvent.class.getSimpleName();
		
		AWTCapturedEvent closureEvent = new AWTCapturedEvent("", "", "Login", JTextField.class.getName(), "value", 0);
		closureEvent.eventLabel = "CausedFocusEvent<";
		
		AWTCapturedEvent capturedEvent = recorder.liveExplore(Arrays.asList(event, closureEvent));
		assertEquals(capturedEvent.getEventType(), EventType.KEY_INPUT);
		assertEquals(capturedEvent.componentName, "Login");
		assertEquals(capturedEvent.businessValue, "value");
	}
	
	@Test
	public void menuRecordTest(){
		AWTCapturedEvent menuEvent = new AWTCapturedEvent("", "", "File", JMenu.class.getName(), null, 0);
		menuEvent.eventLabel = MouseEvent.class.getSimpleName();
		AWTCapturedEvent capturedEvent = recorder.liveExplore(Arrays.asList(menuEvent));
		assertEquals(capturedEvent.getEventType(), EventType.MENU_CLICK);
		assertEquals(capturedEvent.componentName, "File");
	}
	
	
	@Test
	public void menuItemRecordTest(){
		AWTCapturedEvent menuItemEvent = new AWTCapturedEvent("File", "", "Save", JMenuItem.class.getName(), null, 0);
		menuItemEvent.eventLabel = MouseEvent.class.getSimpleName();
		AWTCapturedEvent capturedEvent = recorder.liveExplore(Arrays.asList(menuItemEvent));
		assertEquals(capturedEvent.getEventType(), EventType.MENU_CLICK);
		assertEquals(capturedEvent.componentName, "Save");
	}
	
}
