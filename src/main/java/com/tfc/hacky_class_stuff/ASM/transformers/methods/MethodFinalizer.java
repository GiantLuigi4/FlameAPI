package com.tfc.hacky_class_stuff.ASM.transformers.methods;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodFinalizer extends ClassVisitor {
	private final MethodAdder methodVisitor;
	
	public MethodFinalizer(int api, MethodAdder methodVisitor) {
		super(api);
		this.methodVisitor = methodVisitor;
	}
	
	public MethodFinalizer(int api, ClassVisitor classVisitor, MethodAdder methodVisitor) {
		super(api, classVisitor);
		this.methodVisitor = methodVisitor;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		if (methodVisitor.methodName.equals(name)) {
			return methodVisitor;
		}
		return super.visitMethod(access, name, descriptor, signature, exceptions);
	}
}
