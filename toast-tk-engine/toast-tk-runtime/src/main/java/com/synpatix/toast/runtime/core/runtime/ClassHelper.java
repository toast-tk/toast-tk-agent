package com.synpatix.toast.runtime.core.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.attributes.Attributes;
import org.apache.commons.beanutils.BeanUtils;

public class ClassHelper {

	private Object straightGetProperty(Object object, String property) {
		try {
			return BeanUtils.getProperty(object, property);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean hasProperty(Class<?> clazz, String property) {
		Method[] methods = clazz.getMethods();
		String methoProp = property.substring(0, 1).toUpperCase() + property.substring(1);
		for (Method method : methods) {
			if (method.getName().equals("get" + methoProp)) {
				return true;
			} else if (method.getName().equals("set" + methoProp)) {
				return true;
			} else if (method.getName().equals("has" + methoProp)) {
				return true;
			} else if (method.getName().equals("is" + methoProp)) {
				return true;
			} else if (method.getName().equals(property)) {
				return true;
			}
		}
		return Attributes.hasAttribute(clazz, property);
	}

	public static Class<?> getPropertyClass(Class<?> clazz, String propertyName) {
		Method[] methods = clazz.getMethods();
		String methoProp = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		for (Method method : methods) {
			if (method.getName().equals("get" + methoProp)) {
				return method.getReturnType();
			} else if (method.getName().equals("has" + methoProp)) {
				return method.getReturnType();
			} else if (method.getName().equals("is" + methoProp)) {
				return method.getReturnType();
			} else if (method.getName().equals(propertyName)) {
				return method.getReturnType();
			}
		}
		return null;
	}
}
