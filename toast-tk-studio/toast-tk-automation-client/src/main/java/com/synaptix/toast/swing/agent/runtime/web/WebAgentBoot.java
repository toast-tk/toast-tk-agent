package com.synaptix.toast.swing.agent.runtime.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage.State;

public class WebAgentBoot {

	private static final Logger LOG = LogManager.getLogger(WebAgentBoot.class);
	private static final String PING_URL = "http://localhost:4444/record/ping";
	private static final String PING_SECURE_URL = "https://localhost:4445/record/ping";
	private static final String START_URL = "http://localhost:4444/record/start";
	private static final String STOP_URL = "http://localhost:4444/record/stop";

	public static int executeGET(
		String targetURL,
		String urlParameters) {
		HttpURLConnection connection = null;
		try {
			// Create connection
			URL url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			int responseCode = connection.getResponseCode();
			LOG.info("\nSending 'GET' request to URL : " + url);
			LOG.info("Response Code : " + responseCode);
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return responseCode;
		}
		catch(Exception e) {
			LOG.error(e.getMessage(), e);
			return -1;
		}
		finally {
			if(connection != null) {
				connection.disconnect();
			}
		}
	}
	
	
	public static boolean securePing(EventBus eventBus) {
		int res = 0;
		int tries = 0;
		while(res != 200 && tries <5) {
			res = executeGET(PING_SECURE_URL, "");
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
			tries++;
		}
		if(res == 200){
			eventBus.post(new SeverStatusMessage(State.CONNECTED));
		}
		return res == 200;
	}
	
	public static boolean ping(EventBus eventBus) {
		int res = 0;
		int tries = 0;
		while(res != 200 && tries <5) {
			res = executeGET(PING_URL, "");
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
			tries++;
		}
		if(res == 200){
			eventBus.post(new SeverStatusMessage(State.CONNECTED));
		}
		return res == 200;
	}
	
	public static void startRecording() {
		int res = 0;
		while(res != 200) {
			res = executeGET(START_URL, "");
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return;
	}
	
	public static void stopRecording() {
		int res = 0;
		while(res != 200) {
			res = executeGET(STOP_URL, "");
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return;
	}
	
}
