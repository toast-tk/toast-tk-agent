package io.toast.tk.agent.web;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.TrustOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ErrorHandler;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.guice.WebAgentModule;
import io.toast.tk.agent.ui.MainApp;
import io.toast.tk.agent.ui.NotificationManager;
import io.toast.tk.agent.web.rest.PingHandler;
import io.toast.tk.agent.web.rest.RecordHandler;
import io.toast.tk.agent.web.rest.StopHandler;
import org.apache.logging.log4j.core.net.ssl.TrustStoreConfiguration;


public class RestRecorderService extends AbstractVerticle {

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);

	private Injector injector;
	private MainApp app;
	 
	@Override
	public void start(Future<Void> fut) {
		//LOG.info("Starting..");
		initInjectors();
		Router router = Router.router(vertx);
		router.route().failureHandler(errorHandler());
		initRouter(router);
		try{
			//SECURE ONE
			String keyPath = AgentConfig.getToastHome() + SystemUtils.FILE_SEPARATOR + "server-keystore.jks";
			HttpServerOptions options = new HttpServerOptions().setSsl(true).setKeyStoreOptions(new JksOptions()
							.setPath(keyPath)
							.setPassword("wibble"));
			vertx.createHttpServer(options).requestHandler(router::accept).listen(4445, this::handleResult);
			
			//PLAIN ONE
			vertx.createHttpServer().requestHandler(router::accept).listen(4444, this::handleResult);

			LOG.info("Started !");
		}catch(Exception e){
			LOG.error(e.getMessage(), e);
		}
	}

	private void  handleResult(AsyncResult<HttpServer> result){
		if (result.succeeded()) {
			NotificationManager.showMessage("Web Agent - Active !").showNotification();
		} else {
			NotificationManager.showMessage("Web Agent - Not able to start server, shutting down...").showNotification();
			System.exit(-1);
		}
	}

	private ErrorHandler errorHandler() {
		return ErrorHandler.create();
	}

	private void initRouter(Router router) {
		router.options("/api/record/event").handler(rc ->{
				rc.response().headers().add("Access-Control-Allow-Origin", "*");
				rc.response().headers().add("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, POST");
				rc.response().setStatusCode(200).end();
			}
		);
		router.route("/api/record/event*").handler(BodyHandler.create());
		router.post("/api/record/event").handler(injector.getInstance(RecordHandler.class));
		router.get("/api/record/ping").handler(new PingHandler());
		router.get("/api/record/stop").handler(injector.getInstance(StopHandler.class));
	}

	private void initInjectors() {
		this.injector = Guice.createInjector(new WebAgentModule());
		app = injector.getInstance(MainApp.class);
	}
	
}
