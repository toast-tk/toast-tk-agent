package com.synaptix.toast.runtime.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.BlockType;
import com.synaptix.toast.runtime.core.parse.IBlockParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestParser {

    private static final Logger LOG = LogManager.getLogger(TestParser.class);

    private BlockParserProvider blockParserProvider;

    public TestParser() {
        LOG.info("Parser intializing..");
        blockParserProvider = new BlockParserProvider();
    }

    public TestPage parse(String path) throws IOException, IllegalAccessException {
        Stream<String> lines = Files.lines(Paths.get(path));
        List<String> list = lines.collect(Collectors.toList());
        if (list.isEmpty()) {
            throw new IllegalAccessException("File empty at path: " + path);
        }
        return buildTestPage(list, path);
    }

    private TestPage buildTestPage(List<String> list, String path) throws IllegalAccessException {
        TestPage testPage = new TestPage();
        testPage.setPath(path);
        IBlock block;
        while ((block = readBlock(list, path)) != null) {
            testPage.addBlock(block);
            list = list.subList(block.numberOfLines(), list.size());
        }
        return testPage;
    }

    private IBlock readBlock(List<String> list, String path) throws IllegalAccessException {
        String firstLine = list.get(0);
        BlockType blockType = getBlockType(firstLine);
        IBlockParser blockParser = blockParserProvider.getBlockParser(blockType);
        if (blockParser == null) {
            throw new IllegalAccessException("Could not parse line: " + firstLine);
        }
        return blockParser.digest(list, path);
    }

    protected TestPage readString(String string) {
        String[] split = StringUtils.split(string, "\n");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(split));
        return buildTestPage(list, null);
    }

    public TestPage buildFromString(String input) {
        return readString(input);
    }

    private BlockType getBlockType(String line) throws IllegalAccessException {
        Collection<IBlockParser> allBlockParsers = blockParserProvider.getAllBlockParsers();

        List<BlockType> blockTypes = allBlockParsers.stream()
                .filter(iBlockParser -> iBlockParser.isLineParsable(line))
                .map(IBlockParser::getBlockType)
                .collect(Collectors.toList());

        if (blockTypes.size() == 1) {
            return blockTypes.get(0);
        } else if (blockTypes.size() == 0) {
            return BlockType.COMMENT;
        }
        String join = StringUtils.join(blockTypes, "; ");
        throw new IllegalAccessException("Too many parsers for line [" + line + "] : " + join);
    }
}
