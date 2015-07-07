package com.synaptix.toast.runtime.core.runtime;

import com.synaptix.toast.runtime.guice.AbstractRepositoryTypeParserModule;

public class DefaultRepositoryTypeHandler extends AbstractRepositoryTypeParserModule {

	@Override
	protected void configureModule() {
		addTypeHandler(DefaultRespositoryTypeParser.class);
	}
}
