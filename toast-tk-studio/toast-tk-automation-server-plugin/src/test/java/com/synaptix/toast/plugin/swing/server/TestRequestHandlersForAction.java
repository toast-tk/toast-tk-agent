package com.synaptix.toast.plugin.swing.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.test.TestSuiteHelper;
import com.synaptix.toast.test.server.mock.ButtonTestFrame;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.FakeSwingInspectionServer;

import io.toast.tk.adapter.swing.component.SwingButtonElement;
import io.toast.tk.core.adapter.AutoSwingType;
import io.toast.tk.core.agent.interpret.AWTCapturedEvent;
import io.toast.tk.core.agent.interpret.DefaultEventInterpreter;
import io.toast.tk.core.agent.interpret.IEventInterpreter.EventType;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.core.net.request.InitInspectionRequest;

public class TestRequestHandlersForAction {

	static ButtonTestFrame buttonFieldFrame;
	FakeConnection connection;
	SwingInspectionRecorder recorder;
	FakeSwingInspectionServer server;
	static DefaultEventInterpreter interpreter;

	@BeforeClass
	public static void init() {
		buttonFieldFrame = new ButtonTestFrame();
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
		CommandRequest buildGetInputValueRequest = SwingButtonElement.buildClickRequest(ButtonTestFrame.class.getName() + ":buttonField",AutoSwingType.button.name(),idRequest);
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
		assertEquals(event.getEventType(), EventType.BUTTON_CLICK);
		String onButtonClick = interpreter.onButtonClick(event);
		assertEquals(onButtonClick, "Cliquer sur le button *CLICK ME*");
	}

	@AfterClass
	public static void end() {
		buttonFieldFrame.dispose();
	}
}
