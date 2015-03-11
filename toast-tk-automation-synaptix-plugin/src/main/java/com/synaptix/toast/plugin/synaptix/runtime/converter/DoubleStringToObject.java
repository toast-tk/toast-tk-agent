package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class DoubleStringToObject implements StringToObject {

	DoubleStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Double.parseDouble(strObject);
	}
}
