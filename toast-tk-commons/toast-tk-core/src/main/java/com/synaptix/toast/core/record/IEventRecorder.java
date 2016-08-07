package com.synaptix.toast.core.record;

import java.awt.Component;

import io.toast.tk.core.agent.interpret.AWTCapturedEvent;

public interface IEventRecorder {

	/**
	 * add eventInfo to recorder processing list
	 * @param captureEvent
	 */
	void appendInfo(
		AWTCapturedEvent captureEvent);

	/**
	 * Get the component locator
	 * 
	 * @param component
	 * @return
	 */
	String getComponentLocator(
		Component component);

	/**
	 * refresh the repository swing tree
	 * @param b
	 */
	void scanUi(
		boolean b);

	void startRecording()
		throws Exception;

	void stopRecording()
		throws Exception;
}
