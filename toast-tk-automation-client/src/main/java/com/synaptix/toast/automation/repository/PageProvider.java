package com.synaptix.toast.automation.repository;

import com.synaptix.toast.fixture.web.AbstractWebPage;

public class PageProvider {

	WebRepository<?> repo;

	private PageProvider(WebRepository<?> repo) {
		this.repo = repo;
	}

	@SuppressWarnings("unchecked")
	public <E extends AbstractWebPage> E getWebPage(Class<E> clazz) {
		return (E) repo.getPage(clazz.getName());
	}

	public static PageProvider in(WebRepository<?> repo) {
		PageProvider provider = new PageProvider(repo);
		return provider;
	}

}
