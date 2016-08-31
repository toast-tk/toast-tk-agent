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

	@Override
	public void sendEvent(WebEventRecord eventRecord) {
		String json = new Gson().toJson(eventRecord);
		RestUtils.postWebEventRecord(getWebAppURI()+"/record", json);
	}

	@Override
	public void register() {
		try {
			String localAddress = Inet4Address.getLocalHost().getHostAddress();
			AgentInformation info = new AgentInformation(localAddress, "TOKEN");
			String json = new Gson().toJson(info);
			RestUtils.registerAgent(getWebAppURI()+"/susbcribe/driver", json);
			LOG.info("Agent registred with hotname {}", hostName);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
		
	}

	@Override
	public void unRegister() {
		RestUtils.unRegisterAgent(hostName);	
	}
	
	private String getWebAppURI(){
		return this.app.getWebConfig().getWebAppUrl();
	}

}