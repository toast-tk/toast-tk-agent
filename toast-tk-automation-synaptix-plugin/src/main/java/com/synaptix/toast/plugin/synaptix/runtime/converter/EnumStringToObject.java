package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class EnumStringToObject implements StringToObject {

	EnumStringToObject() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Enum.valueOf((Class) classObject, strObject);
	}
}