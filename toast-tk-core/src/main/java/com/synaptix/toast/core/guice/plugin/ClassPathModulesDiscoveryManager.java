package com.synaptix.toast.core.guice.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Module;

public class ClassPathModulesDiscoveryManager implements IModulesDiscoveryManager {
	static private final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
	static private final String MANIFEST_GUICE_NAME = "Guice-Modules";
	static final private Logger _logger = Logger.getLogger(ClassPathModulesDiscoveryManager.class.getName());
	private final List<Module> _modules = new ArrayList<Module>();

	public ClassPathModulesDiscoveryManager() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public ClassPathModulesDiscoveryManager(ClassLoader loader) {
		_logger.log(Level.INFO, "Loading all dynamic Guice Module");
		try {
			// Get all MANIFEST files in the classpath
			Enumeration<URL> manifests = loader.getResources(MANIFEST_PATH);
			while (manifests.hasMoreElements()) {
				addModule(loader, manifests.nextElement());
			}
		} catch (IOException e) {
			// Should not happen
			_logger.log(Level.SEVERE, "Could not add all dynamic Modules", e);
		}
	}

	private void addModule(ClassLoader loader, URL manifestUrl) {
		InputStream input = null;
		try {
			input = manifestUrl.openStream();
			Manifest manifest = new Manifest(input);
			String modules = manifest.getMainAttributes().getValue(MANIFEST_GUICE_NAME);
			if (modules != null) {
				for (String module : modules.split("[ \t]+")) {
					addModule(loader, module);
				}
			}
		} catch (IOException e) {
			_logger.log(Level.SEVERE, "addModule problem with URL: " + manifestUrl, e);
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void addModule(ClassLoader loader, String module) {
		try {
			Class<?> clazz = Class.forName(module, true, loader);
			_modules.add((Module) clazz.newInstance());
			_logger.log(Level.INFO, "Guice Module: "+ module+" dynamically added.");
		} catch (Exception e) {
			_logger.log(Level.SEVERE, "addModule problem with: " + module, e);
		}
	}

	public Iterable<Module> getModules() {
		return _modules;
	}


}
