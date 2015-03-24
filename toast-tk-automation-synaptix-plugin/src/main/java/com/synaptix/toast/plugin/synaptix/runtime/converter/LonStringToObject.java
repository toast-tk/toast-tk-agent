package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class LonStringToObject implements StringToObject {

	LonStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Long.valueOf(Long.parseLong(strObject));
	}
}