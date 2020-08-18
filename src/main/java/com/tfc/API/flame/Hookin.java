package com.tfc.API.flame;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Shows that the targeted method is a mixin and should be inserted to a different method.
 */
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface Hookin {
	String targetClass();
	
	String targetMethod();
	
	String point();
	
	enum Point {
		TOP("top"),
		BOTTOM("bottom");
		
		String point;
		
		Point(String point) {
			this.point = point;
		}
		
		public static Point fromString(String name) {
			for (Point point : Point.values()) {
				if (point.toString().equals(name)) return point;
			}
			return Point.TOP;
		}
		
		public String toString() {
			return point;
		}
	}
}
