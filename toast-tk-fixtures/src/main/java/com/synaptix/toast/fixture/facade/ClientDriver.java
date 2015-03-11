package com.synaptix.toast.fixture.facade;

import com.synaptix.toast.automation.net.IIdRequest;


public interface ClientDriver {

	public void process(IIdRequest request);
	 
	public void init();
	
	public void start();

	public boolean waitForExist(String requestId);

	public String waitForValue(String requestId);
	 
}
