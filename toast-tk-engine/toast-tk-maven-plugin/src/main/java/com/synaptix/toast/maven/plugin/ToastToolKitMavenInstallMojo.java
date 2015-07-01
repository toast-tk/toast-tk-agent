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
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.adapter.ActionAdapterSentenceRef;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.annotation.ActionAdapter;
import com.synaptix.toast.core.dao.adapter.ActionAdapterDescriptor;
import com.synaptix.toast.core.dao.adapter.ActionAdapterDescriptorLine;
import com.synaptix.toast.core.rest.RestUtils;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.COMPILE)
public class ToastToolKitMavenInstallMojo extends AbstractMojo {

	@Parameter(required = true, alias = "webAppUrl", defaultValue = Property.DEFAULT_WEBAPP_ADDR_PORT)
	private String host;

	@Parameter(required = true, defaultValue = "${project}", readonly = true)
	MavenProject project;

	public void execute() throws MojoExecutionException {
		getLog().info("Toast Tk Maven Plugin - Files will be posted to: " + host);
		try {
			publishAvailableSentences();
		} catch (Exception e) {
			getLog().error(e);
		}
		getLog().info("Toast Tk Maven Plugin - deploy completed !");
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
				final List<ActionAdapterDescriptorLine> sentences = new ArrayList<ActionAdapterDescriptorLine>();
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
								sentence = sentence.replace("\"+VALUE_REGEX+\"", ActionAdapterSentenceRef.VALUE_REGEX);
								sentence = sentence.replace("\"+VAR_OR_VALUE_REGEX+\"", ActionAdapterSentenceRef.VAR_OR_VALUE_REGEX);
								sentence = sentence.replace("\"+SWING_COMPONENT_REGEX+\"", ActionAdapterSentenceRef.SWING_COMPONENT_REGEX);
								sentence = sentence.replace("\"+VAR_IN_REGEX+\"", ActionAdapterSentenceRef.VAR_IN_REGEX);
								sentence = sentence.replace("\"+VAR_OUT_REGEX+\"", ActionAdapterSentenceRef.VAR_OUT_REGEX);
								
								sentence = sentence.replace("\"+VALUE_REGEX", ActionAdapterSentenceRef.VALUE_REGEX + "\"");
								sentence = sentence.replace("\"+VAR_OR_VALUE_REGEX", ActionAdapterSentenceRef.VAR_OR_VALUE_REGEX + "\"");
								sentence = sentence.replace("\"+SWING_COMPONENT_REGEX", ActionAdapterSentenceRef.SWING_COMPONENT_REGEX + "\"");
								sentence = sentence.replace("\"+VAR_IN_REGEX", ActionAdapterSentenceRef.VAR_IN_REGEX + "\"");
								sentence = sentence.replace("\"+VAR_OUT_REGEX", ActionAdapterSentenceRef.VAR_OUT_REGEX + "\"");
								sentence = sentence.substring(1, sentence.length()-1);
								ActionAdapterDescriptorLine descriptorLine = new ActionAdapterDescriptorLine(fixtureName, fixtureKind, sentence);
								sentences.add(descriptorLine);
							}
						}
					}
					
				}

				if (sentences.size() > 0) {
					Gson gson = new Gson();
					ActionAdapterDescriptor descriptor = new ActionAdapterDescriptor(project.getName(), sentences);
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





}
