package com.synaptix.toast.swing.agent.runtime;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
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
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.agent.IStudioApplication;
import com.synaptix.toast.core.agent.config.Config;
import com.synaptix.toast.core.annotation.craft.FixMe;
import com.synaptix.toast.utils.DownloadUtils;
import com.synaptix.toast.utils.StreamGobbler;

@Singleton
public class SutRunnerAsExec {

	private static final Logger LOG = LogManager
			.getLogger(SutRunnerAsExec.class);

	private static final String WINDOWS_SHELL = "C:\\Windows\\System32\\cmd.exe";

	private static String STREAMGOBBLER_OUTPUT_FILEPATH = Property.TOAST_LOG_DIR
			+ "\\process.out.log";

	private static String STREAMGOBBLER_ERROR_FILEPATH = Property.TOAST_LOG_DIR
			+ "\\process.err.log";

	private static String SUT_AGENT_PATH = "\\toast-tk-agent-standalone.jar";

	private static final String JNLP_DTD_VERSION = "JNLP-6.0.dtd";

	private final IStudioApplication appInstance;

	private final Config configuration;

	@Inject
	public SutRunnerAsExec(final IStudioApplication appInstance) {
		super();
		this.appInstance = appInstance;
		this.configuration = appInstance.getConfig();
	}

	public static SutRunnerAsExec FromLocalConfiguration(final Config config) {
		return new SutRunnerAsExec(config);
	}

	private SutRunnerAsExec(final Config config) {
		super();
		this.appInstance = null;
		this.configuration = config;
	}

	public Process executeSutBat() throws IllegalAccessException {
		if (SystemUtils.IS_OS_WINDOWS) {
			final String sutBatPath = Property.TOAST_HOME_DIR
					+ Property.TOAST_SUT_RUNNER_BAT;
			return executeSutBat(sutBatPath);
		} else {
			final String sutBatPath = Property.TOAST_HOME_DIR
					+ Property.TOAST_SUT_RUNNER_BAT;
			return executeSutBat(sutBatPath);
		}
	}

	public Process executeSutBat(String command) throws IllegalAccessException {
		if (SystemUtils.IS_OS_WINDOWS) {
			Process proc = null;
			StreamGobbler outGobbler = null;
			StreamGobbler errGobbler = null;
			LOG.info(String.format("Processing command %s !", command));
			try {
				proc = createSutProcess(command);
				outGobbler = createOutputStreamWriter(proc);
				errGobbler = createErrorStreamWriter(proc);
			} catch (Exception e) {
				String out = String
						.format("Failed to execute cmd: %s", command);
				LOG.error(out, e);
				if (outGobbler != null) {
					outGobbler.interrupt();
				}
				if (errGobbler != null) {
					errGobbler.interrupt();
				}
				if (proc != null) {
					proc.destroy();
				}
			}
			return proc;
		}
		throw new IllegalAccessException("Only Windows is supported !");
	}

	private StreamGobbler createOutputStreamWriter(Process proc) {
		StreamGobbler outGobbler;
		outGobbler = new StreamGobbler(proc.getInputStream(), "OUT",
				STREAMGOBBLER_OUTPUT_FILEPATH);
		outGobbler.start();
		return outGobbler;
	}

	private StreamGobbler createErrorStreamWriter(Process proc) {
		StreamGobbler outGobbler;
		outGobbler = new StreamGobbler(proc.getInputStream(), "ERR",
				STREAMGOBBLER_ERROR_FILEPATH);
		outGobbler.start();
		return outGobbler;
	}

	private Process createSutProcess(String command) throws IOException {
		Process proc;
		ProcessBuilder builder = new ProcessBuilder();
		builder.command().add(WINDOWS_SHELL);
		builder.command().add("/k");
		builder.command().add("\"" + command + "\"");
		proc = builder.start();
		return proc;
	}

	public void init(final String runtimeType, final boolean createBat)
			throws IllegalAccessException, SAXException, IOException,
			ParserConfigurationException {
		if ("JNLP".equals(runtimeType)) {
			String command = downloadDependenciesAndBuildBatCommand();
			if (createBat) {
				if (SystemUtils.IS_OS_WINDOWS) {
					FileWriter w = new FileWriter(Property.TOAST_HOME_DIR
							+ Property.TOAST_SUT_RUNNER_BAT);
					w.write(command + "\n");
					w.close();
				} else {
					FileWriter w = new FileWriter(Property.TOAST_HOME_DIR
							+ Property.TOAST_SUT_RUNNER_SH);
					w.write(command + "\n");
					w.close();
				}
			}
		} else if ("JAR".equals(runtimeType)) {
			JOptionPane.showMessageDialog(null, String.format(
					"Runtime type: %s not yet implemented !", runtimeType));
		} else if ("JVM".equals(runtimeType)) {
			JOptionPane.showMessageDialog(null, String.format(
					"Runtime type: %s not yet implemented !", runtimeType));
		} else {
			JOptionPane.showMessageDialog(null,
					String.format("Unsupported runtime type: %s", runtimeType));
		}
	}

	private String downloadDependenciesAndBuildBatCommand()
			throws SAXException, IOException, IllegalAccessException,
			ParserConfigurationException {
		final File homeDir = new File(Property.TOAST_RUNTIME_DIR);
		final String baseUri = configuration.getJnlpRuntimeHost();
		final String agentPathProperty = getAgentPathProperty();
		if (homeDir.exists()) {
			FileUtils.deleteDirectory(homeDir);
		}
		homeDir.mkdirs();
		String file = DownloadUtils.getFile(
				baseUri + "/" + configuration.getJnlpRuntimeFile(),
				homeDir.getAbsolutePath());
		File jnlpXmlF = new File(file);
		Document doc = parseJnlp(jnlpXmlF);
		NodeList nList = doc.getElementsByTagName("jar");
		downloadDependencies(homeDir, baseUri, nList);
		return createShellCommand(homeDir, agentPathProperty, doc);
	}

	private String getAgentPathProperty() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return "-javaagent:\"" + configuration.getPluginDir()
					+ SUT_AGENT_PATH.replace("/", "\\") + "\"";
		}else{
			String agentPath = "-javaagent:\"" + configuration.getPluginDir()
					+ SUT_AGENT_PATH.replace("\\", "/") + "\"";
			return agentPath.replace("//", "/");
		}
	}

	private Document parseJnlp(File jnlpXmlF)
			throws ParserConfigurationException, IllegalAccessException,
			SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		builder.setEntityResolver(new EntityResolver() {

			@Override
			public InputSource resolveEntity(String publicId, String systemId)
					throws SAXException, IOException {
				if (systemId.contains(JNLP_DTD_VERSION)) {
					return new InputSource(new StringReader(""));
				}
				return null;
			}
		});
		if (!jnlpXmlF.exists()) {
			throw new IllegalAccessException(String.format(
					"JNLP File not available: %s ", jnlpXmlF.getAbsoluteFile()));
		}
		Document doc = builder.parse(FileUtils.openInputStream(jnlpXmlF));
		if (!"jnlp".equals(doc.getDocumentElement().getNodeName())) {
			throw new IllegalAccessException(String.format(
					"Unsupported root node: %s (expected jnlp)", doc
							.getDocumentElement().getNodeName()));
		}
		return doc;
	}

	@FixMe(todo = "link java home to installed jre")
	private String createShellCommand(final File homeDir,
			final String agentPathProperty, Document doc) {
		final String jvmArgs = getConcatElementAttrValue(
				doc.getElementsByTagName("j2se"), "java-vm-args");
		final String mainClass = getConcatElementAttrValue(
				doc.getElementsByTagName("application-desc"), "main-class");
		final String AppArgsDesc = getConcatElementTextValue(doc
				.getElementsByTagName("argument"));
		if (LOG.isDebugEnabled()) {
			LOG.debug("JNLP JVM Args: " + jvmArgs);
			LOG.debug("JNLP Main Class: " + mainClass);
			LOG.debug("JNLP App Args: " + AppArgsDesc);
		}
		final String pluginDirProperty = " -DIGNORE_CLIENT_SERVER_VERSION_CHECK=true -D"
				+ Property.TOAST_PLUGIN_DIR_PROP
				+ "=\""
				+ Property.TOAST_PLUGIN_DIR + "\"";
		final String debugRemoteArgs = " " + configuration.getDebugArgs() + " ";
		String javaHome = System.getenv("TOAST_JRE_HOME");
		String cmdPrefix = SystemUtils.IS_OS_WINDOWS ? "\"" + javaHome
				+ "\\bin\\java.exe\" " : javaHome + " ";

		String command;
		if (SystemUtils.IS_OS_WINDOWS) {
			command = cmdPrefix + agentPathProperty + " " + pluginDirProperty
					+ " " + jvmArgs + debugRemoteArgs + " -cp \""
					+ homeDir.getAbsolutePath() + File.separatorChar + "*\" "
					+ mainClass + " " + AppArgsDesc;
		} else {
			command = "#!/bin/bash\n" + cmdPrefix + agentPathProperty + " " + pluginDirProperty
					+ " " + jvmArgs + debugRemoteArgs + " -cp \""
					+ homeDir.getAbsolutePath() + File.separatorChar + "*\" "
					+ mainClass + " " + AppArgsDesc;
		}

		return command;
	}

	private void downloadDependencies(final File homeDir, String baseUri,
			NodeList nList) {
		float _length = nList.getLength();
		for (int _index = 0; _index < nList.getLength(); _index++) {
			Node _nNode = nList.item(_index);
			if (_nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element _eElement = (Element) _nNode;
				String _fileName = _eElement.getAttribute("href");
				String _fPath = DownloadUtils.getFile(
						baseUri + "/" + _fileName, homeDir.getAbsolutePath());
				if (appInstance != null) {
					appInstance.updateStatusMessage("Downloading: " + _fPath);
				}
				float _nbFile = (_index + 1) * 100f;
				if (appInstance != null) {
					appInstance.updateProgress("Downloading: " + _fileName,
							(int) (_nbFile / _length));
				}
			}
		}
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

	private static String getConcatElementTextValue(NodeList ndList) {
		String res = "";
		for (int i = 0; i < ndList.getLength(); i++) {
			Node nNode = ndList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				res += eElement.getTextContent() + " ";
			}
		}
		return res;
	}

	public void launchJarInspection(File selectedFile)
			throws IllegalAccessException {
		String javaHome = System.getenv("TOAST_JRE_HOME");
		String javaBin = "\"" + javaHome + "\\bin\\java.exe\" ";
		javaBin = "java";
		String plugins = "-D" + Property.TOAST_PLUGIN_DIR_PROP + "=\""
				+ Property.TOAST_PLUGIN_DIR + "\"";
		String command = javaBin + " " + getAgentPathProperty() + " " + plugins
				+ " -jar \"" + selectedFile.getAbsolutePath() + "\"";
		System.out.println(javaBin + " " + getAgentPathProperty() + " "
				+ plugins + " -jar \"" + selectedFile.getAbsolutePath() + "\"");
		executeSutBat(command);
	}

	public static SutRunnerAsExec FromArgs(String[] args) throws ParseException {
		CommandLineParser parser = new DefaultParser();
		Options options = new Options();
		CommandLine cmd = parser.parse(options, args);
		Config config = new Config();
		config.setJnlpRuntimeHost("http://x64ertbidv3.si.fret.sncf.fr:8081/RUSystem");
		config.setJnlpRuntimeFile("RUSystem.jnlp");
		config.setDebugArgs("");
		return SutRunnerAsExec.FromLocalConfiguration(config);
	}
}
