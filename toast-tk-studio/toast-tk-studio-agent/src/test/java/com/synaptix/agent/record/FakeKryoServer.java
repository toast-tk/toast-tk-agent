package com.synaptix.agent.record;

import com.synaptix.toast.agent.web.IAgentServer;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;

public class FakeKryoServer implements IAgentServer{

	public WebEventRecord event;
	
	@Override
	public void sendEvent(WebEventRecord adjustedEvent) {
		this.event = adjustedEvent;
	}

	@Override
	public void close() {
		
	}

}
