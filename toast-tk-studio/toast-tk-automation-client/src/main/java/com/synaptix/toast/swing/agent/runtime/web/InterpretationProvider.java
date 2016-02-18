package com.synaptix.toast.swing.agent.runtime.web;

import java.util.HashMap;
import java.util.Map;


public class InterpretationProvider {
	
	private static InterpretationProvider INSTANCE = new InterpretationProvider();
	
	private static Map<String, IActionInterpret> map;
	
	{
		map = new HashMap<String, IActionInterpret>();
		map.put("click", new WebClickInterpret());
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
		return map.get(type);
	}
}
