package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class IntegerStringToObject implements StringToObject {

	IntegerStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Integer.parseInt(strObject);
	}
}
