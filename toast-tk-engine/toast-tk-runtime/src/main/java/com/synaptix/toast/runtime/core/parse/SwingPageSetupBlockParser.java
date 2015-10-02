package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.BlockType;
import com.synaptix.toast.dao.domain.impl.test.SwingPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.WebPageConfigLine;
import com.synaptix.toast.dao.domain.impl.test.block.SwingPageBlock;
import com.synaptix.toast.dao.domain.impl.test.block.WebPageBlock;
import com.synaptix.toast.runtime.parse.IBlockParser;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SwingPageSetupBlockParser implements IBlockParser {

    @Override
    public BlockType getBlockType() {
        return BlockType.SWING_PAGE_SETUP;
    }

    @Override
    public IBlock digest(List<String> strings, String path) {
        String firstLine = strings.get(0);
        if (!firstLine.startsWith("||")) {
            throw new IllegalArgumentException("Setup block does not have a title: " + firstLine);
        }

        SwingPageBlock swingPageBlock = new SwingPageBlock();

        // Find web page name
        String[] title = StringUtils.split(firstLine, "||");
        if (title.length >= 2) {
        	swingPageBlock.setFixtureName(title[2].trim());
        }

        // Add test lines to block
        for (String string : strings.subList(2, strings.size())) {
            if (!string.startsWith("|")) {
                return swingPageBlock;
            }
            String[] split = StringUtils.split(string, "|");
            if (split.length != 3) {
                throw new IllegalArgumentException("Swing page setup line does not have enough columns: " + firstLine);
            }
            SwingPageConfigLine swingPageConfigLine = new SwingPageConfigLine();
            swingPageConfigLine.setElementName(split[0].trim());
            swingPageConfigLine.setType(split[1].trim());
            swingPageConfigLine.setLocator(split[2].trim());
            swingPageBlock.addLine(swingPageConfigLine);
        }

        return swingPageBlock;
    }

    @Override
    public boolean isFirstLineOfBlock(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.startsWith("||")) {
            String[] split = StringUtils.split(trimmedLine,"||");

            if (split.length >= 2 && split[0].contains("setup")  && split[1].contains("swing")) {
                return true;
            }
        }
        return false;
    }
}
