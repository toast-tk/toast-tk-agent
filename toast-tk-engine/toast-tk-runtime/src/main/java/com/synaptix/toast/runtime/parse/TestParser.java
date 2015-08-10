package com.synaptix.toast.runtime.parse;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.synaptix.toast.adapter.utils.ActionAdapterHelper;
import com.synaptix.toast.dao.domain.impl.test.SwingPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.WebPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.block.BlockLine;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;
import com.synaptix.toast.dao.domain.impl.test.block.ConfigBlock;
import com.synaptix.toast.dao.domain.impl.test.block.InsertBlock;
import com.synaptix.toast.dao.domain.impl.test.block.SetupBlock;
import com.synaptix.toast.dao.domain.impl.test.block.SwingPageBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestPageBlock;
import com.synaptix.toast.dao.domain.impl.test.block.VariableBlock;
import com.synaptix.toast.dao.domain.impl.test.block.WebPageBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestPageBlock.BlockType;
import com.synaptix.toast.runtime.core.TestRunner;

public class TestParser {

	private static final Logger LOG = LogManager.getLogger(TestRunner.class);

	private static String VARIABLE_ASSIGNATION_SEPARATOR = ":=";

	// TODO: link to play domain
	private enum ConfigType {
		WEB_PAGE("web page"), SWING_PAGE("swing page"), SERVICE_ENTITY("service entity");

		public final String value;

		ConfigType(
			String value) {
			this.value = value;
		}
	}

	private String sourceFolder;

	public TestParser() {
		LOG.info("Parser intializing..");
	}

	public TestPage parse(
		File file) {
		sourceFolder = file.getParent();
		return readFile(file);
	}

	public TestPage parseString(
		String input) {
		return readString(input, "new");
	}

	/**
	 * @param blocks
	 */
	private TestPage readBlocks(
		List<TestPageBlock> blocks,
		TestPage testPage) {
		for(TestPageBlock block : blocks) {
			switch(block.getBlockType()) {
				case COMMENT :
					readCommentBlock(testPage, block);
					break;
				case TEST :
					BlockLine firstLine = block.getLines().get(0);
					String testKind = firstLine.getCellAt(0);
					if(isTextEqual(testKind, "scenario")) {
						readTestBlock(testPage, block, firstLine);
					}
					else if(isTextEqual(testKind, "include")) {
						readTestPageInclude(testPage, block);
					}
					else if(isTextEqual(testKind, "auto setup")) {
						String type = block.getLineAt(1).getCellAt(0);
						readAutoSetupBlocks(testPage, block, type);
					}
					else if(isTextEqual(testKind, "setup")) {
						readSetupBlock(testPage, block, firstLine);
					}
					break;
				case VARIABLE :
					readVariableBlock(testPage, block);
					break;
				default :
					LOG.warn("Unhandled block type: " + block.getBlockType());
					break;
			}
		}
		return testPage;
	}

	private void readVariableBlock(
		TestPage testPage,
		TestPageBlock block) {
		VariableBlock varBlock = new VariableBlock();
		varBlock.setBlockLines(block.getLines());
		testPage.addBlock(varBlock);
	}

	private void readAutoSetupBlocks(
		TestPage testPage,
		TestPageBlock block,
		String type) {
		if(isTextEqual(type, ConfigType.SERVICE_ENTITY.value)) {
			readServiceEntityBlock(testPage, block);
		}
		else if(isTextEqual(type, ConfigType.WEB_PAGE.value)) {
			readWebPageBlock(testPage, block);
		}
		else if(isTextEqual(type, ConfigType.SWING_PAGE.value)) {
			readSwingPageBlock(testPage, block);
		}
		else if(isTextEqual(type, "insert")) { // TODO: check if we still need to parse such blocks
			readInsertBlock(testPage, block);
		}
	}

	private void readCommentBlock(
		TestPage testPage,
		TestPageBlock block) {
		CommentBlock commentBlock = new CommentBlock();
		for(BlockLine line : block.getLines()) {
			commentBlock.addLine(line.getCellAt(0));
		}
		testPage.addBlock(commentBlock);
	}

	private void readTestBlock(
		TestPage testPage,
		TestPageBlock block,
		BlockLine firstLine) {
		TestBlock testBlock = new TestBlock();
		testBlock.setFixtureName(firstLine.getCellAt(1));
		for(BlockLine line : block.getLines()) {
			if(block.getLines().indexOf(line) > 0) { // Title line not needed
				testBlock.addLine(line.getCellAt(0), line.getCellAt(1), line.getCellAt(2));
			}
		}
		testPage.addBlock(testBlock);
	}

	private void readTestPageInclude(
		TestPage testPage,
		TestPageBlock block) {
		String fileName = block.getLineAt(1).getCellAt(0);
		String variableName = block.getLineAt(1).getCellAt(1);
		File file = new File(sourceFolder + "/" + fileName); // possible null pointer
		if(variableName == null) {
			TestParser testParser = new TestParser();
			testPage.addBlock(testParser.parse(file));
		}
	}

	private void readSetupBlock(
		TestPage testPage,
		TestPageBlock block,
		BlockLine firstLine) {
		SetupBlock setupBlock = new SetupBlock();
		setupBlock.setFixtureName(firstLine.getCellAt(1));
		for(BlockLine line : block.getLines()) {
			if(block.getLines().indexOf(line) == 1) { // columns titles
				setupBlock.setColumns(line);
			}
			else if(block.getLines().indexOf(line) >= 2) {
				setupBlock.addLine(line);
			}
		}
		testPage.addBlock(setupBlock);
	}

	private void readInsertBlock(
		TestPage testPage,
		TestPageBlock block) {
		InsertBlock insertBlock = new InsertBlock();
		for(BlockLine line : block.lines) {
			if(block.lines.indexOf(line) == 1) {
				insertBlock.setComponentName(line.getCellAt(1));
			}
			else if(block.lines.indexOf(line) == 2) {
				insertBlock.setColumns(line);
			}
			else if(block.lines.indexOf(line) > 2) {
				insertBlock.addline(line);
			}
		}
		testPage.addBlock(insertBlock);
	}

	private void readSwingPageBlock(
		TestPage testPage,
		TestPageBlock block) {
		SwingPageBlock swingPageBlock = new SwingPageBlock();
		for(BlockLine line : block.lines) {
			swingPageBlock.setFixtureName(block.getLineAt(1).getCellAt(1));
			if(block.lines.indexOf(line) >= 3) {
				SwingPageConfigLine cLine = new SwingPageConfigLine();
				// | name | type | locator |
				cLine.setElementName(line.getCellAt(0));
				cLine.setType(line.getCellAt(1));
				cLine.setLocator(line.getCellAt(2));
				swingPageBlock.addLine(cLine);
			}
		}
		testPage.addBlock(swingPageBlock);
	}

	private void readServiceEntityBlock(
		TestPage testPage,
		TestPageBlock block) {
		ConfigBlock configBlock = new ConfigBlock();
		configBlock.setComponentName(ActionAdapterHelper.parseTestString(block.getLineAt(1).getCellAt(1)));
		for(BlockLine line : block.lines) {
			if(block.lines.indexOf(line) >= 3) {
				configBlock.addLine(
					line.getCellAt(0),
					line.getCellAt(1),
					line.getCells().size() == 3 ? line.getCellAt(2) : null);
			}
		}
		testPage.addBlock(configBlock);
	}

	private void readWebPageBlock(
		TestPage testPage,
		TestPageBlock block) {
		WebPageBlock webBlock = new WebPageBlock();
		for(BlockLine line : block.lines) {
			webBlock.setFixtureName(block.getLineAt(1).getCellAt(1));
			if(block.lines.indexOf(line) >= 3) {
				WebPageConfigLine cLine = new WebPageConfigLine();
				// | name | type | locator | method | position |
				cLine.setElementName(line.getCellAt(0));
				cLine.setType(line.getCellAt(1));
				cLine.setLocator(line.getCellAt(2));
				cLine.setMethod(line.getCellAt(3));
				cLine.setPosition(Integer.valueOf(line.getCellAt(4) == null ? "0" : line.getCellAt(4)));
				webBlock.addLine(cLine);
			}
		}
		testPage.addBlock(webBlock);
	}

	/**
	 * @param first
	 * @param second
	 * @return
	 */
	private boolean isTextEqual(
		String first,
		String second) {
		String parseTestString = ActionAdapterHelper.parseTestString(first);
		if(parseTestString != null) {
			return parseTestString.equals(ActionAdapterHelper.parseTestString(second));
		}
		else {
			return false;
		}
	}

	private TestPage readFile(
		File file) {
		if(!file.isFile()) {
			System.err.println("Could not open file.");
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		TestPage testPage = new TestPage();
		testPage.setPageName(file.getName());
		testPage.setFile(file);
		return readBufferedReader(br, testPage);
	}

	public TestPage readString(
		String string,
		String fileName) {
		InputStream is = new ByteArrayInputStream(string.getBytes());
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(is));
		TestPage testPage = new TestPage();
		testPage.setPageName(fileName);
		return readBufferedReader(br, testPage);
	}

	/**
	 * Fills the testPage from buffered reader.
	 * 
	 * @param br
	 * @param testPage
	 * @return
	 */
	public TestPage readBufferedReader(
		BufferedReader br,
		TestPage testPage) {
		String line = null;
		int lineNumber = 0;
		TestPageBlock varBlock = null;
		TestPageBlock currentTestBlock = null;
		List<TestPageBlock> blocks = new ArrayList<TestPageBlock>();
		try {
			while(true) {
				line = br.readLine();
				lineNumber++;
				if(isBlockStart(line)) {
					// Title line for a new page or test block
					if(currentTestBlock != null) {
						blocks.add(currentTestBlock);
						currentTestBlock = null;
					}
					String[] split = line.split("\\|\\|");
					List<String> list = cleanList(split);
					if(CollectionUtils.isEmpty(list)) {
						addErrorMessage(testPage, lineNumber, "empty title");
						return testPage;
					}
					currentTestBlock = new TestPageBlock(BlockType.TEST);
					currentTestBlock.addLine(list);
				}
				else if(isBlockLine(line)) {
					// Here a test or page block should have been created
					if(currentTestBlock == null || BlockType.COMMENT.equals(currentTestBlock.getBlockType())) {
						addErrorMessage(testPage, lineNumber, "new test block without title");
						return testPage;
					}
					String[] split = line.split("\\|");
					List<String> list = cleanList(split);
					currentTestBlock.addLine(list);
				}
				else if(isVarLine(line)) {
					varBlock = handleVarLine(line, varBlock);
				}
				else if(isVarMultiLine(line)) {
					varBlock = handleMultiVarLine(line, br, varBlock);
				}
				else { // Comment line or end of file
					if(line != null) {
						if(currentTestBlock != null) {
							if(!currentTestBlock.getBlockType().equals(BlockType.COMMENT)) {
								blocks.add(currentTestBlock);
								currentTestBlock = null;
							}
						}
						if(currentTestBlock == null) {
							currentTestBlock = new TestPageBlock(BlockType.COMMENT);
						}
						if(!line.isEmpty()) {
							currentTestBlock.addLine(line);
						}
					}
					else {
						blocks.add(currentTestBlock);
						break;
					}
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		if(varBlock != null) {
			blocks.add(varBlock);
		}
		return readBlocks(blocks, testPage);
	}

	public TestPageBlock handleMultiVarLine(
		String line,
		BufferedReader br,
		TestPageBlock varBlock)
		throws IOException {
		if(varBlock == null) {
			varBlock = new TestPageBlock(BlockType.VARIABLE);
		}
		String[] split = line.split(VARIABLE_ASSIGNATION_SEPARATOR);
		String varName = split[0].trim();
		String varValue = "";
		if(line != null) {
			do {
				line = br.readLine();
				if(!line.startsWith("\"\"\"")){
					varValue += line.replace("\n", " ").replace("\t", " ") + " ";
				}
			}
			while(line != null && !line.trim().contains("\"\"\""));
		}
		varBlock.addLine(Arrays.asList(varName, varValue));
		return varBlock;
	}

	public TestPageBlock handleVarLine(
		String line,
		TestPageBlock varBlock) {
		if(varBlock == null) {
			varBlock = new TestPageBlock(BlockType.VARIABLE);
		}
		String[] split = line.split(VARIABLE_ASSIGNATION_SEPARATOR);
		String varName = split[0].trim();
		String varValue = split[1].trim();
		varBlock.addLine(Arrays.asList(varName, varValue));
		return varBlock;
	}

	private boolean isVarMultiLine(
		String line) {
		return line != null && line.startsWith("$")
			&& line.contains(VARIABLE_ASSIGNATION_SEPARATOR)
			&& line.contains("\"\"\"");
	}

	private boolean isVarLine(
		String line) {
		return line != null && line.startsWith("$")
			&& line.contains(VARIABLE_ASSIGNATION_SEPARATOR)
			&& !line.contains("\"\"\"");
	}

	private boolean isBlockLine(
		String line) {
		return line != null && line.contains("|");
	}

	private boolean isBlockStart(
		String line) {
		return line != null && line.contains("||");
	}

	/**
	 * @param testPage
	 * @param message
	 */
	private void addErrorMessage(
		TestPage testPage,
		int lineNumber,
		String message) {
		testPage.setParsingErrorMessage("Syntax error on line " + lineNumber + " in file " + testPage.getPageName()
			+ ": " + message);
	}

	private List<String> cleanList(
		String[] split) {
		for(int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		List<String> list = new ArrayList<String>(Arrays.asList(split));
		list.remove(0);
		return list;
	}
}
