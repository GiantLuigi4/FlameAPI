package com.tfc.hacky_class_stuff.ASM.transformers.methods;

import com.tfc.API.flame.annotations.ASM.Hookin;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class MethodAdder extends MethodVisitor {
	protected final String methodCall;
	protected final Hookin.Point point;
	protected final String clazz;
	protected final String methodName;
	protected final MethodNode append;
	private final boolean itf = false;
	int insn = 0;
	
	public MethodAdder(int api, String methodCall, Hookin.Point point, String clazz, String methodName, MethodNode append) {
		super(api);
		this.methodCall = methodCall;
		this.point = point;
		this.clazz = clazz;
		this.methodName = methodName;
		this.append = append;
	}
	
	public MethodAdder(int api, MethodVisitor mv, String methodCall, Hookin.Point point, String clazz, String methodName, MethodNode append) {
		super(api, mv);
		this.methodCall = methodCall;
		this.point = point;
		this.clazz = clazz;
		this.methodName = methodName;
		this.append = append;
	}
	
	@Override
	public void visitEnd() {
		if (point.equals(Hookin.Point.TOP)) {
			super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "$" + clazz, methodName, methodCall, itf);
//			try {
//				Class<MethodVisitor> visitorClass = MethodVisitor.class;
//				Field f = visitorClass.getDeclaredField("mv");
//				f.setAccessible(true);
//				MethodVisitor visitor = (MethodVisitor) f.get(this);
//				f.set(this, append);
//				super.visitEnd();
//				f.set(this, visitor);
//			} catch (Throwable ignored) {}
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
