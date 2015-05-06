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

Creation date: 5 févr. 2015
@author Sallah Kokaina <sallah.kokaina@gmail.com>

*/

package com.synaptix.toast.agent;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.toast.constant.Property;

public class BootAgent {

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private static final Logger LOG = LoggerFactory.getLogger(Boot.class);
	
	private static final String MANIFEST_SYSTEM_LOAD = "packageSystem";
	
	private static File[] EMPTY_FILES = new File[0];
	
	static final private FileFilter JAR_FILE_FILTER = new JarFileFilter();

	private static Instrumentation instrumentation;
	
	 /**
     * JVM hook to statically load the javaagent at startup.
     * 
     * After the Java Virtual Machine (JVM) has initialized, the premain method
     * will be called. Then the real application main method will be called.
     * 
     */
    public static void premain(String args, Instrumentation inst) throws Exception {
        System.out.println("Premain method invoked with args: {} and inst: {}");
        instrumentation = inst;
        final String redpepperAgentPath = getRootPath(); 
      	final File toastAgentPath = new File(redpepperAgentPath);
      	//loadInterestingJars(toastAgentPath);
        Boot.main(null);
    }
    
    public static void agentmain(String agentArgs, Instrumentation inst) {
    	System.out.println("Main method invoked with args: {} and inst: {}");
        instrumentation = inst;
        Boot.main(null);
    }

    public static void main(String[] args) {
    	Boot.main(null);
	}
    
    static void loadInterestingJars(final File redpepperAgentPath) {
    	final List<URL> collectedJarsInDirectory = collectJarsInDirectory(redpepperAgentPath);
		for(final URL jar : collectedJarsInDirectory) {
			LOG.info("Found plugin jar {}", jar);
			loadInterestingClasses(jar);
		}
    }
    
    private static String getRootPath() {
    	//return "C:\\Users\\pgpn07841\\.toast\\plugins" + "interestingJars.jar";
    	return System.getProperty(Property.TOAST_PLUGIN_DIR_PROP) == null ? Property.TOAST_PLUGIN_DIR : System.getProperty(Property.TOAST_PLUGIN_DIR_PROP);
    }
    
    static void loadInterestingClasses(final URL jar) {
    	try {
    		final JarFile jarFile = new JarFile(jar.getPath());
    		final String[] packagesToLoad = getPackagesToLoad(jarFile);
    		if(packagesToLoad != null) {
    			final int length = packagesToLoad.length;
    			for(int index = 0; index < length; ++index) {
    				final String packageToLoad = packagesToLoad[index];
    				if(StringUtils.isNotBlank(packageToLoad)) {
    					final Collection<JarEntry> interestingJarEntries = retrieveInterestingClasses(packageToLoad, jarFile);
    					final String destPath = getRootPath() + index + "_interestingJars.jar";
    					final File destFile = retrieveDestinationFile(destPath);
    					writeInterestingJarEntries(jarFile, interestingJarEntries, destFile);
    					final JarFile jarFileToLoad = new JarFile(destFile);
    					instrumentation.appendToSystemClassLoaderSearch(jarFileToLoad);
    					tryLoadingClasses(jarFileToLoad);
    					jarFileToLoad.close();
    				}
    			}
    		}
    		jarFile.close();
    	}
    	catch(final Exception e) {
    		LOG.error(e.getMessage(), e);
    	}
    }

	private static String[] getPackagesToLoad(final JarFile jarFile) throws IOException {
		final Manifest manifest = jarFile.getManifest();
		final Attributes attributes = manifest.getAttributes(MANIFEST_SYSTEM_LOAD);
		final String value = attributes != null ? attributes.getValue("id") : null;
		LOG.info("finded {}", value);
		if(value != null && value.contains(",")) {
			final String[] split = value.split(",");
			final int lenght = split.length;
			final List<String> ret = new ArrayList<String>(lenght);
			for(int index  = 0; index < lenght; ++index) {
				final String namePackageToLoad = split[index];
				if(namePackageToLoad != null && !namePackageToLoad.isEmpty()) {
					final String trim = namePackageToLoad.trim();
					if(trim != null && !trim.isEmpty()) {
						ret.add(trim);
					}
				}
			}
			return ret.toArray(new String[ret.size()]);
		}
		return new String[]{value};
	}

    private static void tryLoadingClasses(final JarFile jarFileToLoad) {
    	final Enumeration<JarEntry> jarEntries = jarFileToLoad.entries();
    	final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    	while(jarEntries.hasMoreElements()) {
			tryLoadingClass(jarEntries, systemClassLoader);
    	}
    }

	private static void tryLoadingClass(
			final Enumeration<JarEntry> jarEntries,
			final ClassLoader systemClassLoader
	) {
		final JarEntry jarEntry = jarEntries.nextElement();
		final String name = jarEntry.getName();
		if(isClass(name)) {
			final String normalizedClassName = normalizePath(name);
			tryLoadingInSystemClassLoader(systemClassLoader, normalizedClassName);
		}
	}

	private static void tryLoadingInSystemClassLoader(
			final ClassLoader systemClassLoader,
			final String normalizedClassName) {
		try {
			systemClassLoader.loadClass(normalizedClassName);
			LOG.debug("success loading {}", normalizedClassName);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static boolean isClass(final String name) {
		return name.endsWith(".class");
	}
    
	private static File retrieveDestinationFile(final String destPath) {
		final File destFile = new File(destPath);
		if(destFile.exists()) {
			final boolean delete = destFile.delete();
			LOG.info("can delete {}", delete);
		}
		return destFile;
	}

	private static void writeInterestingJarEntries(
			final JarFile jarFile,
			final Collection<JarEntry> interestingJarEntries,
			final File destFile
	) throws IOException, FileNotFoundException {
		JarOutputStream jos = null;
		try {
			final Manifest manifestJarToLoad = buildManifest();
			jos = new JarOutputStream(new FileOutputStream(destFile), manifestJarToLoad);
			for(final JarEntry jarEntry : interestingJarEntries) {
				addJarEntryToJarFile(jarFile, jos, jarEntry);
			}
		}
		finally {
			if(jos != null) {
				jos.close();
			}
		}
	}

	private static void addJarEntryToJarFile(
			final JarFile jarFile,
			final JarOutputStream jos, 
			final JarEntry jarEntry
	) {
		try {
			final InputStream is = jarFile.getInputStream(jarEntry);
			jos.putNextEntry(new JarEntry(jarEntry.getName()));
			final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int bytesRead = 0;
			while((bytesRead = is.read(buffer)) != -1) {
				jos.write(buffer, 0, bytesRead);
			}
			is.close();
			jos.flush();
			jos.closeEntry();
		}
		catch(final IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static Manifest buildManifest() {
		final Manifest manifestJarToLoad = new Manifest();
		manifestJarToLoad.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
		return manifestJarToLoad;
	}

	private static Collection<JarEntry> retrieveInterestingClasses(
			final String packageToLoad, 
			final JarFile jarFile
	) {
		final Enumeration<JarEntry> jarEntries = jarFile.entries();
		final Collection<JarEntry> interestingJarEntries = new ArrayList<JarEntry>();
		while(jarEntries.hasMoreElements()) {
			final JarEntry jarEntry = jarEntries.nextElement();
			if(isFile(jarEntry)) {
				final String name = normalizePath(jarEntry.getName());
				if(isPackageToLoad(packageToLoad, name)) {
					addJarEntry(interestingJarEntries, jarEntry);
				}
			}
		}
		return interestingJarEntries;
	}

	private static boolean isPackageToLoad(final String packageToLoad,
			final String name) {
		return name.startsWith(packageToLoad);
	}

	private static void addJarEntry(
			final Collection<JarEntry> interestingJarEntries,
			final JarEntry jarEntry) {
		try {
			interestingJarEntries.add(jarEntry);
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static boolean isFile(final JarEntry jarEntry) {
		return !jarEntry.isDirectory();
	}

	private static String normalizePath(final String value) {
		return value.trim().replace('/', '.').replace(".class", "");
	}

    private static List<URL> collectJarsInDirectory(final File directory) {
    	final File[] jarFiles = retrieveJarFiles(directory);
    	final List<URL> allJars = new ArrayList<URL>();
    	for(final File jar : jarFiles) {
			addUrlJar(allJars, jar);
		}
		return allJars;
	}
	
	private static void addUrlJar(final List<URL> jars, final File jar) {
		try {
			jars.add(jarToUrl(jar));
		}
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static URL jarToUrl(final File jar) throws MalformedURLException {
		return jar.toURI().toURL();
	}
	
	private static File[] retrieveJarFiles(final File dir) {
		final File[] jarFiles = dir.listFiles(JAR_FILE_FILTER);
		return jarFiles != null ? jarFiles : EMPTY_FILES;
	}

	private static final class JarFileFilter implements FileFilter {

		public JarFileFilter() {
		}

		@Override
		public boolean accept(final File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".jar");
		}
	}
	
	
}