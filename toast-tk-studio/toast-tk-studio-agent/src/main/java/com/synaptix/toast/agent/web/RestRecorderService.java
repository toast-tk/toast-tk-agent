package com.synaptix.toast.agent.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.agent.guice.WebAgentModule;
import com.synaptix.toast.agent.ui.NotificationManager;
import com.synaptix.toast.agent.ui.MainApp;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.core.annotation.craft.FixMe;

@FixMe(todo = "ensure we have firefow browser installed, use a factory")
public class RestRecorderService extends Verticle {

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);

	private WebDriver driver;
	private boolean isStarted;
	private Thread thread;
	private IAgentServer server;
	private MainApp app;
	private String currentPageName;
	private JFrame frmOpt; 

	 
	@Override
	public void start() {
		LOG.info("Starting..");
		Injector injector = Guice.createInjector(new WebAgentModule());
		app = injector.getInstance(MainApp.class);
		this.server = new AgentServerImpl(app);
		RouteMatcher matcher = new RouteMatcher();
		matcher.options("/record/event", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				req.response().headers().add("Access-Control-Allow-Origin", "*");
				req.response().headers().add("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept, POST");
				req.response().setStatusCode(200).end();
			}
		});
		matcher.post("/record/event", new RecordHandler(this));
		matcher.get("/record/ping", new Handler<HttpServerRequest>() {
			@Override
			public void handle(HttpServerRequest req) {
				LOG.info("Alive ping check!");
				req.response().headers().add("Access-Control-Allow-Origin", "*");
				req.response().setStatusCode(200).end();
			}
		});
		matcher.get("/record/stop", new StopHandler(this));
		try{
			String toastHome = app.getConfig().getToastHome();
			
			//SECURE ONE
			vertx.createHttpServer().requestHandler(matcher)
			.setSSL(true)
			.setKeyStorePath(toastHome + SystemUtils.FILE_SEPARATOR + "server-keystore.jks")
			.setKeyStorePassword("wibble").listen(4445);
			
			//PLAIN ONE
			vertx.createHttpServer().requestHandler(matcher).listen(4444);
			
			app.setService(this);
			LOG.info("Started !");
			NotificationManager.showMessage("Web Agent - Active !").showNotification();
		}catch(Exception e){
			LOG.error(e);
		}
	}

	private Thread buildReinjectionThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						LOG.error(e1.getMessage(), e1);
					}
					if (driver != null && isStarted) {
						final WebElement body = driver.findElement(By.tagName("body"));
						final String attribute = body.getAttribute("recording");
						if (!"true".equals(attribute)) {
							LOG.info("Re-Injecting recorder !");
							try {
								injectRecordScript();
							} catch (IOException e) {
								LOG.error(e.getMessage(), e);
							}
						}
					}
				}
			}
		}, "ALIVE-RECORDER-CHECKER");
	}

	private void injectRecordScript() throws IOException {
		JavascriptExecutor executor = ((JavascriptExecutor) driver);
		InputStream resourceAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("recorder.js");
		String script = IOUtils.toString(resourceAsStream);
		String subscript = "var script = window.document.createElement('script'); script.innerHTML=\""
				+ script.replace("\r\n", "").replace("\n", "")
				+ "\";window.document.head.appendChild(script);";
		executor.executeScript(subscript);
		String recordingStatus = "window.document.body.setAttribute('recording','true');";
		executor.executeScript(recordingStatus);
		LOG.info("Recorder injected !");
		publishNewPageState();
		NotificationManager.showMessage("Web Recording - Ready !").showNotification();
		
	}
	
	private String requestPageName() {
	    if (frmOpt == null) {
	        frmOpt = new JFrame();
	    }
	    frmOpt.setVisible(true);
	    frmOpt.setLocation(100, 100);
	    frmOpt.setAlwaysOnTop(true);
	    String currentPageName = JOptionPane.showInputDialog(frmOpt, "Current Location Page Name :", driver.getCurrentUrl(), JOptionPane.WARNING_MESSAGE);
	    frmOpt.dispose();
	    return currentPageName;
	}

	private void publishNewPageState() {
		currentPageName = requestPageName();
		WebEventRecord record = new WebEventRecord();
		record.setTarget(driver.getCurrentUrl());
		record.setEventType("open");	
		record.setComponent("open");
		getServer().sendEvent(record);
	}

	private WebDriver launchBrowser(String host) {
		String chromeDriverPath = System.getProperty("toast.chromedriver.path");
		LOG.info("ChromeDriverPath = " + chromeDriverPath);
		System.setProperty("webdriver.chrome.driver", app.getWebConfig().getChromeDriverPath());
		WebDriver driver = new ChromeDriver();
		driver.get(host);
		return driver;
	}

	public void openRecordingBrowser(String host) {
		if (driver == null) {
			driver = launchBrowser(host);
			try {
				Thread.sleep(5000);
				RestRecorderService.this.injectRecordScript();
				isStarted = true;
				RestRecorderService.this.thread = buildReinjectionThread();
				thread.start();
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	public void closeBrowser() {
		if(driver != null){
			driver.close();
		}
	}

	public WebDriver getDriver() {
		return driver;
	}

	public IAgentServer getServer() {
		return server;
	}
	
	public String getCurrentPageName(){
		return currentPageName;
	}
	
	// Fonction récupéré sur crunchify.com/how-to-get-ping-status-of-any--http-end-point-in-java/
	public static boolean getStatus(String url) throws IOException {
		 
        boolean result = false;
        try {
            URL siteURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) siteURL
                    .openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
 
            int code = connection.getResponseCode();
            if (code == 200) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
