package com.mongo.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.repository.RepositoryDaoService;

public class TestMongo {

	public static void main(String[] args) {
		Injector in = Guice.createInjector(new MongoModule());
		RepositoryDaoService.Factory repoFactory = in.getInstance(RepositoryDaoService.Factory.class);
		RepositoryDaoService service = repoFactory.create("play_db");
		String jsonRepo = service.getRepoAsJson();
		System.out.println(jsonRepo);
	}

}
