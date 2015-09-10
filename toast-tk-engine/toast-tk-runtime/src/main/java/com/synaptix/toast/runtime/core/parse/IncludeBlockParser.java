package com.synaptix.toast.runtime.core.parse;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.BlockType;
import com.synaptix.toast.dao.domain.impl.test.TestPage;
import com.synaptix.toast.runtime.parse.IBlockParser;
import com.synaptix.toast.runtime.parse.TestParser;

/**
 * Parse an include block.
 * <p/>
 * Created by Nicolas Sauvage on 06/08/2015.
 */
public class IncludeBlockParser implements IBlockParser {
	@Override
	public BlockType getBlockType() {
		return BlockType.INCLUDE;
	}

	@Override
	public IBlock digest(List<String> strings, String path) {
		String string = strings.remove(0);
		String pathName = StringUtils.removeStart(string, "#include").trim();
		Path newPath = Paths.get(path).resolveSibling(pathName);
		TestPage testPage = null;
		try {
			testPage = new TestParser().parse(newPath.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return testPage;
	}

	@Override
	public boolean isFirstLineOfBlock(String line) {
		return line != null && line.startsWith("#include");
	}
}
