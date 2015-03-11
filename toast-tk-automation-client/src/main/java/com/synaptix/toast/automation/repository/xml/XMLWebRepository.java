package com.synaptix.toast.automation.repository.xml;

import java.io.InputStream;

import com.synaptix.toast.automation.repository.WebRepository;
import com.synaptix.toast.automation.repository.source.ISourceConnector;
import com.synaptix.toast.automation.repository.source.svn.SVNConnector;
import com.synaptix.toast.fixture.web.AbstractSynaptixWebPage;

public class XMLWebRepository extends WebRepository<AbstractSynaptixWebPage> {

	public static final String XML_WEB_REPO_PATH = "repository/portal/"; // dont preceed with "/"
	private static final ISourceConnector srcConnector = SVNConnector.getInstance().build("e416869", "sallah"); // /put in config file

	public XMLWebRepository() {
		super();
		String[] resourceListing = srcConnector.getResourceListing(XML_WEB_REPO_PATH);
		for (String ref : resourceListing) {
			InputStream pageStream = srcConnector.getFileStream(XML_WEB_REPO_PATH + ref);
			AbstractSynaptixWebPage page = XMLSourceHelper.getHelper().getPage(pageStream);
			addPage(page.getBeanClassName(), page);
		}
	}
}
