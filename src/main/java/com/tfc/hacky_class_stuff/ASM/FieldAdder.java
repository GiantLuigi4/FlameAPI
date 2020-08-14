package com.tfc.hacky_class_stuff.ASM;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

//https://www.baeldung.com/java-asm
public class FieldAdder extends ClassVisitor {
	private final String fieldName;
	private final Object fieldDefault;
	private final String fieldType;
	private final int access;
	private boolean isFieldPresent;
	
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
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//		if (name.equals(fieldName)) {
		isFieldPresent = true;
//		}
		return cv.visitField(this.access, this.fieldName, this.fieldType, signature, this.fieldDefault);
	}
	
	@Override
	public void visitEnd() {
		if (!isFieldPresent) {
			FieldVisitor fv = cv.visitField(access, fieldName, fieldType, null, fieldDefault);
			if (fv != null) {
				fv.visitEnd();
			}
		}
		cv.visitEnd();
	}
}
