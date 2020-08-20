package com.tfc.hacky_class_stuff.ASM.transformers.methods;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodFinalizer extends ClassVisitor {
	private final HookinApplier methodVisitor;
	
	public MethodFinalizer(int api, HookinApplier methodVisitor) {
		super(api);
		this.methodVisitor = methodVisitor;
	}
	
	public MethodFinalizer(int api, ClassVisitor classVisitor, HookinApplier methodVisitor) {
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
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		methodVisitor.visitCode();
	}
	
	@Override
	public void visitSource(String source, String debug) {
		super.visitSource(source, debug);
	}
	
	@Override
	public void visitEnd() {
		methodVisitor.visitEnd();
		super.visitEnd();
	}
}
