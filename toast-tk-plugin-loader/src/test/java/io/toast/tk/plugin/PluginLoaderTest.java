package io.toast.tk.plugin;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import io.toast.tk.agent.config.AgentConfigProvider;

public class PluginLoaderTest {

	@Test
	@Ignore
	public void pluginsLoadTest(){
		AgentConfigProvider provider = new AgentConfigProvider();
		PluginLoader loader = new PluginLoader(provider);
		List<IAgentPlugin> plugins = loader.loadPlugins();
		Assert.assertEquals(plugins.size(), 1);
	}
	
}
