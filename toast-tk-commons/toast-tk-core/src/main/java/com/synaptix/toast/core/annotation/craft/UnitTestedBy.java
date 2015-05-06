package com.synaptix.toast.core.annotation.craft;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import junit.framework.TestCase;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface UnitTestedBy {
	Class<?> value();
}
