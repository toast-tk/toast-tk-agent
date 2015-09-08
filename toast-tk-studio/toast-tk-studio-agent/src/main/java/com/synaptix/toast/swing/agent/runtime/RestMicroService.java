package com.synaptix.toast.swing.agent.runtime;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

import com.esotericsoftware.minlog.Log;
import com.synaptix.toast.constant.Property;

public class RestMicroService extends Verticle {

	static final StartCommandHandler COMMAND_HANDLER = new StartCommandHandler();

	@Override
	public void start() {
		RouteMatcher matcher = new RouteMatcher();
		initRouteMatcher(matcher);
		vertx.createHttpServer().requestHandler(matcher).listen(Property.TOAST_AGENT_PORT);
	}

	private void initRouteMatcher(
		RouteMatcher matcher) {
		includeRusInitCommand(matcher);
		includeRusStartCommand(matcher);
		includeRusStopCommand(matcher);
	}

	private void includeRusStopCommand(
		RouteMatcher matcher) {
		matcher.get("/rus/stop", new Handler<HttpServerRequest>() {

			@Override
			public void handle(
				HttpServerRequest req) {
				COMMAND_HANDLER.stop();
				req.response().setStatusCode(200).end();
			}
		});
	}

	private void includeRusStartCommand(
		RouteMatcher matcher) {
		matcher.get("/rus/start", new Handler<HttpServerRequest>() {

			@Override
			public void handle(
				HttpServerRequest req) {
				try {
					COMMAND_HANDLER.start();
					req.response().setStatusCode(200).end();
				} catch (IllegalAccessException e) {
					Log.error(e.getMessage(), e);
					req.response().setStatusCode(501).end();
				}
			}
		});
	}

	private void includeRusInitCommand(
		RouteMatcher matcher) {
		matcher.get("/rus/init", new Handler<HttpServerRequest>() {

			@Override
			public void handle(
				HttpServerRequest req) {
				boolean ok = COMMAND_HANDLER.init();
				if(ok) {
					req.response().setStatusCode(200).end();
				}
				else {
					req.response().setStatusCode(404).end();
				}
			}
		});
	}
}