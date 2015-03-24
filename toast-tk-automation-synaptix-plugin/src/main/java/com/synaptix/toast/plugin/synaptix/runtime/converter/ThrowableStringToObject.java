package com.synaptix.toast.plugin.synaptix.runtime.converter;

public class ThrowableStringToObject implements StringToObject {

	@Override
	public Object toObject(
			final String strObject, 
			final Class<?> classObject
	) {
		return strObject;
	}
}