package com.synaptix.toast.swing.agent.interpret;

import java.util.Collection;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientHandlerException;
import com.synaptix.toast.dao.RestMongoWrapper;
import com.synaptix.toast.dao.domain.impl.repository.ElementImpl;
import com.synaptix.toast.dao.domain.impl.repository.RepositoryImpl;
import com.synaptix.toast.swing.agent.config.Config;

public class MongoRepositoryCacheWrapper {

	private static final Logger LOG = LogManager.getLogger(MongoRepositoryCacheWrapper.class);

	Collection<RepositoryImpl> cache = null;

	private Config config;

	@Inject
	public MongoRepositoryCacheWrapper(
		Config config) {
		this.config = config;
	}

	public void initCache() {
		try {
			cache = RestMongoWrapper.loadRepository(config.getWebAppAddr(), config.getWebAppPort());
		}
		catch(ClientHandlerException e) {
			LOG.error(
				String.format("WebApp not active at address %s:%s", config.getWebAppAddr(), config.getWebAppPort()),
				e);
		}
	}

	public String find(
		RepositoryImpl container,
		String type,
		String locator) {
		for(RepositoryImpl repImpl : cache) {
			if(repImpl.getName().equals(container.getName())) {
				if(repImpl.rows != null) {
					for(ElementImpl element : repImpl.rows) {
						if(element.locator.equalsIgnoreCase(locator.toLowerCase())) {
							return "".equals(element.name) || element.name == null ? element.locator : element.name;
						}
					}
				}
			}
		}
		ElementImpl impl = extractElement(type, locator);
		container.rows.add(impl);
		return impl.name;
	}

	private ElementImpl extractElement(
		String type,
		String locator) {
		ElementImpl impl = new ElementImpl();
		impl.locator = locator;
		if(locator.contains(":")) {
			impl.name = locator.split(":")[1];
		}
		else {
			impl.name = locator.contains(".") ? impl.type + "-" + UUID.randomUUID().toString() : locator;
		}
		impl.name = formatLabel(impl.name);
		impl.type = type;
		return impl;
	}

	public RepositoryImpl findContainer(
		String lastKnownContainer) {
		lastKnownContainer = formatLabel(lastKnownContainer);
		for(RepositoryImpl repImpl : cache) {
			if(repImpl.getName().equals(lastKnownContainer)) {
				return repImpl;
			}
		}
		RepositoryImpl repImpl = new RepositoryImpl();
		repImpl.setName(lastKnownContainer);
		repImpl.type = "swing page";
		cache.add(repImpl);
		return repImpl;
	}

	private String formatLabel(
		String name) {
		return name.trim().replace(" ", "_").replace("'", "_").replace("°", "_");
	}

	public boolean saveCache() {
		boolean saveRepository = RestMongoWrapper.saveRepository(cache, config.getWebAppAddr(), config.getWebAppPort());
		initCache();
		return saveRepository;
	}

	public String getWikiFiedRepo() {
		if(cache == null) {
			initCache();
		}
		String res = "";
		for(RepositoryImpl page : cache) {
			res += "#Page id:" + page.getId().toString() + "\n";
			res += "|| auto setup ||\n";
			res += "| " + page.type + " | " + page.name + " |\n";
			res += "| name | type | locator |\n";
			if(page.rows != null) {
				for(ElementImpl row : page.rows) {
					res += "|" + row.name + "|" + row.type + "|" + row.locator + "|\n";
				}
			}
			res += "\n";
		}
		return res;
	}
}
