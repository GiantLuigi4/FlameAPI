package com.tfc.hacky_class_stuff.ASM.transformers;

import com.tfc.API.flame.annotations.ASM.Hookin;
import com.tfc.API.flamemc.FlameASM;
import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import com.tfc.hacky_class_stuff.ASM.API.InstructionData;
import com.tfc.hacky_class_stuff.ASM.ASM;
import com.tfc.hacky_class_stuff.ASM.ClassObject;
import com.tfc.utils.TriObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//TODO:COMMENT THE LIFE OUT OF THIS CLASS (not really though)
public class HookinHandler {
	private static final HashMap<String, HashMap<String, ArrayList<TriObject<Hookin.Point, MethodNode, String>>>> hookins = new HashMap<>();
	private static final HashMap<String, HashMap<String, MethodNode>> replacements = new HashMap<>();
	private static final HashMap<String, ArrayList<FieldNode>> fields = new HashMap<>();
	
	public static ClassObject getClassNode(String name, byte[] bytes) {
		ClassNode node = new ClassNode();
		ClassReader reader;
		try {
			reader = new ClassReader(name);
		} catch (Throwable err) {
			reader = new ClassReader(bytes);
		}
		ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
		reader.accept(node, 0);
		node.accept(writer);
		return new ClassObject(reader, node, writer);
	}
	
	public static ClassObject addMethod(ClassObject clazz, InsnList instructions, String name, FlameASM.AccessType access, String returnType, ArrayList<ParameterNode> nodes) {
		MethodNode node = new MethodNode();
		node.instructions = instructions;
		node.name = name;
		node.access = access.level;
		node.desc = "L" + returnType.replace(".", "/") + ";";
		node.parameters = nodes;
		clazz.getObj2().methods.add(node);
		return clazz;
	}
	
	public static ParameterNode createParameter(String name) {
		return new ParameterNode(name, FlameASM.AccessType.PUBLIC.level);
	}
	
	public static void handleMethodNode(String clazz, MethodNode node) {
		ArrayList<AnnotationNode> annotationNodes = new ArrayList<>();
		try {
			annotationNodes.addAll(node.invisibleAnnotations);
		} catch (Throwable ignored) {
		}
		try {
			annotationNodes.addAll(node.visibleAnnotations);
		} catch (Throwable ignored) {
		}
		AnnotationNode hookinNode = null;
		FlameConfig.field.append("Scanning node: " + node.name + "\n");
		String targetClass = null;
		String targetMethod = null;
		Hookin.Point point = null;
		if (node.visibleAnnotations != null) {
			for (AnnotationNode node1 : annotationNodes) {
				FlameConfig.field.append("Annotation: " + node1.desc + "\n");
				try {
					if (node1.desc.contains("Hookin")) {
						hookinNode = node1;
						List<Object> values = node1.values;
						try {
							for (int i = 0; i < values.size() / 2; i++) {
								if (values.get(i * 2).equals("targetClass")) {
									targetClass = (String) values.get(i * 2 + 1);
								} else if (values.get(i * 2).equals("targetMethod")) {
									targetMethod = (String) values.get(i * 2 + 1);
								} else if (values.get(i * 2).equals("point")) {
									point = Hookin.Point.fromString((String) values.get(i * 2 + 1));
								}
							}
						} catch (Throwable err) {
							FlameConfig.field.append(Arrays.toString(values.toArray()) + "\n");
						}
						FlameConfig.field.append("Found Hookin in class: " + clazz + " targeted at class: " + targetClass + " for method: " + targetMethod + " for point: " + point.toString() + "\n");
						ASM.addHookin(new InstructionData(clazz.replaceAll("\\.", "/") + "." + node.name, point, node.name, node), targetClass);
					} else if (node1.desc.contains("Replace")) {
						List<Object> values = node1.values;
						try {
							for (int i = 0; i < values.size() / 2; i++) {
								if (values.get(i * 2).equals("targetClass")) {
									targetClass = (String) values.get(i * 2 + 1);
								} else if (values.get(i * 2).equals("targetMethod")) {
									targetMethod = (String) values.get(i * 2 + 1);
								}
							}
						} catch (Throwable err) {
							FlameConfig.field.append(Arrays.toString(values.toArray()) + "\n");
						}
						FlameConfig.field.append("Found Replacement in class: " + clazz + " targeted at class: " + targetClass + " for method: " + targetMethod + "\n");
						HashMap<String, MethodNode> map = replacements.getOrDefault(targetClass, new HashMap<>());
						if (map.containsKey(targetMethod))
							map.replace(targetMethod, node);
						else
							map.put(targetMethod, node);
						if (!replacements.containsKey(targetClass))
							replacements.put(targetClass, map);
					}
				} catch (Throwable err) {
					FlameConfig.logError(err);
				}
			}
		}
		if (hookinNode != null) {
			if (!hookins.containsKey(targetClass)) {
				HashMap<String, ArrayList<TriObject<Hookin.Point, MethodNode, String>>> map = new HashMap<>();
				ArrayList<TriObject<Hookin.Point, MethodNode, String>> array = new ArrayList<>();
				array.add(new TriObject<>(point, node, clazz));
				map.put(targetMethod, array);
				hookins.put(targetClass, map);
			} else {
				HashMap<String, ArrayList<TriObject<Hookin.Point, MethodNode, String>>> map = hookins.get(targetClass);
				if (!map.containsKey(targetMethod)) {
					ArrayList<TriObject<Hookin.Point, MethodNode, String>> array = new ArrayList<>();
					array.add(new TriObject<>(point, node, clazz));
					map.put(targetMethod, array);
				} else {
					ArrayList<TriObject<Hookin.Point, MethodNode, String>> array = map.get(targetMethod);
					array.add(new TriObject<>(point, node, clazz));
				}
			}
		}
	}
	
	public static void handleFieldNode(String clazz, FieldNode node) {
		ArrayList<AnnotationNode> annotationNodes = new ArrayList<>();
		try {
			annotationNodes.addAll(node.invisibleAnnotations);
		} catch (Throwable ignored) {
		}
		try {
			annotationNodes.addAll(node.visibleAnnotations);
		} catch (Throwable ignored) {
		}
		try {
			FlameConfig.field.append("Scanning node: " + node.name + "\n");
			if (node.visibleAnnotations != null) {
				for (AnnotationNode node1 : annotationNodes) {
					FlameConfig.field.append("Annotation: " + node1.desc + "\n");
					if (node1.desc.contains("AppendField")) {
						List<Object> values = node1.values;
						String clazzTarg = (String) values.get(values.indexOf("targetClass") + 1);
						String defaultVal = (String) values.get(values.indexOf("defaultVal") + 1);
						String type = (String) values.get(values.indexOf("type") + 1);
						if (!fields.containsKey(clazzTarg))
							fields.put(clazzTarg, new ArrayList<>());
						node.desc = type;
						node.value = defaultVal;
						fields.get(clazzTarg).add(node);
					}
				}
			}
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
	}
	
	public static boolean hasHookin(String clazz) {
//		if (clazz.startsWith("net.minecraft.client")) {
//			FlameConfig.field.append(hookins.size()+"\n");
//			FlameConfig.field.append(replacements.size()+"\n");
//			FlameConfig.field.append(fields.size()+"\n");
//			try {
//				FlameConfig.field.append(replacements.get(clazz)+"\n");
//			} catch (Throwable ignored) {
//			}
//		}
		return hookins.containsKey(clazz) || replacements.containsKey(clazz) || fields.containsKey(clazz);
	}
	
	public static ClassObject handleHookins(ClassObject clazz, MethodNode node) {
		String name = node.name.replace("/", ".");
//		if (
//				replacements.containsKey(clazz.getObj2().name) &&
//						replacements.get(clazz.getObj2().name).containsKey(name)
//		) {
//			node.instructions = replacements.get(clazz.getObj2().name).get(name).instructions;
//			clazz.getObj2().visitEnd();
//		}
//		if (hookins.containsKey(clazz.getObj2().name)) {
//			if (hookins.get(clazz.getObj2().name).containsKey(node.name)) {
//				for (BiObject<Hookin.Point, MethodNode> hookin : hookins.get(clazz.getObj2().name).get(node.name)) {
//					if (checkParams(hookin.getObj2().parameters, node.parameters)) {
//						InsnList instructions = hookin.getObj2().instructions;
//						if (hookin.getObj1().equals(Hookin.Point.TOP)) {
//							for (int i = instructions.size() - 1; i >= 0; i--) {
//								node.instructions.insertBefore(instructions.get(i), node.instructions.get(0));
//							}
//							clazz.getObj2().visitEnd();
//						} else if (hookin.getObj1().equals(Hookin.Point.BOTTOM)) {
//							node.instructions.add(instructions);
//							clazz.getObj2().visitEnd();
//						}
//					}
//				}
//			}
//		}
		if (hookins.containsKey(clazz.getObj2().name)) {
			if (hookins.get(clazz.getObj2().name).containsKey(name)) {
				for (TriObject<Hookin.Point, MethodNode, String> hookin : hookins.get(clazz.getObj2().name).get(name)) {
					ASM.addHookin(new InstructionData(hookin.getObj3().replaceAll("\\.", "/") + "." + hookin.getObj2().name, hookin.getObj1(), node.name, node), clazz.getObj2().name);
				}
			}
		}
		return clazz;
	}
	
	public static ClassObject applyFields(ClassObject clazz) {
		FlameConfig.field.append(clazz.getObj2().name + "\n");
		if (fields.containsKey(clazz.getObj2().name.replace("/", "."))) {
			fields.get(clazz.getObj2().name.replace("/", ".")).forEach(node -> {
				FlameConfig.field.append("Adding field " + node.name + " to class " + clazz.getObj2().name + ".\n");
//				clazz.getObj1().accept(new FieldAdder(FlameAPIConfigs.ASM_Version,node.name,node.value,node.desc,node.access).setSignature(node.signature),0);
				ASM.addFieldNode(clazz.getObj2().name.replace("/", "."), new FieldData(
						node.access, node.name, node.value, node.desc
				));
			});
		}
		return clazz;
	}
	
	private static boolean checkParams(List<ParameterNode> params1, List<ParameterNode> params2) {
		int nodesMatching = 0;
		if (params1 != null && params2 != null) {
			if (params1.size() == params2.size()) {
				for (ParameterNode node1 : params1) {
					for (ParameterNode node2 : params2) {
						if (node1.name.equals(node2.name)) {
							nodesMatching++;
						}
					}
				}
			}
		} else {
			return params1 == params2;
		}
		return nodesMatching == params1.size();
	}
}
