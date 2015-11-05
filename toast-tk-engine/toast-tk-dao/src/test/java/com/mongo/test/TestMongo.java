package com.mongo.test;

import java.util.List;

import javax.ws.rs.core.MediaType;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;

public class TestMongo {

	public static void main(
		String[] args) {
		Injector in = Guice.createInjector(new MongoModule("10.23.252.131", 27017));
		ProjectDaoService.Factory repoFactory = in.getInstance(ProjectDaoService.Factory.class);
		ProjectDaoService service = repoFactory.create("play_db");
		List<Project> findAllReferenceProjects = service.findAllReferenceProjects();
	}

	/**
	 * main test to store the repository through play rest api
	 * @param args
	 */
	public void testPost() {
		String webAppResourceURI = RestUtils.getWebAppURI("localhost", "9000") + "/saveRepository";
		Client httpClient = Client.create();
		String json = "[{\"type\":\"swing page\",\"rows\":[{\"type\":\"button\",\"locator\":\"Fermer\",\"method\":\"\",\"position\":0,\"id\":\"55101a046c6e446c28022c26\",\"lastUpdated\":\"Mar 23, 2015 2:49:56 PM\",\"name\":\"Fermer\"}],\"id\":\"55101a046c6e446c28022c25\",\"lastUpdated\":\"Mar 23, 2015 2:49:56 PM\",\"name\":\"Erreur_de_connexion\"}]";
		WebResource webResource = httpClient.resource(webAppResourceURI);
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
			.post(ClientResponse.class, json);
		int statusCode = response.getStatus();
		System.out.println(statusCode);
	}
}
