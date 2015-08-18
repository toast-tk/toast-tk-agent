package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.block.BlockType;
import com.synaptix.toast.dao.domain.impl.test.block.CommentBlock;

import java.util.Iterator;
import java.util.List;

/**
 * Reads lines in the list in parameter to create a comment block, then removes those lines from the list.
 * <p>
 * Created by Nicolas Sauvage on 06/08/2015.
 */
public class CommentBlockParser implements IBlockParser {
    @Override
    public BlockType getBlockType() {
        return BlockType.COMMENT;
    }

    @Override
    public IBlock digest(List<String> strings, String path) {
        CommentBlock commentBlock = new CommentBlock();
        for (Iterator<String> iterator = strings.iterator(); iterator.hasNext(); ) {
            String string = iterator.next();
            if (string.startsWith("#include") || string.startsWith("|") || string.startsWith("$")) {
                return commentBlock;
            }
            commentBlock.addLine(string);
            iterator.remove();
        }
        return commentBlock;
    }

    /**
     * Any line can be a comment line. Alway false, the TestParser will call the CommentBlockParser only if no other parser wants to parse this line.
     */
    @Override
    public boolean isLineParsable(String line) {
        return false;
    }
}
