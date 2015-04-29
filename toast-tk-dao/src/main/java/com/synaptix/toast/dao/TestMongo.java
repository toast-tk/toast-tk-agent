package com.synaptix.toast.dao;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;

public class TestMongo {

	public static void main(String[] args) {
		Injector in = Guice.createInjector(new MongoModule());
		ProjectDaoService.Factory repoFactory = in.getInstance(ProjectDaoService.Factory.class);
		ProjectDaoService service = repoFactory.create("test_project_db");
		List<Project> findAllLastProjects = service.findAllLastProjects();
		//TestP

	}

}
