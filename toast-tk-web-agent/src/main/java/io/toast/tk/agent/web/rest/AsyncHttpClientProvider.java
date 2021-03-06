package io.toast.tk.agent.web.rest;

import javax.net.ssl.SSLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;
import org.asynchttpclient.Realm;
import org.asynchttpclient.Realm.AuthScheme;
import org.asynchttpclient.netty.ssl.InsecureTrustManagerFactory;
import org.asynchttpclient.proxy.ProxyServer;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;

import io.netty.handler.ssl.SslContextBuilder;
import io.toast.tk.agent.config.AgentConfig;

public class AsyncHttpClientProvider implements Provider<DefaultAsyncHttpClient> {

	private static final Logger LOG = LogManager.getLogger(AsyncHttpClientProvider.class);
	private final AgentConfig config;

	@Inject
	public AsyncHttpClientProvider(AgentConfig config) {
		this.config = config;
	}

	@Override
	public DefaultAsyncHttpClient get() {
		if (Boolean.valueOf(this.config.getProxyActivate())) {
			String proxyPort = this.config.getProxyPort();
			int port = Strings.isNullOrEmpty(proxyPort) ? -1 : Integer.parseInt(proxyPort);
			ProxyServer proxyServer = new ProxyServer.Builder(config.getProxyAdress(), port).build();

			Builder builder = new DefaultAsyncHttpClientConfig.Builder().setProxyServer(proxyServer);

			if (!Strings.isNullOrEmpty(config.getProxyUserName())
					&& !Strings.isNullOrEmpty(config.getProxyUserPswd())) {
				Realm realm = new Realm.Builder(config.getProxyUserName(), config.getProxyUserPswd())
						.setScheme(AuthScheme.BASIC).build();
				builder.setRealm(realm);
			}

			try {
				if (proxyServer.getHost().startsWith("https")) {
					builder.setSslContext(
							SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build());
				}
			} catch (SSLException e) {
				LOG.error(e.getMessage(), e);
			}

			return new DefaultAsyncHttpClient(builder.build());
		}
		return new DefaultAsyncHttpClient();
	}
}
