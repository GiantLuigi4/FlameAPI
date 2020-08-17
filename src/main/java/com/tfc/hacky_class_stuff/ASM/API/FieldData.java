package com.tfc.hacky_class_stuff.ASM.API;

public class FieldData {
	public final int access;
	public final String name;
	public final Object defaultVal;
	public final String type;
	
	public FieldData(int access, String name, Object defaultVal) {
		this.access = access;
		this.name = name;
		this.defaultVal = defaultVal;
		this.type = null;
	}
	
	public FieldData(int access, String name, Object defaultVal, String type) {
		this.access = access;
		this.name = name;
		this.defaultVal = defaultVal;
		this.type = type;
	}
}
