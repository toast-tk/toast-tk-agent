package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class BytStringToObject implements StringToObject {

	BytStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Byte.parseByte(strObject);
	}
}