package com.synaptix.toast.automation.runner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.synaptix.toast.automation.repository.Download;
import com.synaptix.toast.automation.utils.StreamGobbler;
import com.synaptix.toast.core.Property;
import com.synaptix.toast.swing.agent.IToastClientApp;

@Singleton
public class SutRunnerAsExec {

	private static final Logger LOG = LogManager.getLogger(SutRunnerAsExec.class);
	static final String CMD_EXE = "C:\\Windows\\System32\\cmd.exe";
	private final IToastClientApp app;

	@Inject
	public SutRunnerAsExec(final IToastClientApp app) {
		super();
		this.app = app;
	}

	protected void doAppRun(String command) {
		if (command == null) {
			LOG.info(String.format("No command to process !"));
			return;
		}

		LOG.info(String.format("Processing command %s !", command));
		Process proc = null;
		StreamGobbler outGobbler = null;
		try {
			ProcessBuilder builder = new ProcessBuilder();
			builder.command().add(CMD_EXE);
			builder.command().add("/k");
			builder.command().add("\"" + command + "\"");
			proc = builder.start();
			app.updateStatusMessage("Spawning SUT...");
			app.updateProgress("Spawning SUT...", 98);
			outGobbler = new StreamGobbler(proc.getInputStream(), "OUT", Property.TOAST_LOG_DIR + "\\stdout.txt");
			outGobbler.start();
		} catch (Exception e) {
			String out = String.format("Failed to execute cmd: %s", command);
			LOG.error(out, e);
			app.updateStatusMessage(out);
			try {
				if (outGobbler != null)
					outGobbler.join();
			} catch (InterruptedException e1) {
				LOG.error(out, e1);
				e1.printStackTrace();
			}
			if (proc != null) {
				proc.destroy();
			}
		}
	}

	public void init(final String runtimeType, final String commandBase, final String agentPath, final boolean createBat)
			throws IllegalAccessException, SAXException, IOException, ParserConfigurationException {
		if ("JNLP".equals(runtimeType)) {
			String command = lanchJnlpApp(agentPath);
			if (createBat) {
				FileWriter w = new FileWriter(Property.TOAST_HOME_DIR + "run_sut.bat");
				w.write(command + "\n");
				w.close();
			} else {
				doAppRun(command);
			}
		} else {
			JOptionPane.showMessageDialog(null, String.format("Unsupported runtime type: %s", runtimeType));
		}

	}

	private String lanchJnlpApp(final String agentPath) throws SAXException, IOException, IllegalAccessException, ParserConfigurationException {

		// 0 - create runtime dir
		final File homeDir = new File(Property.TOAST_RUNTIME_DIR);
		final String pluginDirProperty = " -DIGNORE_CLIENT_SERVER_VERSION_CHECK=true -D" + Property.TOAST_PLUGIN_DIR_PROP + "="
				+ Property.TOAST_PLUGIN_DIR;
		final String agentPathProperty = "-javaagent:" + agentPath.replace("/", "\\");
		if (!homeDir.exists()) {
			homeDir.mkdirs();
		} else {
			FileUtils.deleteDirectory(homeDir);
			homeDir.mkdirs();
		}

		// 1 - download JNLP
		String baseUri = app.getConfig().getJnlpRuntimeHost();
		String file = Download.getFile(baseUri + "/" + app.getConfig().getJnlpRuntimeFile(), homeDir.getAbsolutePath());

		File jnlpXmlF = new File(file);
		app.updateStatusMessage("Downloading: " + file);

		// 2 - parse JNLP
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		builder = builderFactory.newDocumentBuilder();
		builder.setEntityResolver(new EntityResolver() {
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (systemId.contains("JNLP-6.0.dtd")) {
					return new InputSource(new StringReader(""));
				} 
				return null;
			}
		});
		if (!jnlpXmlF.exists()) {
			throw new IllegalAccessException(String.format("JNLP File not available: %s ", jnlpXmlF.getAbsoluteFile()));
		}
		Document doc = builder.parse(FileUtils.openInputStream(jnlpXmlF));
		String rootNodeName = doc.getDocumentElement().getNodeName();
		if (!"jnlp".equals(rootNodeName)) {
			throw new IllegalAccessException(String.format("Unsupported root node: %s (expected jnlp)", rootNodeName));
		}
		NodeList nList = doc.getElementsByTagName("jar");

		// 3 - download dependencies
		float _length = nList.getLength();
		for (int _index = 0; _index < nList.getLength(); _index++) {
			Node _nNode = nList.item(_index);
			if (_nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element _eElement = (Element) _nNode;
				String _fileName = _eElement.getAttribute("href");
				String _fPath = Download.getFile(baseUri + "/" + _fileName, homeDir.getAbsolutePath());
				app.updateStatusMessage("Downloading: " + _fPath);
				float _nbFile = (_index + 1) * 100f;
				app.updateProgress("Downloading: " + _fileName, (int) (_nbFile / _length));
			}
		}

		// 4 - get arguments
		String jvmArgs = getConcatElementAttrValue(doc.getElementsByTagName("j2se"), "java-vm-args");
		String mainClass = getConcatElementAttrValue(doc.getElementsByTagName("application-desc"), "main-class");
		String AppArgsDesc = getConcatElementValue(doc.getElementsByTagName("argument"));

		if (LOG.isDebugEnabled()) {
			LOG.debug("JNLP JVM Args: " + jvmArgs);
			LOG.debug("JNLP Main Class: " + mainClass);
			LOG.debug("JNLP App Args: " + AppArgsDesc);
		}

		String debugRemoteArgs = " " + app.getConfig().getDebugArgs() + " ";

		String command = "\"" + System.getenv("JAVA_HOME") + "\\bin\\java.exe\" " + agentPathProperty + " " + pluginDirProperty + " " + jvmArgs
				+ debugRemoteArgs + " -cp " + homeDir.getAbsolutePath() + "\\* " + mainClass + " " + AppArgsDesc;
		return command;
	}

	private static String getConcatElementAttrValue(NodeList ndList, String attr) {
		String res = "";
		for (int temp = 0; temp < ndList.getLength(); temp++) {
			Node nNode = ndList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				res += eElement.getAttribute(attr) + " ";
			}
		}
		return res;
	}

	private static String getConcatElementValue(NodeList ndList) {
		String res = "";
		for (int temp = 0; temp < ndList.getLength(); temp++) {
			Node nNode = ndList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				res += eElement.getTextContent() + " ";
			}
		}
		return res;
	}

	// mvn example
	private static void launchMavenApp() {
		final String mvnExec = "C:\\MSOCache\\apache-maven-3.2.3\\bin\\mvn.bat";
		final String workingDir = "C:\\MSOCache\\workspace\\Luna\\ToastPlugin\\RUSClientBoot";
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				ProcessBuilder builder = new ProcessBuilder();
				builder.command().add(mvnExec);
				builder.directory(new File(workingDir));
				String property = "-javaagent:\"C:\\MSOCache\\git\\toast-tk\\toast-tk-automation-client\\target\\toast-tk-automation-client-1.3-rc2.jar\"";
				builder.environment().put("_JAVA_OPTIONS", property);

				if (builder.directory().exists()) {
					builder.command().add("exec:java");
					builder.command().add("-Dexec.args=--urlServer=s76cllcfakr.si.fret.sncf.fr --portServer=8081");
					builder.command().add("-Drus.inspect=true");
					builder.command().add("-DIGNORE_CLIENT_SERVER_VERSION_CHECK=true");
					builder.command().add("-Dexec.mainClass=org.java.plugin.boot.Boot");
					try {
						Process proc = builder.start();
						int waitFor = proc.waitFor();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
}
