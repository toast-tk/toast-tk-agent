package com.synaptix.toast.swing.agent.web;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.platform.Verticle;

import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.swing.agent.tray.SysTrayHook;

@FixMe(todo = "ensure we have firefow browser installed, use a factory")
public class RestRecorderService extends Verticle {

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);

	private WebDriver driver;
	private boolean isStarted;
	private Thread thread;
	private IAgentServer server;
	
	@Override
	public void start() {
		LOG.info("Starting..");
		SysTrayHook.init();
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
			String toastHome = System.getenv("TOAST_HOME");
			
			//SECURE ONE
			vertx.createHttpServer().requestHandler(matcher)
			.setSSL(true)
			.setKeyStorePath(toastHome + SystemUtils.FILE_SEPARATOR + "server-keystore.jks")
			.setKeyStorePassword("wibble").listen(4445);
			
			//PLAIN ONE
			vertx.createHttpServer().requestHandler(matcher).listen(4444);
			server = new KryoAgentServer(this);
			LOG.info("Started !");
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
	}

	private static WebDriver launchBrowser(String host) {
		//String chromeDriverPath = System.getProperty("toast.chromedriver.path");
		//LOG.info("ChromeDriverPath = " + chromeDriverPath);
		//System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		//WebDriver driver = new ChromeDriver();
		FirefoxDriver driver = new FirefoxDriver();
		driver.get(host);
		return driver;
	}

	protected void openRecordingBrowser(String host) {
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
	
}
