package com.tfc.hacky_class_stuff.ASM;

import com.tfc.utils.Bytecode;
import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ASM {
	private static final HashMap<String, ArrayList<FieldData>> fieldNodes = new HashMap<>();
	
	private static final HashMap<String, ArrayList<Access>> accessValues = new HashMap<>();
	
	public static byte[] applyFields(String name, byte[] bytes) {
		if (fieldNodes.containsKey(name) && bytes != null) {
			Bytecode.writeBytes(name, "pre", bytes);
			try {
				ClassReader reader = new ClassReader(bytes);
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				ClassWriter writer = new ClassWriter(reader, Opcodes.ASM7);
				for (FieldData data : fieldNodes.get(name)) {
					reader.accept(new FieldAdder(Opcodes.ASM7, writer, data.name, data.defaultVal, data.defaultVal.getClass().getName(), data.access), 0);
				}
				node.visitEnd();
				writer.visitEnd();
				byte[] bytes1 = writer.toByteArray();
				Bytecode.writeBytes(name, "post_fields", bytes1);
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return bytes;
	}
	
	public static byte[] applyMethodTransformers(String name, byte[] bytes) {
		if (accessValues.containsKey(name)) {
			FlameConfig.field.append("Transforming class: " + name + "\n");
			if (!fieldNodes.containsKey(name)) Bytecode.writeBytes(name, "pre", bytes);
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			ClassWriter writer = new ClassWriter(reader, Opcodes.ASM7);
			for (Access access : accessValues.get(name)) {
				reader.accept(new MethodAccessTransformer(Opcodes.ASM7, writer, access.method, access.type.level), 0);
			}
			node.visitEnd();
			writer.visitEnd();
			byte[] bytes1 = writer.toByteArray();
			Bytecode.writeBytes(name, "post_at", bytes1);
			return bytes1;
		}
		return bytes;
	}
	
	public static void addMethodAT(Access access, String clazz) {
		Iterator<String> classes = accessValues.keySet().iterator();
		Iterator<ArrayList<Access>> valuesA = accessValues.values().iterator();
		boolean hasClazz = false;
		for (int i = 0; i < accessValues.size(); i++) {
			String clazzCheck = classes.next();
			if (clazzCheck.equals(clazz)) {
				hasClazz = true;
				ArrayList<Access> accesses = valuesA.next();
				boolean hasMatch = false;
				for (Access access1 : accesses) {
					if (access1.method.equals(access.method)) {
						access1.increase(access.type);
						hasMatch = true;
					}
				}
				if (!hasMatch) {
					accesses.add(access);
				}
			}
		}
		if (!hasClazz) {
			ArrayList<Access> list = new ArrayList<>();
			list.add(access);
			accessValues.put(clazz, list);
		}
	}
	
	public static void addFieldNode(String clazz, FieldData node) {
		if (!fieldNodes.containsKey(clazz)) fieldNodes.put(clazz, new ArrayList<>());
		fieldNodes.get(clazz).add(node);
	}
}
