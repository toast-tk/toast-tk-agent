package com.synaptix.toast.swing.agent.web;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

public class StopHandler implements Handler<HttpServerRequest>{
	
	private RestRecorderService service;

	public StopHandler(RestRecorderService service2) {
		this.service = service2;
	}

	@Override
	public void handle(HttpServerRequest req) {
		if (service.getDriver() != null) {
			service.getDriver().close();
		}
		service.getServer().close();
		req.response().headers().add("Access-Control-Allow-Origin", "*");
		req.response().setStatusCode(200).end();
		System.exit(0);
	}

}
