package com.synaptix.toast.fixture.swing;

import java.util.UUID;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.ISwingElement;
import com.synaptix.toast.fixture.facade.ClientDriver;
import com.synaptix.toast.fixture.facade.HasTextInput;

/**
 * input element
 * 
 * @author skokaina
 * 
 */
public class SwingInputElement extends SwingAutoElement implements HasTextInput {

	public SwingInputElement(ISwingElement element, ClientDriver driver) {
		super(element, driver);
	}

	public SwingInputElement(ISwingElement element) {
		super(element);
	}

	@Override
	public void setInput(String e) {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).sendKeys(e).build());
	}

	@Override
	public String getValue() {
		exists();
		final String requestId = UUID.randomUUID().toString();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(requestId).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).getValue().build());
		return frontEndDriver.waitForValue(requestId);
	}

	public void clear() {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).clear().build());
	}
}
