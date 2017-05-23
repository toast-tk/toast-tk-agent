package io.toast.tk.agent.record;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.web.record.WebRecorder;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class EventRecordTest {
	
	static FakeKryoServer server;
	static AgentConfigProvider configProvider;
	EventRecordBuilder builder;
	static WebRecorder webRecorder;
	
	@BeforeClass
	public static void before(){
		server = new FakeKryoServer();
		configProvider = new AgentConfigProvider();
		webRecorder = new WebRecorder(server, configProvider);		
	}
	
	@Before
	public void beforeEarch(){
		this.builder = new EventRecordBuilder();
		server.event = null;
	}
	
	@Test
	public void testNonEmptyRecordEvent(){
		WebEventRecord record_focus = builder.input().locator(".text").focusEvent().build();
		webRecorder.process(record_focus);
		Assert.assertNull(server.event);
	}
	
	@Test
	public void testInputRecordEvent(){
		WebEventRecord record_focus = builder.input().locator(".text").focusEvent().build();
		webRecorder.process(record_focus);
		WebEventRecord record_val_change = builder.input().locator(".text").changeEvent().val("checkout").build();
		webRecorder.process(record_val_change);
		Assert.assertEquals("checkout", server.event.getValue());
	}
	
	@Test
	public void testDirtyInputRecordEvent(){
		WebEventRecord record_focus = builder.input().locator(".text").focusEvent().build();
		webRecorder.process(record_focus);
		WebEventRecord record_focus2 = builder.input().locator(".text-2").focusEvent().build();
		webRecorder.process(record_focus2);
		WebEventRecord record_val_change = builder.input().locator(".text-2").changeEvent().val("checkout-2").build();
		webRecorder.process(record_val_change);
		Assert.assertEquals("checkout-2", server.event.getValue());
	}
	
	@Test
	@Ignore
	public void testLinkRecordEvent(){
		WebEventRecord record_click = builder.component("a").locator(".link").ofType("click").val("Click me").build();
		webRecorder.process(record_click);
		Assert.assertEquals("Click me", server.event.getValue());
	}
	
	@Test
	public void testButtonRecordEvent(){
		WebEventRecord record_click = builder.component("button").locator(".button").ofType("click").val("Click me").build();
		webRecorder.process(record_click);
		Assert.assertEquals("Click me", server.event.getValue());
	}
	
	@Test
	public void testSelectRecordEvent(){
		WebEventRecord record_click = builder.component("select").locator(".select").ofType("change").val("SelectedValue").build();
		webRecorder.process(record_click);
		Assert.assertEquals("SelectedValue", server.event.getValue());
	}
}
