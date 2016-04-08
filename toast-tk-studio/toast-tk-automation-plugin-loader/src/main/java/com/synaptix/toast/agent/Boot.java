package com.synaptix.toast.agent;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kristofa.servicepluginloader.ServicePlugin;
import com.github.kristofa.servicepluginloader.ServicePluginClassPath;
import com.github.kristofa.servicepluginloader.ServicePluginLoader;
import com.github.kristofa.servicepluginloader.ServicePluginsClassPathProvider;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.guice.AbstractComponentAdapterModule;
import com.synaptix.toast.core.guice.ICustomRequestHandler;
import com.synaptix.toast.core.guice.plugin.ToastPluginBoot;

public class Boot {

	static final Logger LOG = LoggerFactory.getLogger(Boot.class);

	public static void main(
			String[] args) {
		final ServicePluginLoader<ToastPluginBoot> bootPluginsLoader;
		final String pluginsPath = System.getProperty(Property.TOAST_PLUGIN_DIR_PROP) == null ? Config.TOAST_PLUGIN_DIR : System
				.getProperty(Property.TOAST_PLUGIN_DIR_PROP);
		LOG.info("Loading swing server agent plugins from directory: " + pluginsPath);
		final ServicePluginsClassPathProvider pluginsClassPathProvider = buildServicePluginsClassPathProvider(pluginsPath);
		List<Module> pluginModules = new ArrayList<>();
		bootPluginsLoader = new ServicePluginLoader<>(ToastPluginBoot.class, pluginsClassPathProvider);
		List<ToastPluginBoot> toastBootPlugins = collectPluginModules(bootPluginsLoader, pluginModules);
		Set<Class<?>> actionAdapters = collectActionAdapters(toastBootPlugins);

		AbstractComponentAdapterModule actionHandlersModule = new AbstractComponentAdapterModule() {
			@Override
			protected void configureModule() {
				for (Class<?> actionAdapter : actionAdapters) {
					LOG.info("New Action adapter " + actionAdapter.getName());
					this.addTypeHandler((Class<? extends ICustomRequestHandler>) actionAdapter);
				}
			}
		};
		pluginModules.add(actionHandlersModule);

		Guice.createInjector(pluginModules);
	}

	private static <E extends Enum<E>> Set<Class<?>> collectActionAdapters(List<ToastPluginBoot> pluginModules) {
		Set<Class<?>> list = new HashSet<>();
		for (ToastPluginBoot plugin : pluginModules) {
			Set<Class<?>> customActionAdapters = plugin.getCustomActionAdapters();
			if (customActionAdapters != null) {
				LOG.info("Registering new action adapters " + customActionAdapters.size());
				list.addAll(customActionAdapters);
			}
		}
		return list;
	}

	private static List<ToastPluginBoot> collectPluginModules(
			final ServicePluginLoader<ToastPluginBoot> bootPluginsLoader,
			List<Module> pluginModules) {
		Collection<ServicePlugin<ToastPluginBoot>> load = bootPluginsLoader.load();
		for (ServicePlugin<ToastPluginBoot> servicePlugin : load) {
			ToastPluginBoot plugin = servicePlugin.getPlugin();
			plugin.boot();
			pluginModules.addAll(plugin.getModules());
		}

		return load.stream().map(ServicePlugin::getPlugin).collect(Collectors.toList());
	}

	private static ServicePluginsClassPathProvider buildServicePluginsClassPathProvider(
			final String redpepperAgentPath) {
		return new ServicePluginsClassPathProvider() {

			@Override
			public Collection<ServicePluginClassPath> getPlugins() {
				URL[] collectJarsInDirector = collectJarsInDirector(new File(redpepperAgentPath), Thread
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
			// Should not happen
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
