package com.synaptix.agent.record;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.swing.agent.web.IAgentServer;

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
