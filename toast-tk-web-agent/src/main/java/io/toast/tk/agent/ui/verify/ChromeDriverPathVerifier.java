package io.toast.tk.agent.ui.verify;

import com.google.inject.Inject;
import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.agent.ui.ConfigTesterHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;


public class ChromeDriverPathVerifier implements IPropertyVerifier{

    private static final Logger LOG = LogManager.getLogger(ChromeDriverPathVerifier.class);

    AgentConfigProvider webConfigProvider;

    @Inject
    public ChromeDriverPathVerifier(AgentConfigProvider webConfigProvider){
        this.webConfigProvider = webConfigProvider;
    }

    @Override
    public boolean validate() {
        try {
            return ConfigTesterHelper.testWebAppDirectory(webConfigProvider.get().getChromeDriverPath(), true, true);
        } catch (IOException e) {
            LOG.error(e.getMessage(),e );
            return false;
        }
    }
}
