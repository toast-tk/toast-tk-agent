package com.synaptix.toast.automation.driver.swing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.synaptix.toast.core.agent.inspection.CommonIOUtils;
import com.synaptix.toast.core.driver.IClientDriver;
import com.synaptix.toast.core.net.request.IIdRequest;
import com.synaptix.toast.core.net.request.InitInspectionRequest;
import com.synaptix.toast.core.net.response.ExistsResponse;
import com.synaptix.toast.core.net.response.InitResponse;
import com.synaptix.toast.core.net.response.ValueResponse;
import com.synaptix.toast.core.runtime.ITCPClient;
import com.synaptix.toast.core.runtime.ITCPResponseReceivedHandler;

public class SwingClientDriver implements IClientDriver {
	
	//TODO: add wait loop timeout !
	private static final Logger LOG = LogManager.getLogger(SwingClientDriver.class);
	protected ITCPClient client;
	private final String host = "localhost";
	private static final int RECONNECTION_RATE = 10000;
	protected volatile Map<String, Object> responseMap;
	private final Object VOID_RESULT = new Object();


	public SwingClientDriver() {
		this.client = new KryoTCPClient();
		this.responseMap = new HashMap<String, Object>();
		initListeners();
		start();
	}

	private void initListeners() {
		client.addResponseHandler(new ITCPResponseReceivedHandler(){
			@Override
			public void onResponseReceived(Object object) {
				if (object instanceof ExistsResponse) {
					ExistsResponse response = (ExistsResponse) object;
					responseMap.put(response.id, response.exists);
				} else if (object instanceof ValueResponse) {
					ValueResponse response = (ValueResponse) object;
					responseMap.put(response.getId(), response.value);
				}
				if (object instanceof InitResponse) {
					if(LOG.isDebugEnabled()){
						InitResponse response = (InitResponse) object;
						LOG.debug(response);
					}
				}
				else {
					if (object instanceof IIdRequest) {
						handleResponse((IIdRequest)object);
					}else if(!(object instanceof KeepAlive)){
						LOG.warn(String.format("Unhandled response: %s", object));
					}
				}				
			}
		});
	}

	@Override
	public void start() {
		try {
			client.connect(300000, host, CommonIOUtils.TCP_PORT);
		} catch (IOException e) {
			startConnectionLoop();
		}
	}
	
	protected void startConnectionLoop() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!client.isConnected()) {
					connect();
					try {
						Thread.sleep(RECONNECTION_RATE);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public void connect() {
		try {
			client.reconnect();
		} catch (Exception e) {
			LOG.error(String.format("Server unreachable, reattempting to connect in %d !", RECONNECTION_RATE/1000));
		}
	}

	@Override
	public void process(IIdRequest request) {
		if (!client.isConnected()) {
			connect();
		}
		init();
		if (request.getId() != null) {
			responseMap.put(request.getId(), VOID_RESULT);
		}
		//TODO: block any request with No ID !!
		client.sendRequest(request);
	}

	/**
	 * to call before any request
	 * 
	 * @return
	 */
	public void init() {
		if(!client.isConnected()){
			connect();
		}
		InitInspectionRequest request = new InitInspectionRequest();
		client.sendRequest(request);
	}

	public void command() {

	}

	public static void main(String[] args) {
		try {
			SwingClientDriver c = new SwingClientDriver();
			c.start();
			c.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean waitForExist(String reqId) {
		boolean res = false;
		if (responseMap.containsKey(reqId)) {
			while (VOID_RESULT.equals(responseMap.get(reqId))) {
				try {
					client.keepAlive();
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			res = (Boolean) responseMap.get(reqId);
			responseMap.remove(reqId);
		}
		return res;

	}

	@Override
	public String waitForValue(String reqId) {
		String res = null;
		if (responseMap.containsKey(reqId)) {
			while (VOID_RESULT.equals(responseMap.get(reqId))) {
				try {
					client.keepAlive();
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			res = (String) responseMap.get(reqId);
			responseMap.remove(reqId);
		}
		return res;
	}
	
	protected void handleResponse(IIdRequest response){
	}

	@Override
	public void stop() {
		client.close();
	}
}