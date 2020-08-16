package com.tfc.hacky_class_stuff.ASM;


import org.objectweb.asm.ClassVisitor;

public class SupernameSetter extends ClassVisitor {
	private final String newSupername;
	
	public SupernameSetter(int api, String newSupername) {
		super(api);
		this.newSupername = newSupername;
	}
	
	public SupernameSetter(int api, ClassVisitor cv, String newSupername) {
		super(api, cv);
		this.newSupername = newSupername;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		super.visit(version, access, name, signature, newSupername, interfaces);
	}
}
