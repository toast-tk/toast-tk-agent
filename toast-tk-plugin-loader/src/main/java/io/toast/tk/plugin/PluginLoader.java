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
	private final File pluginDir;

	public PluginLoader(AgentConfigProvider provider) throws IllegalAccessException {
		this(provider.get().getPluginDir());
	}

	public PluginLoader(String pluginDir) throws IllegalAccessException {
		File file = new File(pluginDir);
		if(!file.exists() || !file.isDirectory()){
			throw new IllegalAccessException("Invalid directory: " + pluginDir);
		}
		this.pluginDir = file;
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
			final File pluginDir,
			ClassLoader classLoader) {
		URL[] collectJarsInDirector = collectJarsInDirector(pluginDir);
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
			for (File jar : dir.listFiles(PluginLoader::isJar)) {
				jars.add(jar.toURI().toURL());
			}
		} catch (Exception e) {
			LOG.error("Error to find jars in dir:" + dir + " - " + e.getMessage(), e);
		}
	}
	
	private static boolean isJar(File pathname){
		LOG.error("debug:" + pathname);
		LOG.error("debug:" + pathname.getName());
		LOG.error("debug:" + pathname.isFile());
		return pathname.getName() != null && pathname.isFile() && pathname.getName().endsWith(".jar");
	}
}
