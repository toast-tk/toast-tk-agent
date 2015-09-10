package com.synaptix.toast.runtime.parse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.BlockType;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;

public class TestParser {

	private static final Logger LOG = LogManager.getLogger(TestParser.class);

	private BlockParserProvider blockParserProvider;

	public TestParser() {
		LOG.info("Parser intializing..");
		blockParserProvider = new BlockParserProvider();
	}

	public TestPage parse(String path) throws IOException, IllegalArgumentException {
		Stream<String> lines = Files.lines(Paths.get(path));
		List<String> list = lines.collect(Collectors.toList());
		if (list.isEmpty()) {
			throw new IllegalArgumentException("File empty at path: " + path);
		}
		return buildTestPage(list, path);
	}

	private TestPage buildTestPage(List<String> list, String path) throws IllegalArgumentException {
		TestPage testPage = new TestPage();
		testPage.setPath(path);

		while (CollectionUtils.isNotEmpty(list)) {
			IBlock block = readBlock(list, path);
			testPage.addBlock(block);
			list = list.subList(block.getNumberOfLines(), list.size());
		}

		return testPage;
	}

	private IBlock readBlock(List<String> list, String path) throws IllegalArgumentException {
		String firstLine = list.get(0);
		BlockType blockType = getBlockType(firstLine);
		if (blockType == BlockType.COMMENT) {
			return digestCommentBlock(list);
		} else {
			IBlockParser blockParser = blockParserProvider.getBlockParser(blockType);
			if (blockParser == null) {
				throw new IllegalArgumentException("Could not parse line: " + firstLine);
			}
			return blockParser.digest(list, path);
		}
	}

	private IBlock digestCommentBlock(List<String> strings) {
		CommentBlock commentBlock = new CommentBlock();
		for (String string : strings) {
			if (getBlockType(string) != BlockType.COMMENT) {
				return commentBlock;
			}
			commentBlock.addLine(string);
		}
		return commentBlock;
	}

	public TestPage readString(String string) {
		String[] split = StringUtils.split(string, "\n");
		ArrayList<String> list = new ArrayList<>(Arrays.asList(split));
		return buildTestPage(list, null);
	}

	public BlockType getBlockType(String line) throws IllegalArgumentException {
		Collection<IBlockParser> allBlockParsers = blockParserProvider.getAllBlockParsers();

		List<BlockType> blockTypes = allBlockParsers.stream()
				.filter(iBlockParser -> iBlockParser.isLineParsable(line))
				.map(IBlockParser::getBlockType)
				.collect(Collectors.toList());

		if (blockTypes.size() == 1) {
			return blockTypes.get(0);
		} else if (blockTypes.size() > 1) {
			throw new IllegalArgumentException("Too many parsers for line [" + line + "]: " + StringUtils.join(blockTypes, "; "));
		}
		return BlockType.COMMENT;
	}
}
