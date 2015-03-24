package com.synaptix.toast.plugin.synaptix.runtime.converter;

public class IntStringToObject implements StringToObject {

	IntStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Integer.valueOf(Integer.parseInt(strObject));
	}
}