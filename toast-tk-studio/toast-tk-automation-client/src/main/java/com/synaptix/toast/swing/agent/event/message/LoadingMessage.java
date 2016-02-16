package com.synaptix.toast.swing.agent.event.message;

public class LoadingMessage {

	public final String msg;

	public final int progress;

	public LoadingMessage(
		final String msg) {
		this.msg = msg;
		this.progress = 0;
	}

	public LoadingMessage(
		final String msg,
		final int progress) {
		this.msg = msg;
		this.progress = progress;
	}
}
