package com.synaptix.toast.plugin.synaptix.runtime.converter;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.activation.UnsupportedDataTypeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StringToObjectInstance implements StringToObject {

	private static final StringToObject INSTANCE = new StringToObjectInstance();
	
	private static final Logger LOG = LoggerFactory.getLogger(StringToObjectInstance.class);

	public static StringToObject getInstance() {
		return INSTANCE;
	}

	private final Map<Class<?>, StringToObject> stringToObjects;

	private StringToObjectInstance() {
		this.stringToObjects = new HashMap<Class<?>, StringToObject>(200);
		registersClasses();
	}

	private void registersClasses() {
		stringToObjects.put(int.class, new IntStringToObject());
		stringToObjects.put(Integer.class, new IntegerStringToObject());
		stringToObjects.put(String.class, new StringStringToObject());
		stringToObjects.put(BigDecimal.class, new BigDecimalStringToObject());
		stringToObjects.put(BigInteger.class, new BigIntegerStringToObject());
		stringToObjects.put(boolean.class, new BoolStringToObject());
		stringToObjects.put(Boolean.class, new BooleanStringToObject());
		stringToObjects.put(Date.class, new DateStringToObject());
		stringToObjects.put(long.class, new LonStringToObject());
		stringToObjects.put(Long.class, new LongStringToObject());
		stringToObjects.put(double.class, new DoublStringToObject());
		stringToObjects.put(Double.class, new DoubleStringToObject());
		stringToObjects.put(float.class, new FloaStringToObject());
		stringToObjects.put(Float.class, new FloatStringToObject());
		stringToObjects.put(char.class, new CharStringToObject());
		stringToObjects.put(char[].class, new CharsStringToObject());
		stringToObjects.put(Character.class, new CharacterStringToObject());
		stringToObjects.put(short.class, new ShorStringToObject());
		stringToObjects.put(Short.class, new ShortStringToObject());
		stringToObjects.put(byte.class, new BytStringToObject());
		stringToObjects.put(Byte.class, new ByteStringToObject());
		stringToObjects.put(Enum.class, new EnumStringToObject());
		stringToObjects.put(Throwable.class, new ThrowableStringToObject());
		stringToObjects.put(Array.class, new ArrayStringToObject());
	}

	@Override
	public Object toObject(
			final String strObject,
			final Class<?> classObject
	) {
		final StringToObject stringToObject = stringToObjects.get(classObject);
		if(stringToObject != null) {
			return stringToObject.toObject(strObject, classObject);
		}
		LOG.error("Type not supported default to null {}", classObject, new UnsupportedDataTypeException());
		return null;
	}
}