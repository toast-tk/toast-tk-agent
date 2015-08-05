package com.synaptix.toast.swing.agent.runtime;

import java.util.HashMap;
import java.util.Map;


public class InterpretationProvider {
	
	private static InterpretationProvider INSTANCE = new InterpretationProvider();
	
	private static final Map<String, IActionInterpret> map = new HashMap<String, IActionInterpret>();
	
	{
		map.put("click", new WebClickInterpret());
		map.put("input", new WebClickInterpret());
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
