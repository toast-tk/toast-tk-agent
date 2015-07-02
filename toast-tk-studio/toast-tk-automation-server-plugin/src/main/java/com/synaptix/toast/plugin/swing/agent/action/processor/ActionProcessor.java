package com.synaptix.toast.plugin.swing.agent.action.processor;

import java.awt.Component;

import com.synaptix.toast.core.net.request.CommandRequest;

public interface ActionProcessor<C extends Component> {

	public String processCommandOnComponent(CommandRequest command, C target);
	
}
