package com.synaptix.toast.dao.service.dao.common;

import java.util.HashMap;
import java.util.Map;

public class EntityCollectionManager {

	Map<Class<?>, String> mongoEntityMap = new HashMap<Class<?>, String>();

	public void register(String value, Class<?> c) {
		mongoEntityMap.put(c, value);
	}

	public String getCollection(Class<?> clazz) {
		return mongoEntityMap.get(clazz);
	}

}
