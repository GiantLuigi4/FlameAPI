package com.tfc.hacky_class_stuff.ASM;

import com.tfc.FlameAPIConfigs;
import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import com.tfc.utils.Bytecode;
import com.tfc.utils.TriObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;
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
				ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
				for (FieldData data : fieldNodes.get(name)) {
					reader.accept(new FieldAdder(FlameAPIConfigs.ASM_Version, writer, data.name, data.defaultVal, data.defaultVal.getClass().getName(), data.access), 0);
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
			ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
			for (Access access : accessValues.get(name)) {
				reader.accept(new MethodAccessTransformer(FlameAPIConfigs.ASM_Version, writer, access.method, access.type.level), 0);
			}
			node.visitEnd();
			writer.visitEnd();
			byte[] bytes1 = writer.toByteArray();
			Bytecode.writeBytes(name, "post_at", bytes1);
			return bytes1;
		}
		return bytes;
	}
	
	public static byte[] applyMixins(String name, byte[] bytes) {
		TriObject<ClassReader, ClassNode, ClassWriter> clazz = MixinHandler.getClassNode(name, bytes);
		if (name.startsWith("mixin")) {
			FlameConfig.field.append("Mixin Class: " + name + "\n");
			try {
				clazz.getObj2().methods.forEach(node -> {
					try {
						MixinHandler.handleMethodNode(name, node);
					} catch (Throwable ignored) {
					}
				});
				clazz.getObj2().fields.forEach(node -> {
					try {
						MixinHandler.handleFieldNode(name, node);
					} catch (Throwable ignored) {
					}
				});
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		if (bytes != null && MixinHandler.hasMixin(name)) {
			try {
				ClassReader reader = clazz.getObj1();
				ClassNode classNode = clazz.getObj2();
				ClassWriter writer = clazz.getObj3();
				classNode.methods.forEach(node ->
						MixinHandler.handleMixins(clazz, node)
				);
				MixinHandler.applyFields(clazz);
//				reader.accept(classNode, 0);
				writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				classNode.accept(writer);
				writer.visitEnd();
				byte[] bytes1 = writer.toByteArray();
				if (!Arrays.toString(bytes).equals(Arrays.toString(bytes1))) {
					if (!accessValues.containsKey(name) && !fieldNodes.containsKey(name))
						Bytecode.writeBytes(name, "pre", bytes);
					Bytecode.writeBytes(name, "post_mixins", bytes1);
				}
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
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
