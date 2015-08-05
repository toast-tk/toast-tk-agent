package com.synaptix.toast.swing.agent.event.message;

public class SeverStatusMessage {

	public final State state;

	public enum State {
		CONNECTED, DISCONNECTED
	}

	public SeverStatusMessage(
		State state) {
		this.state = state;
	}
}
