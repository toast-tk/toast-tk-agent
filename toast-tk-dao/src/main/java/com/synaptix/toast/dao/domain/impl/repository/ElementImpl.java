package com.synaptix.toast.dao.domain.impl.repository;

public class ElementImpl {
	public String type;
	public String locator;
	public String name;
	String method;
	int position;

	public ElementImpl() {
		type = "";
		locator = "";
		name = "";
		method = "";
		position = 0;
	}
}
