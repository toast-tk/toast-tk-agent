package com.synaptix.toast.runtime.block;

import com.google.inject.Injector;
import com.synaptix.toast.core.dao.IBlock;
import com.synaptix.toast.core.dao.ITestPage;

public interface IBlockRunner<E extends IBlock> {
	
	public void run(ITestPage page, E block) throws IllegalAccessException, ClassNotFoundException;

	public void setInjector(Injector injector);

}
