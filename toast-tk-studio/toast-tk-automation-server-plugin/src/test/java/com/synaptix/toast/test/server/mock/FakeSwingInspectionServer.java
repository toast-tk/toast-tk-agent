package com.synaptix.toast.test.server.mock;

import java.awt.Component;
import java.util.Set;

import io.toast.tk.core.agent.inspection.ISwingInspectionServer;
import io.toast.tk.core.agent.interpret.AWTCapturedEvent;

public class FakeSwingInspectionServer implements ISwingInspectionServer {

	private AWTCapturedEvent eventObject;

	@Override
	public void highlight(String selectedValue) {
	}

	@Override
	public String getComponentLocator(Component component) {
		return null;
	}

	@Override
	public void publishRecordEvent(AWTCapturedEvent eventObject) {
		this.eventObject = eventObject;
	}

	@Override
	public void publishInterpretedEvent(String sentence) {
		
	}

	@Override
	public Set<String> scan(boolean b) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public AWTCapturedEvent getEvent(){
		return eventObject;
	}
}
