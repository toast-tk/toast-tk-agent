package com.synaptix.toast.plugin.synaptix.runtime.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synaptix.service.IServiceInterceptor;

class DebugServiceInterceptor implements IServiceInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(DebugServiceInterceptor.class);
	
	private final String factory;
	
	DebugServiceInterceptor(final String factory) {
		this.factory = factory;
	}
	
	@Override
	public void interceptBeginService(
			final String serviceName,
			final String methodName, 
			final Class<?>[] argTypes, 
			final Object[] args
	) {
		logInfoService("interceptBeginService", serviceName, methodName);
	}

	@Override
	public void interceptErrorService(
			final String serviceName,
			final String methodName, 
			final Class<?>[] argTypes, 
			final Object[] args
	) {
		logInfoService("interceptErrorService", serviceName, methodName);
	}

	@Override
	public void interceptSucessService(
			final String serviceName,
			final String methodName, 
			final Class<?>[] argTypes, 
			final Object[] args
	) {
		logInfoService("interceptSucessService", serviceName, methodName);
	}
	
	private void logInfoService(
			final String type,
			final String serviceName,
			final String methodName
	) {
		LOG.info("{} for {}/{}/{}", type, factory, serviceName, methodName);
	}
}