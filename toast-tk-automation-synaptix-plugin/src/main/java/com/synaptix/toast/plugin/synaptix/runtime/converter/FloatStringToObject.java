package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class FloatStringToObject implements StringToObject {

	FloatStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Float.parseFloat(strObject);
	}
}