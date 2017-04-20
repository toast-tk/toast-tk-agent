package io.toast.tk.agent.ui.verify;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.utils.ConfigTesterHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class ScriptsDirectoryVerifier implements IPropertyVerifier{

    private static final Logger LOG = LogManager.getLogger(ScriptsDirectoryVerifier.class);

    AgentConfigProvider webConfigProvider;

    @Inject
    public ScriptsDirectoryVerifier(AgentConfigProvider webConfigProvider){
        this.webConfigProvider = webConfigProvider;
    }

    @Override
    public boolean validate() {
        try {
            return ConfigTesterHelper.testWebAppDirectory(webConfigProvider.get().getScriptsDir(), true, false);
        } catch (IOException e) {
            LOG.error(e.getMessage(),e );
            return false;
        }
    }
}