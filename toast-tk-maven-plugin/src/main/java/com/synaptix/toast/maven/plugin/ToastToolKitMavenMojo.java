package com.synaptix.toast.maven.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.json.JSONArray;
import org.json.JSONException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.synaptix.toast.core.Property;
import com.synaptix.toast.core.annotation.Check;
import com.synaptix.toast.core.annotation.Fixture;
import com.synaptix.toast.core.annotation.FixtureSentenceRef;
import com.synaptix.toast.core.dao.fixture.FixtureDescriptor;
import com.synaptix.toast.core.dao.fixture.FixtureDescriptorLine;
import com.synaptix.toast.core.rest.RestUtils;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.VALIDATE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ToastToolKitMavenMojo extends AbstractMojo {

	@Parameter(defaultValue = "${basedir}/src/main/resources/settings", required = true)
	private File outputResourceDirectory;

	@Parameter(defaultValue = "${project.build.directory}", required = true)
	private String buildDir;

	@Parameter(required = false, defaultValue = "toast_settings.json")
	private String settingFileName;

	@Parameter(required = true, alias = "output-package")
	private String outputPackage;

	@Parameter(required = false, alias = "includePatternSentences", defaultValue = "false")
	private Boolean includePatternSentences;

	@Parameter(required = true, alias = "webAppUrl", defaultValue = Property.DEFAULT_WEBAPP_ADDR_PORT)
	private String host;

	@Parameter(required = true, defaultValue = "${project}", readonly = true)
	MavenProject project;

	public void execute() throws MojoExecutionException {
		if (outputResourceDirectory != null && !outputResourceDirectory.exists()) {
			outputResourceDirectory.mkdir();
		} else if (outputResourceDirectory == null) {
			throw new MojoExecutionException("Can't find /src/main/resources directory, please create it!");
		}

		getLog().info("Toast Tk Maven Plugin - Files will be generated in package " + outputPackage);
		getLog().info("Toast Tk Maven Plugin - Connecting to -> " + host);
		try {
			String repository = RestUtils.downloadRepository(host + "/loadWikifiedRepository");
			File scenarioImplFile = new File(outputResourceDirectory, "toast_imported_repository.txt");
			writeFile(scenarioImplFile, repository);
			Client httpClient = Client.create();
			Set<Driver> drivers = downloadScenarii(host + "/wikiScenarii", httpClient);
			StringBuilder builder = new StringBuilder();
			builder.append("{ \"settings\" : [");
			for (Driver driver : drivers) {
				List<Sentence> sentences = new ArrayList<ToastToolKitMavenMojo.Sentence>();
				List<Sentence> dynamicSentences = downloadDynamicSentences(driver, httpClient);
				if (includePatternSentences) {
					List<Sentence> staticSentences = downloadSentences(driver, httpClient);
					sentences.addAll(staticSentences);
				}
				sentences.addAll(dynamicSentences);
				builder.append(writeDriverJson(driver, sentences)).append(",");
			}
			builder.append("]}");

			try {
				// common driver file
				File driverJson = new File(outputResourceDirectory, settingFileName);
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				JsonParser jp = new JsonParser();
				String v = builder.toString().replace(",]}", "]}");
				JsonElement je = jp.parse(v);
				String prettyJsonString = gson.toJson(je);
				writeFile(driverJson, prettyJsonString);
			} catch (MojoExecutionException e) {
				e.printStackTrace();
			}

			try {
				publishAvailableSentences();
			} catch (Exception e) {
				getLog().error(e);
			}
			getLog().info("Toast Tk Maven Plugin - Update completed !");
		} catch (Exception e) {
			getLog().error("Toast Tk Maven Plugin - Update cancelled !");
			getLog().error(e);
		}
	}

	/**
	 * 
	 * @return
	 */
	public void publishAvailableSentences() {
		String sourceDirectory = project.getBuild().getSourceDirectory();
		String srcDirectory = project.getBasedir() + sourceDirectory.substring(sourceDirectory.indexOf(":") + 1);
		getLog().info("Source directory:" + srcDirectory);
		URL url;
		try {
			File file = new File(srcDirectory);
			Iterator<File> iterateFiles = FileUtils.iterateFiles(file, new String[] { "java" }, true);
			for (; iterateFiles.hasNext();) {
				String fixtureKind = null;
				String fixtureName = null;
				final List<FixtureDescriptorLine> sentences = new ArrayList<FixtureDescriptorLine>();
				File javaFile = iterateFiles.next();
				String readFileToString = FileUtils.readFileToString(javaFile, Charset.forName("UTF-8"));
				if (readFileToString.contains("@Fixture") && readFileToString.contains("@Check")) {
					getLog().info("Toast Tk Maven Plugin - Found connector: " + javaFile.getName());
					
					String[] splitedFile = readFileToString.split("\n");
					for (int i = 0; i < splitedFile.length; i++) {
						String line = splitedFile[i].trim();
						if(fixtureKind == null && fixtureName == null){
							if (line.startsWith("@Fixture")) {
								String regex = "@Fixture\\(value[\\s]*=[\\s]*FixtureKind\\.(swing|service|web)[\\s]*,[\\s]*name=\\\"([\\w\\W]+)\\\"[\\s]*\\)";
								Pattern pattern = Pattern.compile(regex);
								Matcher matcher = pattern.matcher(line);
								if (matcher.find()) {
									fixtureKind = matcher.group(1);
									fixtureName = matcher.group(2);
								}
							}else{
								continue;
							}
						}
						if (fixtureKind != null && fixtureName != null) {
							if (line.startsWith("@Check")) {
								String sentence = line.substring("@Check".length() + 1, line.length() - 1);
								sentence = sentence.replace("\"+VALUE_REGEX+\"", FixtureSentenceRef.VALUE_REGEX);
								sentence = sentence.replace("\"+VAR_OR_VALUE_REGEX+\"", FixtureSentenceRef.VAR_OR_VALUE_REGEX);
								sentence = sentence.replace("\"+SWING_COMPONENT_REGEX+\"", FixtureSentenceRef.SWING_COMPONENT_REGEX);
								sentence = sentence.replace("\"+VAR_IN_REGEX+\"", FixtureSentenceRef.VAR_IN_REGEX);
								sentence = sentence.replace("\"+VAR_OUT_REGEX+\"", FixtureSentenceRef.VAR_OUT_REGEX);
								
								sentence = sentence.replace("\"+VALUE_REGEX", FixtureSentenceRef.VALUE_REGEX);
								sentence = sentence.replace("\"+VAR_OR_VALUE_REGEX", FixtureSentenceRef.VAR_OR_VALUE_REGEX);
								sentence = sentence.replace("\"+SWING_COMPONENT_REGEX", FixtureSentenceRef.SWING_COMPONENT_REGEX);
								sentence = sentence.replace("\"+VAR_IN_REGEX", FixtureSentenceRef.VAR_IN_REGEX);
								sentence = sentence.replace("\"+VAR_OUT_REGEX", FixtureSentenceRef.VAR_OUT_REGEX);
								FixtureDescriptorLine descriptorLine = new FixtureDescriptorLine(fixtureName, fixtureKind, sentence);
								sentences.add(descriptorLine);
							}
						}
					}
					
				}

				if (sentences.size() > 0) {
					Gson gson = new Gson();
					FixtureDescriptor descriptor = new FixtureDescriptor(project.getName(), sentences);
					String json = gson.toJson(descriptor);
					RestUtils.post(host + "/postConnector", json);
				}
			}

			// url = file.toURI().toURL();
			// final List<FixtureDescriptor> out = new
			// ArrayList<FixtureDescriptor>();
			// URLClassLoader urlcl = new URLClassLoader(new URL[]{url},
			// ToastToolKitMavenMojo.class.getClassLoader());
			// Reflections reflections = new Reflections(
			// new ConfigurationBuilder().setUrls(
			// ClasspathHelper.forClassLoader(urlcl)
			// ).addClassLoader(urlcl).setScanners(new
			// MethodAnnotationsScanner())
			// );
			//
			// final Set<Method> methodsAnnotatedWith =
			// reflections.getMethodsAnnotatedWith(Check.class);
			// for (Method method : methodsAnnotatedWith) {
			// Check annotation = method.getAnnotation(Check.class);
			// Class<?> declaringClass = method.getDeclaringClass();
			// Fixture docAnnotation =
			// declaringClass.getAnnotation(Fixture.class);
			// final String fixtureKind;
			// if(docAnnotation != null){
			// fixtureKind = docAnnotation.value().name();
			// }else{
			// fixtureKind = "undefined";
			// }
			// out.add(new FixtureDescriptor(declaringClass.getSimpleName(),
			// fixtureKind,annotation.value()));
			//
			// return out;
			// }
		} catch (MalformedURLException e) {
			getLog().error(e);
		} catch (IOException e) {
			getLog().error(e);
		}

	}

	/**
	 * TODO: loop here over all drivers and write a single file in
	 * ./settings/redppeer_descriptor.json
	 * 
	 * @param driver
	 * @param sentences
	 */
	private String writeDriverJson(Driver driver, List<Sentence> sentences) {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append("\"").append("driver").append("\"").append(":").append("\"").append(driver.name).append("\"").append(",");
		builder.append("\"").append("type").append("\"").append(":").append("\"").append(driver.type).append("\"").append(",");
		builder.append("\"").append("class").append("\"").append(":").append("\"").append("Replace with Java Fixture Qualified ClassName")
				.append("\"").append(",");
		builder.append("\"").append("sentences").append("\"").append(":");
		builder.append("[");
		for (Sentence s : sentences) {
			builder.append("\"").append(s.text).append("\"").append(",");
		}
		builder.append("]}");
		return builder.toString();
	}

	private List<Sentence> downloadSentences(Driver driver, Client httpClient) {
		List<Sentence> res = new ArrayList<ToastToolKitMavenMojo.Sentence>();
		String jsonResponse = RestUtils.getJsonResponseAsString(host + "/loadServiceDescriptors/" + driver.type + "/" + driver.name, httpClient);
		JSONArray jsonResult;
		try {
			jsonResult = new JSONArray(jsonResponse);
			for (int i = 0; i < jsonResult.length(); i++) {
				Sentence s = new Sentence();
				s.text = jsonResult.getString(i);
				res.add(s);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	private List<Sentence> downloadDynamicSentences(Driver driver, Client httpClient) {
		List<Sentence> res = new ArrayList<ToastToolKitMavenMojo.Sentence>();
		String jsonResponse = RestUtils.getJsonResponseAsString(host + "/loadCtxSentences/" + driver.type, httpClient);
		JSONArray jsonResult;
		try {
			jsonResult = new JSONArray(jsonResponse);
			for (int i = 0; i < jsonResult.length(); i++) {
				Sentence s = new Sentence();
				s.text = jsonResult.getJSONObject(i).getString("typed_sentence");
				s.pattern = jsonResult.getJSONObject(i).getString("sentence");
				res.add(s);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return res;
	}

	private Set<Driver> downloadScenarii(String uri, Client httpClient) throws MojoExecutionException {
		String jsonResponse = RestUtils.getJsonResponseAsString(uri, httpClient);
		Set<Driver> drivers = new HashSet<ToastToolKitMavenMojo.Driver>();
		JSONArray jsonResult;
		try {
			jsonResult = new JSONArray(jsonResponse);
			getLog().info("Copying " + jsonResult.length() + " scenarios");
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < jsonResult.length(); i++) {
				String scenario = jsonResult.getString(i);
				Pattern pattern1 = Pattern.compile("(scenario driver):(\\w*)");
				Pattern pattern2 = Pattern.compile("(\\|\\| scenario \\|\\| )(\\w*)( \\|\\|)");
				Matcher matcher = pattern1.matcher(scenario);
				String driverName = "";
				while (matcher.find()) {
					driverName = matcher.group(2);
				}
				String driverType = "";
				matcher = pattern2.matcher(scenario);
				while (matcher.find()) {
					driverType = matcher.group(2);
				}
				Driver e = new Driver(driverType, driverName);
				boolean addDriver = true;
				for (Driver d : drivers) {
					if (d.name.equals(e.name) && d.type.equals(e.type)) {
						addDriver = false;
					}
				}
				if (addDriver) {
					drivers.add(e);
				}
				builder.append(scenario);
			}
			File scenarioImplFile = new File(outputResourceDirectory, "toast_imported_scenario.txt");
			writeFile(scenarioImplFile, builder.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return drivers;
	}

	class Settings {
		public List<Driver> settings;
	}

	class Driver {
		public String name;
		public String type;
		public String className;

		public List<Sentence> sentences;

		Driver(String type, String name) {
			this.name = name;
			this.type = type;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Driver) {
				Driver d = (Driver) obj;
				return d.name.equals(this.name) && d.type.equals(this.type);
			}
			return super.equals(obj);
		}

	}

	class Sentence {
		public String pattern;
		public String text;

	}

	private void writeFile(File file, String content) throws MojoExecutionException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			out.write(content);
		} catch (IOException e) {
			getLog().error(e);
			throw new MojoExecutionException("Error creating file " + file, e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// ignore
					getLog().error(e);
				}
			}
		}
	}
}
