/**
 * 
 */
package com.synaptix.toast.dao.domain.impl.test.block;

import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.synaptix.toast.core.setup.TestResult;

/**
 * Standard block line, contains strings.
 * 
 * @author E413544
 * 
 */
// @Entity(value = "blocks")
@Embedded
public class BlockLine {

	private List<String> cells;

	private TestResult testResult;

	public BlockLine() {
	}

	public BlockLine(List<String> cells) {
		this.setCells(cells);
	}

	public List<String> getCells() {
		return cells;
	}

	public void setCells(List<String> cells) {
		this.cells = cells;
	}

	/**
	 * Returns the cell at <code>index</code>, returns null if index is out of bounds.
	 * 
	 * @param index
	 * @return
	 */
	public String getCellAt(int index) {
		if (index < 0 || index >= cells.size()) {
			return null;
		}
		return cells.get(index);
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}
}
