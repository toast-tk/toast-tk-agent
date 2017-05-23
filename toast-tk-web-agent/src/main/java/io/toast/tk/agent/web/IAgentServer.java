package io.toast.tk.agent.web;

import io.toast.tk.core.agent.interpret.WebEventRecord;

import java.net.UnknownHostException;

public interface IAgentServer {

	void sendEvent(WebEventRecord EventRecord, String ApiKey);

	boolean register(String ApiKey);
	
	void unRegister() throws UnknownHostException;

}
