package io.toast.tk.agent.web.rest;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.toast.tk.agent.config.AgentConfig;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig.Builder;
import org.asynchttpclient.Realm;
import org.asynchttpclient.Realm.AuthScheme;
import org.asynchttpclient.proxy.ProxyServer;


public class AsyncHttpClientProvider implements Provider<DefaultAsyncHttpClient> {


    private final AgentConfig config;

    @Inject
    public AsyncHttpClientProvider(AgentConfig config){
        this.config = config;
    }

    @Override
    public DefaultAsyncHttpClient get() {
        if(Boolean.valueOf(this.config.getProxyActivate())){
            String proxyPort = this.config.getProxyPort();
            int port = Strings.isNullOrEmpty(proxyPort ) ? -1 : Integer.parseInt(proxyPort);
            ProxyServer proxyServer = new ProxyServer.Builder(config.getProxyAdress(), port).build();

            Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                    .setProxyServer(proxyServer);
            
            if(!Strings.isNullOrEmpty(config.getProxyUserName()) && !Strings.isNullOrEmpty(config.getProxyUserPswd()) ){
                Realm realm = new Realm.Builder(config.getProxyUserName(), config.getProxyUserPswd())
                		.setScheme(AuthScheme.BASIC)
                		.build();
                
                builder.setRealm(realm);
            }
            
			try {
				SelfSignedCertificate ssc = new SelfSignedCertificate();
				 final SslContext sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
				builder.setSslContext(sslCtx);
			} catch (SSLException | CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
            return new DefaultAsyncHttpClient(builder.build());
        }
        return new DefaultAsyncHttpClient();
    }
}
