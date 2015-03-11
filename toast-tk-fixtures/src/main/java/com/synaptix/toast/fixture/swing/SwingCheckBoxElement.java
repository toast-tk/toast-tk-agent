package com.synaptix.toast.fixture.swing;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.ISwingElement;
import com.synaptix.toast.fixture.facade.ClientDriver;
import com.synaptix.toast.fixture.facade.HasClickAction;

/**
 * input element
 * 
 * @author skokaina
 * 
 */
public class SwingCheckBoxElement extends SwingAutoElement implements HasClickAction {

	public SwingCheckBoxElement(ISwingElement element, ClientDriver driver) {
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

}
