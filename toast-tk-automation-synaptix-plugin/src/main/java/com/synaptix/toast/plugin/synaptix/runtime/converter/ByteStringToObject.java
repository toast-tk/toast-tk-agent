package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class ByteStringToObject implements StringToObject {

	ByteStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Byte.parseByte(strObject);
	}
}