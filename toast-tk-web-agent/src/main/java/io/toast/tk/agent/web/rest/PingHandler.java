package io.toast.tk.agent.web.rest;

import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.vertx.core.Handler;


public class PingHandler implements Handler<RoutingContext>{

	private static final Logger LOG = LogManager.getLogger(PingHandler.class);
	
	@Override
	public void handle(RoutingContext rc) {
		LOG.info("Alive ping check!");
		rc.response().headers().add("Access-Control-Allow-Origin", "*");
		rc.response().setStatusCode(200).end();
	}

}
