package com.synaptix.toast.adapter;

public class FixtureDescriptor {

	public final String name;

	public final String fixtureType;

	public final String pattern;

	public FixtureDescriptor(
		String name,
		String type,
		String pattern) {
		this.name = name;
		this.fixtureType = type;
		this.pattern = pattern;
	}
}
