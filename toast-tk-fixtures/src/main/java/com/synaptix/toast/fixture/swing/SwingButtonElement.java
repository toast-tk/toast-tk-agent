package com.synaptix.toast.fixture.swing;


import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.ISwingElement;
import com.synaptix.toast.fixture.facade.ClientDriver;
import com.synaptix.toast.fixture.facade.HasClickAction;

/**
 * button element
 * 
 * @author skokaina
 * 
 */
public class SwingButtonElement extends SwingAutoElement implements HasClickAction {

	public SwingButtonElement(ISwingElement element, ClientDriver driver) {
		super(element, driver);
	}

	public SwingButtonElement(ISwingElement element) {
		super(element);
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
