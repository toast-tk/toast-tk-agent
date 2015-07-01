
package com.synaptix.toast.adapter.swing;


import java.util.concurrent.TimeoutException;

import com.synaptix.toast.adapter.web.HasClickAction;
import com.synaptix.toast.core.driver.IRemoteSwingAgentDriver;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.core.runtime.ErrorResultReceivedException;
import com.synaptix.toast.core.runtime.ISwingElement;

/**
 * menu item element
 * 
 * @author skokaina
 * 
 */
public class SwingMenuItemElement extends SwingAutoElement implements HasClickAction {

	public SwingMenuItemElement(ISwingElement element, IRemoteSwingAgentDriver driver) {
		super(element, driver);
	}

	public SwingMenuItemElement(ISwingElement element) {
		super(element);
	}

	@Override
	public boolean click() throws TimeoutException, ErrorResultReceivedException {
		boolean res = exists();
		frontEndDriver.process(new CommandRequest.CommandRequestBuilder(null).with(wrappedElement.getLocator()).ofType(wrappedElement.getType().name()).click().build());
		return res;
	}


	@Override
	public void dbClick() {
	}
	


}
