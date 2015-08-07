package com.synaptix.toast.runtime.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.synaptix.toast.runtime.core.runtime.block.BlockRunnerProvider;
import com.synaptix.toast.runtime.core.runtime.block.SwingPageBlockBuilder;
import com.synaptix.toast.runtime.core.runtime.block.TestBlockRunner;
import com.synaptix.toast.runtime.core.runtime.block.WebPageBlockBuilder;

public class RunnerModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(BlockRunnerProvider.class).in(Singleton.class);
		bind(SwingPageBlockBuilder.class).in(Singleton.class);
		bind(WebPageBlockBuilder.class).in(Singleton.class);
		bind(TestBlockRunner.class).in(Singleton.class);
	}

}
