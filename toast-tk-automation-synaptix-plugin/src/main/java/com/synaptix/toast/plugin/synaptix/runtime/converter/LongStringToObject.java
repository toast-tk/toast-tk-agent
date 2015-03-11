package com.synaptix.toast.plugin.synaptix.runtime.converter;

public class LongStringToObject  implements StringToObject {

	LongStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return Long.parseLong(strObject);
	}
}