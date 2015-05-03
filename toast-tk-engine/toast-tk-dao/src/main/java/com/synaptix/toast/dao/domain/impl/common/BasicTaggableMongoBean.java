package com.synaptix.toast.dao.domain.impl.common;

import java.util.List;

import com.github.jmkgreen.morphia.annotations.Reference;
import com.synaptix.toast.core.dao.ITaggable;

public abstract class BasicTaggableMongoBean extends BasicMongoBean implements ITaggable {

	@Reference
	List<TagImpl> tags;

	public List<TagImpl> getTags() {
		return tags;
	}

	public void setTags(List<TagImpl> tags) {
		this.tags = tags;
	}
}
