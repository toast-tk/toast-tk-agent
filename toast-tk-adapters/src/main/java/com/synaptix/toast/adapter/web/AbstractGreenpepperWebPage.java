package com.synaptix.toast.adapter.web;

/**
 * 
 * Abstraction for greenpepper
 * 
 * @author skokaina
 * 
 */
public abstract class AbstractGreenpepperWebPage extends AbstractWebPage {
	public String name;
	public String type;
	public String locator;
	public String method;
	public Integer position;

	/**
	 * greenpepper Only, to manage via annotation processing => AbstractGreenPepperWebPage<E extends Enum<E>>
	 */
	public void enterRow() {
		super.initElement(name, type, method, locator, position);
	}

}
