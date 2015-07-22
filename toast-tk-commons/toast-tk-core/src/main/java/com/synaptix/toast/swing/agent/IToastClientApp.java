package com.synaptix.toast.swing.agent;

import com.synaptix.toast.swing.agent.config.Config;

public interface IToastClientApp {

	public void updateStatusMessage(
		String msg);

	public void startProgress(
		String msg);

	public void updateProgress(
		String msg,
		int progress);

	public void stopProgress(
		String msg);

	public boolean isConnected();

	public Config getConfig();

	public String getRuntimeType();

	public void openConfigDialog();

	public String getRuntimeCommand();

	public String getAgentType();

	public void initProperties();
}
