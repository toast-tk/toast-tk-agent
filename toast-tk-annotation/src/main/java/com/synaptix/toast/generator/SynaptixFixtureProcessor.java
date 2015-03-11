package com.synaptix.toast.generator;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.apache.commons.lang3.StringUtils;

import com.gwtplatform.dispatch.annotation.helper.BuilderGenerationHelper;
import com.gwtplatform.dispatch.annotation.helper.ReflectionHelper;
import com.gwtplatform.dispatch.annotation.processor.GenProcessor;
import com.synaptix.toast.annotation.SynaptixSeleniumFixture;
import com.synaptix.toast.fixture.web.ElementFactory;
import com.synaptix.toast.fixture.web.WebAutoElement;
import com.synaptix.toast.fixture.web.WebInputElement;
import com.synaptix.toast.web.annotations.gwt.shared.AutoField;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("com.synaptix.redpepper.annotation.SynaptixSeleniumFixture")
public class SynaptixFixtureProcessor extends GenProcessor {

	private static final String WEBELEMENTS_PACKAGE = "com.synaptix.redpepper.automation.elements.impl.";

	private static Map<String, Class<?>> uiEquivClassMap = new HashMap<String, Class<?>>();

	static {
		// uiEquivClassMap.put("com.github.gwtbootstrap.datepicker.client.ui.DateBox", );
		uiEquivClassMap.put("com.github.gwtbootstrap.client.ui.TextBox", WebInputElement.class);
	}

	@Override
	public void process(Element annotatedElement) {
		processFields(annotatedElement);
	}

	private class FieldDescriptor {
		public String type;
		public String varName;
		public String debugId;

		public FieldDescriptor(String type, String varName, String debugId) {
			this.type = type;
			this.varName = varName;
			this.debugId = debugId;
		}

		public FieldDescriptor(String type, String varName) {
			this(type, varName, null);
		}
	}

	private void processFields(Element element) {
		boolean isGreenPepper = false;
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ClassClassPath(getClass()));
		List<FieldDescriptor> extraViewSynchronizedList = new ArrayList<FieldDescriptor>();
		if (element.getAnnotationMirrors() != null && element.getAnnotationMirrors().size() > 0) {
			AnnotationMirror annotationMirror = element.getAnnotationMirrors().get(0);
			if (annotationMirror.getAnnotationType().toString().equals(SynaptixSeleniumFixture.class.getName().toString())) {
				if (annotationMirror.getElementValues() != null & annotationMirror.getElementValues().size() > 0) {
					for (ExecutableElement aField : annotationMirror.getElementValues().keySet()) {
						String targetviewfield = SynaptixSeleniumFixture.targetViewField;
						if (targetviewfield.equals(aField.getSimpleName().toString())) {
							Object value = annotationMirror.getElementValues().get(aField).getValue();
							printMessage("Detected target View: " + value);
							try {
								CtClass ctClass = pool.get(value.toString());
								for (CtField field : ctClass.getFields()) {
									boolean typeProvided = false;
									Object[] availableAnnotations = field.getAvailableAnnotations();
									for (Object annotation : availableAnnotations) {
										if (annotation instanceof AutoField) {
											AutoField autoField = (AutoField) annotation;
											if (autoField.uiType() != null) {
												Class<? extends WebAutoElement> typeClass = ElementFactory.getTypeClass(autoField.uiType());
												if (typeClass != null) {
													typeProvided = true;
													extraViewSynchronizedList.add(new FieldDescriptor(typeClass.getSimpleName(), field.getName(), autoField.debugId()));
												}
											}
										}
									}
									if (!typeProvided && uiEquivClassMap.containsKey(field.getType().getName())) {
										extraViewSynchronizedList.add(new FieldDescriptor(uiEquivClassMap.get(field.getType().getName()).getSimpleName(), field.getName()));
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								printMessage(e.getCause().getMessage());
							}
						}
					}
					for (AnnotationValue annotationValue : annotationMirror.getElementValues().values()) {
						if (annotationValue.getValue().toString().endsWith("GREENPEPPER")) {
							isGreenPepper = true;
						}
					}
				}
			}
		}

		if (isGreenPepper) {
			printMessage("Deployment mode: GreenPepper");
			prepareHeader("Fixture", "AbstractGreenpepperWebPage", element, extraViewSynchronizedList);
		} else {
			printMessage("Deployment mode: Default (Synaptix)");
			prepareHeader("Impl", "AbstractSynaptixWebPage", element, extraViewSynchronizedList);
		}

	}

	private void prepareHeader(String suffix, String superClass, Element element, List<FieldDescriptor> extraFields) {
		BuilderGenerationHelper writer = null;
		try {
			ReflectionHelper reflection = new ReflectionHelper(getEnvironment(), (TypeElement) element);
			String webElementSimpleName = reflection.getSimpleClassName();
			String fixtureSimpleName = rename(webElementSimpleName) + suffix;
			String packageName = reflection.getPackageName();
			String fixtureClassName = packageName != null ? packageName + '.' + fixtureSimpleName : fixtureSimpleName;

			printMessage("Generating '" + fixtureClassName + "' from '" + webElementSimpleName + "'.");

			Writer sourceWriter = getEnvironment().getFiler().createSourceFile(fixtureClassName, element).openWriter();
			writer = new BuilderGenerationHelper(sourceWriter);

			writer.generatePackageDeclaration(packageName);
			writer.println();
			writer.println("import " + WEBELEMENTS_PACKAGE + "*;");

			if ("AbstractSynaptixWebPage".equals(superClass)) {
				writer.println("import java.util.HashMap;");
				writer.println("import java.util.Map;");
			}

			Set<Modifier> modifiers = new HashSet<Modifier>();
			modifiers.add(Modifier.PUBLIC);
			modifiers.add(Modifier.FINAL);

			writer.generateClassHeader(fixtureSimpleName + " extends " + superClass, null, modifiers);

			writer.println();
			writer.println();

			if ("AbstractSynaptixWebPage".equals(superClass)) {
				writer.println("\tstatic HashMap<String, String> debugIdMap = new HashMap<String, String>();");
				writer.println("\tstatic {");
				for (FieldDescriptor fieldDescrpition : extraFields) {
					if (fieldDescrpition.debugId != null && !fieldDescrpition.debugId.isEmpty()) {
						writer.println("\t\tdebugIdMap.put(\"{0}\", \"{1}\");", fieldDescrpition.varName, fieldDescrpition.debugId);
					}
				}
				writer.println("\t}");
				writer.println();
			}

			List<String> items = new ArrayList<String>();
			for (VariableElement variable : reflection.getFields()) {
				if (variable.asType().toString().startsWith(WEBELEMENTS_PACKAGE)) {
					String type = variable.asType().toString().replace(WEBELEMENTS_PACKAGE, "");
					String varName = variable.getSimpleName().toString();
					writer.println("\tpublic " + type + " " + varName + ";");
					items.add(variable.getSimpleName().toString());
					writer.println();
					addSetter(writer, type, varName);
					writer.println();
					addGetter(writer, type, varName);
				}
			}

			for (FieldDescriptor fieldDescrpition : extraFields) {
				String varName = fieldDescrpition.varName;
				String type = fieldDescrpition.type;
				writer.println("\tpublic " + type + " " + varName + ";");
				items.add(varName);
				writer.println();
				addSetter(writer, type, varName);
				writer.println();
				addGetter(writer, type, varName);
			}
			writer.println();

			String join = StringUtils.join(items, ",\n\t\t");
			writer.println("\tpublic static enum Tokens {" + join + "}");
			writer.println();
			writer.println();

			if ("AbstractSynaptixWebPage".equals(superClass)) {
				writer.println("\t@Override");
				writer.println("\tpublic Map<String, String> getDebugIdMap() {");
				writer.println("\t\treturn debugIdMap;");
				writer.println("\t}");
			}

			writer.generateFooter();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void addSetter(BuilderGenerationHelper w, String type, String name) {
		w.println("\tpublic void set{1}({0} {1}) {", type, name);
		w.println("\t\tthis.{0} = {0};", name);
		w.println("\t}");

	}

	private void addGetter(BuilderGenerationHelper w, String type, String name) {
		w.println("\tpublic {0} get{1}() {", type, name);
		w.println("\t\treturn {0};", name);
		w.println("\t}");
	}

	private String rename(String name) {
		String res = name;
		if (name.length() > 2) {
			String second = name.substring(1, 2);
			boolean startI = name.substring(0, 1).equals("I") && second.toUpperCase().equals(second);
			if (startI) {
				res = name.substring(1);
			}
		}
		return res;
	}

}
