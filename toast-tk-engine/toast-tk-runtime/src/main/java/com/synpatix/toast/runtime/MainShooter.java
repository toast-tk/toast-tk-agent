package com.synpatix.toast.runtime;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.synaptix.toast.automation.repository.xml.XMLWebRepository;

/**
 * main class for test only
 * 
 * @author skokaina
 * 
 */
public class MainShooter {

	public static void main(String arg[]) {
		new XMLWebRepository();
		// try {
		// DAVRepositoryFactory.setup();
		// SVNURL url = SVNURL.parseURIDecoded("http://10.61.128.222/svn/psc/trunk/test-repository");
		// SVNRepository repository = SVNRepositoryFactory.create(url, null);
		// ISVNAuthenticationManager authManager = new BasicAuthenticationManager("e416869", "sallah");
		// repository.setAuthenticationManager(authManager);
		// listEntries(repository, "");
		// System.out.println(isFile(repository, "repository/portal/logout.xml"));
		// if (isFile(repository, "repository/portal/logout.xml")) {
		// readFile(repository, "repository/portal/logout.xml");
		// }
		// } catch (SVNException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public static void listEntries(SVNRepository repository, String path) throws SVNException {
		Collection<?> entries = repository.getDir(path, -1, null, (Collection<?>) null);
		Iterator<?> iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName());
			if (entry.getKind() == SVNNodeKind.DIR) {
				listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
			}
		}
	}

	public static final boolean isFile(SVNRepository repository, String filePath) throws SVNException {
		SVNNodeKind nodeKind = repository.checkPath(filePath, -1);
		boolean isFile = true;
		if (nodeKind == SVNNodeKind.NONE) {
			System.err.println("There is no entry ");
			isFile = false;
		} else if (nodeKind == SVNNodeKind.DIR) {
			isFile = false;
			System.err.println("The entry is a directory while a file was expected.");
		}
		return isFile;
	}

	/**
	 * reads the file content and returns a byte array representation
	 * 
	 * @param repository
	 * @param path
	 * @return
	 * @throws SVNException
	 */
	public static String readFile(SVNRepository repository, String path) throws SVNException {
		@SuppressWarnings("rawtypes")
		Map<?, ?> fileProperties = new HashMap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		repository.getFile(path, -1, fileProperties, baos);
		return baos.toString();
	}
}
