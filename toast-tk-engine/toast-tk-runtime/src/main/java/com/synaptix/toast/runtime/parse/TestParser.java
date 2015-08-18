package com.synaptix.toast.runtime.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.dao.domain.impl.test.block.BlockType;
import com.synaptix.toast.runtime.core.parse.IBlockParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public TestPage parse(String path) {
        TestPage testPage = null;

        try {
            Stream<String> lines = Files.lines(Paths.get(path));
            List<String> list = lines.collect(Collectors.toList());
            testPage = readStringList(list, path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return testPage;
    }

    private TestPage readStringList(List<String> list, String path) throws Exception {
        TestPage testPage = new TestPage();
        IBlock block = readBlock(list, path);
        while (block != null) {
            testPage.addBlock(block);
            block = readBlock(list, path);
        }
        return testPage;
    }

    private IBlock readBlock(List<String> list, String path) throws Exception {
        if (list.isEmpty()) {
            return null;
        }
        BlockType blockType = getBlockType(list.get(0));
        IBlockParser blockParser = blockParserProvider.getBlockParser(blockType);

        if (blockParser == null) {
            throw new Exception("Could not parse line");
        }
        return blockParser.digest(list, path);
    }

    public TestPage readString(String string, String fileName) {
        String[] split = StringUtils.split(string, "\n");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(split));
        TestPage testPage = null;
        try {
            testPage = readStringList(list, null);
            testPage.setPageName(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testPage;
    }

    public TestPage parseString(String input) {
        return readString(input, "new"); // TODO
    }

    private BlockType getBlockType(String line) throws Exception {
        Collection<IBlockParser> allBlockParsers = blockParserProvider.getAllBlockParsers();

        List<BlockType> blockTypes = allBlockParsers.stream()
                .filter(iBlockParser -> iBlockParser.isLineParsable(line))
                .map(IBlockParser::getBlockType)
                .collect(Collectors.toList());

        if (blockTypes.size() == 1) {
            return blockTypes.get(0);
        } else {
            return BlockType.COMMENT;
        }
    }

}
