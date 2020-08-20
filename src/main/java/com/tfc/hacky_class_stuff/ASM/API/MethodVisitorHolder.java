package com.tfc.hacky_class_stuff.ASM.API;

import org.objectweb.asm.MethodVisitor;

public class MethodVisitorHolder {
	public final String method;
	public final MethodVisitor visitor;
	
	public MethodVisitorHolder(String method, MethodVisitor visitor) {
		this.method = method;
		this.visitor = visitor;
	}
}
