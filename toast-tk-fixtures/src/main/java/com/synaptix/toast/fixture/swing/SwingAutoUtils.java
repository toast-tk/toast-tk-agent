package com.synaptix.toast.fixture.swing;

import java.util.UUID;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.fixture.facade.ClientDriver;

public class SwingAutoUtils {
	
	public static boolean confirmExist(ClientDriver frontEndDriver, String locator, String type){
		int retry = 20;
		while(retry > 0){
			try {
				if(exists(frontEndDriver, locator, type)){
					return true;
				}
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retry--;
		}
		return false;
	}

	
	public static boolean exists(ClientDriver frontEndDriver, String locator, String type){
		final String requestId = UUID.randomUUID().toString();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(requestId).with(locator).ofType(type).exists().build());
		return frontEndDriver.waitForExist(requestId);
	}
}
