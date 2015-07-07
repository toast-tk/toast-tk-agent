package com.synaptix.toast.core.guice.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Module;

public class ClassPathModulesDiscoveryManager implements IModulesDiscoveryManager {

	private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

	private static final String MANIFEST_GUICE_NAME = "Guice-Modules";

	private static final Logger LOG = LogManager.getLogger(ClassPathModulesDiscoveryManager.class);

	private final List<Module> modules = new ArrayList<Module>();

	public ClassPathModulesDiscoveryManager() {
		this(Thread.currentThread().getContextClassLoader());
	}

	public ClassPathModulesDiscoveryManager(
		final ClassLoader loader) {
		LOG.info("Loading all dynamic Guice Module");
		try {
			// Get all MANIFEST files in the classpath
			final Enumeration<URL> manifests = loader.getResources(MANIFEST_PATH);
			while(manifests.hasMoreElements()) {
				addModule(loader, manifests.nextElement());
			}
		}
		catch(final IOException e) {
			LOG.error("Could not add all dynamic Modules", e);
		}
	}

	private void addModule(
		final ClassLoader loader,
		final URL manifestUrl
		) {
		InputStream input = null;
		try {
			input = manifestUrl.openStream();
			Manifest manifest = new Manifest(input);
			final String modules = manifest.getMainAttributes().getValue(MANIFEST_GUICE_NAME);
			if(modules != null) {
				for(final String module : modules.split("[ \t]+")) {
					addModule(loader, module);
				}
			}
		}
		catch(final IOException e) {
			LOG.error("addModule problem with URL: {}", manifestUrl, e);
		}
		finally {
			closeInput(input);
		}
	}

	private static void closeInput(
		final InputStream input) {
		if(input != null) {
			try {
				input.close();
			}
			catch(final IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private void addModule(
		final ClassLoader loader,
		final String module
		) {
		try {
			Class<?> clazz = Class.forName(module, true, loader);
			modules.add((Module) clazz.newInstance());
			LOG.info("Guice Module: {} dynamically added.", module);
		}
		catch(final Exception e) {
			LOG.error("addModule problem with: {}", module, e);
		}
	}

	@Override
	public Iterable<Module> getModules() {
		return modules;
	}
}
