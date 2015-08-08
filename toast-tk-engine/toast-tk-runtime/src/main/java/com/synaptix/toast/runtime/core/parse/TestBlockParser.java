package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.block.TestBlock;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;

/**
 * Parse a test block.
 *
 * Created by Nico on 06/08/2015.
 */
public class TestBlockParser implements IBlockParser {
    @Override
    public IBlock digest(List<String> strings) throws Exception {
        String firstLine = strings.get(0);
        if (!firstLine.startsWith("||")) {
            throw new Exception("Test block does not have a title: " + firstLine);
        }

        TestBlock testBlock = new TestBlock();

        // Find default action type
        String[] title = StringUtils.split(firstLine, "||");
        if (title.length >= 2) {
            testBlock.setFixtureName(title[1]);
        }
        strings.remove(0);

        // Add test lines to block
        for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
            String string = iterator.next();
            if (!string.startsWith("|")) {
                return testBlock;
            }
            String[] split = StringUtils.split(string, "|");
            if (split.length > 2) {

            }
            testBlock.addLine(split[0], split[1], split[2]);
            iterator.remove();
        }

        return testBlock;
    }
}
