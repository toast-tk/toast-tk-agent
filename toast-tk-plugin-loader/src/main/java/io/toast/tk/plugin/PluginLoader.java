package io.toast.tk.plugin;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

import io.toast.tk.agent.config.AgentConfigProvider;
import io.toast.tk.plugin.IAgentPlugin;

public class PluginLoader {

	private static final Logger LOG = LoggerFactory.getLogger(PluginLoader.class);
	private AgentConfigProvider configProvider;

	public PluginLoader(AgentConfigProvider provider) {
		this.configProvider = provider;
	}

	private static void addSoftwareLibrary(File file,
			ClassLoader classLoader) throws Exception {
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(classLoader, new Object[]{file.toURI().toURL()});
	}
	
	public  List<IAgentPlugin> loadPlugins(
			ClassLoader classLoader) {
		extendClassLoaderPath(configProvider.get().getPluginDir(), classLoader);
		ServiceLoader<IAgentPlugin> loader = ServiceLoader.load(IAgentPlugin.class, classLoader);
		List<IAgentPlugin> list = new ArrayList<>();
		loader.forEach(list::add);
		LOG.info("Found {} plugins !", list.size());
		return list;
	}
	
	public Module[] collectGuiceModules(List<IAgentPlugin> agentPlugins){
		List<Module> pluginModules = new ArrayList<>();
		for (IAgentPlugin plugin : agentPlugins) {
			pluginModules.addAll(plugin.getModules());
		}
		return pluginModules.toArray(new Module[]{});
	}

	private static void  extendClassLoaderPath(
			final String pluginDir,
			ClassLoader classLoader) {
		URL[] collectJarsInDirector = collectJarsInDirector(new File(pluginDir));
		for (URL jar : collectJarsInDirector) {
			LOG.info("Found plugin jar {}", jar);
			try {
				addSoftwareLibrary(new File(jar.toURI()), classLoader);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	public static URL[] collectJarsInDirector(
			File directory) {
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
