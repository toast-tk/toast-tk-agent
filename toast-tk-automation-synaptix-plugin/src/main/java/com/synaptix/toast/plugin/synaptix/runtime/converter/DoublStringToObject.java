package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class DoublStringToObject implements StringToObject {

	DoublStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Double.valueOf(Double.parseDouble(strObject));
	}
}