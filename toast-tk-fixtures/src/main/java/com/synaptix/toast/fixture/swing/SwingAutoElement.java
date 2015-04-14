package com.synaptix.toast.fixture.swing;

import java.util.UUID;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.ISwingElement;
import com.synaptix.toast.fixture.facade.ClientDriver;

/**
 * Automation super class
 * 
 * @author skokaina
 * 
 */
public abstract class SwingAutoElement {

	protected ISwingElement wrappedElement;
	protected ClientDriver frontEndDriver;

	protected void setWrappedElement(ISwingElement wrappedElement) {
		this.wrappedElement = wrappedElement;
	}

	protected ClientDriver getFrontEndDriver() {
		return frontEndDriver;
	}

	protected void setFrontEndDriver(ClientDriver frontEndDriver) {
		this.frontEndDriver = frontEndDriver;
	}

	public SwingAutoElement(ISwingElement element, ClientDriver driver) {
		this.wrappedElement = element;
		this.frontEndDriver = driver;
	}

	public SwingAutoElement(ISwingElement element) {
		this.wrappedElement = element;
	}

	public SwingAutoElement() {

	}
	
	public boolean exists(){
		final String requestId = UUID.randomUUID().toString();
		final CommandRequest command = new CommandRequest.CommandRequestBuilder(requestId)
														.with(wrappedElement.getLocator())
														.ofType(wrappedElement.getType().name())
														.exists()
														.build();
		frontEndDriver.process(command);
		return frontEndDriver.waitForExist(requestId);
	}

	public ISwingElement getWrappedElement() {
		return wrappedElement;
	}
}
