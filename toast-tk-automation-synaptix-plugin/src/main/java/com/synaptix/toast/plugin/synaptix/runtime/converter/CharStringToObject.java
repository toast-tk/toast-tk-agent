package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class CharStringToObject implements StringToObject {

	CharStringToObject() {

	}

	@Override
	public Object toObject(
			final String strObject,
			final Class<?> classObject) {
		return Character.valueOf(strObject.toCharArray()[0]);
	}
}