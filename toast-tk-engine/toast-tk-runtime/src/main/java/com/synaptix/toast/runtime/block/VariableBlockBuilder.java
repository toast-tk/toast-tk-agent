package com.synaptix.toast.runtime.block;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.synaptix.toast.core.dao.ITestPage;
import com.synaptix.toast.core.runtime.IActionItemRepository;
import com.synaptix.toast.dao.domain.impl.test.block.BlockLine;
import com.synaptix.toast.dao.domain.impl.test.block.VariableBlock;

public class VariableBlockBuilder implements IBlockRunner<VariableBlock>{

	@Inject
	IActionItemRepository objectRepository;
	
	@Override
	public void run(ITestPage page, VariableBlock block) {
		List<BlockLine> blockLines = block.getBlockLines();
		for(BlockLine blockLine : blockLines) {
			String varName = blockLine.getCellAt(0);
			String varValue = blockLine.getCellAt(1);
			objectRepository.getUserVariables().put(varName, varValue);
		}
	}
	
	@Override
	public void setInjector(Injector injector) {
		this.objectRepository = injector.getInstance(IActionItemRepository.class);
	}
}
