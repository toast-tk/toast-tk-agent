package com.synaptix.toast.swing.agent.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@BindingAnnotation
@Target({
		ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface StudioEventBus {

}
