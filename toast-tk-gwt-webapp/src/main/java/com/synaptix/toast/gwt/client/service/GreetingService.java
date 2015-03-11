package com.synaptix.toast.gwt.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.bean.ProjectInfoDto;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {

	List<String> loadTestScripts(String selectedJar) throws IllegalArgumentException;

	String play(String selectedJar, String testName) throws IllegalArgumentException;

	List<PageInfoDto> loadPages(String selectedJar) throws IllegalArgumentException;

	void saveElementInfo(PageInfoDto currentPageInfo, ElementInfoDto elementDto) throws IllegalArgumentException;

	void loadBackendTests() throws IllegalArgumentException;

	String runBackendTest(String backendTest) throws IllegalArgumentException;

	List<String> listAllJarsInUri(String uri) throws IllegalArgumentException;

	List<ProjectInfoDto> listAllReports() throws IllegalArgumentException;
}
