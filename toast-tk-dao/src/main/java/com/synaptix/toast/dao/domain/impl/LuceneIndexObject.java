package com.synaptix.toast.dao.domain.impl;

import java.util.List;

import org.bson.types.ObjectId;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.github.jmkgreen.morphia.annotations.Id;

@Entity(value = "indexes", noClassnameStored = true)
public class LuceneIndexObject {
	@Id
	ObjectId id = new ObjectId();
	public int targetObjectPosition;
	public String targetCollection;
	public String idTargetObject;
	public List<String> indtext;
}
