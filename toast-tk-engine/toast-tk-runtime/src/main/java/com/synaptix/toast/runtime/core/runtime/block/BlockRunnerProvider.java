package com.synaptix.toast.runtime.core.runtime.block;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.dao.domain.impl.test.block.WebPageBlock;

public class BlockRunnerProvider {

	private static final Logger LOG = LogManager.getLogger(BlockRunnerProvider.class);
	private Map<Class<? extends IBlock>, IBlockRunner<? extends IBlock>> map;
	
	@Inject
	public BlockRunnerProvider(){
		map = new HashMap<Class<? extends IBlock>, IBlockRunner<? extends IBlock>>();
		map.put(WebPageBlock.class, new WebPageBlockBuilder());
	}
		
	public IBlockRunner<? extends IBlock> getBlockRunner(Class<? extends IBlock> clazz, Injector injector) throws IllegalAccessException{
		IBlockRunner<? extends IBlock> runner = map.get(clazz);
		if(runner == null){
			LOG.info("No runner found for : " + clazz.getSimpleName());
		}
		runner.setInjector(injector);
		return runner;
	}
	
}
