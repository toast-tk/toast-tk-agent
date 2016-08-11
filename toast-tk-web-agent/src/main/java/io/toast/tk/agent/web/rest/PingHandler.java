package io.toast.tk.agent.web.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import io.toast.tk.agent.web.RestRecorderService;

public class PingHandler implements Handler<HttpServerRequest>{

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);
	
	@Override
	public void handle(HttpServerRequest event) {
		LOG.info("Alive ping check!");
		event.response().headers().add("Access-Control-Allow-Origin", "*");
		event.response().setStatusCode(200).end();
	}

}
