package com.synaptix.agent.record;

import com.synaptix.toast.agent.web.IAgentServer;

import io.toast.tk.core.agent.interpret.WebEventRecord;

public class FakeKryoServer implements IAgentServer{

	public WebEventRecord event;
	
	@Override
	public void sendEvent(WebEventRecord adjustedEvent) {
		this.event = adjustedEvent;
	}

	@Override
	public void register() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unRegister() {
		// TODO Auto-generated method stub
		
	}


}
