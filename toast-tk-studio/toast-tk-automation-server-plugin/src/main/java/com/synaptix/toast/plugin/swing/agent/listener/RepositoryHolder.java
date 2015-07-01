
package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepositoryHolder {

	private Map<String, Component> repository;
	
	public RepositoryHolder(){
		repository = new ConcurrentHashMap<String, Component>();
	}

	public Map<String, Component> getRepo() {
		return repository;
	}
	
}
