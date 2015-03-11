package com.synaptix.toast.automation.repository.source;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.synaptix.toast.automation.repository.Download;

public class ClassPathSourceConnector implements ISourceConnector {

	@Override
	public String[] getResourceListing(String path) {
		try {
			return getResourceListing(Thread.currentThread().getClass(), path);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * List directory contents for a resource folder. Not recursive. This is basically a brute-force implementation. Works for regular files and also JARs.
	 * 
	 * @author Greg Briggs
	 * @param clazz
	 *            Any java class that lives in the same place as the resources you want.
	 * @param path
	 *            Should end with "/", but not start with one.
	 * @return Just the name of each member item, not the full paths.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
		URL dirURL = clazz.getClassLoader().getResource(path);
		System.out.println("We are looking for path: " + dirURL.getPath());

		// a virer
		if (dirURL != null && dirURL.getPath().startsWith("http")) {
			String currentWebAppDir = System.getenv("webapp.current.dir") == null ? System.getProperty("webapp.current.dir") : System.getenv("webapp.current.dir");
			System.out.println("Where the new webapp current dir ? => " + currentWebAppDir);
			String myJar = dirURL.getPath().split("!")[0];
			Download.getFile(myJar, null);
			String fileName = myJar.substring(myJar.lastIndexOf('/') + 1);

			/* A JAR path */
			try {
				JarFile jar = new JarFile(URLDecoder.decode(currentWebAppDir + fileName, "UTF-8"));
				System.out.println("Where the new webapp current jar ? => " + dirURL.getPath());
				Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
				System.out.println("Where the new webapp current entries ? => " + entries.hasMoreElements());
				Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
				while (entries.hasMoreElements()) {
					String name = entries.nextElement().getName();
					if (name.startsWith(path)) { // filter according to the path
						String entry = name.substring(path.length());
						int checkSubdir = entry.indexOf("/");
						if (checkSubdir >= 0) {
							// if it is a subdirectory, we just return the directory name
							entry = entry.substring(0, checkSubdir);
						}
						if (!entry.trim().isEmpty()) {
							result.add(entry);
						}
					}
				}
				System.out.println("We have following resources: " + result);
				return result.toArray(new String[result.size()]);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		// a virer - fin

		if (dirURL != null && dirURL.getProtocol().equals("file")) {
			/* A file path: easy enough */
			return new File(dirURL.toURI()).list();
		}

		if (dirURL == null) {
			/*
			 * In case of a jar file, we can't actually find a directory. Have to assume the same jar as clazz.
			 */
			String me = clazz.getName().replace(".", "/") + ".class";
			dirURL = clazz.getClassLoader().getResource(me);
		}

		if (dirURL.getProtocol().equals("jar")) {
			System.out.println("We have a jar !!");
			/* A JAR path */
			String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); // strip out only the JAR file
			JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
			Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
			Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.startsWith(path)) { // filter according to the path
					String entry = name.substring(path.length());
					int checkSubdir = entry.indexOf("/");
					if (checkSubdir >= 0) {
						// if it is a subdirectory, we just return the directory name
						entry = entry.substring(0, checkSubdir);
					}
					if (!entry.trim().isEmpty()) {
						result.add(entry);
					}
				}
			}
			System.out.println("We have following resources: " + result);
			return result.toArray(new String[result.size()]);
		}

		throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
	}

	@Override
	public InputStream getFileStream(String filePath) {
		return getClass().getResourceAsStream(filePath);
	}
}
