package io.toast.tk.agent.ui.verify;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfig;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;
import io.toast.tk.agent.config.AgentConfigProvider;

public class MailVerifier implements IPropertyVerifier{

    AgentConfigProvider webConfigProvider;

    @Inject
    public MailVerifier(AgentConfigProvider webConfigProvider){
        this.webConfigProvider = webConfigProvider;
    }

    @Override
    public boolean validate() {
        final AgentConfig agentConfig = webConfigProvider.get();
        
        return ConfigTesterHelper.testUserMail(agentConfig.getMailTo(), true);       
    }
}    