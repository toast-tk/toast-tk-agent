package io.toast.tk.agent.ui.verify;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;


public class PluginDirectoryVerifier implements IPropertyVerifier{

    AgentConfigProvider webConfigProvider;

    @Inject
    public PluginDirectoryVerifier(AgentConfigProvider webConfigProvider){
        this.webConfigProvider = webConfigProvider;
    }

    @Override
    public boolean validate() {
        return ConfigTesterHelper.testWebAppDirectory(webConfigProvider.get().getPluginDir(), true, false);
    }
}
