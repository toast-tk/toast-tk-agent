package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class FloaStringToObject implements StringToObject {

	FloaStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Float.valueOf(Float.parseFloat(strObject));
	}
}