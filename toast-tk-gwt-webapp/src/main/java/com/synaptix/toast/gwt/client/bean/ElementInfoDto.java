package com.synaptix.toast.gwt.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ElementInfoDto implements IsSerializable {

	public String locator;
	public String method;
	private String type;
	private String name;
	public int position;

	public ElementInfoDto() {

	}

	/**
	 * 
	 * @param name
	 * @param type
	 * @param locator
	 * @param method
	 * @param position
	 */
	public ElementInfoDto(String name, String type, String locator, String method, int position) {
		this.locator = locator;
		this.method = method;
		this.position = position;
		this.type = type;
		this.name = name;
	}

	public String getLocator() {
		return locator;
	}

	public void setLocator(String locator) {
		this.locator = locator;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
