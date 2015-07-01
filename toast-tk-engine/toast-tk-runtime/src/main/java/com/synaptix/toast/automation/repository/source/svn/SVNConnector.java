
package com.synaptix.toast.automation.repository.source.svn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.StringUtils;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;

import com.synaptix.toast.automation.repository.source.ISourceConnector;

/**
 * SVN connector utility class
 * 
 * @author skokaina
 * 
 */
public class SVNConnector implements ISourceConnector {

	public static final String SVNURI = "http://10.61.128.222/svn/psc/trunk/";
	public static final String SVNTESTREPO = "test-repository";
	private static final SVNConnector INSTANCE = new SVNConnector();
	SVNRepository repository = null;

	private SVNConnector() {

	}

	public static SVNConnector getInstance() {
		return INSTANCE;
	}

	public SVNConnector build(String login, String pass) {
		if (repository == null) {
			DAVRepositoryFactory.setup();
			SVNURL url;
			try {
				url = SVNURL.parseURIDecoded(SVNURI + SVNTESTREPO);
				repository = SVNRepositoryFactory.create(url, null);
			} catch (SVNException e1) {
				e1.printStackTrace();
			}
		}
		ISVNAuthenticationManager authManager = new BasicAuthenticationManager(login, pass);
		repository.setAuthenticationManager(authManager);
		return INSTANCE;
	}

	/**
	 * list entries in current path
	 * 
	 * @param path
	 * @throws SVNException
	 */
	public List<String> listEntries(List<String> res, String path) throws SVNException {
		Collection<?> entries = repository.getDir(path, -1, null, (Collection<?>) null);
		Iterator<?> iterator = entries.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			// System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName());
			if (entry.getKind() == SVNNodeKind.FILE) {
				res.add(entry.getName());
			}
			if (entry.getKind() == SVNNodeKind.DIR) {
				listEntries(res, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
			}
		}

		return res;
	}

	public long getFileRevision(String filePath) throws SVNException {
		long rev = -1;
		if (isFile(filePath)) {
			rev = repository.getFile(filePath, -1, null, null); // stream not needed
		}
		return rev;
	}

	/**
	 * check if the file path corresponds to a File
	 * 
	 * @param filePath
	 * @return
	 * @throws SVNException
	 */
	public boolean isFile(String filePath) throws SVNException {
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
	 * @param path
	 * @return
	 * @throws SVNException
	 */
	public String readFile(String path) throws SVNException {
		@SuppressWarnings("rawtypes")
		Map<?, ?> fileProperties = new HashMap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		repository.getFile(path, -1, fileProperties, baos);
		return baos.toString();
	}

	public boolean commitFileChangeOnLatestRevision(String newContent, String dirPath, String fileName) throws SVNException {
		String logMessage = "log message";
		boolean success = true;
		if (repository != null) {
			String filePath = dirPath + "/" + fileName;
			ISVNEditor editor = repository.getCommitEditor(logMessage, null /* locks */, true /* keepLocks */, null /* mediator */);
			editor.openRoot(-1);
			editor.addDir(dirPath, null, -1);
			editor.addFile(filePath, null, -1);
			long fileRevision = getFileRevision(filePath);
			editor.openFile(filePath, fileRevision);
			editor.applyTextDelta(filePath, null);
			byte[] bytesUtf8 = StringUtils.getBytesUtf8(newContent);
			success = compareAndCommit(filePath, editor, fileRevision, bytesUtf8);
		}
		return success;
	}

	private boolean compareAndCommit(String filePath, ISVNEditor editor, long fileRevision, byte[] bytesUtf8) throws SVNException {
		boolean success;
		SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
		String checksum = deltaGenerator.sendDelta(filePath, new ByteArrayInputStream(bytesUtf8), editor, true);
		editor.closeFile(filePath, checksum);
		editor.closeDir();
		editor.closeDir();
		SVNCommitInfo commitInfo = editor.closeEdit();
		success = commitInfo.getNewRevision() != fileRevision;
		return success;
	}

	@Override
	public String[] getResourceListing(String path) {
		ArrayList<String> res = new ArrayList<String>();
		try {
			return listEntries(res, path).toArray(new String[res.size()]);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InputStream getFileStream(String filePath) {
		String stringRepr;
		try {
			stringRepr = readFile(filePath);
			InputStream in = new ByteArrayInputStream(stringRepr.getBytes());
			return in;
		} catch (SVNException e) {
			e.printStackTrace();
		}
		return null;
	}
}
