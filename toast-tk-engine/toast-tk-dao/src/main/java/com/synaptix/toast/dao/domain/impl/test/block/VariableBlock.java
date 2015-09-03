package com.synaptix.toast.dao.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.synaptix.toast.core.dao.IBlock;

@Embedded
public class VariableBlock implements IBlock {

	private List<BlockLine> blockLines;

	private BlockLine columns;

	public List<BlockLine> getBlockLines() {
		return blockLines;
	}

	int number0fLines;

	public VariableBlock() {
		blockLines = new ArrayList<BlockLine>();
		number0fLines = 0;
	}

	public void setBlockLines(
			List<BlockLine> blockLines) {
		this.blockLines = blockLines;
	}

	public BlockLine getColumns() {
		return columns;
	}

	public void setColumns(
			BlockLine columns) {
		this.columns = columns;
	}

	public void addline(
			BlockLine line) {
		blockLines.add(line);
	}

	@Override
	public String getBlockType() {
		return "variable";
	}

	@Override
	public int getNumberOfLines() {
		return number0fLines;
	}

	public void setNumber0fLines(int number0fLines){
		this.number0fLines = number0fLines;
	}
}
