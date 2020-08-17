package com.tfc.hacky_class_stuff.ASM;

import com.tfc.API.flamemc.FlameASM;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodAccessTransformer extends ClassVisitor {
	private final String methodName;
	private boolean isMethodPresent;
	private int access;
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
			FlameASM.AccessType access1 = FlameASM.AccessType.forLevel(access);
			FlameASM.AccessType access2 = FlameASM.AccessType.forLevel(this.access);
			Access access3 = new Access(access1, name);
			//Get the highest of both accesses.
			access3.increase(access2);
			this.access = access3.type.level;
//			this.access = Math.max(this.access, access);
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
