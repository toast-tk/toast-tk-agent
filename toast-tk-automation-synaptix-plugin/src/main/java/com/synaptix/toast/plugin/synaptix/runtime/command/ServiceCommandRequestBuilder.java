package com.synaptix.toast.plugin.synaptix.runtime.command;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.CommandRequest.COMMAND_TYPE;
import com.synaptix.toast.automation.net.CommandRequest.CommandRequestBuilder;

public class ServiceCommandRequestBuilder extends CommandRequestBuilder {

	public ServiceCommandRequestBuilder(final String id) {
		super(id);
		this.itemType = "service";
	}

	public ServiceCommandRequestBuilder call() {
        this.action = COMMAND_TYPE.GET;
        return this;
	}
	
	@Override
	public CommandRequest build() {
		return new ServiceCommandRequest(this);
	}
	
	@Override
	public final CommandRequestBuilder ofType(final String itemType) {
		return this;
	}
}
