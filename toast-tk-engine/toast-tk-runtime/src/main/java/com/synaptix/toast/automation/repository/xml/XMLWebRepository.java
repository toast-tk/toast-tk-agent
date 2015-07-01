
package com.synaptix.toast.automation.repository.xml;

import java.io.InputStream;

import com.synaptix.toast.adapter.web.AbstractSynaptixWebPage;
import com.synaptix.toast.automation.repository.WebRepository;
import com.synaptix.toast.automation.repository.source.ISourceConnector;
import com.synaptix.toast.automation.repository.source.svn.SVNConnector;

public class XMLWebRepository extends WebRepository<AbstractSynaptixWebPage> {

	public XMLWebRepository(String login, String password, String path) {
		super();
		ISourceConnector SOURCE_CONNECTOR = SVNConnector.getInstance().build(login, password);
		String[] resourceListing = SOURCE_CONNECTOR.getResourceListing(path);
		for (String ref : resourceListing) {
			InputStream pageStream = SOURCE_CONNECTOR.getFileStream(path + ref);
			AbstractSynaptixWebPage page = XMLSourceHelper.getHelper().getPage(pageStream);
			addPage(page.getBeanClassName(), page);
		}
	}
}
