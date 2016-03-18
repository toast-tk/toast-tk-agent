package com.synaptix.toast.swing.agent.runtime.web;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.synaptix.toast.automation.driver.swing.KryoTCPClient;
import com.synaptix.toast.core.agent.inspection.CommonIOUtils;
import com.synaptix.toast.core.agent.interpret.InterpretedEvent;
import com.synaptix.toast.core.agent.interpret.WebEventRecord;
import com.synaptix.toast.core.driver.IRemoteSwingAgentDriver;
import com.synaptix.toast.core.net.request.IIdRequest;
import com.synaptix.toast.core.net.request.PoisonPill;
import com.synaptix.toast.core.net.response.WebRecordResponse;
import com.synaptix.toast.core.runtime.ErrorResultReceivedException;
import com.synaptix.toast.core.runtime.ITCPClient;
import com.synaptix.toast.core.runtime.ITCPResponseReceivedHandler;
import com.synaptix.toast.dao.domain.api.test.ITestResult;
import com.synaptix.toast.swing.agent.guice.StudioEventBus;
import com.synaptix.toast.swing.agent.interpret.MongoRepositoryCacheWrapper;
import com.synaptix.toast.swing.agent.runtime.web.interpret.IActionInterpret;
import com.synaptix.toast.swing.agent.runtime.web.interpret.InterpretationProvider;

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
			IActionInterpret interpret = interpretationProvider.getSentenceBuilder(eventRecord.component);
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
								// TODO Auto-generated catch block
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