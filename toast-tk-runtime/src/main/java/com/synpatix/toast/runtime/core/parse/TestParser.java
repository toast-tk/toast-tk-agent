package com.synpatix.toast.runtime.core.parse;

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
import com.synaptix.toast.dao.domain.impl.test.block.WebPageBlock;
import com.synaptix.toast.fixture.utils.FixtureHelper;

public class TestParser {

	/**
	 * @author E413544
	 * 
	 */
	private enum BlockType {
		TEST, COMMENT
	}

	private class Block {
		private final List<BlockLine> lines;
		private final BlockType blockType;

		public Block(BlockType type) {
			lines = new ArrayList<BlockLine>();
			blockType = type;
		}

		public void addLine(List<String> cells) {
			getLines().add(new BlockLine(cells));
		}

		public void addLine(String cell) {
			getLines().add(new BlockLine(Arrays.asList(cell)));
		}

		public BlockType getBlockType() {
			return blockType;
		}

		public List<BlockLine> getLines() {
			return lines;
		}

		public BlockLine getLineAt(int i) {
			return lines.get(i);
		}
	}

	private String sourceFolder;

	public TestParser() {
		System.out.println("Parser started...");
	}

	public TestPage parse(File file) {
		sourceFolder = file.getParent();
		return readFile(file);
	}

	public TestPage parseString(String input) {
		return readString(input, "new");
	}
	
	/**
	 * @param blocks
	 */
	private TestPage readBlocks(List<Block> blocks, TestPage testPage) {
		for (Block block : blocks) {
			if (BlockType.COMMENT.equals(block.getBlockType())) {
				CommentBlock commentBlock = new CommentBlock();
				for (BlockLine line : block.getLines()) {
					commentBlock.addLine(line.getCellAt(0));
				}
				testPage.addBlock(commentBlock);
			} else if (BlockType.TEST.equals(block.getBlockType())) {
				BlockLine firstLine = block.getLines().get(0);
				String testKind = firstLine.getCellAt(0);
				if (isTextEqual(testKind, "scenario")) {
					TestBlock testBlock = new TestBlock();
					testBlock.setFixtureName(firstLine.getCellAt(1));
					for (BlockLine line : block.getLines()) {
						if (block.getLines().indexOf(line) > 0) { // Title line not needed
							testBlock.addLine(line.getCellAt(0), line.getCellAt(1), line.getCellAt(2));
						}
					}
					testPage.addBlock(testBlock);
				} else if (isTextEqual(testKind, "include")) {
					String fileName = block.getLineAt(1).getCellAt(0);
					String variableName = block.getLineAt(1).getCellAt(1);
					File file = new File(sourceFolder + "/" + fileName); //possible null pointer
					if (variableName == null) {
						TestParser testParser = new TestParser();
						testPage.addBlock(testParser.parse(file));
					}
				} else if (isTextEqual(testKind, "auto setup")) {
					String type = block.getLineAt(1).getCellAt(0);
					if (isTextEqual(type, "configure entity")) {
						ConfigBlock configBlock = new ConfigBlock();
						configBlock.setComponentName(FixtureHelper.parseTestString(block.getLineAt(1).getCellAt(1)));
						for (BlockLine line : block.lines) {
							if (block.lines.indexOf(line) >= 3) {
								configBlock.addLine(line.getCellAt(0), line.getCellAt(1), line.getCells().size() == 3 ? line.getCellAt(2) : null);
							}
						}
						testPage.addBlock(configBlock);
					} else if (isTextEqual(type, "web page")) {
						WebPageBlock webBlock = new WebPageBlock();
						for (BlockLine line : block.lines) {
							webBlock.setFixtureName(block.getLineAt(1).getCellAt(1));
							if (block.lines.indexOf(line) >= 3) {
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
					else if (isTextEqual(type, "swing page")) {
						SwingPageBlock swingPageBlock = new SwingPageBlock();
						for (BlockLine line : block.lines) {
							swingPageBlock.setFixtureName(block.getLineAt(1).getCellAt(1));
							if (block.lines.indexOf(line) >= 3) {
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
					else if (isTextEqual(type, "insert")) {
						InsertBlock insertBlock = new InsertBlock();
						for (BlockLine line : block.lines) {
							if (block.lines.indexOf(line) == 1) {
								insertBlock.setComponentName(line.getCellAt(1));
							} else if (block.lines.indexOf(line) == 2) {
								insertBlock.setColumns(line);
							} else if (block.lines.indexOf(line) > 2) {
								insertBlock.addline(line);
							}
						}
						testPage.addBlock(insertBlock);
					}
				} else if (isTextEqual(testKind, "setup")) {
					SetupBlock setupBlock = new SetupBlock();
					setupBlock.setFixtureName(firstLine.getCellAt(1));
					for (BlockLine line : block.getLines()) {
						if (block.getLines().indexOf(line) == 1) { // columns titles
							setupBlock.setColumns(line);
						} else if (block.getLines().indexOf(line) >= 2) {
							setupBlock.addLine(line);
						}
					}
					testPage.addBlock(setupBlock);
				}
			}
		}
		return testPage;
	}

	/**
	 * @param first
	 * @param second
	 * @return
	 */
	private boolean isTextEqual(String first, String second) {
		String parseTestString = FixtureHelper.parseTestString(first);
		if (parseTestString != null) {
			return parseTestString.equals(FixtureHelper.parseTestString(second));
		} else {
			return false;
		}
	}

	private TestPage readFile(File file) {
		if (!file.isFile()) {
			System.err.println("Could not open file.");
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		TestPage testPage = new TestPage();
		testPage.setPageName(file.getName());
		testPage.setFile(file);

		return readBufferedReader(br, testPage);
	}

	public TestPage readString(String string, String fileName) {
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
	public TestPage readBufferedReader(BufferedReader br, TestPage testPage) {

		String line = null;
		int lineNumber = 0;

		Block currentTestBlock = null;
		List<Block> blocks = new ArrayList<TestParser.Block>();

		try {
			while (true) {
				line = br.readLine();
				lineNumber++;
				if (line != null && line.contains("||")) {
					// Title line for a new page or test block
					if (currentTestBlock != null) {
						blocks.add(currentTestBlock);
						currentTestBlock = null;
					}
					String[] split = line.split("\\|\\|");
					List<String> list = cleanList(split);
					if (CollectionUtils.isEmpty(list)) {
						addErrorMessage(testPage, lineNumber, "empty title");
						return testPage;
					}
					currentTestBlock = new Block(BlockType.TEST);
					currentTestBlock.addLine(list);
				} else if (line != null && line.contains("|")) {
					// Here a test or page block should have been created
					if (currentTestBlock == null || BlockType.COMMENT.equals(currentTestBlock.getBlockType())) {
						addErrorMessage(testPage, lineNumber, "new test block without title");
						return testPage;
					}

					String[] split = line.split("\\|");
					List<String> list = cleanList(split);
					currentTestBlock.addLine(list);
				} else { // Comment line or end of file
					if (line != null) {
						if (currentTestBlock != null) {
							if (!currentTestBlock.getBlockType().equals(BlockType.COMMENT)) {
								blocks.add(currentTestBlock);
								currentTestBlock = null;
							}
						}
						if (currentTestBlock == null) {
							currentTestBlock = new Block(BlockType.COMMENT);
						}
						if (!line.isEmpty()) {
							currentTestBlock.addLine(line);
						}
					} else {
						blocks.add(currentTestBlock);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return readBlocks(blocks, testPage);
	}

	/**
	 * @param testPage
	 * @param message
	 */
	private void addErrorMessage(TestPage testPage, int lineNumber, String message) {
		testPage.setParsingErrorMessage("Syntax error on line " + lineNumber + " in file " + testPage.getPageName() + ": " + message);
	}

	private List<String> cleanList(String[] split) {
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		List<String> list = new ArrayList<String>(Arrays.asList(split));
		list.remove(0);
		return list;
	}
}
