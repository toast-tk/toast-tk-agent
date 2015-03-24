package com.synaptix.toast.automation.drivers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.Listener;
import com.synaptix.toast.automation.net.ExistsResponse;
import com.synaptix.toast.automation.net.IIdRequest;
import com.synaptix.toast.automation.net.InitInspectionRequest;
import com.synaptix.toast.automation.net.InitResponse;
import com.synaptix.toast.automation.net.ValueResponse;
import com.synaptix.toast.core.inspection.CommonIOUtils;
import com.synaptix.toast.fixture.facade.ClientDriver;

/**
 * Created by skokaina on 07/11/2014.
 */
public class SwingClientDriver implements ClientDriver {

	private static final Logger LOG = LoggerFactory.getLogger(SwingClientDriver.class);
	protected Client client;
	private String host;
	private static final int RECONNECTION_RATE = 10000;
	protected volatile Map<String, Object> responseMap;

	public SwingClientDriver(String host) throws IOException {
		this.client = new Client();
		this.responseMap = new HashMap<String, Object>();
		this.host = host;
		CommonIOUtils.initSerialization(client.getKryo());
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof ExistsResponse) {
					ExistsResponse response = (ExistsResponse) object;
					responseMap.put(response.id, response.exists);
				} else if (object instanceof ValueResponse) {
					ValueResponse response = (ValueResponse) object;
					responseMap.put(response.getId(), response.value);
				}
				if (object instanceof InitResponse) {
					InitResponse response = (InitResponse) object;
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
		client.start();
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
			responseMap.put(request.getId(), null);
		}
		//TODO: block any request with No ID !!
		client.sendTCP(request);
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
		client.sendTCP(request);
	}

	public void command() {

	}

	public static void main(String[] args) {
		try {
			SwingClientDriver c = new SwingClientDriver("localhost");
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
			while (responseMap.get(reqId) == null) {
				try {
					client.sendTCP(FrameworkMessage.keepAlive);
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
			while (responseMap.get(reqId) == null) {
				try {
					client.sendTCP(FrameworkMessage.keepAlive);
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
		//TODO
	}
}