package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class ShorStringToObject implements StringToObject {

	ShorStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Short.parseShort(strObject);
	}
}