package com.tfc.API.flame;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ElementType.FIELD})
public @interface AppendField {
	String targetClass();
	
	String defaultVal();
	
	String type();
}
