/**
 * 
 */
package com.synaptix.toast.dao.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.core.report.TestResult;

/**
 * A setup block.
 * 
 * @author E413544
 * 
 */
// @Entity(value = "blocks")
@Embedded
public class SetupBlock implements IBlock {

	private List<BlockLine> blockLines;

	private BlockLine columns;

	private String fixtureName;

	private TestResult testResult;

	/**
	 * 
	 */
	public SetupBlock() {
		blockLines = new ArrayList<BlockLine>();
	}

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

	public String getFixtureName() {
		return fixtureName;
	}

	public void setFixtureName(String fixtureName) {
		this.fixtureName = fixtureName;
	}

	public void addLine(List<String> cells) {
		blockLines.add(new BlockLine(cells));
	}

	public void addLine(BlockLine line) {
		blockLines.add(line);
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}
}
