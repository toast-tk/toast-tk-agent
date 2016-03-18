package com.synaptix.toast.swing.agent.runtime.web.interpret;

import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;


public class WebClickInterpret extends AbstractInterpretationProvider{

	public WebClickInterpret(MongoRepositoryCacheWrapper mongoRepoManager) {
		super(mongoRepoManager);
	}

	@Override
	public String getSentence(
		WebEventRecord event) {
		return "Click on *" + getLabel(event) + "*";
	}

	@Override
	public String convertToKnowType(String type) {
		return type;
	}
}
