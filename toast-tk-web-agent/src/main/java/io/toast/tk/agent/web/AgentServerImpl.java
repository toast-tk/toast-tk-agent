package io.toast.tk.agent.web;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Strings;
import io.toast.tk.agent.web.rest.AsyncHttpClientProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.inject.Inject;

import io.toast.tk.agent.ui.IAgentApp;
import io.toast.tk.core.agent.interpret.WebEventRecord;
import io.toast.tk.core.rest.HttpRequest;
import io.toast.tk.core.rest.RestUtils;
import org.asynchttpclient.ws.WebSocket;
import org.asynchttpclient.ws.WebSocketTextListener;
import org.asynchttpclient.ws.WebSocketUpgradeHandler;

public class AgentServerImpl implements IAgentServer {

	private static final Logger LOG = LogManager.getLogger(AgentServerImpl.class);
	private final AsyncHttpClientProvider wsSlientProvider;
	private IAgentApp app;

	@Inject
	public AgentServerImpl(IAgentApp app, AsyncHttpClientProvider wsSlientProvider){
		this.app = app;
		this.wsSlientProvider = wsSlientProvider;
	}

	public void sendEvent(WebEventRecord eventRecord, String apiKey) {
		String json = new Gson().toJson(eventRecord);
		String url = getWebAppURI() + "/api/record";
		RestUtils.postWebEventRecord(buildRequest(url, json, apiKey));
	}

	public boolean register(String apiKey) {
		try {
			String url = getWebAppURI() + "/api/susbcribe";
			String localAddress = Inet4Address.getLocalHost().getHostAddress();
			AgentInformation info = new AgentInformation(localAddress, apiKey);
			String json = new Gson().toJson(info);

			LOG.info("Registering driver: {}", json);

			boolean isRegistered = RestUtils.registerAgent(buildRequest(url, json, apiKey));

			if(isRegistered) {
				openAliveSocket(apiKey, localAddress);
			} else {
				LOG.info("The webApp does not anwser at " + url);
			}
			return isRegistered;
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
		return false;
	}

	private void openAliveSocket(String apiKey, String localAddress) {
		LOG.info("Agent registered - hotname {}", localAddress);
		//Open Alive WebSocket
		try {
            openAliveSocket(apiKey);
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e.getMessage(), e);
        }
	}

	private void openAliveSocket(String apiKey) throws InterruptedException, ExecutionException {
		final String socketUrl = (app.getConfig().getWebAppUrl() + "/api/agent/stream/"+apiKey)
				.replace("https://","wss://")
				.replace("http://", "ws://")
				.replace("//", "/")
				.replace(":/", "://");

		wsSlientProvider.get().prepareGet(socketUrl).execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
				new WebSocketTextListener() {
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
		).build()).get();
	}


	private HttpRequest buildRequest(String uri, String json, String apiKey) {
		HttpRequest request = HttpRequest.Builder.create().uri(uri).json(json).withKey(apiKey).build();
		if(Boolean.valueOf(app.getConfig().getProxyActivate())){
			String proxyPort = app.getConfig().getProxyPort();
			int port = Strings.isNullOrEmpty(proxyPort) ? -1 : Integer.parseInt(proxyPort);
			request.setProxyInfo(app.getConfig().getProxyAdress(),
					port,
					app.getConfig().getProxyUserName(),
					app.getConfig().getProxyUserPswd());
		}
		return request;
	}

	@Override
	public void unRegister() throws UnknownHostException {
		String localAddress = Inet4Address.getLocalHost().getHostAddress();
		RestUtils.unRegisterAgent(localAddress);
	}

	private String getWebAppURI(){
		String url = this.app.getConfig().getWebAppUrl();
		if(url.endsWith("/")) {
			url = (new StringBuilder(url)).deleteCharAt(url.length()-1).toString();
		}
		return url;
	}

}
