package com.synaptix.toast.adapter.swing;


import com.synaptix.toast.adapter.widget.api.web.HasClickAction;
import com.synaptix.toast.adapter.widget.api.web.HasSubItems;
import com.synaptix.toast.core.driver.IClientDriver;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.runtime.ISwingElement;

/**
 * button element
 * 
 * @author skokaina
 * 
 */
public class SwingMenuElement extends SwingAutoElement implements HasClickAction, HasSubItems {

	public SwingMenuElement(ISwingElement element, IClientDriver driver) {
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
