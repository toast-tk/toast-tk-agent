package com.synaptix.toast.runtime.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.core.parse.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestParser {

    private static final Logger LOG = LogManager.getLogger(TestParser.class);

    private static String VARIABLE_ASSIGNATION_SEPARATOR = ":=";

    private String sourceFolder;

    public TestParser() {
        LOG.info("Parser intializing..");
    }

    public TestPage parse(File file) {
        sourceFolder = file.getParent();
        return readFile(file);
    }

//    private TestPage readFile_old(File file) {
//        if (!file.isFile()) {
//            System.err.println("Could not open file.");
//        }
//        BufferedReader br = null;
//        try {
//            br = new BufferedReader(new FileReader(file));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        TestPage testPage = new TestPage();
//        testPage.setPageName(file.getName());
//        testPage.setFile(file);
//        return readBufferedReader(br, testPage);
//    }

    public TestPage readFile(File file) {
        if (!file.isFile()) {
            System.err.println("Could not open file.");
        }

        TestPage testPage = null;

        try {
            Stream<String> lines = Files.lines(Paths.get("c:/temp", "data.txt"));
            List<String> list = lines.collect(Collectors.toList());

            testPage = readStringList(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return testPage;
    }

    private TestPage readStringList(List<String> list) throws Exception {
        TestPage testPage = new TestPage();
        IBlock block = readBlock(list);
        while (block != null) {
            testPage.addBlock(block);
            block = readBlock(list);
        }
        return testPage;
    }

    private IBlock readBlock(List<String> list) throws Exception {
        if (list.isEmpty()) {
            return null;
        }
        IBlockParser blockParser = getBlockParser(getBlockType(list.get(0)));
        if (blockParser == null) {
            throw new Exception("Could not parse line");
        }
        return blockParser.digest(list);
    }

    private IBlockParser getBlockParser(BlockType blockType) {
        switch (blockType) {
            case COMMENT:
                return new CommentBlockParser();
            case INCLUDE:
                return new IncludeBlockParser();
            case TEST:
                return new TestBlockParser();
            case VARIABLE:
                return new VariableBlockParser();
        }
        return null;
    }

    public TestPage readString(String string, String fileName) {
        String[] split = StringUtils.split(string, "\n");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(split));
        TestPage testPage = null;
        try {
            testPage = readStringList(list);
            testPage.setPageName(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testPage;
    }

//    public TestPage readString_old(String string, String fileName) {
//        InputStream is = new ByteArrayInputStream(string.getBytes());
//        BufferedReader br;
//        br = new BufferedReader(new InputStreamReader(is));
//        TestPage testPage = new TestPage();
//        testPage.setPageName(fileName);
//        return readBufferedReader(br, testPage);
//    }

    /**
     * Fills the testPage from buffered reader.
     */
//    public TestPage readBufferedReader(BufferedReader br, TestPage testPage) {
//        String line;
//        int lineNumber = 0;
//        TestPageBlock varBlock = null;
//        TestPageBlock currentTestBlock = null;
//        List<TestPageBlock> blocks = new ArrayList<TestPageBlock>();
//        try {
//            while (true) {
//                line = br.readLine();
//                lineNumber++;
//                if (isBlockStart(line)) {
//                    // Title line for a new page or test block
//                    if (currentTestBlock != null) {
//                        blocks.add(currentTestBlock);
//                    }
//                    String[] split = line.split("\\|\\|");
//                    List<String> list = cleanList(split);
//                    if (CollectionUtils.isEmpty(list)) {
//                        addErrorMessage(testPage, lineNumber, "empty title");
//                        return testPage;
//                    }
//                    currentTestBlock = new TestPageBlock(BlockType.TEST);
//                    currentTestBlock.addLine(list);
//                } else if (isBlockLine(line)) {
//                    // Here a test or page block should have been created
//                    if (currentTestBlock == null || BlockType.COMMENT.equals(currentTestBlock.getBlockType())) {
//                        addErrorMessage(testPage, lineNumber, "new test block without title");
//                        return testPage;
//                    }
//                    String[] split = line.split("\\|");
//                    List<String> list = cleanList(split);
//                    currentTestBlock.addLine(list);
//                } else if (isVarLine(line)) {
//                    varBlock = handleVarLine(line, varBlock);
//                } else if (isVarMultiLine(line)) {
//                    varBlock = handleMultiVarLine(line, br, varBlock);
//                } else { // Comment line or end of file
//                    if (line != null) {
//                        if (currentTestBlock != null) {
//                            if (!currentTestBlock.getBlockType().equals(BlockType.COMMENT)) {
//                                blocks.add(currentTestBlock);
//                                currentTestBlock = null;
//                            }
//                        }
//                        if (currentTestBlock == null) {
//                            currentTestBlock = new TestPageBlock(BlockType.COMMENT);
//                        }
//                        if (!line.isEmpty()) {
//                            currentTestBlock.addLine(line);
//                        }
//                    } else {
//                        blocks.add(currentTestBlock);
//                        break;
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            br.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (varBlock != null) {
//            blocks.add(varBlock);
//        }
//        return readBlocks(blocks, testPage);
//    }
    public TestPage parseString(String input) {
        return readString(input, "new");
    }

//    private TestPage readBlocks(List<TestPageBlock> blocks, TestPage testPage) {
//        for (TestPageBlock block : blocks) {
//            switch (block.getBlockType()) {
//                case COMMENT:
//                    IBlock readBlock = readCommentBlock(block);
//                    testPage.addBlock(readBlock);
//                    break;
//                case TEST:
//                    IBlock testBlock = readTestBlock(block);
//                    testPage.addBlock(testBlock);
//                    break;
//                case VARIABLE:
//                    readVariableBlock(testPage, block);
//                    break;
//                case INCLUDE:
//                    readTestPageInclude(null);
//                    break;
//                default:
//                    LOG.warn("Unhandled block type: " + block.getBlockType());
//                    break;
//            }
//        }
//        return testPage;
//    }

//    private void readVariableBlock(TestPage testPage, TestPageBlock block) {
//        VariableBlock varBlock = new VariableBlock();
//        varBlock.setBlockLines(block.getLines());
//        testPage.addBlock(varBlock);
//    }
//
//    private IBlock readTestBlock(TestPageBlock block) {
//        TestBlock testBlock = new TestBlock();
//        testBlock.setFixtureName(block.getLineAt(0).getCellAt(1));
//        for (BlockLine line : block.getLines()) {
//            if (block.getLines().indexOf(line) > 0) { // Title line not needed
//                testBlock.addLine(line.getCellAt(0), line.getCellAt(1), line.getCellAt(2));
//            }
//        }
//        return testBlock;
//    }

//    private IBlock readTestPageInclude(TestPageBlock block) {
//        String fileName = block.getLineAt(1).getCellAt(0);
//        String variableName = block.getLineAt(1).getCellAt(1);
//        File file = new File(sourceFolder + "/" + fileName); // possible null
//// pointer
//        if (variableName == null) {
//            TestParser testParser = new TestParser();
//            // testPage.addBlock(testParser.parse(file));
//            return testParser.parse(file);
//        }
//        return null;
//    }

//    public TestPageBlock handleMultiVarLine(String line, BufferedReader br, TestPageBlock varBlock)
//            throws IOException {
//        if (varBlock == null) {
//            varBlock = new TestPageBlock(BlockType.VARIABLE);
//        }
//        String[] split = line.split(VARIABLE_ASSIGNATION_SEPARATOR);
//        String varName = split[0].trim();
//        String varValue = "";
//        do {
//            line = br.readLine();
//            if (!line.startsWith("\"\"\"")) {
//                varValue += line.replace("\n", " ").replace("\t", " ") + " ";
//            }
//        }
//        while (!line.trim().contains("\"\"\""));
//        varBlock.addLine(Arrays.asList(varName, varValue));
//        return varBlock;
//    }

//    public TestPageBlock handleVarLine(String line, TestPageBlock varBlock) {
//        if (varBlock == null) {
//            varBlock = new TestPageBlock(BlockType.VARIABLE);
//        }
//        String[] split = line.split(VARIABLE_ASSIGNATION_SEPARATOR);
//        String varName = split[0].trim();
//        String varValue = split[1].trim();
//        varBlock.addLine(Arrays.asList(varName, varValue));
//        return varBlock;
//    }

    private boolean isVarMultiLine(String line) {
        return line != null && line.startsWith("$")
                && line.contains(VARIABLE_ASSIGNATION_SEPARATOR)
                && line.contains("\"\"\"");
    }

    private boolean isVarLine(String line) {
        return line != null && line.startsWith("$")
                && line.contains(VARIABLE_ASSIGNATION_SEPARATOR)
                && !line.contains("\"\"\"");
    }

    private boolean isBlockLine(String line) {
        return line != null && line.contains("|");
    }

    private boolean isBlockStart(String line) {
        return line != null && line.contains("||");
    }

    private void addErrorMessage(TestPage testPage, int lineNumber, String message) {
        testPage.setParsingErrorMessage("Syntax error on line " + lineNumber + " in file " + testPage.getPageName()
                + ": " + message);
    }

    private List<String> cleanList(String[] split) {
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
        }
        List<String> list = new ArrayList<String>(Arrays.asList(split));
        list.remove(0);
        return list;
    }

    private BlockType getBlockType(String line) {
        if (line.isEmpty()) {
            return null;
        }
        if (isVarLine(line) || isVarMultiLine(line)) {
            return BlockType.VARIABLE;
        }
        if (isBlockStart(line)) {
            return BlockType.TEST;
        }
        if (line.startsWith("#include")) {
            return BlockType.INCLUDE;
        }
        return BlockType.COMMENT;
    }

    protected enum BlockType {
        TEST, COMMENT, VARIABLE, INCLUDE
    }
}
