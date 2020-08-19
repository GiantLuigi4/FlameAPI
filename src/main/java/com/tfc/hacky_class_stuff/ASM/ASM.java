package com.tfc.hacky_class_stuff.ASM;

import com.tfc.FlameAPIConfigs;
import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import com.tfc.hacky_class_stuff.ASM.API.InstructionData;
import com.tfc.hacky_class_stuff.ASM.transformers.HookinHandler;
import com.tfc.hacky_class_stuff.ASM.transformers.fields.FieldAccessTransformer;
import com.tfc.hacky_class_stuff.ASM.transformers.fields.FieldAdder;
import com.tfc.hacky_class_stuff.ASM.transformers.methods.MethodAccessTransformer;
import com.tfc.hacky_class_stuff.ASM.transformers.methods.MethodAdder;
import com.tfc.utils.Bytecode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ASM {
	private static final HashMap<String, ArrayList<FieldData>> fieldNodes = new HashMap<>();
	private static final HashMap<String, ArrayList<InstructionData>> hookins = new HashMap<>();
	
	private static final HashMap<String, ArrayList<Access>> accessValues = new HashMap<>();
	public static final String transformAll = generateKey();
	
	public static byte[] applyFields(String name, byte[] bytes) {
		if (fieldNodes.containsKey(name) && bytes != null) {
			Bytecode.writeBytes(name, "pre", bytes);
			try {
				ClassReader reader = new ClassReader(bytes);
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
				for (FieldData data : fieldNodes.get(name)) {
					if (data == null) {
						FlameConfig.field.append("Data is " + null + ".\n");
					} else if (reader == null) {
						FlameConfig.field.append("Reader is " + null + ".\n");
					} else if (writer == null) {
						FlameConfig.field.append("Writer is " + null + ".\n");
					} else if (data.type == null) {
						reader.accept(new FieldAdder(FlameAPIConfigs.ASM_Version, writer, data.name, data.defaultVal, data.defaultVal.getClass().getName(), data.access), 0);
					} else {
						reader.accept(new FieldAdder(FlameAPIConfigs.ASM_Version, writer, data.name, data.defaultVal, data.type, data.access), 0);
					}
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
	
	public static byte[] applyMethods(String name, byte[] bytes) {
		if (hookins.containsKey(name) && bytes != null) {
			Bytecode.writeBytes(name, "pre", bytes);
			try {
				ClassReader reader = new ClassReader(bytes);
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
				FlameConfig.field.append(hookins.size() + "\n");
				for (InstructionData data : hookins.get(name)) {
					try {
						FlameConfig.field.append(data.toString() + "\n");
						AtomicInteger access = new AtomicInteger(0);
						AtomicReference<String> descriptor = new AtomicReference<>();
						AtomicReference<String> signature = new AtomicReference<>();
						AtomicReference<String[]> exceptions = new AtomicReference<>();
						node.methods.forEach(node1 -> {
							if (node1.name.equals(data.method)) {
								access.set(node1.access);
								descriptor.set(node1.desc);
								signature.set(node1.signature);
								Object[] exceptionsListObjects = node1.exceptions.toArray();
								String[] exceptionsList = new String[exceptionsListObjects.length];
								for (int i = 0; i < exceptionsListObjects.length; i++) {
									exceptionsList[i] = exceptionsListObjects[i].toString();
								}
								exceptions.set(exceptionsList);
							}
						});
						if (data == null) {
							FlameConfig.field.append("Data is " + null + ".\n");
						} else if (reader == null) {
							FlameConfig.field.append("Reader is " + null + ".\n");
						} else if (writer == null) {
							FlameConfig.field.append("Writer is " + null + ".\n");
						} else {
							new MethodAdder(FlameAPIConfigs.ASM_Version, writer.visitMethod(access.get(), data.method, descriptor.get(), signature.get(), exceptions.get()), data.call, data.point, name, data.method).visitEnd();
						}
					} catch (Throwable ignored) {
					}
				}
				node.visitEnd();
				writer.visitEnd();
				byte[] bytes1 = writer.toByteArray();
				Bytecode.writeBytes(name, "post_methods", bytes1);
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return bytes;
	}
	
	private static final HashMap<String, ArrayList<Access>> accessValuesF = new HashMap<>();
	
	public static byte[] applyMethodTransformers(String name, byte[] bytes) {
		if (accessValues.containsKey(name)) {
			FlameConfig.field.append("Transforming class: " + name + "\n");
			if (!fieldNodes.containsKey(name)) Bytecode.writeBytes(name, "pre", bytes);
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
			for (Access access : accessValues.get(name)) {
				reader.accept(new MethodAccessTransformer(FlameAPIConfigs.ASM_Version, writer, access.name, access.type.level), 0);
			}
			node.visitEnd();
			writer.visitEnd();
			byte[] bytes1 = writer.toByteArray();
			Bytecode.writeBytes(name, "post_at_m", bytes1);
			return bytes1;
		}
		return bytes;
	}
	
	public static byte[] applyHookins(String name, byte[] bytes) {
		ClassObject clazz = HookinHandler.getClassNode(name, bytes);
		if (name.startsWith("mixin")) {
			FlameConfig.field.append("Mixin Class: " + name + "\n");
			try {
				clazz.getObj2().methods.forEach(node -> {
					try {
						HookinHandler.handleMethodNode(name, node);
					} catch (Throwable ignored) {
					}
				});
				clazz.getObj2().fields.forEach(node -> {
					try {
						HookinHandler.handleFieldNode(name, node);
					} catch (Throwable ignored) {
					}
				});
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		if (bytes != null && HookinHandler.hasHookin(name)) {
			try {
				ClassReader reader = clazz.getObj1();
				ClassNode classNode = clazz.getObj2();
				ClassWriter writer = clazz.getObj3();
//				clazz = new ClassObject(reader,classNode,writer);
//				writer.visit(52,reader.getAccess(),reader.getClassName(),classNode.signature,reader.getSuperName(),reader.getInterfaces());
				classNode.methods.forEach(node ->
						HookinHandler.handleHookins(clazz, node)
				);
				HookinHandler.applyFields(clazz);
				classNode.visitEnd();
				writer.visitEnd();
				byte[] bytes1 = writer.toByteArray();
				if (!Arrays.toString(bytes).equals(Arrays.toString(bytes1))) {
					if (!accessValues.containsKey(name) && !fieldNodes.containsKey(name))
						Bytecode.writeBytes(name, "pre", bytes);
					Bytecode.writeBytes(name, "post_mixins", bytes1);
				}
				return bytes;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return bytes;
	}
	
	public static void addHookin(InstructionData data, String targetClazz) {
		FlameConfig.field.append(targetClazz + "\n");
		targetClazz = targetClazz.replace("/", ".");
		FlameConfig.field.append(targetClazz + "\n");
		ArrayList<InstructionData> arrayList = hookins.getOrDefault(targetClazz, new ArrayList<>());
		arrayList.add(data);
		if (!hookins.containsKey(targetClazz)) hookins.put(targetClazz, arrayList);
	}
	
	public static byte[] applyFieldTransformers(String name, byte[] bytes) {
		if (accessValuesF.containsKey(name)) {
			FlameConfig.field.append("Transforming class: " + name + "\n");
			if (!fieldNodes.containsKey(name)) Bytecode.writeBytes(name, "pre", bytes);
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
			for (Access access : accessValuesF.get(name)) {
				reader.accept(new FieldAccessTransformer(FlameAPIConfigs.ASM_Version, writer, access.name, access.type.level), 0);
			}
			node.visitEnd();
			writer.visitEnd();
			byte[] bytes1 = writer.toByteArray();
			Bytecode.writeBytes(name, "post_at_f", bytes1);
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
					if (access1.name.equals(access.name)) {
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
	
	public static void addFeildAT(Access access, String clazz) {
		Iterator<String> classes = accessValuesF.keySet().iterator();
		Iterator<ArrayList<Access>> valuesA = accessValuesF.values().iterator();
		boolean hasClazz = false;
		for (int i = 0; i < accessValuesF.size(); i++) {
			String clazzCheck = classes.next();
			if (clazzCheck.equals(clazz)) {
				hasClazz = true;
				ArrayList<Access> accesses = valuesA.next();
				boolean hasMatch = false;
				for (Access access1 : accesses) {
					if (access1.name.equals(access.name)) {
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
			accessValuesF.put(clazz, list);
		}
	}
	
	private static String generateKey() {
		Random r = new Random();
		
		StringBuilder key = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			key.append((char) (r.nextInt(26) + r.nextInt(26) + 26));
		}
		key.append(":all_");
		for (int i = 0; i < 10; i++) {
			key.append((char) (r.nextInt(26) + r.nextInt(26) + 26));
		}
		return key.toString();
	}
}
