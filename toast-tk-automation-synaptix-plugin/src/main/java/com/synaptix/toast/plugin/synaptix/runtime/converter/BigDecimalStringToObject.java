package com.synaptix.toast.plugin.synaptix.runtime.converter;

import java.math.BigDecimal;

public final class BigDecimalStringToObject implements StringToObject {

	BigDecimalStringToObject() {

	}

	@Override
	public Object toObject(final String strObject, final Class<?> classObject) {
		return new BigDecimal(strObject);
	}
}