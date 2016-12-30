package io.toast.tk.agent.web;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.toast.tk.agent.ui.IAgentApp;
import io.toast.tk.core.agent.interpret.WebEventRecord;
import io.toast.tk.core.rest.HttpRequest;
import io.toast.tk.core.rest.RestUtils;

public class AgentServerImpl  implements IAgentServer{

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);
	private String hostName;
	private IAgentApp app;
	
	@Inject
	public AgentServerImpl(IAgentApp app){
		try {
			this.app = app;
			this.hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void sendEvent(WebEventRecord eventRecord, String apiKey) {
		String json = new Gson().toJson(eventRecord);
		String url = getWebAppURI() + "/record";
		RestUtils.postWebEventRecord(buildRequest(url, json, apiKey));
	}

	public boolean register() {
		return register(null);
	}
	
	public boolean register(String apiKey) {
		try {
			String url = getWebAppURI() + "/susbcribe";

			String localAddress = Inet4Address.getLocalHost().getHostAddress();
			AgentInformation info = new AgentInformation(localAddress, apiKey);
			String json = new Gson().toJson(info);
			
			boolean isRegistered = RestUtils.registerAgent(buildRequest(url, json, apiKey));
			
			if(isRegistered) {
				LOG.info("Agent registred with hotname {}", hostName);
			}
			else {
				LOG.info("The webApp does not anwser at " + url);
			}
			return isRegistered;
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}
	

	private HttpRequest buildRequest(String uri, String json, String apiKey) {
		HttpRequest request = new HttpRequest(uri, json);
		String proxyPort = app.getConfig().getProxyPort();
		int port = proxyPort == null ? -1 : Integer.valueOf(proxyPort).intValue();
		request.setProxyInfo(app.getConfig().getProxyAdress(),
							 port,
							 app.getConfig().getProxyUserName(),
							 app.getConfig().getProxyUserPswd());
		request.setApiKey(apiKey);
		return request;
	}


	@Override
	public void unRegister() {
		RestUtils.unRegisterAgent(hostName);	
	}
	
	private String getWebAppURI(){
		String url = this.app.getConfig().getWebAppUrl();
		if(url.endsWith("/")) {
			url = (new StringBuilder(url)).deleteCharAt(url.length()-1).toString();
		}
		return url;
	}

}
