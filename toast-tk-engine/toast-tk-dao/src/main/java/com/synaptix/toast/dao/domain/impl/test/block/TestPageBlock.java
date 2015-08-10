package com.synaptix.toast.dao.domain.impl.test.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPageBlock {

	public enum BlockType {
		TEST, COMMENT, VARIABLE
	}

	public final List<BlockLine> lines;

	public final BlockType blockType;

	public TestPageBlock(
		BlockType type) {
		lines = new ArrayList<BlockLine>();
		blockType = type;
	}

	public void addLine(
		List<String> cells) {
		getLines().add(new BlockLine(cells));
	}

	public void addLine(
		String cell) {
		getLines().add(new BlockLine(Arrays.asList(cell)));
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public List<BlockLine> getLines() {
		return lines;
	}

	public BlockLine getLineAt(
		int i) {
		return lines.get(i);
	}
}
