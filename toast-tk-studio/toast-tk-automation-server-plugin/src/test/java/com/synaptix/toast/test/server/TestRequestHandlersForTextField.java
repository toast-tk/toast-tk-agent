package com.synaptix.toast.test.server;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.adapter.swing.component.SwingInputElement;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.SwingActionRequestListener;
import com.synaptix.toast.test.TestSuiteHelper;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.TexfieldTestFrame;

public class TestRequestHandlersForTextField {

	static TexfieldTestFrame textFieldFrame;

	@BeforeClass
	public static void init() {
		textFieldFrame = new TexfieldTestFrame();
	}

	@Before
	public void initGuiRepository() {
		InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(InitRequestListener.class);
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
	}

	@Test
	public void testGettingResponse() {
		String idRequest = null;
		CommandRequest buildGetInputValueRequest = SwingInputElement.buildGetInputValueRequest(
			TexfieldTestFrame.class.getName() + ":inputField",
			AutoSwingType.input.name(),
			idRequest);
		SwingActionRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(
			SwingActionRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(true, connection.result instanceof ValueResponse);
	}

	@Test
	public void testSettingValue() {
		textFieldFrame.setTextValue("");
		SwingActionRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(
			SwingActionRequestListener.class);
		FakeConnection connection = new FakeConnection();
		String value = "type";
		textFieldFrame.setTextFocus();
		typeValueInTexfield(requestHandler, connection, value);
		readValueFromTextfield(connection);
		assertEquals(true, connection.result instanceof ValueResponse);
		assertEquals(value, ((ValueResponse) connection.result).value);
	}

	private void readValueFromTextfield(
		FakeConnection connection) {
		SwingActionRequestListener requestHandler;
		CommandRequest buildGetInputValueRequest = SwingInputElement.buildGetInputValueRequest(
			TexfieldTestFrame.class.getName() + ":inputField", AutoSwingType.input.name(), "fake-id");
		requestHandler = TestSuiteHelper.getInjector().getInstance(SwingActionRequestListener.class);
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(1000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void typeValueInTexfield(
		SwingActionRequestListener requestHandler,
		FakeConnection connection,
		String value) {
		CommandRequest buildTypeInputValueRequest = new CommandRequest.CommandRequestBuilder(null)
			.with(null).ofType(null).sendKeys(value).build();
		requestHandler.received(connection, buildTypeInputValueRequest);
		try {
			Thread.sleep(2000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGettingInputValue() {
		String value = "test";
		textFieldFrame.setTextValue(value);
		String idRequest = "fake-id";
		CommandRequest buildGetInputValueRequest = SwingInputElement.buildGetInputValueRequest(
			TexfieldTestFrame.class.getName() + ":inputField",
			AutoSwingType.input.name(),
			idRequest);
		SwingActionRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(
			SwingActionRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(2000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(value, ((ValueResponse) connection.result).value);
	}

	@AfterClass
	public static void end() {
		textFieldFrame.dispose();
	}
}
