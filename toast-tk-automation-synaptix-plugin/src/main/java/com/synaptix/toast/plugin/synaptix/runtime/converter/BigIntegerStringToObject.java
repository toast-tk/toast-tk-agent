package com.synaptix.toast.plugin.synaptix.runtime.converter;

import java.math.BigInteger;

public final class BigIntegerStringToObject implements StringToObject {

	BigIntegerStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return new BigInteger(strObject);
	}
}