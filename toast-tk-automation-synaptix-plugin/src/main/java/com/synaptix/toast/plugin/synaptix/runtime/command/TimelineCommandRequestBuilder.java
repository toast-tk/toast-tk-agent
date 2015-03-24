package com.synaptix.toast.plugin.synaptix.runtime.command;

import com.synaptix.toast.automation.net.CommandRequest;
import com.synaptix.toast.automation.net.CommandRequest.COMMAND_TYPE;
import com.synaptix.toast.automation.net.CommandRequest.CommandRequestBuilder;

public class TimelineCommandRequestBuilder extends CommandRequestBuilder {

	public TimelineCommandRequestBuilder(final String id) {
		super(id);
	}
	
	public TimelineCommandRequestBuilder selectTask() {
        this.action = COMMAND_TYPE.CLICK;
        this.customCommand = "timeline";
        return this;
	}
	
	@Override
	public CommandRequest build() {
		return new TimelineCommandRequest(this);
	}
}