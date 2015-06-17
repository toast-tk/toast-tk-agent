package com.synaptix.toast.core.driver;

import java.util.concurrent.TimeoutException;

import com.synaptix.toast.core.net.request.IIdRequest;


public interface IClientDriver {

	public void process(IIdRequest request);
	 
	public String processAndWaitForValue(IIdRequest requestId) throws IllegalAccessException, TimeoutException;

	public void init();
	
	public void start(String host);

	public boolean waitForExist(String requestId) throws TimeoutException;

	public void stop();
	 
}
