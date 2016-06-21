package com.synaptix.toast.agent.web.rest;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;

import com.google.inject.Inject;
import com.synaptix.toast.agent.web.BrowserManager;
import com.synaptix.toast.agent.web.IAgentServer;

public class StopHandler implements Handler<HttpServerRequest>{
	
	private BrowserManager browserManager;
	private IAgentServer agentServer;

	@Inject
	public StopHandler(BrowserManager browserManager, IAgentServer agentServer) {
		this.browserManager = browserManager;
		this.agentServer = agentServer;
	}

	@Override
	public void handle(HttpServerRequest req) {
		if (browserManager.getDriver() != null) {
			browserManager.getDriver().close();
		}
		agentServer.unRegister();
		req.response().headers().add("Access-Control-Allow-Origin", "*");
		req.response().setStatusCode(200).end();
		System.exit(0);
	}

}
