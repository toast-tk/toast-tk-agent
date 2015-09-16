package com.synaptix.toast.dao.domain.impl.test.block;

import java.util.ArrayList;
import java.util.List;

import com.github.jmkgreen.morphia.annotations.Embedded;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.ComponentConfigLine;

@Embedded
@Deprecated
public class ConfigBlock implements IBlock {

	private String componentName;

	@Embedded
	private List<ComponentConfigLine> lines;

	public ConfigBlock() {
		lines = new ArrayList<ComponentConfigLine>();
	}

	public List<ComponentConfigLine> getLines() {
		return lines;
	}

	public void setLines(
		List<ComponentConfigLine> lines) {
		this.lines = lines;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(
		String componentName) {
		this.componentName = componentName;
	}

	public void addLine(
		String testName,
		String systemName,
		String componentAssociation) {
		lines.add(new ComponentConfigLine(testName, systemName, componentAssociation));
	}

	@Override
	public String getBlockType() {
		return "config";
	}

	@Override
	public int getNumberOfLines() {
		return 0;
	}

	@Override
	public int getOffset() {
		return 0;
	}
}
