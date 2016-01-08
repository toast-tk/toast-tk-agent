package com.synaptix.toast.plugin.swing.agent.listener;

import java.awt.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RepositoryHolder {

	private Map<String, Component> repository;
	private Map<String, Component> idRepository;


	public RepositoryHolder() {
		repository = new ConcurrentHashMap<String, Component>();
		idRepository= new ConcurrentHashMap<String, Component>();
	}

	public Map<String, Component> getRepo() {
		return repository;
	}
	
	public Map<String, Component> getIdRepo() {
		return idRepository;
	}
}
