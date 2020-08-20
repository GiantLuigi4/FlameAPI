package com.tfc.API.flame.annotations.ASM;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Makes it so that FlameASM can't modify whatever you annotate with this.
 * (NYI, do take note if you want something to be almost fully inaccessible)
 * PLEASE... do not overuse this...
 * I like having the ability to access private/protected stuff without reflection.
 * Only use it if necessary.
 */
@Documented
@Retention(RUNTIME)
@Target({
		ElementType.FIELD,
		ElementType.CONSTRUCTOR,
		ElementType.METHOD,
		ElementType.TYPE
})
@Unmodifiable
public @interface Unmodifiable {
}
