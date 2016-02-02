package com.synaptix.toast.core.agent;

import com.synaptix.toast.core.agent.config.Config;

public interface IStudioApplication {

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


}
