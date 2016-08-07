package com.synaptix.toast.swing.agent.runtime.web;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;

import io.toast.tk.action.interpret.web.IActionInterpret;
import io.toast.tk.action.interpret.web.InterpretationProvider;
import io.toast.tk.automation.driver.swing.KryoTCPClient;
import io.toast.tk.core.agent.inspection.CommonIOUtils;
import io.toast.tk.core.agent.interpret.InterpretedEvent;
import io.toast.tk.core.agent.interpret.WebEventRecord;
import io.toast.tk.core.driver.IRemoteSwingAgentDriver;
import io.toast.tk.core.net.request.IIdRequest;
import io.toast.tk.core.net.response.WebRecordResponse;
import io.toast.tk.core.runtime.ErrorResultReceivedException;
import io.toast.tk.core.runtime.ITCPClient;
import io.toast.tk.core.runtime.ITCPResponseReceivedHandler;
import io.toast.tk.dao.domain.api.test.ITestResult;

public class RemoteWebAgentDriverImpl implements IRemoteSwingAgentDriver {

	private static final Logger LOG = LogManager.getLogger(RemoteWebAgentDriverImpl.class);

	protected final ITCPClient client;

	protected final String host;

	private boolean started;

	private EventBus eventBus;

	private InterpretationProvider interpretationProvider;

	@Inject
	public RemoteWebAgentDriverImpl(
		@Named("host") String host, 
		@StudioEventBus EventBus eventBus,
		InterpretationProvider interpretationProvider) {
		this.client = new KryoTCPClient();
		this.started = false;
		this.host = host;
		this.eventBus = eventBus;
		this.interpretationProvider = interpretationProvider;
		initListeners();
	}

	private void initListeners() {
		client.addResponseHandler(new ITCPResponseReceivedHandler() {
			@Override
			public void onResponseReceived(
				Object object) {
				if(object instanceof WebRecordResponse) {
					WebRecordResponse result = (WebRecordResponse) object;
					String command = buildFormat(result);
					eventBus.post(new InterpretedEvent(command, 0L));
					LOG.info("Received: " + object);
				}
			}
		});
		client.addDisconnectionHandler(new ITCPResponseReceivedHandler() {
			@Override
			public void onResponseReceived(
				Object object) {
				LOG.info("Disconnect received !");
			}
		});
	}

	private String buildFormat(
		WebRecordResponse response) {
			final WebEventRecord eventRecord = response.value;
			IActionInterpret interpret = interpretationProvider.getSentenceBuilder(eventRecord.getComponent());
			return interpret == null ? null : interpret.getSentence(eventRecord);
	}
	
	@Override
	public void start(
		String host) {
		try {
			client.connect(300000, host, CommonIOUtils.AGENT_TCP_PORT);
			if(client.isConnected()){
				this.started = true;
				new Thread(new Runnable(){
					@Override
					public void run() {
						while(started){
							try {
								Thread.sleep(100);
								client.keepAlive();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
			}else{
				this.started = false;
			}
		}
		catch(IOException e) {
			LOG.error(e);
			this.started = false;
		}
	}

	public void connect() {
		try {
			client.reconnect();
			if(client.isConnected()){
				this.started = true;
			}else{
				this.started = false;
			}
		}
		catch(Exception e) {
			LOG.error(String.format("Server unreachable !"));
			this.started = false;
		}
	}

	@Override
	public void process(
		IIdRequest request) {
		checkConnection();
		client.sendRequest(request);
	}

	private void checkConnection() {
		if(!started) {
			start(host);
		}
		if(!client.isConnected()) {
			connect();
		}
	}

	@Override
	public ITestResult processAndWaitForValue(
		IIdRequest request)
		throws IllegalAccessException, TimeoutException, ErrorResultReceivedException {
		return null;
	}

	@Override
	public void stop() {
		client.close();
	}

	@Override
	public boolean waitForExist(
		String requestId)
		throws TimeoutException, ErrorResultReceivedException {
		return false;
	}
	
	public boolean isConnected(){
		return client.isConnected();
	}

	@Override
	public void init() {
		
	}

	public boolean isStarted() {
		return this.started;
	}
}