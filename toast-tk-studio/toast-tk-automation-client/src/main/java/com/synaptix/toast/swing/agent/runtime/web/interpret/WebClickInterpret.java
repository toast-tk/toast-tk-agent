package com.synaptix.toast.swing.agent.runtime.web.interpret;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;


public class WebClickInterpret implements IActionInterpret{

	@Override
	public String getSentence(
		WebEventRecord event) {
		return "Click on *" + event.value + "*";
	}
}
