package com.synaptix.toast.plugin.synaptix.test;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.guice.ICustomFixtureHandler;
import com.synaptix.toast.plugin.synaptix.runtime.command.CenterCellsCommandRequestBuilder;
import com.synaptix.toast.plugin.synaptix.runtime.handler.SwingCustomWidgetHandler;

public class PluginTestCenterCells {

	static CenterCellsTestFrame centerCellsFrame;
	
	static ICustomFixtureHandler handler;
	
	public PluginTestCenterCells() {
		//this.handler = new SwingCustomWidgetHandler();
		//this.handler = PluginTestSuiteHelper.getInjector().getInstance(ICustomFixtureHandler.class);
	}
	
	public static CommandRequest buildGetInputCenterCellValueRequest(
			String locator, 
			String type, 
			final String requestId
	) {
		return new CenterCellsCommandRequestBuilder("fake-id").asCustomCommand("command").build();
		//return new CommandRequest.CommandRequestBuilder(requestId).with(locator).ofType(type).getValue().build();
	}
	
	@BeforeClass
	public static void init() {
		handler = new SwingCustomWidgetHandler();
		centerCellsFrame = new CenterCellsTestFrame(); 
	}
	
	public static void main(String[] args) {
		centerCellsFrame = new CenterCellsTestFrame();
	}
	
	@Before
	public void initGuiRepository() {
		
	}
	
	@Test
	public void testGettingResponse() {
		final String id = UUID.randomUUID().toString();
		final CommandRequest command = new CommandRequest.CommandRequestBuilder(id)
		.asCustomCommand("Ouvrir le menu sur (testCenterCellsPanel:POSTAL 6991) 03/02/15")
		.ofType("centerCells").build();
		handler.processCustomCall(command);
		PluginTestSuiteHelper.sleepInSeconds(1);
		assertEquals(Boolean.TRUE, Boolean.TRUE);
	}

	@Test
	public void testGettingInputValue() {
		/*String value = "test";
		centerCellsFrame.setActif(1, 1);
		String idRequest = "fake-id";
		CommandRequest buildGetInputValueRequest = SwingInputElement.buildGetInputValueRequest(TexfieldTestFrame.class.getName()+":inputField", AutoSwingType.input.name(), idRequest);
		CommandRequestListener requestHandler =  TestSuiteHelper.getInjector().getInstance(CommandRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		PluginTestSuiteHelper.sleepInSeconds(2);
		assertEquals(value, ((ValueResponse)connection.result).value);*/
	}
	
	@AfterClass
	public static void end() {
		centerCellsFrame.dispose(); 
	}
}
