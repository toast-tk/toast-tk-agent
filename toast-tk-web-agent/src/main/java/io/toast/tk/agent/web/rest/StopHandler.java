package io.toast.tk.agent.web.rest;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import com.google.inject.Inject;

import io.toast.tk.agent.web.BrowserManager;
import io.toast.tk.agent.web.IAgentServer;
import io.vertx.ext.web.RoutingContext;

import java.net.UnknownHostException;

public class StopHandler implements Handler<RoutingContext>{
	
	private BrowserManager browserManager;
	private IAgentServer agentServer;

	@Inject
	public StopHandler(BrowserManager browserManager, IAgentServer agentServer) {
		this.browserManager = browserManager;
		this.agentServer = agentServer;
	}

	@Override
	public void handle(RoutingContext rc) {
		if (browserManager.getDriver() != null) {
			browserManager.getDriver().close();
		}
		try {
			agentServer.unRegister();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}finally {
			rc.response().headers().add("Access-Control-Allow-Origin", "*");
			rc.response().setStatusCode(200).end();
			System.exit(0);
		}

	}

}
