package com.tfc.hacky_class_stuff.ASM;

import com.tfc.hacky_class_stuff.ASM.API.FieldNode;
import org.objectweb.asm.ClassReader;

import java.util.ArrayList;
import java.util.HashMap;

public class ASM {
	private static final HashMap<String, ArrayList<FieldNode>> fieldNodes = new HashMap<>();
	
	public static byte[] apply(String name, byte[] bytes) {
		ClassReader reader = new ClassReader(bytes);
		Writer writer = new Writer(reader, 2);
		fieldNodes.getOrDefault(name, new ArrayList<>()).forEach(node -> {
			writer.adders.add(new FieldAdder(4, node.name, node.defaultVal, node.defaultVal.getClass().getName(), node.access));
		});
		return writer.doTransform();
	}
	
	public static void addFieldNode(String clazz, FieldNode node) {
		if (!fieldNodes.containsKey(clazz)) fieldNodes.put(clazz, new ArrayList<>());
		fieldNodes.get(clazz).add(node);
	}
}
