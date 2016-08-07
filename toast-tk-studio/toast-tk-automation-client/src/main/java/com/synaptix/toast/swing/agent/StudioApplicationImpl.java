package com.synaptix.toast.swing.agent;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.IStudioApplication;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.swing.agent.event.message.LoadingMessage;
import com.synaptix.toast.swing.agent.event.message.StatusMessage;
import com.synaptix.toast.swing.agent.event.message.StopLoadingMessage;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;

import io.toast.tk.core.agent.inspection.ISwingAutomationClient;

public class StudioApplicationImpl implements IStudioApplication {

	private static final Logger LOG = LogManager.getLogger(StudioApplicationImpl.class);

	private final EventBus eventBus;

	private final ISwingAutomationClient serverClient;
	
	private final IWorkspaceBuilder workspaceBuilder;

	private final Config config;

	private IStudioAppContext context;

	@Inject
	public StudioApplicationImpl(
		final Config config,
		final @StudioEventBus EventBus eventBus,
		final IWorkspaceBuilder workspaceBuilder,
		final IStudioAppContext context,
		final ISwingAutomationClient serverClient) {
		this.eventBus = eventBus;
		this.serverClient = serverClient;
		this.config = config;
		this.context = context;
		this.workspaceBuilder = workspaceBuilder;
		workspaceBuilder.initWorkspace();
		if(!serverClient.isConnectedToWebApp()) {
			displayDialogAndExitSystem(config);
		}
	}

	private void displayDialogAndExitSystem(
		final Config config) {
		String message = String.format(
			"The webapp looks down @%s:%s, please check your configuration and restart the agent !",
			config.getWebAppAddr(),
			config.getWebAppPort());
		JOptionPane.showMessageDialog(null, message);
		System.exit(-1);
	}
	
	@Override
	public void updateStatusMessage(
		String msg) {
		eventBus.post(new StatusMessage(msg));
	}

	@Override
	public void startProgress(
		String msg) {
		eventBus.post(new LoadingMessage(msg));
	}

	@Override
	public void updateProgress(
		String msg,
		int progress) {
		eventBus.post(new LoadingMessage(msg, progress));
	}

	@Override
	public void stopProgress(
		String msg) {
		eventBus.post(new StopLoadingMessage(msg));
	}

	@Override
	public boolean isConnected() {
		return serverClient.isConnected();
	}

	@Override
	public Config getConfig() {
		return this.config;
	}

	@Override
	public String getRuntimeType() {
		return this.workspaceBuilder.getRuntimeType();
	}
	
}
