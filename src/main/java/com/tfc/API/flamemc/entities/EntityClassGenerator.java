//package com.tfc.API.flamemc.entities;
//
//import com.tfc.API.flame.annotations.ASM.Unmodifiable;
//import com.tfc.API.flame.utils.logging.Logger;
//import com.tfc.API.flame.utils.reflection.Fields;
//import com.tfc.API.flame.utils.reflection.Methods;
//import com.tfc.utils.flamemc.Mapping;
//import com.tfc.bytecode.utils.Formatter;
//import com.tfc.utils.ScanningUtils;
//import entries.FlameAPI.Main;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.concurrent.atomic.AtomicReference;
//
///**
// * Reflection isn't the best for speed, and entities are sorta a thing of good optimization, or problems
// * Because of this, I'm not putting a wrapper for it, and instead I'm making you generate a class at runtime
// * Anyway, here's the utils for it
// */
//@Unmodifiable
//public class ClassGenerator {
//	private static final Class<?> registryClass;
//	private static final Method getName;
//	private static final Method createEntity;
//	private static final HashMap<String, Object> entityTypes = new HashMap<>();
//	private static final HashMap<String, Object> entityClasses = new HashMap<>();
//	private static final String entityClassTemplate;
//
//	static {
//		try {
//			String registry = Mapping.getUnmappedFor("net.minecraft.init.Entities");
//			registryClass = Class.forName(registry);
//			AtomicReference<Method> name = new AtomicReference<>(null);
//			AtomicReference<Method> create = new AtomicReference<>(null);
//
//			Methods.forEach(registryClass, (method) -> {
//				if (
//						method.toString().contains("public static") &&
//								method.getReturnType().getName().equals(ScanningUtils.toClassName(Main.getResourceLocationClass())) &&
//								method.getParameterTypes().length == 1 &&
//								method.getParameterTypes()[0].equals(registryClass)
//				) {
//					name.set(method);
//				} else if (
//						method.toString().contains("public") &&
//								!method.toString().contains("static") &&
//								method.getReturnType().getName().equals(ScanningUtils.toClassName(Main.getEntityClass())) &&
//								method.getParameterTypes().length == 1 &&
//								method.getParameterTypes()[0].getName().equals(ScanningUtils.toClassName(Main.getWorldClass()))
//				) {
//					create.set(method);
//				}
//			});
//
//			getName = name.get();
//			createEntity = create.get();
//
//			try {
//				Fields.forEach(registryClass, (entity) -> {
//					try {
//						entity.setAccessible(true);
//						Object e = entity.get(null);
//						Object eName = getName.invoke(null, e);
//						entityTypes.put(eName.toString(), e);
//					} catch (Throwable ignored) {
//					}
//				});
//			} catch (Throwable ignored) {
//			}
//
//			Logger.logLine(entityTypes.toString());
//			Logger.logLine(getName.toString());
//			Logger.logLine(createEntity.toString());
//			createClassCache();
//
//			InputStream entityClassTemplateStream = ClassGenerator.class.getClassLoader().getResourceAsStream("entity_class.java");
//			byte[] bytes = new byte[entityClassTemplateStream.available()];
//			entityClassTemplateStream.read(bytes);
//			entityClassTemplateStream.close();
//			entityClassTemplate = new String(bytes);
//		} catch (Throwable err) {
//			throw new RuntimeException(err);
//		}
//	}
//
//	private static void createClassCache() {
//		entityTypes.forEach((name, type) -> {
//			try {
//				entityClasses.put(name, createEntity.invoke(type, (Object) null).getClass().getName());
//			} catch (Throwable ignored) {
//			}
//		});
//
//		String json = entityClasses.toString();
//
//		json = json
//				.replace("{", "{\n\t\"")
//				.replace("}", "\"\n}")
//				.replace(",", "\",\n\t\"")
//				.replace("=", "\" : \"")
//				.replace("\t\" ", "\t\"")
//		;
//
//		try {
//			File f = new File((Main.getGameDir() == null ? Main.getExecDir() : Main.getGameDir()) + "\\caches\\entities\\" + Main.getVersion() + ".json");
//			f.getParentFile().mkdirs();
//			f.createNewFile();
//			FileOutputStream stream = new FileOutputStream(f);
//			stream.write(json.getBytes());
//			stream.close();
//		} catch (Throwable ignored) {
//		}
//	}
//
//	/**
//	 * Get the class for an entity
//	 *
//	 * @param entityName the name of the entity (for example, minecraft:phantom)
//	 * @return the class of the entity
//	 */
//	public static String getEntityClass(String entityName) {
//		try {
//			return createEntity.invoke(entityTypes.get(entityName), (Object) null).getClass().getName();
//		} catch (Throwable ignored) {
//		}
//		return null;
//	}
//
//	public static String generate(String name, String superName, String code) {
//		String trueSuper = superName;
//		if (superName.contains(":")) trueSuper = getEntityClass(superName);
//		String clazz = "" +
//				"pacakage " + name.substring(0, name.lastIndexOf(".")) + ";" +
//				"public class " + name.substring(name.lastIndexOf(".")).replace(".", "") + " extends " + trueSuper + " {" +
//				"" + code +
//				"}";
//		Logger.log(Formatter.formatForCompile(clazz));
//		return null;
//	}
//}


package com.tfc.API.flamemc.entities;

import com.tfc.bytecode.Compiler;
import com.tfc.bytecode.EnumCompiler;
import com.tfc.utils.flamemc.Mapping;
import entries.FlameAPI.Main;

//TODO
public class EntityClassGenerator {
	public static String generate(String name, String superName, String imports, String code) {
		if (superName == null) superName = Main.getEntityClass();
		String packageName = "";
		if (name.contains(".")) {
			packageName = "package " + name.substring(0, name.lastIndexOf(".")) + ";";
			name = name.substring(name.lastIndexOf(".") + 1);
		}
		return
				packageName +
						imports +
						"public class " + name + " extends " + superName + " {"
						+ code
						.replace("%tick%", Main.getEntity$tick().getName())
						.replace("%world_class%", Main.getWorldClass())
						.replace("%entity_type_class%", Mapping.getUnmappedFor("net.minecraft.init.Entities"))
						.replace("%getX%", Main.getEntity$getX().getName() + "()")
						.replace("%getY%", Main.getEntity$getY().getName() + "()")
						.replace("%getZ%", Main.getEntity$getZ().getName() + "()")
						.replace("%move%", Main.getEntity$move().getName())
						.replace("%writeNBT%", Main.getEntity$writeAdditionalSaveData().getName())
						.replace("%readNBT%", Main.getEntity$readAdditionalSaveData().getName())
						.replace("%nbt_class%", Main.getNBTObjectClass())
						.replace("%item_stack_class%", Main.getItemStackClass())
						.replace("%equipment_slot_class%", Main.getEquipmentSlotClass())
						.replace("%get_armor_items%", Main.getEntity$getArmorItems().getName())
						.replace("%equip_stack%", Main.getEntity$equipStack().getName())
						.replace("%get_equipped_stack%", Main.getEntity$getEquippedStack().getName())
						.replace("%arm_class%", Main.getArmClass())
						.replace("%get_main_arm%", Main.getGetMainArm())
						+
						"}";
	}
	
	public static byte[] compile(String clazz) {
//		return Compiler.compile(EnumCompiler.JANINO, clazz);
		return Compiler.compile(EnumCompiler.JAVASSIST, clazz);
	}
}