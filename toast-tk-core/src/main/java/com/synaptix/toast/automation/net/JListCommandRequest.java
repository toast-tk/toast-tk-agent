package com.synaptix.toast.automation.net;

import com.synaptix.toast.automation.net.CommandRequest.CommandRequestBuilder;

public class JListCommandRequest extends CommandRequest {

	public final JListCommandRequestQuery query;
	
	public JListCommandRequest() {
		this(null);
	}
	
	public JListCommandRequest(final JListCommandRequestQuery query) {
		this.query = query;
	}
	
	public static class TableCommandRequestBuilder extends CommandRequestBuilder {

		public TableCommandRequestBuilder(String id) {
			super(id);
		}
	}
}