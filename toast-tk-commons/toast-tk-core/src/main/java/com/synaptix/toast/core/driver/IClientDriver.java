package com.synaptix.toast.core.driver;

import com.synaptix.toast.core.net.request.IIdRequest;


public interface IClientDriver {

	public void process(IIdRequest request);
	 
	public void init();
	
	public void start();

	public boolean waitForExist(String requestId);

	public String waitForValue(String requestId);

	public void stop();
	 
}