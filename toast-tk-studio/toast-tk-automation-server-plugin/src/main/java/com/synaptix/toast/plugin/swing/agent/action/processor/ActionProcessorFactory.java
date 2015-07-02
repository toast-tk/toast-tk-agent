package com.synaptix.toast.plugin.swing.agent.action.processor;

import com.synaptix.toast.core.net.request.CommandRequest;

public abstract class ActionProcessorFactory {

	public abstract ActionProcessor getProcessor(CommandRequest command);

}
