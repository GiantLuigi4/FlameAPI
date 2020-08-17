package com.tfc.hacky_class_stuff.ASM;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

//https://www.baeldung.com/java-asm#1-working-with-fields
public class FieldAdder extends ClassVisitor {
	private final String fieldName;
	private final Object fieldDefault;
	private final String fieldType;
	private final int access;
	private boolean isFieldPresent;
	private String signature = "null";
	
	public FieldAdder(int api, String fieldName, Object fieldDefault, String fieldType, int access) {
		super(api);
		this.fieldName = fieldName;
		this.fieldDefault = fieldDefault;
		this.fieldType = fieldType;
		this.access = access;
	}
	
	public FieldAdder(int api, ClassVisitor cv, String fieldName, Object fieldDefault, String fieldType, int access) {
		super(api, cv);
		this.fieldName = fieldName;
		this.fieldDefault = fieldDefault;
		this.fieldType = fieldType;
		this.access = access;
	}
	
	public FieldAdder(int api, String fieldName, Object fieldDefault, String fieldType) {
		super(api);
		this.fieldName = fieldName;
		this.fieldDefault = fieldDefault;
		this.fieldType = fieldType;
		access = org.objectweb.asm.Opcodes.ACC_PUBLIC;
	}
	
	public FieldAdder(int api, ClassVisitor cv, String fieldName, Object fieldDefault, String fieldType) {
		super(api, cv);
		this.fieldName = fieldName;
		this.fieldDefault = fieldDefault;
		this.fieldType = fieldType;
		access = org.objectweb.asm.Opcodes.ACC_PUBLIC;
	}
	
	public FieldAdder setSignature(String signature) {
		this.signature = signature;
		return this;
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (name.equals(this.fieldName)) {
			isFieldPresent = true;
		}
		return cv.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public void visitEnd() {
		if (!isFieldPresent) {
			if (cv != null) {
				FieldVisitor fv = cv.visitField(access, fieldName, "L" + fieldType.replace(".", "/") + ";", this.signature, fieldDefault);
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
