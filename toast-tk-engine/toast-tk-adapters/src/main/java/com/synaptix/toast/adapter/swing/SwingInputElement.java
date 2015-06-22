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
public class SwingInputElement extends SwingAutoElement implements HasTextInput, HasStringValue {

	public SwingInputElement(ISwingElement element, IClientDriver driver) {
		super(element, driver);
	}

	public SwingInputElement(ISwingElement element) {
		super(element);
	}

	@Override
	public void setInput(String e) throws TimeoutException, ErrorResultReceivedException {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).sendKeys(e).build());
	}

	@Override
	public String getValue() throws IllegalAccessException, TimeoutException, ErrorResultReceivedException {
		exists();
		final String requestId = UUID.randomUUID().toString();
		CommandRequest request = buildGetInputValueRequest(wrappedElement.getLocator(), wrappedElement.getType().name(), requestId);
		return frontEndDriver.processAndWaitForValue(request);
	}

	public static CommandRequest buildGetInputValueRequest(String locator, String type, final String requestId) {
		return new CommandRequest.CommandRequestBuilder(requestId).with(locator).ofType(type).getValue().build();
	}

	public void clear() throws TimeoutException, ErrorResultReceivedException {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).clear().build());
	}
}
