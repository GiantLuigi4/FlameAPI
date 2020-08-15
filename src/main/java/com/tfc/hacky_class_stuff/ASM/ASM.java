package com.tfc.hacky_class_stuff.ASM;

import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import com.tfc.hacky_class_stuff.ASM.API.MethodAccess;
import entries.FlameAPI.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ASM {
	private static final HashMap<String, ArrayList<FieldData>> fieldNodes = new HashMap<>();
	
	private static HashMap<String, ArrayList<MethodAccess>> accessValues = new HashMap<>();
	
	public static byte[] applyFields(String name, byte[] bytes) {
		if (fieldNodes.containsKey(name)&&bytes!=null) {
			writeBytes(name, "pre", bytes);
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
				writeBytes(name, "post_fields", bytes1);
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return bytes;
	}
	
	public static byte[] applyMethodTransformers(String name, byte[] bytes) {
		if (accessValues.containsKey(name)) {
			if (!fieldNodes.containsKey(name)) {
				writeBytes(name, "pre", bytes);
			}
			ClassReader reader = new ClassReader(bytes);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			ClassWriter writer = new ClassWriter(reader, Opcodes.ASM7);
			for (MethodAccess access : accessValues.get(name)) {
				reader.accept(new MethodAccessTransformer(Opcodes.ASM7, writer, access.method, access.type.level), 0);
			}
			node.visitEnd();
			writer.visitEnd();
			byte[] bytes1 = writer.toByteArray();
			writeBytes(name, "post_at", bytes1);
			return bytes1;
		}
		return bytes;
	}
	
	public static void addMethodAT(MethodAccess access, String clazz) {
		Iterator<String> classes = accessValues.keySet().iterator();
		Iterator<ArrayList<MethodAccess>> valuesA = accessValues.values().iterator();
		boolean hasClazz = false;
		for (int i = 0; i < accessValues.size(); i++) {
			String clazzCheck = classes.next();
			if (clazzCheck.equals(clazz)) {
				hasClazz = true;
				ArrayList<MethodAccess> accesses = valuesA.next();
				boolean hasMatch = false;
				for (MethodAccess access1 : accesses) {
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
			ArrayList<MethodAccess> list = new ArrayList<>();
			list.add(access);
			accessValues.put(clazz, list);
		}
	}
	
	private static void writeBytes(String clazz, String file, byte[] bytes) {
		try {
			File f1 = new File(Main.getGameDir() + "\\FlameASM\\" + file + "\\" + clazz.replace(".", "\\") + ".class");
			if (!f1.exists()) {
				f1.getParentFile().mkdirs();
				f1.createNewFile();
			}
			//https://www.geeksforgeeks.org/convert-byte-array-to-file-using-java/#:~:text=To%20convert%20byte%5B%5D%20to,and%20write%20in%20a%20file.
			OutputStream writer1 = new FileOutputStream(f1);
			writer1.write(bytes);
			writer1.close();
		} catch (Throwable err) {
		}
	}
	
	public static void addFieldNode(String clazz, FieldData node) {
		if (!fieldNodes.containsKey(clazz)) fieldNodes.put(clazz, new ArrayList<>());
		fieldNodes.get(clazz).add(node);
	}
}
