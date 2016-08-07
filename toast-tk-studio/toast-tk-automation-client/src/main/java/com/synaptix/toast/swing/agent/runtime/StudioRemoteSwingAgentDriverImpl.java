package com.synaptix.toast.swing.agent.runtime;

import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.agent.config.WebConfig;
import com.synaptix.toast.swing.agent.event.message.SeverStatusMessage;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;
import com.synaptix.toast.swing.agent.runtime.web.RemoteWebAgentDriverImpl;

import io.toast.tk.action.interpret.web.InterpretationProvider;
import io.toast.tk.automation.driver.swing.RemoteSwingAgentDriverImpl;
import io.toast.tk.core.agent.inspection.ISwingAutomationClient;
import io.toast.tk.core.agent.interpret.IEventInterpreter;
import io.toast.tk.core.agent.interpret.InterpretedEvent;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.core.net.request.HighLightRequest;
import io.toast.tk.core.net.request.IIdRequest;
import io.toast.tk.core.net.request.PoisonPill;
import io.toast.tk.core.net.request.RecordRequest;
import io.toast.tk.core.net.request.ScanRequest;
import io.toast.tk.core.net.response.RecordResponse;
import io.toast.tk.core.net.response.ScanResponse;
import io.toast.tk.core.runtime.ITCPResponseReceivedHandler;
import io.toast.tk.swing.agent.interpret.LiveRedPlayEventInterpreter;
import io.toast.tk.swing.agent.interpret.MongoRepositoryCacheWrapper;

//FIXME: split the class in 2: distinguer le recorder web du recordeur swing !
public class StudioRemoteSwingAgentDriverImpl extends RemoteSwingAgentDriverImpl implements ISwingAutomationClient {

	private static final Logger LOG = LogManager.getLogger(StudioRemoteSwingAgentDriverImpl.class);

	private EventBus eventBus;

	private String previousInput;

	private IEventInterpreter interpreter;

	private RemoteWebAgentDriverImpl webDriver;

	private WebConfig webConfig;

	private boolean isWebMode;
	
	private InterpretationProvider interpretationProvider;


	public StudioRemoteSwingAgentDriverImpl(String host) throws IOException {
		super(host);
	}

	@Inject
	public StudioRemoteSwingAgentDriverImpl(final @StudioEventBus EventBus eventBus, final Config config,
			final WebConfig webConfig, final MongoRepositoryCacheWrapper mongoRepoManager,
			 InterpretationProvider interpretationProvider) throws IOException {
		this("localhost");
		this.eventBus = eventBus;
		this.webConfig = webConfig;
		this.interpretationProvider = interpretationProvider;
		client.addConnectionHandler(new ITCPResponseReceivedHandler() {
			@Override
			public void onResponseReceived(Object object) {
				eventBus.post(new SeverStatusMessage(SeverStatusMessage.State.CONNECTED));
			}
		});
		client.addDisconnectionHandler(new ITCPResponseReceivedHandler() {
			@Override
			public void onResponseReceived(Object object) {
				eventBus.post(new SeverStatusMessage(SeverStatusMessage.State.DISCONNECTED));
			}
		});
		this.interpreter = new LiveRedPlayEventInterpreter(mongoRepoManager, config.getWebAppAddr(), config.getWebAppPort());
	}

	@Override
	public void highlight(String selectedValue) {
		process(new HighLightRequest(selectedValue));
	}

	@Override
	public void scanUi(final boolean selected) {
		final String requestId = UUID.randomUUID().toString();
		ScanRequest scanRequest = new ScanRequest(requestId, selected);
		client.sendRequest(scanRequest);
	}

	@Override
	protected void handleResponse(IIdRequest response) {
		if (response instanceof ScanResponse) {
			eventBus.post((ScanResponse) response);
		} else if (response instanceof RecordResponse) {
			RecordResponse result = (RecordResponse) response;
			if (result.getSentence() != null) {
				eventBus.post(new InterpretedEvent(result.getSentence()));
			} else {
				String command = buildFormat(result);
				if (command != null && !command.equals(previousInput)) {
					eventBus.post(new InterpretedEvent(command, result.value.timeStamp));
				}
				previousInput = command;
			}
		}

	}

	@Override
	public void startRecording() {
		client.sendRequest(new RecordRequest(true));
	}

	@Override
	public void stopRecording() {
		if (webDriver != null) {
			webDriver.stop();
		} else {
			client.sendRequest(new RecordRequest(false));
		}
	}

	@Override
	public void setMode(int mode) {
	}

	private String buildFormat(RecordResponse response) {
		switch (response.value.getEventType()) {
		case BUTTON_CLICK:
			return interpreter.onButtonClick(response.value);
		case RADIO_CLICK:
			return interpreter.onButtonClick(response.value);
		case CHECKBOX_CLICK:
			return interpreter.onCheckBoxClick(response.value);
		case CLICK:
			return interpreter.onClick(response.value);
		case TABLE_CLICK:
			return interpreter.onTableClick(response.value);
		case MENU_CLICK:
			return interpreter.onMenuClick(response.value);
		case COMBOBOX_CLICK:
			return interpreter.onComboBoxClick(response.value);
		case WINDOW_DISPLAY:
			return interpreter.onWindowDisplay(response.value);
		case KEY_INPUT:
			return interpreter.onKeyInput(response.value);
		case BRING_ON_TOP_DISPLAY:
			return interpreter.onBringOnTop(response.value);
		case POPUP_MENU_CLICK:
			return interpreter.onPopupMenuClick(response.value);
		default:
			return "unhandled event interpretation !";
		}
	}

	@Override
	public void processCustomCommand(String command) {
		CommandRequest request = new CommandRequest.CommandRequestBuilder(null).asCustomCommand(command).build();
		client.sendRequest(request);
	}

	@Override
	public void processCustomCommand(IIdRequest request) {
		client.sendRequest(request);
	}

	@Override
	public void killServer() {
		LOG.info("Terminating inspection server - Poison Pill !");
		client.sendRequest(new PoisonPill());
		if (webDriver == null) {
			webDriver = new RemoteWebAgentDriverImpl("localhost", eventBus, interpretationProvider);
			webDriver.start("localhost");
			if (webDriver.isConnected()) {
				webDriver.process(new PoisonPill());
			}
			webDriver = null;
		}
	}

	@Override
	public boolean saveObjectsToRepository() {
		if (interpreter instanceof LiveRedPlayEventInterpreter) {
			return ((LiveRedPlayEventInterpreter) interpreter).saveObjectsToRepository();
		} else {
			LOG.info("Current interpreter doesn't support repository update operation: "
					+ interpreter.getClass().getSimpleName());
			return false;
		}
	}

	@Override
	public boolean isConnected() {
		if (webDriver != null) {
			return client.isConnected() || webDriver.isConnected();
		}
		return client.isConnected();
	}

	@Override
	public boolean isConnectedToWebApp() {
		return interpreter.isConnectedToWebApp();
	}

	@Override
	public void switchToSwingRecordingMode() {
		System.out.println("swing recording mode");
	}

	@Override
	public void switchToWebRecordingMode() {
		this.isWebMode = true;
	}

	@Override
	public boolean isWebMode() {
		return this.isWebMode;
	}

	@Override
	public void startWebRecording(String url) {
		if (this.isWebMode) {
			initRemoteWebRecordingAgent();
		}
	}

	private void initRemoteWebRecordingAgent() {
		if (this.webDriver == null) {
			webDriver = new RemoteWebAgentDriverImpl("localhost", eventBus, interpretationProvider);
		}
		if (!this.webDriver.isConnected()) {
			webDriver.start("localhost");
		}
	}


	@Override
	public void disconnect() {
		if (this.isWebMode()) {
			if (this.webDriver != null) {
				this.webDriver.stop();
			}
		} else {
			// swing mode
			super.stop();
		}
	}

	@Override
	public void connect() {
		if (this.isWebMode()) {
			startWebRecording(null);
		} else {
			start(host);
		}
	}
}
