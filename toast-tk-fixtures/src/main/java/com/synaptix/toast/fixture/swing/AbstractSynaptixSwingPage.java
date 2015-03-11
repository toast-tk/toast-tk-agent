package com.synaptix.toast.fixture.swing;

import java.util.Map;

import com.synaptix.toast.core.ISwingElement;
import com.synaptix.toast.core.IWebElement;

/**
 * abstraction for Red pepper
 * 
 * @author skokaina
 * 
 */
public abstract class AbstractSynaptixSwingPage extends AbstractSwingPage {

	@Override
	public void initElement(ISwingElement webElement) {
		super.initElement(webElement);
	}

	public abstract Map<String, String> getDebugIdMap();

}
