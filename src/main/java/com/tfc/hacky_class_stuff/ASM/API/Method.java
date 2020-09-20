package com.tfc.hacky_class_stuff.ASM.API;

import org.objectweb.asm.tree.InsnList;

public class Method {
	private final String name;
	private final String descriptor;
	private final String access;
	private final InsnList instructions;
	
	public Method(String name, String descriptor, String access, InsnList instructions) {
		this.name = name;
		this.descriptor = descriptor;
		this.access = access;
		this.instructions = instructions;
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
	
	public InsnList getInstructions() {
		return instructions;
	}
}
