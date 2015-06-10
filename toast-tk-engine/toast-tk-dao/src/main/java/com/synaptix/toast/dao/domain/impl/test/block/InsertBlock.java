/**
 * 
 */
package com.synaptix.toast.dao.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.synaptix.toast.core.dao.IBlock;

/**
 * An insert block.
 * 
 * @author E413544
 * 
 */
// @Entity(value = "blocks")
@Embedded
public class InsertBlock implements IBlock {

	private List<BlockLine> blockLines;

	private BlockLine columns;

	private String componentString;

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

	public String getComponentName() {
		return componentString;
	}

	public void setComponentName(String componentName) {
		this.componentString = componentName;
	}

	public void addline(BlockLine line) {
		blockLines.add(line);
	}

	/**
	 * 
	 */
	public InsertBlock() {
		blockLines = new ArrayList<BlockLine>();
	}

	@Override
	public String getBlockType() {
		return "insert";
	}
}
