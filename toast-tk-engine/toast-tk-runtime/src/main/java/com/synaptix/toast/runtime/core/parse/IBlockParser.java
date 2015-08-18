package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.block.BlockType;

import java.util.List;

/**
 * Block parsers must implement this interface. A block parser can read a list of strings, and create an IBlock. See digest method.
 * <p>
 * Created by Nicolas Sauvage on 06/08/2015.
 */
public interface IBlockParser {

    BlockType getBlockType();

    /**
     * Create a block from the list of strings.
     * Must remove all parsed lines from list in parameter.
     *
     * @param path Path of the test text file.
     */
    IBlock digest(List<String> strings, String path) throws Exception;

    /**
     * Return true if this string should be parsed with this parser.
     */
    boolean isLineParsable(String line);
}
