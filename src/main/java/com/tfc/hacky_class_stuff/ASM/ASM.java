package com.tfc.hacky_class_stuff.ASM;

import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
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

public class ASM {
	private static final HashMap<String, ArrayList<FieldData>> fieldNodes = new HashMap<>();
	
	public static byte[] apply(String name, byte[] bytes) {
		if (fieldNodes.containsKey(name)) {
			writeBytes(name, "pre", bytes);
			try {
				ClassReader reader = new ClassReader(bytes);
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				ClassWriter writer = new ClassWriter(reader, Opcodes.ASM7);
				for (FieldData data : fieldNodes.get(name)) {
//					node.fields.add(new FieldNode(data.access,data.name,"L"+(data.defaultVal.getClass().toString().replace(".","/")),"",data.defaultVal));
					reader.accept(new FieldAdder(Opcodes.ASM7, writer, data.name, data.defaultVal, data.defaultVal.getClass().getName(), data.access), 0);
				}
				node.visitEnd();
				writer.visitEnd();
				byte[] bytes1 = writer.toByteArray();
				writeBytes(name, "post", bytes1);
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return bytes;
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
