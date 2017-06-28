package io.toast.tk.agent.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;

public class WebAppSocketListener implements WebSocketTextListener {
	

	private static final Logger LOG = LogManager.getLogger(WebAppSocketListener.class);
	
	@Override
	public void onOpen(WebSocket webSocket) {
		//NO-OP
	}

	@Override
	public void onClose(WebSocket webSocket) {
		//NO-OP
	}

	@Override
	public void onError(Throwable throwable) {
		LOG.error(throwable.getMessage(), throwable);
	}

	@Override
	public void onMessage(String s) {
		//NO-OP
	}
}