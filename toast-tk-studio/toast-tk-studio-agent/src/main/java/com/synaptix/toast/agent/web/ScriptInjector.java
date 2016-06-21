package com.synaptix.toast.agent.web;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.google.inject.Inject;
import com.synaptix.toast.agent.ui.NotificationManager;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class ScriptInjector {

	private static final Logger LOG = LogManager.getLogger(ScriptInjector.class);	
	private WebDriver driver;
	private boolean isStarted;
	private JFrame frmOpt; 
	private IAgentServer server;
	private UriChangeListener uriChangeListener;
	
	@Inject
	public ScriptInjector(IAgentServer server, UriChangeListener uriChangeListener){
		this.server = server;
		this.uriChangeListener = uriChangeListener;
	}
	
	private Thread initInjectionRetryThread() {
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
								injectScript();
							} catch (IOException e) {
								LOG.error(e.getMessage(), e);
							}
						}
					}
				}
			}
		}, "ALIVE-RECORDER-CHECKER");
	}

	public void injectScript() throws IOException {
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
		initInjectionRetryThread().start();
	}
	
	private void publishNewPageState() {
		this.uriChangeListener.onUriChange(requestPageName());
		WebEventRecord record = new WebEventRecord();
		record.setTarget(driver.getCurrentUrl());
		record.setEventType("open");	
		record.setComponent("open");
		this.server.sendEvent(record);
	}
	
	private String requestPageName() {
	    if (frmOpt == null) {
	        frmOpt = new JFrame();
	    }
	    frmOpt.setVisible(true);
	    frmOpt.setLocation(100, 100);
	    frmOpt.setAlwaysOnTop(true);
	    String currentPageName = JOptionPane.showInputDialog(frmOpt, 
	    													"Current Location Page Name :", 
												    		driver.getCurrentUrl(), 
												    		JOptionPane.WARNING_MESSAGE);
	    frmOpt.dispose();
	    return currentPageName;
	}
	
	public void setDriver(WebDriver driver){
		this.driver = driver;
	}

}
