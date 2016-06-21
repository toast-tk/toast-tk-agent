package com.synaptix.toast.agent.web.rest;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.synaptix.toast.agent.web.IAgentServer;
import com.synaptix.toast.agent.web.RestRecorderService;
import com.synaptix.toast.agent.web.record.WebRecorder;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class RecordHandler implements Handler<HttpServerRequest>{
	
	final Gson gson = new Gson();
	private WebRecorder recorder;
	private IAgentServer agentServer;
	
	@Inject
	public RecordHandler(IAgentServer agentServer) {
		this.agentServer = agentServer;
		this.recorder = new WebRecorder(agentServer);
	}

	@Override
	public void handle(HttpServerRequest req) {
		req.bodyHandler(new Handler<Buffer>() {
			@Override
			public void handle(Buffer buffer) {
				String eventJson = buffer.toString();
				WebEventRecord eventRecord = gson.fromJson(eventJson,WebEventRecord.class);
				String pageName = agentServer.getCurrentPageName() != null ? agentServer.getCurrentPageName() : eventRecord.parent;
				eventRecord.setParent(pageName);
				processEvent(eventRecord);
			}
		});
		req.response().headers().add("Access-Control-Allow-Origin", "*");
		req.response().setStatusCode(200).end();
	}
	
	public void processEvent(WebEventRecord record) {
		recorder.append(record);
	}

}
