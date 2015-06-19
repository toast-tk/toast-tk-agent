package com.synaptix.toast.core.driver;

import java.util.concurrent.TimeoutException;

import com.synaptix.toast.core.net.request.IIdRequest;
import com.synaptix.toast.core.runtime.ErrorResultReceivedException;


public interface IClientDriver {

	public void process(IIdRequest request);
	 
	public String processAndWaitForValue(IIdRequest requestId) throws IllegalAccessException, TimeoutException, ErrorResultReceivedException;

	public void init();
	
	public void start(String host);

	public boolean waitForExist(String requestId) throws TimeoutException, ErrorResultReceivedException;

	public void stop();
	 
}
