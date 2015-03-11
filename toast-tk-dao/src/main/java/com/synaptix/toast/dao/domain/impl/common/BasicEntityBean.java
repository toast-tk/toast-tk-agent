package com.synaptix.toast.dao.domain.impl.common;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Id;

public abstract class BasicEntityBean extends BasicTaggableMongoBean {

	@Id
	ObjectId id = new ObjectId();

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

}
