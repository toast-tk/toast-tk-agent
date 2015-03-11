package com.synaptix.toast.annotation;

public @interface SynaptixSeleniumFixture {
	public TEST_ENV method() default TEST_ENV.DEFAULT;

	public Class<?> targetView() default SynaptixSeleniumFixture.class;

	public static final String targetViewField = "targetView";
	public static final String methodField = "method";
}
