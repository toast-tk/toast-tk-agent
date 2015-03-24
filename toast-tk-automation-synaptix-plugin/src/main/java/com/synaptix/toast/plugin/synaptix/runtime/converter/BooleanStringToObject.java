package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class BooleanStringToObject implements StringToObject {

	BooleanStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Boolean.valueOf(Boolean.parseBoolean(strObject));
	}
}