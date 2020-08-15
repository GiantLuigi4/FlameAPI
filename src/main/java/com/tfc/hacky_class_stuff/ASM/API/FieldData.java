package com.tfc.hacky_class_stuff.ASM.API;

@Deprecated
/**
 * Deprecated
 * Not deleting it because it could be useful, we'll see it together
 * My version is better, both in file size (only one) and in code (eh eh daily flex)
 */
public class FieldData {
	public final int access;
	public final String name;
	public final Object defaultVal;
	
	public FieldData(int access, String name, Object defaultVal) {
		this.access = access;
		this.name = name;
		this.defaultVal = defaultVal;
	}
}
