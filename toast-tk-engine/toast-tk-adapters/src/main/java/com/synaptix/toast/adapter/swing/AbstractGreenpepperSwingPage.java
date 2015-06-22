package com.synaptix.toast.adapter.swing;

/**
 * 
 * Abstraction for greenpepper
 * 
 * @author skokaina
 * 
 */
public abstract class AbstractGreenpepperSwingPage extends AbstractSwingPage {
	public String name;
	public String type;
	public String locator;

	/**
	 * greenpepper Only, to manage via annotation processing => AbstractGreenPepperWebPage<E extends Enum<E>>
	 */
	public void enterRow() {
		super.initElement(name, type, locator);
	}

}
