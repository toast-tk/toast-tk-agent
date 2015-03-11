package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class StringStringToObject implements StringToObject {

	StringStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return strObject;
	}
}