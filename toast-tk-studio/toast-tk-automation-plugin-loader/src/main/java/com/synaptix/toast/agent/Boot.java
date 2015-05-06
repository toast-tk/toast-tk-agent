/**Copyright (c) 2013-2015, Synaptix Labs
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Creation date: 18 f�vr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

 */

package com.synaptix.toast.agent;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kristofa.servicepluginloader.ServicePlugin;
import com.github.kristofa.servicepluginloader.ServicePluginClassPath;
import com.github.kristofa.servicepluginloader.ServicePluginLoader;
import com.github.kristofa.servicepluginloader.ServicePluginsClassPathProvider;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.guice.plugin.ToastPluginBoot;

public class Boot {

	static final Logger LOG = LoggerFactory.getLogger(Boot.class);
	
	public static void main(String[] args) {
		final ServicePluginLoader<ToastPluginBoot> bootPluginsLoader;
		//TODO: else throw big error
		//final String redpepperAgentPath = System.getProperty(Property.TOAST_PLUGIN_DIR_PROP) == null ? Property.TOAST_PLUGIN_DIR : System.getProperty(Property.TOAST_PLUGIN_DIR_PROP); 
		final String redpepperAgentPath = "C:\\Users\\PSKA09831\\.toast\\plugins";
		LOG.info("Loading swing server agent plugins from directory: " + redpepperAgentPath);
		final ServicePluginsClassPathProvider pluginsClassPathProvider = new ServicePluginsClassPathProvider() {
			@Override
			public Collection<ServicePluginClassPath> getPlugins() {
				URL[] collectJarsInDirector = collectJarsInDirector(new File(redpepperAgentPath), Thread.currentThread().getContextClassLoader());
				List<ServicePluginClassPath> plugins = new ArrayList<ServicePluginClassPath>();
				for (URL jar : collectJarsInDirector) {
					LOG.info("Found plugin jar {}", jar);
					plugins.add(new ServicePluginClassPath(jar));
				}
				return plugins;
			}
		};
		
		List<Module> pluginModules = new ArrayList<Module>();
		bootPluginsLoader = new ServicePluginLoader<ToastPluginBoot>(ToastPluginBoot.class, pluginsClassPathProvider);
		Collection<ServicePlugin<ToastPluginBoot>> load = bootPluginsLoader.load();
		for (ServicePlugin<ToastPluginBoot> servicePlugin : load) {
			ToastPluginBoot plugin = servicePlugin.getPlugin();
			plugin.boot();
			pluginModules.addAll(plugin.getModules());
		}
		Guice.createInjector(pluginModules);
		
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				 if (event.getID() == WindowEvent.WINDOW_CLOSING) {
			          System.exit(0);
				 }
			}
		}, AWTEvent.WINDOW_EVENT_MASK);
	}
	
	public static URL[] collectJarsInDirector(File directory,  ClassLoader parent) {
		List<URL> allJars = new ArrayList<URL>();
		fillJarsList(allJars, directory);
		return allJars.toArray(new URL[allJars.size()]);
	}
	
	static private void fillJarsList(List<URL> jars, File dir) {
		try {
			for (File jar : dir.listFiles(_jarsFilter)) {
				jars.add(jar.toURI().toURL());
			}
		} catch (Exception e) {
			// Should not happen
			e.printStackTrace();
		}
	}
	
	static final private FileFilter _jarsFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".jar");
		}
	};

}
