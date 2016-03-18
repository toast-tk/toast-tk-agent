package com.synaptix.toast.agent.web;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

import com.google.gson.Gson;
import com.synaptix.toast.agent.web.record.WebRecorder;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class RecordHandler implements Handler<HttpServerRequest>{
	
	final Gson gson = new Gson();
	private WebRecorder recorder;
	
	public RecordHandler(RestRecorderService service2) {
		this.recorder = new WebRecorder(service2.getServer());
	}

	@Override
	public void handle(HttpServerRequest req) {
		req.bodyHandler(new Handler<Buffer>() {
			@Override
			public void handle(Buffer buffer) {
				String eventJson = buffer.toString();
				WebEventRecord eventRecord = gson.fromJson(eventJson,WebEventRecord.class);
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
