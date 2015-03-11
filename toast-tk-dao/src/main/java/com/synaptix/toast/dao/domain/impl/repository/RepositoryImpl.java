package com.synaptix.toast.dao.domain.impl.repository;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Entity;
import com.synaptix.toast.dao.domain.impl.common.BasicEntityBean;

@Entity(value = "repository", noClassnameStored = true)
public class RepositoryImpl extends BasicEntityBean {

	public String type;
	
	public List<ElementImpl> rows = new ArrayList<ElementImpl>();;
}
