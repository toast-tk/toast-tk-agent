package com.synaptix.toast.plugin.synaptix.runtime.converter;

public final class CharacterStringToObject implements StringToObject {

	CharacterStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return new Character(strObject.toCharArray()[0]);
	}
}