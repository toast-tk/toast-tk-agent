package io.toast.tk.plugin;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kristofa.servicepluginloader.ServicePlugin;
import com.github.kristofa.servicepluginloader.ServicePluginClassPath;
import com.github.kristofa.servicepluginloader.ServicePluginLoader;
import com.github.kristofa.servicepluginloader.ServicePluginsClassPathProvider;
import com.google.inject.Module;

import io.toast.tk.agent.config.AgentConfigProvider;

public class PluginLoader {

	static final Logger LOG = LoggerFactory.getLogger(PluginLoader.class);
	private AgentConfigProvider configProvider;
	
	public PluginLoader(AgentConfigProvider configProvider){
		this.configProvider = configProvider;
	}

	public List<IAgentPlugin> loadPlugins() {
		final String pluginsPath = this.configProvider.get().getPluginDir();
		final ServicePluginsClassPathProvider pluginsClassPathProvider = buildServicePluginsClassPathProvider(pluginsPath);
		final ServicePluginLoader<IAgentPlugin> bootPluginsLoader = new ServicePluginLoader<>(IAgentPlugin.class, pluginsClassPathProvider);
		List<IAgentPlugin> agentPlugins = collectPlugins(bootPluginsLoader); //EXTRA ACTIONS HERE
		return agentPlugins;
	}
	
	public Module[] collectGuiceModules(List<IAgentPlugin> agentPlugins){
		List<Module> pluginModules = new ArrayList<>();
		for (IAgentPlugin plugin : agentPlugins) {
			pluginModules.addAll(plugin.getModules());
		}
		return pluginModules.toArray(new Module[]{});
	}

	private static List<IAgentPlugin> collectPlugins(
			final ServicePluginLoader<IAgentPlugin> bootPluginsLoader) {
		Collection<ServicePlugin<IAgentPlugin>> load = bootPluginsLoader.load();
		for (ServicePlugin<IAgentPlugin> servicePlugin : load) {
			IAgentPlugin plugin = servicePlugin.getPlugin();
			plugin.boot();
		}
		return load.stream().map(ServicePlugin::getPlugin).collect(Collectors.toList());
	}

	private static ServicePluginsClassPathProvider buildServicePluginsClassPathProvider(
			final String pluginDir) {
		return new ServicePluginsClassPathProvider() {
			@Override
			public Collection<ServicePluginClassPath> getPlugins() {
				URL[] collectJarsInDirector = collectJarsInDirector(new File(pluginDir), Thread
						.currentThread().getContextClassLoader());
				List<ServicePluginClassPath> plugins = new ArrayList<>();
				for (URL jar : collectJarsInDirector) {
					LOG.info("Found plugin jar {}", jar);
					plugins.add(new ServicePluginClassPath(jar));
				}
				return plugins;
			}
		};
	}

	public static URL[] collectJarsInDirector(
			File directory,
			ClassLoader parent) {
		List<URL> allJars = new ArrayList<>();
		fillJarsList(allJars, directory);
		return allJars.toArray(new URL[allJars.size()]);
	}

	private static void fillJarsList(
			List<URL> jars,
			File dir) {
		try {
			for (File jar : dir.listFiles(JAR_FILTER)) {
				jars.add(jar.toURI().toURL());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static final FileFilter JAR_FILTER = new FileFilter() {
		@Override
		public boolean accept(
				File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".jar");
		}
	};
}
