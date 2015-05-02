package com.synaptix.toast.dao.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.synaptix.toast.dao.config.Config;
import com.synaptix.toast.dao.config.ConfigProvider;
import com.synaptix.toast.dao.service.dao.common.EntityCollectionManager;
import com.synaptix.toast.dao.service.init.DbStarter;
import com.synaptix.toast.dao.service.init.MongoDefaultStarterImpl;

public class MongoModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DbStarter.class).to(MongoDefaultStarterImpl.class).asEagerSingleton();
		bind(Config.class).toProvider(ConfigProvider.class).in(Singleton.class);
		bind(EntityCollectionManager.class).in(Singleton.class);
		bindConstant().annotatedWith(Names.named("default_db")).to("test_project_db");
		install(new MongoDaoModule());
	}
}
