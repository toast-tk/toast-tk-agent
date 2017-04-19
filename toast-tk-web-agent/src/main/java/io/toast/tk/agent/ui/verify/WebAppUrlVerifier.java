package io.toast.tk.agent.ui.verify;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.ConfigTesterHelper;
import io.toast.tk.agent.ui.MainApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class WebAppUrlVerifier implements IPropertyVerifier{

    private static final Logger LOG = LogManager.getLogger(WebAppUrlVerifier.class);

    AgentConfigProvider webConfigProvider;

    @Inject
    public WebAppUrlVerifier(AgentConfigProvider webConfigProvider){
      this.webConfigProvider = webConfigProvider;
    }

    @Override
    public boolean validate() {
        AgentConfig agentConfig = webConfigProvider.get();
        if(agentConfig.getProxyActivate().equals("true")) {
            try {
                return ConfigTesterHelper.testWebAppUrl(agentConfig.getWebAppUrl(), true,
                        agentConfig.getProxyAdress(),
                        agentConfig.getProxyPort(),
                        agentConfig.getProxyUserName(),
                        agentConfig.getProxyUserPswd());
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        else{
            try {
                return ConfigTesterHelper.testWebAppUrl(agentConfig.getWebAppUrl(), true);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return false;
    }
}
