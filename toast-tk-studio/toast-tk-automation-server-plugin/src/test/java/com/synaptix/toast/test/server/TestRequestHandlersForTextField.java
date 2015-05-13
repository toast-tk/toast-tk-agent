/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 26 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.test.server;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.adapter.swing.SwingInputElement;
import com.synaptix.toast.core.adapter.AutoSwingType;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.plugin.swing.agent.listener.CommandRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.server.boot.Boot;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.TexfieldTestFrame;

public class TestRequestHandlersForTextField {
	
	static TexfieldTestFrame textFieldFrame;
	
	@BeforeClass
	public static void init(){
		textFieldFrame = new TexfieldTestFrame(); 
	}
	
	@Before
	public void initGuiRepository(){
		InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(InitRequestListener.class);
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
	}
	
	@Test
	public void testGettingResponse() {
		String idRequest = null;
		CommandRequest buildGetInputValueRequest = SwingInputElement.buildGetInputValueRequest(TexfieldTestFrame.class.getName()+":inputField", AutoSwingType.input.name(), idRequest);
		CommandRequestListener requestHandler =  TestSuiteHelper.getInjector().getInstance(CommandRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(true, connection.result instanceof ValueResponse);
	}

	@Test
	public void testSettingValue() {
		textFieldFrame.setTextValue("");
		CommandRequestListener requestHandler =  TestSuiteHelper.getInjector().getInstance(CommandRequestListener.class);
		FakeConnection connection = new FakeConnection();
		String value = "typed_value";
		textFieldFrame.setTextFocus();
		CommandRequest buildTypeInputValueRequest = new CommandRequest.CommandRequestBuilder(null)
		.with(null).ofType(null).sendKeys(value).build();
		requestHandler.received(connection, buildTypeInputValueRequest);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CommandRequest buildGetInputValueRequest = SwingInputElement.buildGetInputValueRequest(
				TexfieldTestFrame.class.getName()+":inputField", AutoSwingType.input.name(), "fake-id");
		requestHandler =  TestSuiteHelper.getInjector().getInstance(CommandRequestListener.class);
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(true, connection.result instanceof ValueResponse);
		assertEquals(value, ((ValueResponse)connection.result).value);
	}
	
	@Test
	public void testGettingInputValue() {
		String value = "test";
		textFieldFrame.setTextValue(value);
		String idRequest = "fake-id";
		CommandRequest buildGetInputValueRequest = SwingInputElement.buildGetInputValueRequest(TexfieldTestFrame.class.getName()+":inputField", AutoSwingType.input.name(), idRequest);
		CommandRequestListener requestHandler =  TestSuiteHelper.getInjector().getInstance(CommandRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(value, ((ValueResponse)connection.result).value);
	}
	
	@AfterClass
	public static void end(){
		textFieldFrame.dispose(); 
	}
}