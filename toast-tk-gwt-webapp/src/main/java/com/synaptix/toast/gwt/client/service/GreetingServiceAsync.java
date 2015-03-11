package com.synaptix.toast.gwt.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.bean.ProjectInfoDto;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {

	void loadTestScripts(String selectedJar, AsyncCallback<List<String>> callback) throws IllegalArgumentException;

	void play(String selectedJar, String testName, AsyncCallback<String> callback) throws IllegalArgumentException;

	void loadPages(String selectedJar, AsyncCallback<List<PageInfoDto>> callback) throws IllegalArgumentException;

	void saveElementInfo(PageInfoDto currentPageInfo, ElementInfoDto dto, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void loadBackendTests(AsyncCallback<Void> callback) throws IllegalArgumentException;

	void runBackendTest(String backendTest, AsyncCallback<String> callback) throws IllegalArgumentException;

	void listAllJarsInUri(String uri, AsyncCallback<List<String>> callback) throws IllegalArgumentException;

	void listAllReports(AsyncCallback<List<ProjectInfoDto>> asyncCallback);
}
