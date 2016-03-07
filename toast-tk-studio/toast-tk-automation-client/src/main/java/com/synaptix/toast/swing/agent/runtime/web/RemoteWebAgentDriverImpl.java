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

public class RemoteWebAgentDriverImpl implements IRemoteSwingAgentDriver {

	private static final Logger LOG = LogManager.getLogger(RemoteWebAgentDriverImpl.class);

	protected final ITCPClient client;

	protected final String host;

	private boolean started;

	private EventBus eventBus;

	@Inject
	public RemoteWebAgentDriverImpl(
		@Named("host") String host, 
		@StudioEventBus EventBus eventBus) {
		this.client = new KryoTCPClient();
		this.started = false;
		this.host = host;
		this.eventBus = eventBus;
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
					System.out.println("Received: " + object);
				}
			}
		});
		client.addDisconnectionHandler(new ITCPResponseReceivedHandler() {
			@Override
			public void onResponseReceived(
				Object object) {
				System.out.println("Disconnected !");
			}
		});
	}

	private String buildFormat(
		WebRecordResponse response) {
			final WebEventRecord eventRecord = response.value;
			IActionInterpret interpret = InterpretationProvider.getSentenceBuilder(eventRecord.type);
			return interpret == null ? null : interpret.getSentence(eventRecord);
	}
	
	@Override
	public void start(
		String host) {
		try {
			client.connect(300000, host, CommonIOUtils.AGENT_TCP_PORT);
			if(client.isConnected()){
				this.started = true;
			}else{
				this.started = false;
			}
		}
		catch(IOException e) {
			LOG.error(e);
			this.started = false;
		}
	}

	protected void startConnectionLoop() {
		//lame hack to keep kryo connection active
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				while(true){
//					if(client.isConnected()){
//						try {
//							Thread.sleep(500);
//						}
//						catch(InterruptedException e) {
//							e.printStackTrace();
//						}
//						client.keepAlive();
//					}
//				}
//			}
//		}).start();
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