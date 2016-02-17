package com.synaptix.toast.plugin.swing.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.adapter.swing.component.SwingButtonElement;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.agent.interpret.AWTCapturedEvent;
import com.synaptix.toast.core.agent.interpret.DefaultEventInterpreter;
import com.synaptix.toast.core.agent.interpret.IEventInterpreter.EventType;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.test.TestSuiteHelper;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.FakeSwingInspectionServer;
import com.synaptix.toast.test.server.mock.JRadioButtonTestFrame;

public class TestRequestHandlersForRaddioButton {

	static JRadioButtonTestFrame buttonFieldFrame;
	static DefaultEventInterpreter interpreter;
	FakeConnection connection;
	SwingInspectionRecorder recorder;
	FakeSwingInspectionServer server;

	@BeforeClass
	public static void init() throws Exception {
		buttonFieldFrame = new JRadioButtonTestFrame();
		interpreter = new DefaultEventInterpreter();
	}

	@Before
	public void initGuiRepository() throws Exception {
		InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(InitRequestListener.class);
		this.recorder = TestSuiteHelper.getInjector().getInstance(SwingInspectionRecorder.class);
		this.server = new FakeSwingInspectionServer();
		this.recorder.setCommandServer(this.server);
		this.recorder.startRecording();
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
		String idRequest = "fake-button-request-id";
		CommandRequest buildGetInputValueRequest = SwingButtonElement.buildClickRequest(JRadioButtonTestFrame.class.getName() + ":jRadioButton",AutoSwingType.radio.name(),idRequest);
		buttonFieldFrame.requestFocus();
		SwingActionRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(SwingActionRequestListener.class);
		this.connection = new FakeConnection();
		requestHandler.received(this.connection, buildGetInputValueRequest);
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testClickRecord() {
		AWTCapturedEvent event = this.server.getEvent();
		assertNotNull(event);
		assertEquals(event.getEventType(), EventType.RADIO_CLICK);
		String onButtonClick = interpreter.onButtonClick(event);
		assertEquals(onButtonClick, "Cliquer sur le button *Check Me !*");
	}

	@AfterClass
	public static void end() {
		buttonFieldFrame.dispose();
	}
}
