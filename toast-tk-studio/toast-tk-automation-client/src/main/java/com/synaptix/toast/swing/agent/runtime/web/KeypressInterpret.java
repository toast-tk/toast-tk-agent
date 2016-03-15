package com.synaptix.toast.swing.agent.runtime.web;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;


public class KeypressInterpret implements IActionInterpret{

	String buffer = "";
	
	@Override
	public String getSentence(
		WebEventRecord event) {
		return "Type *"+event.getValue()+"* in *"+ event.getTarget() +"*";
	}
}
