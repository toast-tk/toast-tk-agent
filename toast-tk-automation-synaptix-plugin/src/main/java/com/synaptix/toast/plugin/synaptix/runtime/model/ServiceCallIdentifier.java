package com.synaptix.toast.plugin.synaptix.runtime.model;

public class ServiceCallIdentifier {

	public final String factory;

	public final String serviceName;

	public final String methodName;

	public final Class<?>[] argsType;

	public final Object[] args;

	public ServiceCallIdentifier(
			final String factory,
			final String serviceName,
			final String methodName,
			final Class<?>[] argsType,
			final Object[] args
	) {
		this.factory = factory;
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.argsType = argsType;
		this.args = args;
	}
}