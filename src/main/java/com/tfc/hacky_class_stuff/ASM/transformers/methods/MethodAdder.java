package com.tfc.hacky_class_stuff.ASM.transformers.methods;

import com.tfc.API.flame.Hookin;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodAdder extends MethodVisitor {
	private final String methodCall;
	private final Hookin.Point point;
	private final String clazz;
	private final String methodName;
	private final boolean itf = false;
	int insn = 0;
	
	public MethodAdder(int api, String methodCall, Hookin.Point point, String clazz, String methodName) {
		super(api);
		this.methodCall = methodCall;
		this.point = point;
		this.clazz = clazz;
		this.methodName = methodName;
	}
	
	public MethodAdder(int api, MethodVisitor mv, String methodCall, Hookin.Point point, String clazz, String methodName) {
		super(api, mv);
		this.methodCall = methodCall;
		this.point = point;
		this.clazz = clazz;
		this.methodName = methodName;
	}
	
	@Override
	public void visitEnd() {
		if (point.equals(Hookin.Point.TOP)) {
			super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, clazz, methodName, methodCall, itf);
		}
		super.visitEnd();
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		if (name.equals(methodName))
			insn++;
		super.visitMethodInsn(opcode, owner, name, desc, itf);
	}
}
