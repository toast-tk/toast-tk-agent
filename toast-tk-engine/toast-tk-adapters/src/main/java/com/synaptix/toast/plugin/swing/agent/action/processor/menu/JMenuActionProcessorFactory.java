package com.synaptix.toast.plugin.swing.agent.action.processor.menu;

import javax.swing.JMenu;

import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessor;
import com.synaptix.toast.plugin.swing.agent.action.processor.ActionProcessorFactory;

public class JMenuActionProcessorFactory extends ActionProcessorFactory {

	@Override
	public ActionProcessor<JMenu> getProcessor(
		CommandRequest command) {
		switch(command.action) {
			case CLICK :
				return new JMenuClickActionProcessor();
			case SELECT :
				return new JMenuSelectActionProcessor();
			default :
				return null;
		}
	}
}
