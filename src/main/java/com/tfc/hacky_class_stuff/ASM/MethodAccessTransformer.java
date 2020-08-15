package com.tfc.hacky_class_stuff.ASM;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodAccessTransformer extends ClassVisitor {
	private final String methodName;
	private final int access;
	private boolean isMethodPresent;
	private String methodType = "";
	private String signature = "";
	private String[] exceptions;
	
	public MethodAccessTransformer(int api, String methodName, int access) {
		super(api);
		this.methodName = methodName;
		this.access = access;
	}
	
	public MethodAccessTransformer(int api, ClassVisitor classVisitor, String methodName, int access) {
		super(api, classVisitor);
		this.methodName = methodName;
		this.access = access;
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		if (name.equals(methodName)) {
			isMethodPresent = true;
			methodType = descriptor;
			this.signature = signature;
			this.exceptions = exceptions;
		}
		return super.visitMethod(access, name, descriptor, signature, exceptions);
	}
	
	@Override
	public void visitEnd() {
		if (!isMethodPresent) {
			if (cv != null) {
				FieldVisitor fv = cv.visitField(access, methodName, methodType, signature, exceptions);
				if (fv != null) {
					fv.visitEnd();
				}
			}
		}
		if (cv != null) {
			cv.visitEnd();
		}
	}
}
