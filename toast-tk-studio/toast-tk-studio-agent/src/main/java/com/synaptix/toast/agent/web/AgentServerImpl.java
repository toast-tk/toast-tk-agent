package com.synaptix.toast.agent.web;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import com.synaptix.toast.agent.ui.MainApp;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.core.rest.RestUtils;

public class AgentServerImpl  implements IAgentServer{

	private static final Logger LOG = LogManager.getLogger(RestRecorderService.class);
	private String hostName;
	private MainApp app;
	
	public AgentServerImpl(MainApp app){
		try {
			this.app = app;
			this.hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			Log.error(e.getMessage(), e);
		}
	}

	@Override
	public void sendEvent(WebEventRecord eventRecord) {
		String json = new Gson().toJson(eventRecord);
		RestUtils.postWebEventRecord(getWebAppURI()+"/record", json);
	}

	@Override
	public void register() {
		RestUtils.registerAgent(getWebAppURI()+"/susbcribe/driver");
		LOG.info("Agent registred with hotname {}", hostName);
	}

	@Override
	public void unRegister() {
		RestUtils.unRegisterAgent(hostName);	
	}
	
	private String getWebAppURI(){
		return this.app.getWebConfig().getWebAppUrl();
	}

	
	
	
}
