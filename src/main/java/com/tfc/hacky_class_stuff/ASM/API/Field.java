package com.tfc.hacky_class_stuff.ASM.API;

public class Field {
	private final String name;
	private final String descriptor;
	private final String access;
	private final Object defaultValue;
	
	public Field(String name, String descriptor, String access, Object defaultValue) {
		this.name = name;
		this.descriptor = descriptor;
		this.access = access;
		this.defaultValue = defaultValue;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescriptor() {
		return descriptor;
	}
	
	public String getAccess() {
		return access;
	}
	
	public Object getDefaultValue() {
		return defaultValue;
	}
}
