package com.synaptix.toast.agent.web;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.agent.guice.WebAgentModule;
import com.synaptix.toast.agent.ui.MainApp;
import com.synaptix.toast.agent.ui.NotificationManager;
import com.synaptix.toast.agent.web.rest.PingHandler;
import com.synaptix.toast.agent.web.rest.RecordHandler;
import com.synaptix.toast.agent.web.rest.StopHandler;
import com.synaptix.toast.core.annotation.craft.FixMe;

@FixMe(todo = "ensure we have firefow browser installed, use a factory")
public class RestRecorderService extends Verticle {

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);

	private MainApp app;
	private Injector injector;
	 
	@Override
	public void start() {
		LOG.info("Starting..");
		initInjectors();
		RouteMatcher matcher = initMatchers();
		
		try{
			String toastHome = app.getConfig().getToastHome();
			
			//SECURE ONE
			vertx.createHttpServer().requestHandler(matcher)
			.setSSL(true)
			.setKeyStorePath(toastHome + SystemUtils.FILE_SEPARATOR + "server-keystore.jks")
			.setKeyStorePassword("wibble").listen(4445);
			
			//PLAIN ONE
			vertx.createHttpServer().requestHandler(matcher).listen(4444);
			
			NotificationManager.showMessage("Web Agent - Active !").showNotification();
			LOG.info("Started !");
		}catch(Exception e){
			LOG.error(e);
		}
	}

	private RouteMatcher initMatchers() {
		RouteMatcher matcher = new RouteMatcher();
		matcher.options("/record/event", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				req.response().headers().add("Access-Control-Allow-Origin", "*");
				req.response().headers().add("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, POST");
				req.response().setStatusCode(200).end();
			}
		});
		matcher.post("/record/event", injector.getInstance(RecordHandler.class));
		matcher.get("/record/ping", new PingHandler());
		matcher.get("/record/stop", injector.getInstance(StopHandler.class));
		return matcher;
	}

	private void initInjectors() {
		this.injector = Guice.createInjector(new WebAgentModule());
		app = injector.getInstance(MainApp.class);
	}
	
}
