package com.tfc.asmlorenzo;

import com.tfc.flame.FlameConfig;
import entries.FlameAPI.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MyASM implements Opcodes {


	public static void checkMethodsAndFields(String className) throws IOException {
		ClassNode node = acceptNode(className);
		for (MethodNode method : node.methods) {
			FlameConfig.field.append(method.name + "---" + method.access + "---" + method.desc + "\n");
		}
		for (FieldNode field : node.fields) {
			FlameConfig.field.append(field.name + "---" + field.access + "---" + field.desc + "---" + field.value + "\n");
		}
	}

	/**
	 * Adds a method to the class or entirely replace an existing one
	 * @param className the name of the class (with package and stuff)
	 * @param access the method access
	 * @param methodName the method name
	 * @param descriptor the method descriptor (based on those rules you sent on discord)
	 * @param signature the method signature (can be null, I still don't know what it means, but it's needed for the MethodNode)
	 * @param exceptions exceptions that method throws (can be null)
	 * @param instructions an InsnList, containing all the instructions of the method written in assembly (kinda, I'll show you an example)
	 */

	public static void addOrReplaceMethod(String className, MyASM.AccessType access, String methodName, String descriptor, String signature, String[] exceptions, InsnList instructions) throws IOException {
		ClassNode node = acceptNode(className);
		boolean existing = false;
		MethodNode newMethod = new MethodNode(access.level, methodName, descriptor, signature, exceptions);
		MethodNode oldMethod = null;
		if (!node.methods.isEmpty()) {
			for (MethodNode method : node.methods) {
				if (newMethod.name.equals(method.name) && method.access == newMethod.access && newMethod.desc.equals(method.desc)) {
					existing = true;
					oldMethod = method;
					break;
				}
			}
		}
		if (!existing) {
			FlameConfig.field.append("Adding new Method!\n");
			newMethod.instructions.add(instructions);
			node.methods.add(newMethod);
		} else {
			FlameConfig.field.append("Replacing new Method!\n");
			oldMethod.instructions.insert(new InsnNode(RETURN));
		}
		node.visitEnd();
		writeOut(node, className);
	}

	/**
	 * Adds a field to the class or replace initial value of an existing one
	 * @param className the name of the class (with package and stuff)
	 * @param access the field access
	 * @param fieldName the field name
	 * @param descriptor the field descriptor (based on those rules you sent on discord)
	 * @param signature the field signature (can be null, I still don't know what it means, but it's needed for the MethodNode)
	 * @param initialValue the initial value of the field (idk if it works, it didn't work very well for me)
	 */

	public static void addField(String className, MyASM.AccessType access, String fieldName, String descriptor, String signature, Object initialValue) throws IOException  {
		ClassNode node = acceptNode(className);
		boolean existing = false;
		if (!node.fields.isEmpty()) {
			for (FieldNode field : node.fields) {
				if (fieldName.equals(field.name) && field.access == access.level && descriptor.equals(field.desc)) {
					existing = true;
					break;
				}
			}
		}
		if (!existing) {
			FlameConfig.field.append("Adding new Field!\n");
			node.visitField(access.level, fieldName, descriptor, signature, initialValue);
		} else {
			FlameConfig.field.append("Field existing!");
		}
		node.visitEnd();
		writeOut(node, className);
	}


	/**
	 * Internal method to automatically accept the ClassNode, instead of always writing that
	 */
	private static ClassNode acceptNode(String className) throws IOException {
		ClassNode node = new ClassNode();
		ClassReader cr = new ClassReader(className);
		cr.accept(node, 0);
		return node;
	}

	/**
	 * Internal method to automatically accept the ClassWriter and write the transformed class, instead of always writing that
	 */
	private static void writeOut(ClassNode node, String name) throws IOException {
		ClassWriter out = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		node.accept(out);
		File f1 = new File(Main.getExecDir() + "\\BetterASM\\pre\\" + name.replace(".", "\\") + ".class");
		if (!f1.exists()) {
			f1.getParentFile().mkdirs();
			f1.createNewFile();
		}
		OutputStream writer1 = new FileOutputStream(f1);
		writer1.write(out.toByteArray());
		writer1.close();
	}

	//Also I moved this here (Lorenzo)
	public enum AccessType {
		PUBLIC(ACC_PUBLIC),
		PUBLIC_STATIC(ACC_PUBLIC + ACC_STATIC),
		PRIVATE(ACC_PRIVATE),
		//Grudgingly, I add this...
		//Please don't use it...
		//I hate private and protected...
		//Especially on static things.
		PRIVATE_STATIC(ACC_PRIVATE + ACC_STATIC),
		PROTECTED(ACC_PROTECTED),
		PROTECTED_STATIC(ACC_PROTECTED + ACC_STATIC);

		public final int level;

		AccessType(int level) {
			this.level = level;
		}


		@Override
		public String toString() {
			return "level: " + level;
		}
	}

}
