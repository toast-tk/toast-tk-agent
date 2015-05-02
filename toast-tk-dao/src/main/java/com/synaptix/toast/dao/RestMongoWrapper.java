/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 10 mars 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.dao;

import java.lang.reflect.Type;
import java.util.Collection;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.synaptix.toast.core.rest.RestUtils;
import com.synaptix.toast.dao.domain.impl.repository.RepositoryImpl;

public class RestMongoWrapper extends RestUtils{

	public static Collection<RepositoryImpl> loadRepository(String host, String port){
		String webAppResourceURI = getWebAppURI(host, port) + "/loadRepository";
		Client httpClient = Client.create();
		String response = getJsonResponseAsString(webAppResourceURI, httpClient);
		Gson g = new Gson();
		Type typeOfT = new TypeToken<Collection<RepositoryImpl>>(){}.getType();
		Collection<RepositoryImpl> repository = (Collection<RepositoryImpl>)g.fromJson(response, typeOfT);
		return repository;
	}
	
	public static boolean saveRepository(Collection<RepositoryImpl> repoToSave, String host, String port){
		String webAppResourceURI = getWebAppURI(host, port) + "/saveRepository";
		Client httpClient = Client.create();
		GsonBuilder gson = new GsonBuilder();
		gson.registerTypeHierarchyAdapter(ObjectId.class, new com.google.gson.JsonSerializer<ObjectId>() {
			@Override
			public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
				if(src == null){
					return null;
				}
				return new JsonPrimitive(src.toString());
			}
		});
		String json = gson.create().toJson(repoToSave);
		WebResource webResource = httpClient.resource(webAppResourceURI);
		ClientResponse response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
		int statusCode = response.getStatus();
		return statusCode == Response.Status.OK.getStatusCode();
	}
	
}
