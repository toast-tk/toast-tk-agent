package io.toast.tk.agent.ui.verify;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.ConfigTesterHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class PluginDirectoryVerifier implements IPropertyVerifier{

    private static final Logger LOG = LogManager.getLogger(PluginDirectoryVerifier.class);

    AgentConfigProvider webConfigProvider;

    @Inject
    public PluginDirectoryVerifier(AgentConfigProvider webConfigProvider){
        this.webConfigProvider = webConfigProvider;
    }

    @Override
    public boolean validate() {
        try {
            return ConfigTesterHelper.testWebAppDirectory(webConfigProvider.get().getPluginDir(), true, false);
        } catch (IOException e) {
            LOG.error(e.getMessage(),e );
            return false;
        }
    }
}
