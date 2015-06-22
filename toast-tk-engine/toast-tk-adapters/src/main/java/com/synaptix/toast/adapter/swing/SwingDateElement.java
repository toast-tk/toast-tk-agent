package com.synaptix.toast.adapter.swing;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import com.synaptix.toast.adapter.web.HasStringValue;
import com.synaptix.toast.adapter.web.HasTextInput;
import com.synaptix.toast.core.driver.IClientDriver;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.runtime.ErrorResultReceivedException;
import com.synaptix.toast.core.runtime.ISwingElement;

/**
 * input element
 * 
 * @author skokaina
 * 
 */
public class SwingDateElement extends SwingAutoElement implements HasTextInput, HasStringValue {

	public SwingDateElement(ISwingElement element, IClientDriver driver) {
		super(element, driver);
	}

	public SwingDateElement(ISwingElement element) {
		super(element);
	}

	@Override
	public void setInput(String e) throws TimeoutException, ErrorResultReceivedException {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).sendKeys(e).build());
	}
	
	public void setDateText(String e) throws TimeoutException, ErrorResultReceivedException {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType("date_text").sendKeys(e).build());
	}


	@Override
	public String getValue() throws IllegalAccessException, TimeoutException, ErrorResultReceivedException {
		exists();
		final String requestId = UUID.randomUUID().toString();
		CommandRequest request = new CommandRequest.CommandRequestBuilder(requestId).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).getValue().build();
		return frontEndDriver.processAndWaitForValue(request);
	}
}
