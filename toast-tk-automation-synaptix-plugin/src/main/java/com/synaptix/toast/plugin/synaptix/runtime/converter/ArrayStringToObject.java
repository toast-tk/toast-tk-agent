package com.synaptix.toast.plugin.synaptix.runtime.converter;

import java.lang.reflect.Array;

public class ArrayStringToObject implements StringToObject {

	@Override
	public Object toObject(
			final String strObject, 
			final Class<?> classObject
	) {
		int length = Array.getLength(classObject);
	    for (int i = 0; i < length; i ++) {
	        Object arrayElement = Array.get(classObject, i);
	        System.out.println(arrayElement);
	    }
	    return "";
	}
}