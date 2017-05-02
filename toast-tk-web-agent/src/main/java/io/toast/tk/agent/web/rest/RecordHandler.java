package io.toast.tk.agent.web.rest;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.toast.tk.agent.web.UriChangeListener;
import io.toast.tk.agent.web.record.WebRecorder;
import io.toast.tk.core.agent.interpret.WebEventRecord;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class RecordHandler implements Handler<RoutingContext> {
	
	final Gson gson = new Gson();
	private WebRecorder recorder;
	private UriChangeListener uriChangeListener;
	
	@Inject
	public RecordHandler(WebRecorder webRecorder, UriChangeListener uriChangeListener) {
		this.uriChangeListener= uriChangeListener;
		this.recorder = webRecorder;
	}
	
	public void processEvent(WebEventRecord record) {
		recorder.process(record);
	}

	@Override
	public void handle(RoutingContext routingContext) {
		final WebEventRecord eventRecord = Json.decodeValue(routingContext.getBodyAsString(),
				WebEventRecord.class);
		String pageName = uriChangeListener.getLocation() != null ?
				uriChangeListener.getLocation() :
				eventRecord.getParent();
		eventRecord.setParent(pageName);
		processEvent(eventRecord);
		routingContext.response().headers().add("Access-Control-Allow-Origin", "*");
		routingContext.response().setStatusCode(200).end();
	}

}
