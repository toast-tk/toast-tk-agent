package com.synaptix.toast.plugin.swing.agent.listener;

public interface ISynchronizationPoint {

	/**
	 * Indicate to the caller if the system under test is busy to process a futher command
	 * 
	 * @return
	 */
	public boolean hasToWait();
}
