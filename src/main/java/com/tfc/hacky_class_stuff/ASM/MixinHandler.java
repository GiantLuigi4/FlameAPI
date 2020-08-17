package com.tfc.hacky_class_stuff.ASM;

import com.tfc.API.flame.Mixin;
import com.tfc.API.flamemc.FlameASM;
import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import com.tfc.utils.BiObject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

//TODO:COMMENT THE LIFE OUT OF THIS CLASS (not really though)
public class MixinHandler {
	private static final HashMap<String, HashMap<String, ArrayList<BiObject<Mixin.Point, MethodNode>>>> mixins = new HashMap<>();
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
		AnnotationNode mixinNode = null;
		FlameConfig.field.append("Scanning node: " + node.name + "\n");
		String targetClass = null;
		String targetMethod = null;
		Mixin.Point point = null;
		if (node.visibleAnnotations != null) {
			for (AnnotationNode node1 : annotationNodes) {
				FlameConfig.field.append("Annotation: " + node1.desc + "\n");
				try {
					if (node1.desc.contains("Mixin")) {
						mixinNode = node1;
						List<Object> values = node1.values;
						try {
							for (int i = 0; i < values.size() / 2; i++) {
								if (values.get(i * 2).equals("targetClass")) {
									targetClass = (String) values.get(i * 2 + 1);
								} else if (values.get(i * 2).equals("targetMethod")) {
									targetMethod = (String) values.get(i * 2 + 1);
								} else if (values.get(i * 2).equals("point")) {
									point = Mixin.Point.fromString((String) values.get(i * 2 + 1));
								}
							}
						} catch (Throwable err) {
							FlameConfig.field.append(Arrays.toString(values.toArray()) + "\n");
						}
						FlameConfig.field.append("Found Mixin in class: " + clazz + " targeted at class: " + targetClass + " for method: " + targetMethod + " for point: " + point.toString() + "\n");
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
		if (mixinNode != null) {
			if (!mixins.containsKey(targetClass)) {
				HashMap<String, ArrayList<BiObject<Mixin.Point, MethodNode>>> map = new HashMap<>();
				ArrayList<BiObject<Mixin.Point, MethodNode>> array = new ArrayList<>();
				array.add(new BiObject<>(point, node));
				map.put(targetMethod, array);
				mixins.put(targetClass, map);
			} else {
				HashMap<String, ArrayList<BiObject<Mixin.Point, MethodNode>>> map = mixins.get(targetClass);
				if (!map.containsKey(targetMethod)) {
					ArrayList<BiObject<Mixin.Point, MethodNode>> array = new ArrayList<>();
					array.add(new BiObject<>(point, node));
					map.put(targetMethod, array);
				} else {
					ArrayList<BiObject<Mixin.Point, MethodNode>> array = map.get(targetMethod);
					array.add(new BiObject<>(point, node));
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
	
	public static boolean hasMixin(String clazz) {
//		if (clazz.startsWith("net.minecraft.client")) {
//			FlameConfig.field.append(mixins.size()+"\n");
//			FlameConfig.field.append(replacements.size()+"\n");
//			FlameConfig.field.append(fields.size()+"\n");
//			try {
//				FlameConfig.field.append(replacements.get(clazz)+"\n");
//			} catch (Throwable ignored) {
//			}
//		}
		return mixins.containsKey(clazz) || replacements.containsKey(clazz) || fields.containsKey(clazz);
	}
	
	public static ClassObject handleMixins(ClassObject clazz, MethodNode node) {
		String name = node.name;
		if (
				replacements.containsKey(clazz.getObj2().name) &&
						replacements.get(clazz.getObj2().name).containsKey(name)
		) {
			node.instructions = replacements.get(clazz.getObj2().name).get(name).instructions;
			clazz.getObj2().visitEnd();
		}
		if (mixins.containsKey(clazz.getObj2().name)) {
			if (mixins.get(clazz.getObj2().name).containsKey(node.name)) {
				for (BiObject<Mixin.Point, MethodNode> mixin : mixins.get(clazz.getObj2().name).get(node.name)) {
					if (checkParams(mixin.getObj2().parameters, node.parameters)) {
						InsnList instructions = mixin.getObj2().instructions;
						if (mixin.getObj1().equals(Mixin.Point.TOP)) {
							for (int i = instructions.size() - 1; i >= 0; i--) {
								node.instructions.insertBefore(instructions.get(i), node.instructions.get(0));
							}
							clazz.getObj2().visitEnd();
						} else if (mixin.getObj1().equals(Mixin.Point.BOTTOM)) {
							node.instructions.add(instructions);
							clazz.getObj2().visitEnd();
						}
					}
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
