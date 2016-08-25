package io.toast.tk.agent.web;

import io.toast.tk.core.agent.interpret.WebEventRecord;

public interface IAgentServer {

	void sendEvent(WebEventRecord adjustedEvent);
	
	boolean register(String ApiKey);
	
	void unRegister();

}
