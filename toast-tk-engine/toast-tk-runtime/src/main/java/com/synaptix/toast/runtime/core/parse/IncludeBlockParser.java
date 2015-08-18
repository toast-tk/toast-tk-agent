package com.synaptix.toast.runtime.core.parse;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.block.BlockType;

import java.util.List;

/**
 * Created by Nico on 06/08/2015.
 */
public class IncludeBlockParser implements IBlockParser {
    @Override
    public BlockType getBlockType() {
        return BlockType.INCLUDE;
    }

    @Override
    public IBlock digest(List<String> strings) {
        //File file = new File(sourceFolder + "/" + fileName); TODO find file
//        TestPage testPage = new TestParser().parse(file);
//        return testPage;
        return null;
    }

    @Override
    public boolean isLineParsable(String line) {
        return line != null && line.startsWith("#include");
    }
}
