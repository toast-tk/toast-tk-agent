package io.toast.tk.agent.web;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.inject.Inject;

import io.toast.tk.agent.config.WebConfig;
import io.toast.tk.agent.config.WebConfigProvider;
import io.toast.tk.agent.ui.ConfigPanel;

public class BrowserManager {

	private static final Logger LOG = LogManager.getLogger(ConfigPanel.class);
	
	private WebConfigProvider webConfigProvider;
	private ScriptInjector scriptInjector;
	private WebDriver driver;

	@Inject
	public BrowserManager(WebConfigProvider webConfigProvider, ScriptInjector scriptInjector){
		this.webConfigProvider = webConfigProvider;
		this.scriptInjector= scriptInjector;
	}
	
	private WebDriver buildDriver() {
		String chromeDriverPath = webConfigProvider.get().getChromeDriverPath();
		LOG.info("Loading chrome driver: " + chromeDriverPath);
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		WebDriver driver = new ChromeDriver();
		return driver;
	}

	public void startRecording() {
		if (driver == null || !this.scriptInjector.isStarted()) {
			driver = buildDriver();
			String host = getWebConfig().getWebInitRecordingUrl();
			driver.get(host);
			try {
				Thread.sleep(5000);
				this.scriptInjector.setDriver(driver);
				this.scriptInjector.injectScript();
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
			driver = null;
		}
	}

	public WebDriver getDriver() {
		return driver;
	}

	public WebConfig getWebConfig() {
		return webConfigProvider.get();
	}

}
