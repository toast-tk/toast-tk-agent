package io.toast.tk.agent.web;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.Inject;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.NotificationManager;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class ScriptInjector {

	private static final Logger LOG = LogManager.getLogger(ScriptInjector.class);	
	private WebDriver driver;
	private boolean isStarted;
	private JFrame frmOpt; 
	private IAgentServer server;
	private UriChangeListener uriChangeListener;
	private AgentConfigProvider configProvider;
	private volatile boolean recording;

	@Inject
	public ScriptInjector(IAgentServer server, UriChangeListener uriChangeListener, AgentConfigProvider configProvider){
		this.server = server;
		this.uriChangeListener = uriChangeListener;
		this.configProvider = configProvider;
		this.recording = true;
	}
	
	private Thread initInjectionRetryThread() {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				while (recording) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						LOG.error(e1.getMessage(), e1);
					}
					if (driver != null && Strings.isNotEmpty(driver.getCurrentUrl())) {
						final WebElement body = driver.findElement(By.tagName("body"));
						final String attribute = body.getAttribute("recording");
						if (!"true".equals(attribute)) {
							LOG.info("Re-Injecting recorder !");
							try {
								injectScript();
							} catch (IOException e) {
								LOG.error(e.getMessage(), e);
							}
						}else{
							LOG.info("Recorder is still alive !");
						}
					}else {
						LOG.info("Injection thread info: driver={}, isStarted={}", driver, isStarted);
					}
				}
			}
		}, "ALIVE-RECORDER-CHECKER");
	}

	public void injectScript() throws IOException {
		this.isStarted = true;
		this.recording = true;
		JavascriptExecutor executor = ((JavascriptExecutor) driver);
		InputStream resourceAsStream = RestRecorderService.class.getClassLoader().getResourceAsStream("recorder.js");
		String script = IOUtils.toString(resourceAsStream);
		StringBuilder subsScriptBuilder = new StringBuilder();
		subsScriptBuilder.append("var script = window.document.createElement('script');");
		subsScriptBuilder.append("script.innerHTML=\"");
		subsScriptBuilder.append(script.replace("\r\n", "").replace("\n", ""));
		subsScriptBuilder.append( "\";window.document.head.appendChild(script);");
		executor.executeScript(subsScriptBuilder.toString());
		String recordingStatus = "window.document.body.setAttribute('recording','true');";
		executor.executeScript(recordingStatus);
		LOG.info("Recorder injected !");
		publishNewPageState();
		NotificationManager.showMessage("Web Recording - Ready !").showNotification();
		initInjectionRetryThread().start();
	}
	
	private void publishNewPageState() {
		this.uriChangeListener.onUriChange(requestPageName());
		WebEventRecord record = new WebEventRecord();
		record.setTarget(driver.getCurrentUrl());
		record.setEventType("open");	
		record.setComponent("open");
		this.server.sendEvent(record, configProvider.get().getApiKey());
	}
	
	private String requestPageName() {
	    if (frmOpt == null) {
	        frmOpt = new JFrame();
	    }
	    frmOpt.setVisible(true);
	    frmOpt.setLocation(0, 0);
	    frmOpt.setAlwaysOnTop(true);
	    String currentPageName = JOptionPane.showInputDialog(frmOpt, 
	    													"Current Location - Repository Page Name :", 
												    		driver.getCurrentUrl(), 
												    		JOptionPane.WARNING_MESSAGE);
	    frmOpt.dispose();
	    return currentPageName;
	}
	
	public void setDriver(WebDriver driver){
		this.driver = driver;
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void stop() {
		this.recording = false;
	}
}
