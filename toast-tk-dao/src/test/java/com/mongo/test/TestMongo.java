package com.mongo.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.dao.domain.impl.report.Campaign;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.domain.impl.repository.RepositoryImpl;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;
import com.synaptix.toast.dao.service.dao.access.repository.RepositoryDaoService;

public class TestMongo {

	public static void main(String[] args) {
		Injector in = Guice.createInjector(new MongoModule());

		ProjectDaoService.Factory repoFactory = in.getInstance(ProjectDaoService.Factory.class);
		ProjectDaoService service = repoFactory.create(null);
		Project p = new Project();
		p.setName("test");
		
		Campaign c = new Campaign();
		c.setName("test");
		
		//TestP

	}

}
