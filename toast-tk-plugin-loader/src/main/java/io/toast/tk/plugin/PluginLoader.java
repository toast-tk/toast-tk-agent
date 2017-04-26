package io.toast.tk.plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;
import io.toast.tk.agent.config.AgentConfigProvider;

public class PluginLoader {

	private static final Logger LOG = LoggerFactory.getLogger(PluginLoader.class);
	private final String pluginDir;

	public PluginLoader(AgentConfigProvider provider) {
		this(provider.get().getPluginDir());
	}

	public PluginLoader(String pluginDir) {
		this.pluginDir = pluginDir;
	}

	private static void addSoftwareLibrary(File file,
			ClassLoader classLoader) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException {
	    Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
	    method.setAccessible(true);
	    method.invoke(classLoader, new Object[]{file.toURI().toURL()});
	}
	
	public  List<IAgentPlugin> loadPlugins(
			ClassLoader classLoader) {
		extendClassLoaderPath(pluginDir, classLoader);
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
			for (File jar : dir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".jar"))) {
				jars.add(jar.toURI().toURL());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
