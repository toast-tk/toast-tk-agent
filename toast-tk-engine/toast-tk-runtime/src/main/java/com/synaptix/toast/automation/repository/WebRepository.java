package com.synaptix.toast.automation.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synaptix.toast.adapter.web.AbstractWebPage;

public abstract class WebRepository<W extends AbstractWebPage> {

	Map<String, W> pageMap;

	public WebRepository() {
		pageMap = new HashMap<String, W>();
	}

	protected W getPage(
		String name) {
		return pageMap.get(name);
	}

	protected void addPage(
		String name,
		W page) {
		pageMap.put(name, page);
	}

	public List<W> getPages() {
		return new ArrayList<W>(pageMap.values());
	}

	public void printPages() {
		System.out.println(StringUtils.join(pageMap.keySet(), ","));
	}
}
