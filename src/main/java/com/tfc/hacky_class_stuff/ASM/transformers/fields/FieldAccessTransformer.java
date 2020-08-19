package com.tfc.hacky_class_stuff.ASM.transformers.fields;

import com.tfc.API.flamemc.FlameASM;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.ASM;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

public class FieldAccessTransformer extends ClassVisitor {
	private final String fieldName;
	Object value = null;
	private boolean isFieldPresent;
	private int access;
	private String feildType = "";
	private String signature = "";
	
	public FieldAccessTransformer(int api, String fieldName, int access) {
		super(api);
		this.fieldName = fieldName;
		this.access = access;
	}
	
	public FieldAccessTransformer(int api, ClassVisitor classVisitor, String methodName, int access) {
		super(api, classVisitor);
		this.fieldName = methodName;
		this.access = access;
	}
	
	
	@Override
	public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
		if (name.equals(fieldName)) {
			isFieldPresent = true;
			feildType = descriptor;
			this.signature = signature;
			FlameASM.AccessType access1 = FlameASM.AccessType.forLevel(access);
			FlameASM.AccessType access2 = FlameASM.AccessType.forLevel(this.access);
			Access access3 = new Access(access1, name);
			//Get the highest of both accesses.
			access3.increase(access2);
			this.access = access3.type.level;
			this.value = value;
		}
		if (this.fieldName.equals(ASM.transformAll)) {
			FlameASM.AccessType access1 = FlameASM.AccessType.forLevel(access);
			FlameASM.AccessType access2 = FlameASM.AccessType.forLevel(this.access);
			Access access3 = new Access(access1, name);
			access3.increase(access2);
			int transformVal = access3.type.level;
			return super.visitField(transformVal, name, descriptor, signature, value);
		}
		return super.visitField(access, name, descriptor, signature, value);
	}
	
	@Override
	public void visitEnd() {
		if (!isFieldPresent) {
			if (cv != null) {
				FieldVisitor fv = cv.visitField(access, fieldName, feildType, signature, value);
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
