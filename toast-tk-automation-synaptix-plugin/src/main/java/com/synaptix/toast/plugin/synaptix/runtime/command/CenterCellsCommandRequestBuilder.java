package com.synaptix.toast.plugin.synaptix.runtime.command;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.CommandRequest.COMMAND_TYPE;
import com.synaptix.toast.automation.net.CommandRequest.CommandRequestBuilder;

public class CenterCellsCommandRequestBuilder extends CommandRequestBuilder {

	public CenterCellsCommandRequestBuilder(final String id) {
		super(id);
		this.itemType = "centerCells";
	}

	public CenterCellsCommandRequestBuilder call() {
        this.action = COMMAND_TYPE.GET;
        return this;
	}
	
	@Override
	public CommandRequest build() {
		return new CenterCellsCommandRequest(this);
	}
	
	@Override
	public final CommandRequestBuilder ofType(final String itemType) {
		return this;
	}
}
