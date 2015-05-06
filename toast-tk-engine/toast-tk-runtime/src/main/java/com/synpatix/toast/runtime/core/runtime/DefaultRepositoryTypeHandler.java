package com.synpatix.toast.runtime.core.runtime;

import com.synpatix.toast.runtime.guice.AbstractRepositoryTypeParserModule;

public class DefaultRepositoryTypeHandler extends AbstractRepositoryTypeParserModule {

	@Override
	protected void configureModule() {
		addTypeHandler(DefaultRespositoryTypeParser.class);
	}

}
