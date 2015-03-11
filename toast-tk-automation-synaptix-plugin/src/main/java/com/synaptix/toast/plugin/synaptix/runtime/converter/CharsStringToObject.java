package com.synaptix.toast.plugin.synaptix.runtime.converter;

public class CharsStringToObject implements  StringToObject {

	CharsStringToObject() {

	}

	@Override
	public Object toObject(
			final String strObject,
			final Class<?> classObject) {
		return strObject.toCharArray();
	}
}