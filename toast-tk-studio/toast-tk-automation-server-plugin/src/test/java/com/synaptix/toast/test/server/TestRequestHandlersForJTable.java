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

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.core.net.request.TableCommandRequest;
import com.synaptix.toast.core.net.request.TableCommandRequestQueryCriteria;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.plugin.swing.agent.listener.CommandRequestListener;
import com.synaptix.toast.plugin.swing.agent.listener.InitRequestListener;
import com.synaptix.toast.plugin.swing.server.SwingInspectionServer;
import com.synaptix.toast.test.server.mock.FakeConnection;
import com.synaptix.toast.test.server.mock.TableTestFrame;

public class TestRequestHandlersForJTable {
	static String FRAME_TABLE_FIELD_NAME = "table";
	static TableTestFrame tableTestFrame;
	
	@BeforeClass
	public static void init(){
		tableTestFrame = new TableTestFrame(); 
	}
	
	@Before
	public void initGuiRepository(){
		InitRequestListener initRequestHandler = TestSuiteHelper.getInjector().getInstance(InitRequestListener.class);
		initRequestHandler.received(new FakeConnection(), new InitInspectionRequest());
	}

	@Test
	public void testGettingInputValue() {
		String idRequest = "fake-id";
		String expectedRow = "2";
		String locator = TableTestFrame.class.getName()+":"+FRAME_TABLE_FIELD_NAME;
    	List<TableCommandRequestQueryCriteria> tableCriteria = new ArrayList<TableCommandRequestQueryCriteria>();
    	tableCriteria.add(new TableCommandRequestQueryCriteria("Column One", "Row2-Column1"));
    	tableCriteria.add(new TableCommandRequestQueryCriteria("Column Three", "Row2-Column3"));
		TableCommandRequest buildGetInputValueRequest = buildMultiCriteriaRequest(tableCriteria, locator, idRequest);
		CommandRequestListener requestHandler = TestSuiteHelper.getInjector().getInstance(CommandRequestListener.class);
		FakeConnection connection = new FakeConnection();
		requestHandler.received(connection, buildGetInputValueRequest);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertEquals(expectedRow, ((ValueResponse)connection.result).value);
	}
	
	public static TableCommandRequest buildMultiCriteriaRequest(List<TableCommandRequestQueryCriteria> tableCriteria, String locator, String idRequest) {
		return new TableCommandRequest.TableCommandRequestBuilder(idRequest).find(tableCriteria).with(locator).build();
	}

	
	@AfterClass
	public static void end(){
		tableTestFrame.dispose(); 
	}
}
