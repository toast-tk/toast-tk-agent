package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class ShortStringToObject implements StringToObject {

	ShortStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Short.parseShort(strObject);
	}
}