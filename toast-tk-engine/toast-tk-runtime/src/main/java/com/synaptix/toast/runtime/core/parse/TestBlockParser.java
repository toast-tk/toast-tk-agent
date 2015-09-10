package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.BlockType;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import com.synaptix.toast.runtime.parse.IBlockParser;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Parse a test block.
 * <p>
 * A test block starts with a title, which defines the block type. Each test line starts with a pipe '|'
 * <p>
 * <p>
 * Example : <br>
 * <p>
 * || Scenario || Swing || <br>
 * | do something | <br>
 * | check something | <br>
 * | check something 2 | <br>
 * | do something 2 | <br>
 * <p>
 * Created by Nicolas Sauvage on 06/08/2015.
 */
public class TestBlockParser implements IBlockParser {
    @Override
    public BlockType getBlockType() {
        return BlockType.TEST;
    }

    @Override
    public IBlock digest(List<String> strings, String path) {
        String firstLine = strings.get(0);
        if (!firstLine.startsWith("||")) {
            throw new IllegalArgumentException("Test block does not have a title: " + firstLine);
        }

        TestBlock testBlock = new TestBlock();

        // Find default action type
        String[] title = StringUtils.split(firstLine, "||");
        if (title.length >= 2) {
            testBlock.setFixtureName(title[1]);
        }

        // Add test lines to block
        for (String string : strings.subList(1, strings.size())) {
            if (!string.startsWith("|")) {
                return testBlock;
            }
            String[] split = StringUtils.split(string, "|");
            testBlock.addLine(split[0], split.length > 1 ? split[1] : null, split.length > 2 ? split[2] : null);
        }

        return testBlock;
    }

    @Override
    public boolean isFirstLineOfBlock(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.startsWith("||")) {
            String[] split = StringUtils.split(trimmedLine,"||");

            if (split.length >= 1 && split[0].contains("scenario")) {
                return true;
            }
        }
        return false;
    }
}
