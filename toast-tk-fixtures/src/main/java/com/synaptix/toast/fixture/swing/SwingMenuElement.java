package com.synaptix.toast.fixture.swing;


import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.core.ISwingElement;
import com.synaptix.toast.fixture.facade.ClientDriver;
import com.synaptix.toast.fixture.facade.HasClickAction;
import com.synaptix.toast.fixture.facade.HasSubItems;

/**
 * button element
 * 
 * @author skokaina
 * 
 */
public class SwingMenuElement extends SwingAutoElement implements HasClickAction, HasSubItems {

	public SwingMenuElement(ISwingElement element, ClientDriver driver) {
		super(element, driver);
	}

	public SwingMenuElement(ISwingElement element) {
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

	@Override
	public void clickOn(String itemName) {
		exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).select(itemName).build());
	}
	
}
