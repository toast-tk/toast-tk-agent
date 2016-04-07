package com.synaptix.agent.record;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.synaptix.toast.agent.web.record.WebRecorder;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

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
		WebEventRecord record_focus = builder.component("text").target(".text").ofType("focus").withValue("check").build();
		webRecorder.append(record_focus);
		WebEventRecord record_blur = builder.component("text").target(".text").ofType("blur").withValue("checkout").build();
		webRecorder.append(record_blur);
		Assert.assertEquals("checkout", server.event.getValue());
	}
	
	@Test
	public void testLinkRecordEvent(){
		WebEventRecord record_click = builder.component("a").target(".link").ofType("click").withValue("Click me").build();
		webRecorder.append(record_click);
		Assert.assertEquals("Click me", server.event.getValue());
	}
	
	@Test
	public void testButtonRecordEvent(){
		WebEventRecord record_click = builder.component("button").target(".button").ofType("click").withValue("Click me").build();
		webRecorder.append(record_click);
		Assert.assertEquals("Click me", server.event.getValue());
	}
	
	@Test
	public void testSelectRecordEvent(){
		WebEventRecord record_click = builder.component("select").target(".select").ofType("change").withValue("SelectedValue").build();
		webRecorder.append(record_click);
		Assert.assertEquals("SelectedValue", server.event.getValue());
	}
}
