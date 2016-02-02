package com.synaptix.toast.swing.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage.State;
import com.synaptix.toast.utils.StreamGobbler;

public class WebAgentBoot {

	private static final Logger LOG = LogManager.getLogger(WebAgentBoot.class);

	private static String STREAMGOBBLER_OUTPUT_FILEPATH = Config.TOAST_LOG_DIR + "\\agent.log";
	private static String STREAMGOBBLER_ERROR_FILEPATH = Config.TOAST_LOG_DIR + "\\agent_error.log";
	private static final String WINDOWS_SHELL = "C:\\Windows\\System32\\cmd.exe";
	private static final String AGENT_CMD = "\"../addons/agent/toast-agent.bat\"";

	private static final String TARGET_URL = "http://localhost:4444/record/ping";

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

	public static Process executeSutBat() {
		Process proc = null;
		StreamGobbler outGobbler = null;
		final String sutBatPath = AGENT_CMD;
		try {
			proc = createSutProcess(sutBatPath);
			outGobbler = createStreamWriter(proc);
		}
		catch(Exception e) {
			String out = String.format("Failed to execute cmd: %s", sutBatPath);
			LOG.error(out, e);
			if(outGobbler != null) {
				outGobbler.interrupt();
			}
			if(proc != null) {
				proc.destroy();
			}
		}
		return proc;
	}

	private static Process createSutProcess(
		String command)
		throws IOException {
		Process proc;
		ProcessBuilder builder = new ProcessBuilder();
		builder.command().add(WINDOWS_SHELL);
		builder.command().add("/k");
		builder.command().add("\"" + command + "\"");
		LOG.info(String.format("Processing command %s %s %s !", WINDOWS_SHELL, "/k", "\"" + command + "\""));
		proc = builder.start();
		return proc;
	}

	private static StreamGobbler createStreamWriter(
		Process proc) {
		StreamGobbler outGobbler;
		outGobbler = new StreamGobbler(proc.getInputStream(), "OUT", STREAMGOBBLER_OUTPUT_FILEPATH);
		outGobbler.start();
		StreamGobbler errorGobbler;
		errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERR", STREAMGOBBLER_ERROR_FILEPATH);
		errorGobbler.start();
		return outGobbler;
	}

	public static void boot(EventBus eventBus) {
		executeSutBat();	
		int res = 0;
		while(res != 200) {
			res = executeGET(TARGET_URL, "");
			try {
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		eventBus.post(new SeverStatusMessage(State.CONNECTED));
		return;
	}
	
	public static void main(
		String[] args) {
		
		boot(null);
	}
}
