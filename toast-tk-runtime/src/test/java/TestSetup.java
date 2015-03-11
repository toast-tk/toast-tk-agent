//package com.synpatix.redpepper.backend.core.runtime;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.ByteArrayInputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.commons.lang.exception.ExceptionUtils;
//import org.joda.time.LocalDate;
//import org.joda.time.LocalTime;
//
//import com.google.common.base.CaseFormat;
//import com.google.inject.Inject;
//import com.synaptix.redpepper.commons.init.Check;
//import com.synaptix.redpepper.commons.init.Display;
//import com.synaptix.redpepper.commons.init.ITestManager;
//import com.synaptix.redpepper.commons.setup.Cell;
//import com.synaptix.redpepper.commons.setup.CellColor;
//import com.synaptix.redpepper.commons.setup.Row;
//import com.synaptix.redpepper.commons.setup.TestResult;
//import com.synaptix.redpepper.commons.setup.TestTable;
//import com.synaptix.redpepper.commons.setup.TestResult.ResultKind;
//
//public class TestSetup {
//	
//	@Inject
//	private RepositorySetup autoSetup;
//
//	private Map<String, Class<?>> services;
//
//	private String sourceFolder;
//
//	public TestSetup() {
//		System.out.println("Parser started...");
//	}
//
//	/**
//	 * @param args
//	 * @throws IOException
//	 */
//	public void run(File file) throws IOException {
//		int lineNumber = 0;
//		services = new HashMap<String, Class<?>>();
//
//		StringBuilder htmlReport = new StringBuilder();
//		htmlReport.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />");
//		htmlReport.append("Test " + file.getName() + " effecué le " + LocalDate.now().toString("dd/MM/yyyy") + " à " + LocalTime.now().toString("HH'h'mm") + "<br><br>");
//
//		sourceFolder = file.getParent();
//
//		readFile(file, htmlReport);
//
//		htmlReport.append("<script language=\"javascript\"> function toggle(text) { var ele = document.getElementById(text);if(ele.style.display == \"block\") { "
//				+ "ele.style.display = \"none\";}else {ele.style.display = \"block\";}}</script>");
//
//		try {
//			FileWriter fstream = new FileWriter(file.getParentFile().getCanonicalPath() + "\\out.html");
//			BufferedWriter out = new BufferedWriter(fstream);
//			out.write(htmlReport.toString());
//			out.close();
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
//
//		System.out.println("Done");
//	}
//
//	private void readFile(File file, StringBuilder htmlReport) {
//		if (!file.isFile()) {
//			System.err.println("Could not open file.");
//		}
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(file));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		readBufferedReader(htmlReport, br);
//	}
//
//	public void readString(String string, StringBuilder htmlReport) {
//		InputStream is = new ByteArrayInputStream(string.getBytes());
//		BufferedReader br = null;
//		br = new BufferedReader(new InputStreamReader(is));
//
//		readBufferedReader(htmlReport, br);
//	}
//
//	public void readBufferedReader(StringBuilder htmlReport, BufferedReader br) {
//		String line = null;
//		TestTable testTable = null;
//		try {
//			while (true) {
//				line = br.readLine();
//				if (line != null && line.contains("||")) {
//					testTable = new TestTable();
//					String[] split = line.split("\\|\\|");
//					List<String> list = cleanList(split);
//					testTable.setHeader(list);
//				} else if (line != null && line.contains("|")) {
//					String[] split = line.split("\\|");
//					List<String> list = cleanList(split);
//					if (testTable != null) {
//						testTable.addRow(list);
//					}
//				} else {
//					if (testTable != null) {
//						readTable(testTable, htmlReport);
//						htmlReport.append(testTable.toHtml());
//						htmlReport.append("<br>");
//						testTable = null;
//					}
//					if (line == null) {
//						break;
//					} else {
//						readWikiLine(htmlReport, line);
//					}
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		try {
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void readWikiLine(StringBuilder htmlReport, String line) {
//		if (line.startsWith("h1.")) {
//			htmlReport.append("<h1>");
//		}
//		htmlReport.append(line.replace("h1. ", ""));
//		htmlReport.append("<br>");
//		if (line.startsWith("h1.")) {
//			htmlReport.append("</h1>");
//		}
//	}
//
//	private void readTable(TestTable table, StringBuilder htmlReport) {
//		// Read header
//		List<Cell> headerCells = table.getHeader().getCells();
//
//		String interpreterName = headerCells.get(0).getValue();
//
//		if (interpreterName.equals("setup")) {
//			processSetup(table, headerCells);
//		} else if (interpreterName.equals("scenario")) {
//			String scenarioService = headerCells.get(1).getValue();
//
//			for (int i = 0; i < table.getRows().size(); i++) {
//				Row row = table.getRowAt(i);
//				TestResult result = parseServiceCall(row.getCellAt(0).getValue(), scenarioService);
//				decorateRowWithResult(row, result);
//			}
//		} else if (interpreterName.equals("auto setup")) {
//			String setupType = table.getRowAt(0).getCellAt(0).getValue();
//			String entityName = table.getRowAt(0).getCellAt(1).getValue();
//
//			System.out.println("Process " + setupType + " for " + entityName);
//
//			if (setupType.equals("insert")) {
//				List<String> columns = new ArrayList<String>();
//
//				// Get columns names
//				for (Cell cell : table.getRowAt(1).getCells()) {
//					columns.add(cell.getValue());
//				}
//
//				// Get row values
//				for (int i = 2; i < table.getRows().size(); i++) {
//					Row row = table.getRowAt(i);
//					if (row == null) {
//						table.addRow(new Row(new Cell("Missing line here", CellColor.YELLOW)));
//						break;
//					}
//					Map<String, String> values = new HashMap<String, String>();
//					for (int cellIndex = 0; cellIndex < row.getCells().size(); cellIndex++) {
//						Cell cell = row.getCellAt(cellIndex);
//						values.put(columns.get(cellIndex), cell.getValue());
//					}
//
//					TestResult result = insertEntity(entityName, values);
//					decorateRowWithResult(row, result);
//				}
//			} else if (setupType.equals("configure entity")) {
//				Row headerRow = table.getRowAt(1);
//				if (!headerRow.getCellAt(0).getValue().equals("greenpepper name")) {
//					decorateRowWithResult(headerRow, new TestResult("Missing line [greepepper name|name]", ResultKind.ERROR));
//				}
//				// Get row values
//				for (int i = 2; i < table.getRows().size(); i++) {
//					Row row = table.getRowAt(i);
//					String searchBy = null;
//					if (row.getCells().size() >= 3) {
//						searchBy = row.getCellAt(2).getValue();
//					}
//					TestResult result = autoSetup.addProperty(entityName, row.getCellAt(0).getValue(), row.getCellAt(1).getValue(), searchBy);
//					decorateRowWithResult(row, result);
//				}
//			}
//		} else if (interpreterName.equals("include")) {
//			String fileName = table.getRowAt(0).getCellAt(0).getValue();
//			File file = new File(sourceFolder + "\\" + fileName);
//			htmlReport.append("Include: ");
//			htmlReport.append("<button  onclick=\"javascript:toggle('" + fileName + "');\">" + fileName + "</button>");
//			htmlReport.append("<div style=\"border-color: black;border-style: solid;display: none;\" id=\"" + fileName + "\">");
//			readFile(file, htmlReport);
//			htmlReport.append("</div>");
//		}
//	}
//
//	private void decorateRowWithResult(Row row, TestResult result) {
//		if (result.getResultKind().equals(ResultKind.FAILURE)) {
//			if (result.getMessage() != null && result.getMessage().contains("ORA-00001")) {
//				row.setColor(CellColor.BLUE);
//				row.addCell("Cette ligne existe déjà", CellColor.BLUE);
//			} else {
//				row.setColor(CellColor.RED);
//				row.addCell(result.getMessage(), CellColor.RED);
//			}
//		} else if (result.getResultKind().equals(ResultKind.ERROR)) {
//			row.setColor(CellColor.YELLOW);
//			row.addCell(result.getMessage(), CellColor.YELLOW);
//		} else if (result.getResultKind().equals(ResultKind.INFO)) {
//			row.setColor(CellColor.BLUE);
//			row.addCell(result.getMessage(), CellColor.BLUE);
//		} else if (result.getResultKind().equals(ResultKind.SUCCESS)) {
//			row.setColor(CellColor.GREEN);
//		}
//	}
//
//	private void processSetup(TestTable table, List<Cell> headerCells) {
//		String fixtureName;
//		// Get fixture name
//		fixtureName = headerCells.get(1).getValue();
//
//		// Get columns names
//		List<String> columns = new ArrayList<String>();
//		for (Cell cell : table.getRows().get(0).getCells()) {
//			String value = cell.getValue();
//			if (value.contains(" ")) {
//				value = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value.replace(" ", "_"));
//			}
//			columns.add(value);
//		}
//
//		// Get values
//		for (int i = 1; i < table.getRows().size(); i++) {
//			Row row = table.getRows().get(i);
//
//			if (fixtureName.equals("domain")) {
//				boolean result = addDomain(row.getCellAt(0).getValue(), row.getCellAt(1).getValue(), row.getCellAt(2).getValue());
//				row.setColor(result ? CellColor.GREEN : CellColor.YELLOW);
//			} else if (fixtureName.equals("entity")) {
//				TestResult result = addEntity(row.getCellAt(1).getValue(), row.getCellAt(0).getValue(), row.getCellAt(2).getValue());
//				decorateRowWithResult(row, result);
//			} else if (fixtureName.equals("service")) {
//				TestResult result = addService(row.getCellAt(0).getValue(), row.getCellAt(1).getValue());
//				decorateRowWithResult(row, result);
//			} else {
//				Class<?> fixtureClass = services.get(fixtureName);
//				if (fixtureClass == null) {
//					decorateRowWithResult(table.getHeader(), new TestResult("Fixture " + fixtureName + " not found", ResultKind.ERROR));
//					return;
//				}
//				Object instance = autoSetup.getTestManager().getClassInstance(fixtureClass);
//				for (int cellIndex = 0; cellIndex < row.getCells().size(); cellIndex++) {
//					Cell cell = row.getCellAt(cellIndex);
//					try {
//						fixtureClass.getField(columns.get(cellIndex)).set(instance, cell.getValue());
//					} catch (Exception e) {
//						e.printStackTrace();
//						decorateRowWithResult(row, new TestResult(ExceptionUtils.getRootCauseMessage(e)));
//						return;
//					}
//				}
//				try {
//					fixtureClass.getMethod("enterRow").invoke(instance);
//					decorateRowWithResult(row, new TestResult());
//				} catch (Exception e) {
//					e.printStackTrace();
//					decorateRowWithResult(row, new TestResult(ExceptionUtils.getRootCauseMessage(e)));
//				}
//			}
//		}
//		return;
//	}
//
//	private List<String> cleanList(String[] split) {
//		for (int i = 0; i < split.length; i++) {
//			split[i] = split[i].trim();
//		}
//		List<String> list = new ArrayList<String>(Arrays.asList(split));
//		list.remove(0);
//		return list;
//	}
//
//	private TestResult parseServiceCall(String string, String scenarioService) {
//		string = string.replace("*", "");
//		System.out.println("Try: " + string);
//		Class<?> serviceClass = services.get(scenarioService);
//		if (serviceClass == null) {
//			return new TestResult("Service " + scenarioService + " not found", ResultKind.ERROR);
//		}
//		Object instance = autoSetup.getTestManager().getClassInstance(serviceClass);
//		FixtureService methodAndMatcher = findMethodInClass(string, serviceClass);
//		if (methodAndMatcher == null) {
//			methodAndMatcher = findMethodInClass(string, serviceClass.getSuperclass());
//		}
//		if (methodAndMatcher != null) {
//			Matcher matcher = methodAndMatcher.matcher;
//			matcher.matches();
//			int groupCount = matcher.groupCount();
//			Object[] args = new Object[groupCount];
//			for (int i = 0; i < groupCount; i++) {
//				args[i] = matcher.group(i + 1);
//			}
//
//			try {
//				return (TestResult) methodAndMatcher.method.invoke(instance, args);
//			} catch (Exception e) {
//				e.printStackTrace();
//				return new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR);
//			}
//		} else {
//			return new TestResult("Method not found");
//		}
//	}
//
//	private FixtureService findMethodInClass(String string, Class<?> serviceClass) {
//		Method[] methods = serviceClass.getMethods();
//		for (Method method : methods) {
//			Annotation[] annotations = method.getAnnotations();
//			for (Annotation annotation : annotations) {
//				String methodRegex = null;
//				if (annotation.annotationType().equals(Check.class)) {
//					methodRegex = ((Check) annotation).value();
//				}
//				if (annotation.annotationType().equals(Display.class)) {
//					methodRegex = ((Display) annotation).value();
//				}
//				if (methodRegex != null) {
//					Pattern regexPattern = Pattern.compile(methodRegex);
//					Matcher matcher = regexPattern.matcher(string);
//					boolean matches = matcher.matches();
//					if (matches) {
//						return new FixtureService(method, matcher);
//					}
//				}
//			}
//		}
//		return null;
//	}
//
//	public class FixtureService {
//		Method method;
//		Matcher matcher;
//
//		public FixtureService(Method method, Matcher matcher) {
//			this.method = method;
//			this.matcher = matcher;
//		}
//	}
//
//	private TestResult addService(String testName, String className) {
//		Class<?> serviceClass = null;
//		try {
//			serviceClass = Class.forName(className);
//		} catch (ClassNotFoundException e) {
//			return new TestResult("The class " + className + " was not found", ResultKind.ERROR);
//		}
//		services.put(testName, serviceClass);
//		return new TestResult();
//	}
//
//	private TestResult addEntity(String className, String testName, String searchBy) {
//		return autoSetup.addClass(className, testName, searchBy);
//	}
//
//	private TestResult insertEntity(String entityName2, Map<String, String> values2) {
//		System.out.println("Insert entity: " + entityName2 + " [" + values2 + "]");
//		TestResult result;
//		try {
//			result = autoSetup.insertComponent(entityName2, values2);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new TestResult(ExceptionUtils.getRootCauseMessage(e), ResultKind.ERROR);
//		}
//		return result;
//	}
//
//	private boolean addDomain(String domainTestName, String domainClassName, String tableName) {
//		System.out.println("Add domain: [" + domainTestName + "] [" + domainClassName + "]");
//		TestResult addDomain = autoSetup.addDomain(domainClassName, domainTestName, tableName);
//		return addDomain.isSuccess();
//	}
//}
