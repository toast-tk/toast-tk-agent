package com.synaptix.toast.gwt.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.reflections.Reflections;
import org.tmatesoft.svn.core.SVNException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.synaptix.toast.annotation.SynaptixSeleniumFixture;
import com.synaptix.toast.automation.repository.Download;
import com.synaptix.toast.automation.repository.source.svn.SVNConnector;
import com.synaptix.toast.automation.repository.xml.XMLSourceHelper;
import com.synaptix.toast.automation.repository.xml.XMLWebRepository;
import com.synaptix.toast.automation.runner.FileRedPepperRunner;
import com.synaptix.toast.automation.test.SeleniumTestScriptBase;
import com.synaptix.toast.core.IWebElement;
import com.synaptix.toast.dao.domain.impl.report.Campaign;
import com.synaptix.toast.dao.domain.impl.report.Project;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.guice.MongoModule;
import com.synaptix.toast.dao.service.dao.access.project.ProjectDaoService;
import com.synaptix.toast.fixture.web.AbstractSynaptixWebPage;
import com.synaptix.toast.fixture.web.WebAutoElement;
import com.synaptix.toast.gwt.client.bean.ElementInfoDto;
import com.synaptix.toast.gwt.client.bean.PageInfoDto;
import com.synaptix.toast.gwt.client.bean.ProjectInfoDto;
import com.synaptix.toast.gwt.client.service.GreetingService;
import com.synaptix.toast.gwt.shared.NoElementException;

/**
 * The server side implementation of the RPC service.
 */
@Singleton
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	static String baseURL = "http://genexus:8080/nexus/content/repositories/snapshots/";
	static String mPackage = "fr/gefco/tli/psc/PscTestGreenPepper/1.4-SNAPSHOT/";
	static String localWorkDir = "F:\\redpepper\\";
	static String standaloneSutJar = "PscTestGreenPepper-1.4-SNAPSHOT-jar-with-dependencies.jar";

	static String reflexionPath = "fr.gefco.tli.psc";
	ProjectDaoService projectService;

	@Inject
	public GreetingServiceImpl(ProjectDaoService.Factory pfactory) {
		try{
			projectService = pfactory.create("test_project_db");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public List<String> listAllJarsInUri(String uri) throws IllegalArgumentException {
		Document doc;
		List<String> jars = new ArrayList<String>();
		try {
			doc = Jsoup.connect(baseURL + mPackage).get();
			for (Element file : doc.select("a")) {
				String attr = file.attr("href");
				if (attr.endsWith(".jar")) {
					jars.add(attr);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jars;
	}

	@Override
	public List<String> loadTestScripts(String selectedJar) throws IllegalArgumentException {
		List<String> scripts = new ArrayList<String>();
		try {
			addPath(new URL(selectedJar), getClass());
			// addPath(new URL(baseURL + mPackage + jar), getClass());
			Reflections reflections = new Reflections("fr.gefco.tli.psc");
			@SuppressWarnings("rawtypes")
			Set<Class<? extends SeleniumTestScriptBase>> subTypes = reflections.getSubTypesOf(SeleniumTestScriptBase.class);
			for (Class<?> tCase : subTypes) {
				scripts.add(tCase.getName());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return scripts;
	}

	@SuppressWarnings("static-access")
	@Override
	public String play(String selectJar, String testName) throws IllegalArgumentException {
		System.setProperty("webapp.current.dir", getServletContext().getRealPath("/"));
		String result = "";
		try {
			downloadAndAddPath(selectJar);
			String jarFile = selectJar.split("/")[selectJar.split("/").length - 1];
			result = startSecondJVM(System.getProperty("webapp.current.dir") + jarFile, testName);
			File out = new File(System.getProperty("webapp.current.dir") + "test.out.txt");
			result = fileToString(out);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param sutJar
	 * @param testName
	 * @return
	 * @throws Exception
	 */
	private String startSecondJVM(String sutJar, String testName) throws Exception {
		return startSecondJVM(sutJar, null, null, testName);
	}

	/**
	 * 
	 * @param jarUnderTestHttpUrlPath
	 * @param testClass
	 * @throws Exception
	 */
	private static String startSecondJVM(String jarUnderTestHttpUrlPath, String outerClassPath, String mainClass, String testClass) throws Exception {
		System.out.println("Start second JVM");
		if (new File(jarUnderTestHttpUrlPath).exists()) {
			System.out.println("Jar file found");
		} else {
			System.err.println("Jar file not found");
			return null;
		}

		String separator = System.getProperty("file.separator");
		String javaPath = System.getProperty("java.home") + separator + "bin" + separator + "java";
		String classPath = "";
		String mavenPath = "F:\\Apps\\.m2";
		if (outerClassPath == null) {
			classPath = "\"" + jarUnderTestHttpUrlPath;
			classPath += ";" + mavenPath + "\\com\\synaptix\\redpepper\\redpepper-automation\\1.3-alpha\\redpepper-automation-1.3-alpha.jar";
			classPath += ";" + mavenPath + "\\commons-beanutils\\commons-beanutils\\1.8.3\\commons-beanutils-1.8.3.jar";
			classPath += ";" + mavenPath + "\\commons-logging\\commons-logging\\1.1.1\\commons-logging-1.1.1.jar";
			classPath += ";" + mavenPath + "\\com\\svnkit\\svnkit\\1.1.0\\svnkit-1.1.0.jar";
			classPath += ";F:\\Gefco\\psc_workspace\\RedPepper\\red-pepper-webapp\\target\\selenium-server-standalone-2.32.0.jar";
			classPath += ";" + mavenPath + "\\com\\synaptix\\redpepper\\redpepper-web-annotation\\1.3-alpha\\redpepper-web-annotation-1.3-alpha.jar\"";
		} else {
			classPath = outerClassPath;
		}

		if (mainClass == null) {
			mainClass = FileRedPepperRunner.class.getName();
		}
		ProcessBuilder processBuilder = null;

		if (testClass == null) {
			processBuilder = new ProcessBuilder(javaPath, "-cp", classPath, mainClass);
		} else {
			processBuilder = new ProcessBuilder(javaPath, "-cp", classPath, mainClass, testClass);
		}

		processBuilder.environment().put("webapp.current.dir", System.getProperty("webapp.current.dir"));
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		InputStream out = process.getInputStream();
		StringBuffer response = new StringBuffer();
		byte[] buffer = new byte[4000];
		while (isAlive(process)) {
			int no = out.available();
			if (no > 0) {
				int n = out.read(buffer, 0, Math.min(no, buffer.length));
				String x = new String(buffer, 0, n);
				System.out.println(x);
				response.append(x);
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(process.exitValue());
		process.waitFor();
		return response.toString();
	}

	public static boolean isAlive(Process p) {
		try {
			p.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	private void downloadAndAddPath(String path) throws Exception {
		URL jarURL = new URL(path);
		if (jarURL != null && path.startsWith("http") && path.endsWith(".jar")) {
			Download.getFile(path, null);
			addPath(jarURL, getClass());
		}
	}

	private static void addPath(URL u, Class<?> clazz) throws Exception {
		URLClassLoader urlClassLoader = (URLClassLoader) clazz.getClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { u });
	}

	public static void findTestCases() {
		Reflections reflections = new Reflections(reflexionPath);
		@SuppressWarnings("rawtypes")
		Set<Class<? extends SeleniumTestScriptBase>> subTypes = reflections.getSubTypesOf(SeleniumTestScriptBase.class);
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(SynaptixSeleniumFixture.class);
		System.out.println("Test Cases Annotated:" + subTypes.size());
		System.out.println("Entities Annotated:" + annotated.size());
	}

	private Set<Class<? extends AbstractSynaptixWebPage>> getAnnotatedSynaptixWebPages(String selectedJar) throws MalformedURLException, Exception {
		addPath(new URL(selectedJar), getClass());
		Reflections reflections = new Reflections(reflexionPath);
		return reflections.getSubTypesOf(AbstractSynaptixWebPage.class);
	}

	@Override
	public List<PageInfoDto> loadPages(String selectedJar) throws IllegalArgumentException {
		List<PageInfoDto> pagesDto = new ArrayList<PageInfoDto>();
		try {
			Set<Class<? extends AbstractSynaptixWebPage>> annotated = getAnnotatedSynaptixWebPages(selectedJar);
			System.out.println("Test Cases Annotated in project: " + annotated.size());

			// collect representation classes from svn
			SVNConnector svnConnector = SVNConnector.getInstance().build("e416869", "sallah");
			List<String> res = new ArrayList<String>();
			svnConnector.listEntries(res, XMLWebRepository.XML_WEB_REPO_PATH);
			Map<String, AbstractSynaptixWebPage> pages = new HashMap<String, AbstractSynaptixWebPage>();
			Map<String, PageInfoDto> pageDtos = new HashMap<String, PageInfoDto>();
			for (String entry : res) {
				// little nasty patch
				if (entry.startsWith("/") && entry.contains("//")) {
					entry = entry.substring(1);
					entry = entry.replace("//", "/");
				}
				String filePath = XMLWebRepository.XML_WEB_REPO_PATH + entry;
				if (entry.endsWith(".xml") && svnConnector.isFile(filePath)) {
					InputStream fileStream = svnConnector.getFileStream(filePath);
					AbstractSynaptixWebPage page = XMLSourceHelper.getHelper().getPage(fileStream);
					PageInfoDto pInfo = new PageInfoDto();
					pInfo.setDirPath(XMLWebRepository.XML_WEB_REPO_PATH);
					pInfo.setFileName(entry);
					pInfo.setFilePath(filePath); // not enough, we need to add more info, like revision number to avoid bugs while commiting changes !
					pageDtos.put(page.getBeanClassName(), pInfo);
					pages.put(page.getBeanClassName(), page);
				}
			}

			// create dtos containing all informations to be able to modify abstract pages xml svn descriptor
			for (Class<?> annotatedPageClass : annotated) {
				if (pages.containsKey(annotatedPageClass.getName()) && pageDtos.containsKey(annotatedPageClass.getName())) {
					AbstractSynaptixWebPage synaptixXmlPage = pages.get(annotatedPageClass.getName());
					PageInfoDto pInfo = pageDtos.get(annotatedPageClass.getName());
					pInfo.setName(annotatedPageClass.getSimpleName().toString());
					pInfo.setNumElements(annotatedPageClass.getFields().length - 1);
					pInfo.setShortName(annotatedPageClass.getName().toString());

					pInfo.setElements(new ArrayList<ElementInfoDto>());
					for (Field f : annotatedPageClass.getFields()) {
						if (WebAutoElement.class.isAssignableFrom(f.getType())) {
							// found counterpart field in synaptixXmlPage
							IWebElement element = synaptixXmlPage.getElement(f.getName());
							if (element != null) {
								ElementInfoDto elInfo = new ElementInfoDto();
								elInfo.setPosition(element.getPosition());
								elInfo.setMethod(element.getMethod().name());
								elInfo.setType(element.getType().name());
								elInfo.setName(element.getName());
								elInfo.setLocator(element.getLocator());
								pInfo.getElements().add(elInfo);
							} else {
								// this is a problem, we should throw a NoElementException
								throw new NoElementException(f.getName() + " for " + synaptixXmlPage.getBeanClassName() + " in " + pInfo.getFileName());
							}
						}
					}

					pagesDto.add(pInfo);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pagesDto;
	}

	@Override
	public void loadBackendTests() throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public String runBackendTest(String backendTest) throws IllegalArgumentException {
		System.out.println("Start test");
		System.setProperty("webapp.current.dir", getServletContext().getRealPath("/"));
		String result = "";
		String standaloneJar = standaloneSutJar;
		String output = "out.html";
		String workDir = localWorkDir;
		try {
			File f = new File("cTest.txt");
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.write(backendTest);
			writer.close();
			result = startSecondJVM(workDir + standaloneJar, null, "fr.gefco.tli.psc.testParser.MainTestParser", System.getProperty("webapp.current.dir") + "cTest.txt");
			File out = new File(System.getProperty("webapp.current.dir") + output);
			result = fileToString(out);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Description of the Method
	 * 
	 * @param file
	 *            The file to be turned into a String
	 * @return The file as String encoded in the platform default encoding
	 */
	public static String fileToString(File f) {
		String result = null;
		DataInputStream in = null;

		try {
			byte[] buffer = new byte[(int) f.length()];
			in = new DataInputStream(new FileInputStream(f));
			in.readFully(buffer);
			result = new String(buffer);
		} catch (IOException e) {
			throw new RuntimeException("IO problem in fileToString", e);
		} finally {
			try {
				in.close();
			} catch (IOException e) { /* ignore it */
			}
		}
		return result;
	}

	@Override
	public void saveElementInfo(PageInfoDto currentPageInfo, ElementInfoDto elementDto) throws IllegalArgumentException {
		SVNConnector svnConnector = SVNConnector.getInstance().build("e416869", "sallah");
		InputStream fileStream = svnConnector.getFileStream(currentPageInfo.getFilePath());
		String updateItemLocator = XMLSourceHelper.updateItemLocator(fileStream, elementDto.getName(), elementDto.getMethod(), elementDto.getPosition(), elementDto.getType(), elementDto.getLocator());
		try {
			System.out.println(updateItemLocator);
			svnConnector.commitFileChangeOnLatestRevision(updateItemLocator, currentPageInfo.getDirPath(), currentPageInfo.getFileName());
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ProjectInfoDto> listAllReports() throws IllegalArgumentException {
		if (projectService == null) {
			throw new IllegalArgumentException("Error, no service bound !");
		}
		List<Project> projects = projectService.find().asList();
		List<ProjectInfoDto> res = new ArrayList<ProjectInfoDto>();
		for (Project p : projects) {
			ProjectInfoDto d = new ProjectInfoDto();
			d.setName(p.getName());
			d.setInteration(String.valueOf(p.getIteration()));
			long executionTotal = 0;
			Date execDate = null;
			for (Campaign campaign : p.getCampaigns()) {
				for (TestPage testPage : campaign.getTestCases()) {
					execDate = campaign.getExecDay();
					executionTotal += testPage.getExecutionTime();
				}
			}
			d.setVersion(p.getVersion());
			d.setTotalExecutionTime(String.valueOf(executionTotal / 1000));
			SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy hh:mm");
			d.setExecutedOn(f.format(execDate));
			res.add(d);
		}
		return res;
	}
	
	public static void main(String[] args) {
		Injector i = Guice.createInjector(new MongoModule());
		i.getInstance(ProjectDaoService.Factory.class);
		
		
	}
}
