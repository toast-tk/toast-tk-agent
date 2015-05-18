package com.mongo.test;

import javax.ws.rs.core.MediaType;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.repository.RepositoryDaoService;

public class TestMongo {

	public static void main(String[] args) {
		Injector in = Guice.createInjector(new MongoModule("someHost", 27017));
		RepositoryDaoService.Factory repoFactory = in.getInstance(RepositoryDaoService.Factory.class);
		RepositoryDaoService service = repoFactory.create("play_db");
		String jsonRepo = service.getRepoAsJson();
		System.out.println(jsonRepo);
	}

	/**
	 * main test to store the repository through play rest api
	 * @param args
	 */
   public void testPost(){
		String webAppResourceURI = RestUtils.getWebAppURI("localhost", "9000") + "/saveRepository";
		Client httpClient = Client.create();
		String json = "[{\"type\":\"swing page\",\"rows\":[{\"type\":\"button\",\"locator\":\"Fermer\",\"method\":\"\",\"position\":0,\"id\":\"55101a046c6e446c28022c26\",\"lastUpdated\":\"Mar 23, 2015 2:49:56 PM\",\"name\":\"Fermer\"}],\"id\":\"55101a046c6e446c28022c25\",\"lastUpdated\":\"Mar 23, 2015 2:49:56 PM\",\"name\":\"Erreur_de_connexion\"}]";
		WebResource webResource = httpClient.resource(webAppResourceURI);
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
		int statusCode = response.getStatus();
		System.out.println(statusCode);
   }
}
