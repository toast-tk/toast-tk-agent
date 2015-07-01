package com.synaptix.toast.adapter.web;

import java.util.Map;

import com.synaptix.toast.core.runtime.IWebElement;

/**
 * abstraction for Web Pages
 * 
 * @author skokaina
 * 
 */
public abstract class AbstractSynaptixWebPage extends AbstractWebPage {

	/**
	 * greenpepper Only, to manage via annotation processing => AbstractGreenPepperWebPage //<E extends Enum<E>>
	 */
	@Override
	public void initElement(IWebElement webElement) {
		super.initElement(webElement);
	}

	public abstract Map<String, String> getDebugIdMap();

}
