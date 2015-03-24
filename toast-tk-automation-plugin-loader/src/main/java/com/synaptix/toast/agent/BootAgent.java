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

import com.synaptix.toast.core.Property;

public class BootAgent {

	private static final Logger LOG = LoggerFactory.getLogger(Boot.class);
	
	private static final String MANIFEST_SYSTEM_LOAD = "packageSystem";

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
      	loadInterestingJars(toastAgentPath);
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
    	final URL[] collectedJarsInDirectory = collectJarsInDirectory(redpepperAgentPath);
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
    		final String packageToLoad = getPackageToLoad(jarFile);
    		if(StringUtils.isNotBlank(packageToLoad)) {
    			final Collection<JarEntry> interestingJarEntries = retrieveInterestingClasses(packageToLoad, jarFile);
    			final String destPath = getRootPath() + "interestingJars.jar";
    			final File destFile = retrieveDestinationFile(destPath);
    			writeInterestingJarEntries(jarFile, interestingJarEntries, destFile);
    			final JarFile jarFileToLoad = new JarFile(destFile);
    			instrumentation.appendToSystemClassLoaderSearch(jarFileToLoad);
    			testLoadingClasses(jarFileToLoad);
    			jarFileToLoad.close();
    		}
    		jarFile.close();
    	}
    	catch(final Exception e) {
    		LOG.error(e.getMessage(), e);
    	}
    }

	private static String getPackageToLoad(final JarFile jarFile) throws IOException {
		final Manifest manifest = jarFile.getManifest();
		final Attributes attributes = manifest.getAttributes(MANIFEST_SYSTEM_LOAD);
		return attributes.getValue("id");
	}

    private static void testLoadingClasses(final JarFile jarFileToLoad) {
    	final Enumeration<JarEntry> jarEntries = jarFileToLoad.entries();
    	final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
    	while(jarEntries.hasMoreElements()) {
			final JarEntry jarEntry = jarEntries.nextElement();
			final String name = jarEntry.getName();
			if(isClass(name)) {
				final String normalizedClassName = normalizePath(name);
				try {
					LOG.info("trying to load {}", normalizedClassName);
					systemClassLoader.loadClass(normalizedClassName);
					LOG.info("success loading {}", normalizedClassName);
				}
				catch(final Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
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
		final Manifest manifestJarToLoad = buildManifest();
		final JarOutputStream jos = new JarOutputStream(new FileOutputStream(destFile), manifestJarToLoad);
		for(final JarEntry jarEntry : interestingJarEntries) {
			final InputStream is = jarFile.getInputStream(jarEntry);
			jos.putNextEntry(new JarEntry(jarEntry.getName()));
			final byte[] buffer = new byte[4096];
			int bytesRead = 0;
			while((bytesRead = is.read(buffer)) != -1) {
				jos.write(buffer, 0, bytesRead);
			}
			is.close();
			jos.flush();
			jos.closeEntry();
		}
		jos.close();
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
			LOG.info("jarEntry {}", jarEntry);
			if(isFile(jarEntry)) {
				final String name = normalizePath(jarEntry.getName());
				if(name.startsWith(packageToLoad)) {
					try {
						interestingJarEntries.add(jarEntry);
					}
					catch(final Exception e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}
		}
		return interestingJarEntries;
	}

	private static boolean isFile(final JarEntry jarEntry) {
		return !jarEntry.isDirectory();
	}

	private static String normalizePath(final String value) {
		return value.trim().replace('/', '.').replace(".class", "");
	}

    public static URL[] collectJarsInDirectory(final File directory) {
    	final List<URL> allJars = new ArrayList<URL>();
		fillJarsList(allJars, directory);
		return allJars.toArray(new URL[allJars.size()]);
	}
	
	static private void fillJarsList(
			final List<URL> jars, 
			final File dir
	) {
		try {
			for(final File jar : dir.listFiles(JAR_FILE_FILTER)) {
				jars.add(jar.toURI().toURL());
			}
		} 
		catch(final Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	static final private FileFilter JAR_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(final File pathname) {
			return pathname.isFile() && pathname.getName().endsWith(".jar");
		}
	};
}