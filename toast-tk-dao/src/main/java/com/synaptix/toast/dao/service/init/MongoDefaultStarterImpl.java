package com.synaptix.toast.dao.service.init;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.Morphia;
import com.github.jmkgreen.morphia.annotations.Entity;
import com.google.inject.Inject;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.synaptix.toast.dao.config.Config;
import com.synaptix.toast.dao.domain.Domain;
import com.synaptix.toast.dao.service.dao.common.EntityCollectionManager;

public class MongoDefaultStarterImpl implements DbStarter {
	
	private static final Logger LOG = LogManager.getLogger(MongoDefaultStarterImpl.class);
	private Morphia morphia;
	private MongoClient mClient;
	private final Config config;
	private Map<String, Datastore> dsMap;
	private final EntityCollectionManager enitityManager;

	@Inject
	public MongoDefaultStarterImpl(Config config, EntityCollectionManager enitityManager) {
		this.config = config;
		this.enitityManager = enitityManager;
		init();
	}

	private void init() {
		try {
			dsMap = new HashMap<String, Datastore>();
			mClient = new MongoClient(config.getMongoServer(), config.getMongoPort());
			mClient.setWriteConcern(WriteConcern.JOURNALED);
			morphia = new Morphia();
			processMappings();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private void processMappings() {
		Reflections reflection = new Reflections(Domain.class.getPackage().getName());
		Set<Class<?>> typesAnnotatedWith = reflection.getTypesAnnotatedWith(Entity.class);
		for (Class<?> c : typesAnnotatedWith) {
			Entity entity = c.getAnnotation(Entity.class);
			enitityManager.register(entity.value(), c);
			morphia.map(c);
			LOG.info(c + " type has been registered to Morphia !");
		}
	}

	@Override
	public Datastore getDatabaseByName(String name) {
		if (dsMap.get(name) == null) {
			Datastore ds = morphia.createDatastore(mClient, name);
			ds.ensureCaps();
			ds.ensureIndexes();
			dsMap.put(name, ds);
		}
		return dsMap.get(name);
	}

}
