package com.synaptix.toast.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.google.gson.Gson;
import com.synaptix.toast.constant.Property;
import com.synaptix.toast.core.adapter.ActionAdapterKind;
import com.synaptix.toast.core.annotation.Action;
import com.synaptix.toast.core.annotation.ActionAdapter;
import com.synaptix.toast.core.dao.adapter.ActionAdapterDescriptor;
import com.synaptix.toast.core.dao.adapter.ActionAdapterDescriptorLine;
import com.synaptix.toast.core.rest.RestUtils;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.COMPILE)
public class DeployConnectorMojo extends AbstractMojo {

	static String ACTION_ANNOTATION_NAME = "@" + Action.class.getSimpleName();

	static String ACTION_ADAPTER_ANNOTATION_NAME = "@" + ActionAdapter.class.getSimpleName();

	static String ACTION_ADAPTER_KIND_NAME = ActionAdapterKind.class.getSimpleName();

	@Parameter(required = true, alias = "webAppUrl", defaultValue = Property.DEFAULT_WEBAPP_ADDR_PORT)
	private String host;

	@Parameter(required = true, defaultValue = "${project}", readonly = true)
	MavenProject project;

	public void execute()
		throws MojoExecutionException {
		getLog().info("Toast Tk Maven Plugin - Files will be posted to: " + host);
		try {
			publishAvailableSentencesFromClass();
		}
		catch(Exception e) {
			getLog().error(e);
		}
		getLog().info("Toast Tk Maven Plugin - deploy completed !");
	}

	public void publishAvailableSentencesFromClass()
		throws NotFoundException {
		ClassPool cp = initClassPath();
		File file = new File(project.getBuild().getOutputDirectory());
		Iterator<File> iterateFiles = FileUtils.iterateFiles(file, new String[]{
			"class"
		}, true);
		for(; iterateFiles.hasNext();) {
			try {
				processClassAndPostConnector(cp, iterateFiles);
			}
			catch(Exception e) {
				getLog().error(e);
			}
		}
	}
	
	private ClassPool initClassPath()
		throws NotFoundException {
		ClassPool cp = ClassPool.getDefault();
		URLClassLoader contextClassLoader = (URLClassLoader) project.getClass().getClassLoader();
		cp.insertClassPath(new LoaderClassPath(contextClassLoader));
		cp.insertClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
		cp.appendClassPath(project.getBuild().getOutputDirectory());
		return cp;
	}

	private void processClassAndPostConnector(
		ClassPool cp,
		Iterator<File> iterateFiles)
		throws IOException, NotFoundException, ClassNotFoundException {
		final List<ActionAdapterDescriptorLine> sentences = new ArrayList<ActionAdapterDescriptorLine>();
		String className = buildClassName(iterateFiles);
		CtClass cc = cp.get(className);
		if(cc.hasAnnotation(ActionAdapter.class)) {
			ActionAdapter adapter = (ActionAdapter) cc.getAnnotation(ActionAdapter.class);
			String adapterKind = adapter.value().name();
			String adapterName = adapter.name();
			CtMethod[] methods = cc.getMethods();
			for(CtMethod ctMethod : methods) {
				if(ctMethod.hasAnnotation(Action.class)) {
					sentences.add(buildActionLineDescriptor(adapterKind, adapterName, ctMethod));
				}
			}
			getLog().info("Adapter: " + adapterName + " -> Posting " + sentences.size() + " actions");
			postConnector(sentences);
		}
	}

	private String buildClassName(
		Iterator<File> iterateFiles)
		throws IOException {
		File classFile = iterateFiles.next();
		String classRelativePath = classFile.getCanonicalPath().replace(
			project.getBuild().getOutputDirectory() + "\\",
			"");
		String className = classRelativePath.replace(".class", "").replace("\\", ".");
		return className;
	}

	private ActionAdapterDescriptorLine buildActionLineDescriptor(
		String adapterKind,
		String adapterName,
		CtMethod ctMethod)
		throws ClassNotFoundException {
		Action action = (Action) ctMethod.getAnnotation(Action.class);
		ActionAdapterDescriptorLine descriptorLine = new ActionAdapterDescriptorLine(
			adapterName, adapterKind, action.description(), action.action());
		return descriptorLine;
	}

	private void postConnector(
		final List<ActionAdapterDescriptorLine> sentences) {
		Gson gson = new Gson();
		ActionAdapterDescriptor descriptor = new ActionAdapterDescriptor(project.getName(),
			sentences);
		String json = gson.toJson(descriptor);
		RestUtils.post(host + "/postConnector", json);
	}

}
