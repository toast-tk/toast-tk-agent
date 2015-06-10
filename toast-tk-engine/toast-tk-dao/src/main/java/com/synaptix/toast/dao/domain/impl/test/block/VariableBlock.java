/**
 * 
 */
package com.synaptix.toast.dao.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.synaptix.toast.core.dao.IBlock;

/**
 * A variable block.
 * 
 */
@Embedded
public class VariableBlock implements IBlock {

	private List<BlockLine> blockLines;

	private BlockLine columns;

	public List<BlockLine> getBlockLines() {
		return blockLines;
	}

	public void setBlockLines(List<BlockLine> blockLines) {
		this.blockLines = blockLines;
	}

	public BlockLine getColumns() {
		return columns;
	}

	public void setColumns(BlockLine columns) {
		this.columns = columns;
	}


	public void addline(BlockLine line) {
		blockLines.add(line);
	}

	/**
	 * 
	 */
	public VariableBlock() {
		blockLines = new ArrayList<BlockLine>();
	}

	@Override
	public String getBlockType() {
		return "variableBlock";
	}
}
