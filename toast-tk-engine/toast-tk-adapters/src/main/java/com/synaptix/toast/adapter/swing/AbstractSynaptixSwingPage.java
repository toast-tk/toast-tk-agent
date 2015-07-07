package com.synaptix.toast.adapter.swing;

import java.util.Map;

import com.synaptix.toast.core.runtime.ISwingElement;
import com.synaptix.toast.core.runtime.IWebElement;

/**
 * abstraction for Red pepper
 * 
 * @author skokaina
 * 
 */
public abstract class AbstractSynaptixSwingPage extends AbstractSwingPage {

	@Override
	public void initElement(
		ISwingElement webElement) {
		super.initElement(webElement);
	}

	public abstract Map<String, String> getDebugIdMap();
}
