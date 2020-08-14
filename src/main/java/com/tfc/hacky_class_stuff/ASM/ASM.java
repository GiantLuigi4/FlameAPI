package com.tfc.hacky_class_stuff.ASM;

import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.FieldNode;
import entries.FlameAPI.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ASM {
	private static final HashMap<String, ArrayList<FieldNode>> fieldNodes = new HashMap<>();
	
	public static byte[] apply(String name, byte[] bytes) {
		if (fieldNodes.containsKey(name)) {
			
			try {
				ClassReader reader = new ClassReader(bytes);
				File f1 = new File(Main.getGameDir() + "\\FlameASM\\pre\\" + name.replace(".", "\\") + ".class");
				if (!f1.exists()) {
					f1.getParentFile().mkdirs();
					f1.createNewFile();
				}
				//https://www.geeksforgeeks.org/convert-byte-array-to-file-using-java/#:~:text=To%20convert%20byte%5B%5D%20to,and%20write%20in%20a%20file.
				OutputStream writer1 = new FileOutputStream(f1);
				writer1.write(bytes);
				writer1.close();
//				FlameConfig.field.append("Name pre-transform: " + reader.getClassName() + "\n");
				Writer writer = new Writer(reader, 0);
				fieldNodes.getOrDefault(name, new ArrayList<>()).forEach(node -> {
					FlameConfig.field.append("Adding node: " + node.name + " to class: " + name + ".\n");
					writer.adders.add(new FieldAdder(Opcodes.ASM4, node.name, node.defaultVal, node.defaultVal.getClass().getName(), node.access));
				});
//				FlameConfig.field.append("Name post-transform: " + reader.getClassName() + "\n");
				byte[] bytes1 = writer.doTransform();
				if (FlameConfig.log_bytecode) FlameConfig.field.append(Arrays.toString(bytes1));
				File f2 = new File(Main.getGameDir() + "\\FlameASM\\post\\" + name.replace(".", "\\") + ".class");
				if (!f2.exists()) {
					f2.getParentFile().mkdirs();
					f2.createNewFile();
				}
				//https://www.geeksforgeeks.org/convert-byte-array-to-file-using-java/#:~:text=To%20convert%20byte%5B%5D%20to,and%20write%20in%20a%20file.
				OutputStream writer2 = new FileOutputStream(f2);
				writer2.write(bytes1);
				writer2.close();
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return bytes;
	}
	
	public static void addFieldNode(String clazz, FieldNode node) {
		if (!fieldNodes.containsKey(clazz)) fieldNodes.put(clazz, new ArrayList<>());
		fieldNodes.get(clazz).add(node);
	}
}
