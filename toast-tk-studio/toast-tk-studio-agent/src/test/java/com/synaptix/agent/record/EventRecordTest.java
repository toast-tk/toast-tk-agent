package com.synaptix.agent.record;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.synaptix.toast.agent.web.record.WebRecorder;

import io.toast.tk.core.agent.interpret.WebEventRecord;

public class EventRecordTest {
	
	FakeKryoServer server;
	EventRecordBuilder builder;
	WebRecorder webRecorder;
	
	@Before
	public void before(){
		this.server = new FakeKryoServer();
		this.builder = new EventRecordBuilder();
		this.webRecorder = new WebRecorder(server);		
	}
	
	@Test
	public void testInputRecordEvent(){
		WebEventRecord record_focus = builder.input().locator(".text").focusEvent().build();
		webRecorder.append(record_focus);
		WebEventRecord record_val_change = builder.input().locator(".text").changeEvent().val("checkout").build();
		webRecorder.append(record_val_change);
		Assert.assertEquals("checkout", server.event.getValue());
	}
	
	@Test
	public void testLinkRecordEvent(){
		WebEventRecord record_click = builder.component("a").locator(".link").ofType("click").val("Click me").build();
		webRecorder.append(record_click);
		Assert.assertEquals("Click me", server.event.getValue());
	}
	
	@Test
	public void testButtonRecordEvent(){
		WebEventRecord record_click = builder.component("button").locator(".button").ofType("click").val("Click me").build();
		webRecorder.append(record_click);
		Assert.assertEquals("Click me", server.event.getValue());
	}
	
	@Test
	public void testSelectRecordEvent(){
		WebEventRecord record_click = builder.component("select").locator(".select").ofType("change").val("SelectedValue").build();
		webRecorder.append(record_click);
		Assert.assertEquals("SelectedValue", server.event.getValue());
	}
}
