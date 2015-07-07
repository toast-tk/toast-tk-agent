package com.synaptix.toast.swing.agent.event.message;

public class StopLoadingMessage extends LoadingMessage {

	public StopLoadingMessage(
		final String msg) {
		super(msg, 100);
	}
}