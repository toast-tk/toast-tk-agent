package com.synaptix.toast.plugin.swing.agent.action.processor.list;

import javax.swing.JComboBox;

import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessor;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessorFactory;

public class JComboBoxActionProcessorFactory extends ActionProcessorFactory {

	@Override
	public ActionProcessor<JComboBox> getProcessor(
		CommandRequest command) {
		switch(command.action) {
			case SET :
				return new JComboBoxSetActionProcessor();
			case GET :
				return new JComboBoxGetActionProcessor();
			case SELECT :
				return new JComboBoxSelectActionProcessor();
			default :
				return null;
		}
	}
}
