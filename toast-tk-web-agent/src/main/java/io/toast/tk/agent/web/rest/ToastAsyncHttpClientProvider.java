package io.toast.tk.agent.web.rest;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfig;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.asynchttpclient.DefaultAsyncHttpClient;


public class ToastAsyncHttpClient extends DefaultAsyncHttpClient {

    @Inject
    public ToastAsyncHttpClient(AgentConfig config){
        config.getProxyActivate()
    }
}
