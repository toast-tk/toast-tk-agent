package com.synaptix.toast.adapter.swing;

import java.util.UUID;

import com.synaptix.toast.adapter.web.HasClickAction;
import com.synaptix.toast.adapter.web.HasStringValue;
import com.synaptix.toast.core.driver.IClientDriver;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.runtime.ISwingElement;

/**
 * input element
 * 
 * @author skokaina
 * 
 */
public class SwingCheckBoxElement extends SwingAutoElement implements HasClickAction, HasStringValue {

	public SwingCheckBoxElement(ISwingElement element, IClientDriver driver) {
		super(element, driver);
	}

	public SwingCheckBoxElement(ISwingElement element) {
		super(element);
	}

	public void select() {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).sendKeys("true").build());
	}
	
	public void deselect() {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).sendKeys("false").build());
	}
	
	@Override
	public boolean click() {
		boolean res = exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).click().build());
		return res;
	}

	@Override
	public void dbClick() {
		
	}

	@Override
	public String getValue() {
		exists();
		final String requestId = UUID.randomUUID().toString();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(requestId).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).getValue().build());
		return frontEndDriver.waitForValue(requestId);
	}
}