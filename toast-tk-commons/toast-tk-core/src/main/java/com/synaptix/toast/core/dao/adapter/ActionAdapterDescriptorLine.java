package com.synaptix.toast.core.dao.adapter;

public class ActionAdapterDescriptorLine {

	public final String name;

	public final String fixtureType;

	public final String pattern;
	
	public final String description;

	public ActionAdapterDescriptorLine(
		String name,
		String type,
		String description,
		String pattern) {
		this.name = name;
		this.fixtureType = type;
		this.pattern = pattern;
		this.description = description;
	}
}
