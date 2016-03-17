package com.synaptix.toast.swing.agent.runtime.web.interpret;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.core.record.RecordedEvent;


public interface IActionInterpret {
	
	String getSentence(WebEventRecord eventRecord);
}
