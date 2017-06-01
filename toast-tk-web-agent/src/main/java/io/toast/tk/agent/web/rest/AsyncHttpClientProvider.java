package io.toast.tk.agent.web.rest;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.toast.tk.agent.config.AgentConfig;

import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
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
            
            if(!Strings.isNullOrEmpty(config.getProxyUserName()) && !Strings.isNullOrEmpty(config.getProxyUserPswd()) ){
                Realm realm = new Realm.Builder(config.getProxyUserName(), config.getProxyUserPswd())
                		.setScheme(AuthScheme.BASIC)
                		.build();
                
                AsyncHttpClientConfig cf = new DefaultAsyncHttpClientConfig.Builder()
                        .setProxyServer(proxyServer)
                        .setRealm(realm)
                        .build();
                return new DefaultAsyncHttpClient(cf);
            }
            
            AsyncHttpClientConfig cf = new DefaultAsyncHttpClientConfig.Builder()
                    .setProxyServer(proxyServer).build();
            return new DefaultAsyncHttpClient(cf);
        }
        return new DefaultAsyncHttpClient();
    }
}