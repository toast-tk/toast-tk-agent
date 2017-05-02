package io.toast.tk.agent.web.rest;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Provider;
import io.toast.tk.agent.config.AgentConfig;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Realm;
import org.asynchttpclient.proxy.ProxyServer;


public class ToastAsyncHttpClientProvider implements Provider<DefaultAsyncHttpClient> {


    private final AgentConfig config;

    @Inject
    public ToastAsyncHttpClientProvider(AgentConfig config){
        this.config = config;
    }

    @Override
    public DefaultAsyncHttpClient get() {
        if(Boolean.valueOf(this.config.getProxyActivate()) == true){
            String proxyPort = this.config.getProxyPort();
            int port = proxyPort == null || proxyPort.isEmpty() ? -1 : Integer.valueOf(proxyPort).intValue();
            ProxyServer proxyServer = new ProxyServer.Builder(config.getProxyAdress(), port).build();
            if(!Strings.isNullOrEmpty(config.getProxyUserName()) && !Strings.isNullOrEmpty(config.getProxyUserPswd()) ){
                Realm realm = new Realm.Builder(config.getProxyUserName(), config.getProxyUserPswd()).build();
                AsyncHttpClientConfig cf = new DefaultAsyncHttpClientConfig.Builder()
                        .setProxyServer(proxyServer).setRealm(realm).build();
                return new DefaultAsyncHttpClient(cf);
            }
            AsyncHttpClientConfig cf = new DefaultAsyncHttpClientConfig.Builder()
                    .setProxyServer(proxyServer).build();
            return new DefaultAsyncHttpClient(cf);
        }
        return new DefaultAsyncHttpClient();
    }
}
