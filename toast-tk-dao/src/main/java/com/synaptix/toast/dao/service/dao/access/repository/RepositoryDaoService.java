package com.synaptix.toast.dao.service.dao.access.repository;

import java.lang.reflect.Type;
import java.util.Collection;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jmkgreen.morphia.query.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import com.mongodb.WriteConcern;
import com.synaptix.toast.dao.domain.impl.repository.RepositoryImpl;
import com.synaptix.toast.dao.service.dao.common.AbstractMongoDaoService;
import com.synaptix.toast.dao.service.dao.common.CommonMongoDaoService;
import com.synaptix.toast.dao.service.init.DbStarter;

public class RepositoryDaoService extends AbstractMongoDaoService<RepositoryImpl> {

	public interface Factory {
		RepositoryDaoService create(@Nullable @Assisted String dbName);
	}

	private static final Logger LOG = LoggerFactory.getLogger(RepositoryDaoService.class);
	static final String CONTAINER_TYPE = "swing page";
	
	@Inject
	public RepositoryDaoService(DbStarter starter, CommonMongoDaoService cService, @Named("default_db") String default_db, @Nullable @Assisted String dbName) {
		super(RepositoryImpl.class, starter.getDatabaseByName(dbName != null ? dbName: default_db), cService);
	}


	public String getRepoAsJson(){
		Gson gSon = new Gson();
		Query<RepositoryImpl> query = createQuery();
		query.field("type").equal(CONTAINER_TYPE);
		return gSon.toJson(query.asList());
	}
	
	public boolean saveRepoAsJson(String jsonRepo){
		Gson g = new Gson();
		Type typeOfT = new TypeToken<Collection<RepositoryImpl>>(){}.getType();
		try{
			Collection<RepositoryImpl> repository = (Collection<RepositoryImpl>)g.fromJson(jsonRepo, typeOfT);
			for(RepositoryImpl r: repository){
				save(r, WriteConcern.ACKNOWLEDGED);
			}
			return true;
		}catch(Exception e){
			LOG.error("Couldn't save json representation to mongo instance", e);
			return false;
		}
	}
	
	

}
