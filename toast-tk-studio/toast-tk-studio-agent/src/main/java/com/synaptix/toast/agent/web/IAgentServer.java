package com.synaptix.toast.agent.web;

import io.toast.tk.core.agent.interpret.WebEventRecord;

public interface IAgentServer {

	void sendEvent(WebEventRecord adjustedEvent);
	
	void register();
	
	void unRegister();

}
