package com.synaptix.toast.web.annotations.gwt.shared;

import java.lang.annotation.*;

/**
 * Created by Sallah Kokaina on 12/11/2014.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface InspectionEntryPoint {
    String name() default "none";
}
