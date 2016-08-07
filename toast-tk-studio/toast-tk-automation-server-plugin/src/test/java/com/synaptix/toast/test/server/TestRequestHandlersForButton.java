package com.synaptix.toast.test.server;

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

import io.toast.tk.adapter.swing.component.SwingButtonElement;
import io.toast.tk.core.adapter.AutoSwingType;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.core.net.request.InitInspectionRequest;
import io.toast.tk.core.net.response.ValueResponse;

public class TestRequestHandlersForButton {

	static ButtonTestFrame buttonFieldFrame;
	FakeConnection connection;

	@BeforeClass
	public static void init() {
		buttonFieldFrame = new ButtonTestFrame();
	}

	@Before
	public void initGuiRepository() throws Exception {
		InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(InitRequestListener.class);
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
		String idRequest = "fake-button-request-id";
		CommandRequest buildGetInputValueRequest = SwingButtonElement.buildClickRequest(
			ButtonTestFrame.class.getName() + ":buttonField",
			AutoSwingType.button.name(),
			idRequest);
		buttonFieldFrame.requestFocus();
		SwingActionRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(SwingActionRequestListener.class);
		connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testClickResponse() {
		assertEquals(true, connection.result instanceof ValueResponse);
	}
	
	@Test
	public void testClickScreenshot() {
		assertNotNull(((ValueResponse) connection.result).b64ScreenShot);
	}

	@AfterClass
	public static void end() {
		buttonFieldFrame.dispose();
	}
}
