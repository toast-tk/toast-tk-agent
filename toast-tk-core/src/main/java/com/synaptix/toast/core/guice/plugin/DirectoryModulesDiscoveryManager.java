package com.synaptix.toast.core.guice.plugin;

import java.io.File;
import java.util.Arrays;

public class DirectoryModulesDiscoveryManager extends ClassPathModulesDiscoveryManager {
	
	public DirectoryModulesDiscoveryManager(boolean includeSubDirs, File... directories) {
		this(Thread.currentThread().getContextClassLoader(), includeSubDirs, directories);
	}

	public DirectoryModulesDiscoveryManager(ClassLoader parent, boolean includeSubDirs, File... directories) {
		super(ClassLoaderHelper.buildClassLoader(Arrays.asList(directories), includeSubDirs, parent));
	}
}