package com.synaptix.toast.swing.agent.runtime.web.interpret;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;


public class InterpretationProvider {
	
	private static InterpretationProvider INSTANCE = new InterpretationProvider();
	
	private static Map<String, IActionInterpret> map;
	
	{
		map = new HashMap<String, IActionInterpret>();
		map.put("a", new WebClickInterpret());
		map.put("select", new WebClickInterpret());
		map.put("button", new WebClickInterpret());
		map.put("input", new KeypressInterpret());
	}
	
	public static InterpretationProvider getInstance(){
		return INSTANCE;
	}

	public static IActionInterpret getSentenceBuilder(
		String type) {
		return getInstance().getInterpretFor(type);
	}

	private IActionInterpret getInterpretFor(
		String type) {
		if(type != null && type.contains(":")){
			type = StringUtils.split(type, ":")[0];
		}
		return map.get(type);
	}
}
