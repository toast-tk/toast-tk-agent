package io.toast.tk.agent.record;

import io.toast.tk.agent.web.IAgentServer;
import io.toast.tk.core.agent.interpret.WebEventRecord;

public class FakeKryoServer implements IAgentServer{

	public WebEventRecord event;
	
	@Override
	public void sendEvent(WebEventRecord adjustedEvent, String ApiKey) {
		this.event = adjustedEvent;
	}

	@Override
	public boolean register(String ApiKey) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void unRegister() {
		// TODO Auto-generated method stub
		
	}


}
