package com.tfc.hacky_class_stuff.ASM.transformers.methods;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodReplacer extends ClassVisitor {
	private final MethodVisitor visitor;
	private final String name;
	
	public MethodReplacer(int api, MethodVisitor visitor, String name) {
		super(api);
		this.visitor = visitor;
		this.name = name;
	}
	
	public MethodReplacer(int api, ClassVisitor classVisitor, MethodVisitor visitor, String name) {
		super(api, classVisitor);
		this.visitor = visitor;
		this.name = name;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		if (name.equals(this.name)) {
			return visitor;
		}
		return super.visitMethod(access, name, descriptor, signature, exceptions);
	}
}
