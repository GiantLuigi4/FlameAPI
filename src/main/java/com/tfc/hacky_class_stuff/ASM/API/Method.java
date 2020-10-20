package com.tfc.hacky_class_stuff.ASM.API;

import org.objectweb.asm.tree.InsnList;

public class Method {
	private final String name;
	private final String descriptor;
	private final String access;
	private final InsnList instructions;
	private final boolean replace;
	
	public Method(String name, String descriptor, String access, InsnList instructions) {
		this.name = name;
		this.descriptor = descriptor;
		this.access = access;
		this.instructions = instructions;
		this.replace = false;
	}
	
	public Method(String name, String descriptor, String access, InsnList instructions, boolean replace) {
		this.name = name;
		this.descriptor = descriptor;
		this.access = access;
		this.instructions = instructions;
		this.replace = replace;
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
	
	public boolean getReplace() {
		return replace;
	}
	
	public InsnList getInstructions() {
		return instructions;
	}
}
