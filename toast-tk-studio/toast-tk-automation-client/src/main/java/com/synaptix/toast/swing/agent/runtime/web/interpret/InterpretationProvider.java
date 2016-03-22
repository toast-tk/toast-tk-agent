package com.synaptix.toast.swing.agent.runtime.web.interpret;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;


public class InterpretationProvider {
	
	private MongoRepositoryCacheWrapper mongoRepoManager;

	private Map<String, IActionInterpret> map;
	
	@Inject
	InterpretationProvider(MongoRepositoryCacheWrapper mongoRepoManager){
		this.mongoRepoManager = mongoRepoManager;
		map = new HashMap<String, IActionInterpret>();
		map.put("a", new WebClickInterpret(this.mongoRepoManager));
		map.put("select", new SelectInterpret(this.mongoRepoManager));
		map.put("button", new WebClickInterpret(this.mongoRepoManager));
		map.put("text", new KeypressInterpret(this.mongoRepoManager));
	}

	public IActionInterpret getSentenceBuilder(
		String type) {
		return getInterpretFor(type);
	}

	private IActionInterpret getInterpretFor(
		String type) {
		if(type != null && type.contains(":")){
			type = StringUtils.split(type, ":")[0];
		}
		return map.get(type);
	}
}
