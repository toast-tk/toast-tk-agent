package io.toast.tk.agent.ui.verify;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.ui.ConfigTesterHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.toast.tk.agent.config.AgentConfigProvider;

import java.io.IOException;


public class WebRecordingVerifier implements IPropertyVerifier{
    private static final Logger LOG = LogManager.getLogger(WebRecordingVerifier.class);

    AgentConfigProvider webConfigProvider;

    @Inject
    public WebRecordingVerifier(AgentConfigProvider webConfigProvider){
        this.webConfigProvider = webConfigProvider;
    }

    @Override
    public boolean validate() {
        try {
            final AgentConfig agentConfig = webConfigProvider.get();
            if(agentConfig.getProxyActivate().equals("true")) {
            return ConfigTesterHelper.testWebAppUrl(agentConfig.getWebInitRecordingUrl(), true,
                    agentConfig.getProxyAdress(),
                    agentConfig.getProxyPort(),
                    agentConfig.getProxyUserName(),
                    agentConfig.getProxyUserPswd());
            }
            else{
                return ConfigTesterHelper.testWebAppUrl(agentConfig.getWebInitRecordingUrl(), true);
            }
            
        } catch (IOException e) {
            LOG.error(e.getMessage(),e );
            return false;
        }
    }
}    