package entries.FlameAPI;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flamemc.FlameASM;
import com.tfc.API.flamemc.Registry;
import com.tfc.API.flamemc.blocks.BlockPropeteries;
import com.tfc.flame.FlameConfig;
import com.tfc.flame.IFlameAPIMod;
import com.tfc.flamemc.FlameLauncher;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.ASM;
import com.tfc.hacky_class_stuff.ASM.ClassObject;
import com.tfc.hacky_class_stuff.BlockClass;
import com.tfc.utils.ScanningUtils;
import mixins.FlameAPI.ClientBrandRetriever;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Main implements IFlameAPIMod {
	private static final HashMap<String, String> registryClassNames = new HashMap<>();
	
	public static final ArrayList<Constructor<?>> blockConstructors = new ArrayList<>();
	
	//private static final HashMap<String, Class<?>> registryClasses = new HashMap<>();
	private static String gameDir;
	private static String version;
	private static String assetVersion; //for snapshots
	private static final String execDir = System.getProperty("user.dir");
	
	private static String mainRegistry = "";
	private static String blockClass = "";
	private static String itemClass = "";
	private static String itemStackClass = "";
	private static String resourceLocationClass = "";
	
	public static String getMainRegistry() {
		return mainRegistry;
	}
	
	private static HashMap<String, String> registries = null;
	
	public static String getGameDir() {
		return gameDir;
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getExecDir() {
		return execDir;
	}
	
	public static String getAssetVersion() {
		return assetVersion;
	}
	
	public static String getBlockClass() {
		return blockClass;
	}
	
	public static String getItemClass() {
		return itemClass;
	}
	
	public static String getItemStackClass() {
		return itemStackClass;
	}
	
	public static String getResourceLocationClass() {
		return resourceLocationClass;
	}
	
	public static HashMap<String, String> getResourceTypeClasses() {
		HashMap<String, String> resourceTypes = new HashMap<>();
		resourceTypes.put("Block", blockClass);
		resourceTypes.put("Item", itemClass);
		resourceTypes.put("ItemStack", itemStackClass);
		resourceTypes.put("ResourceLocation", resourceLocationClass);
		return resourceTypes;
	}
	
	public static HashMap<String, String> getRegistries() {
		return (HashMap<String, String>) registries.clone();
	}
	
	@Override
	public void setupAPI(String[] args) {
		try {
			Class.forName("org.objectweb.asm.ClassVisitor");
			Class.forName("org.objectweb.asm.ClassReader");
			Class.forName("org.objectweb.asm.ClassWriter");
			Class.forName("com.tfc.hacky_class_stuff.ASM.ASM");
			Class.forName("com.tfc.hacky_class_stuff.ASM.transformers.fields.FieldAdder");
			Class.forName("com.tfc.hacky_class_stuff.ASM.API.FieldData");
			Class.forName("com.tfc.hacky_class_stuff.ASM.API.InstructionData");
			Class.forName("com.tfc.hacky_class_stuff.ASM.transformers.methods.MethodAdder");
			Class.forName("com.tfc.utils.Bytecode");
			Class.forName("com.tfc.utils.ScanningUtils");
			Class.forName("com.tfc.API.flamemc.EmptyClass");
			Class.forName("RegistryClassFinder");
			Class.forName("GenericClassFinder");
			Class.forName("com.tfc.API.flamemc.FlameASM");
			Class.forName("com.tfc.API.flame.annotations.ASM.Hookin");
			Class.forName("com.tfc.hacky_class_stuff.ASM.transformers.methods.MethodAccessTransformer");
			Class.forName("com.tfc.hacky_class_stuff.ASM.transformers.HookinHandler");
			Class.forName("com.tfc.utils.BiObject");
			Class.forName("com.tfc.utils.TriObject");
			Class.forName("com.tfc.FlameAPIConfigs");
			Class.forName("java.util.function.Consumer");
			Class.forName("org.objectweb.asm.tree.ClassNode");
			Class.forName("org.objectweb.asm.tree.MethodNode");
			Class.forName("org.objectweb.asm.tree.FieldNode");
			Class.forName("org.objectweb.asm.tree.AnnotationNode");
			FlameASM.AccessType type = FlameASM.AccessType.PUBLIC;
			Object obj1 = ClassObject.class;
		} catch (Throwable err) {
			Logger.logErrFull(err);
			throw new RuntimeException(err);
		}
		
		try {
			boolean isAssetIndex = false;
			boolean isVersion = false;
			boolean isDir = false;
			for (String s : args) {
				if (s.equals("--version")) {
					isVersion = true;
				} else if (isVersion) {
					version = s;
					isVersion = false;
				} else if (s.equals("--gameDir")) {
					isDir = true;
				} else if (isDir) {
					gameDir = s;
					isDir = false;
				} else if (s.equals("--assetIndex")) {
					isAssetIndex = true;
				} else if (isAssetIndex) {
					assetVersion = s;
					isAssetIndex = false;
				}
			}
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}

		ScanningUtils.checkVersion();
		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.Block", BlockClass::getBlock);
		
		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.ASM", ASM::applyASM);
		
		Access access = new Access(FlameASM.AccessType.PRIVATE, "hello");
		FlameConfig.field.append(access.type.name() + "\n");
		FlameConfig.field.append(access.type.level + "\n");
		
		access.increase(FlameASM.AccessType.PROTECTED);
		FlameConfig.field.append(access.type.name() + "\n");
		FlameConfig.field.append(access.type.level + "\n");
		
		access.increase(FlameASM.AccessType.PUBLIC);
		FlameConfig.field.append(access.type.name() + "\n");
		FlameConfig.field.append(access.type.level + "\n");
		
		access.increase(FlameASM.AccessType.PUBLIC_STATIC);
		FlameConfig.field.append(access.type.name() + "\n");
		FlameConfig.field.append(access.type.level + "\n");

//		try {
//			Class.forName("mixins.FlameAPI.ClientBrandRetriever");
//		} catch (Throwable ignored) {}

//		try {
//			FlameLauncher.addClassReplacement("replacements.FlameAPI.net.minecraft.client.ClientBrandRetriever");
//			FlameLauncher.addClassReplacement("replacements.FlameAPI.net.client.ClientBrandRetriever");
//		} catch (Throwable err) {
//			Logger.logErrFull();(err);
//		}
		
		try {
			registries = (HashMap<String, String>) Class.forName("RegistryClassFinder").getMethod("findRegistryClass", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			FlameConfig.field.append("PreInit Registries:" + registries.size() + "\n");
			if (ScanningUtils.isVersionGreaterThan12) {
				mainRegistry = (String) Class.forName("RegistryClassFinder").getMethod("findMainRegistry", HashMap.class, File.class).invoke(null, registries, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
				FlameConfig.field.append("Main Registry Class:" + mainRegistry + "\n");
			}
			HashMap<String, String> genericClasses = (HashMap<String, String>) Class.forName("GenericClassFinder").getMethod("findRegistrableClasses", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			itemClass = genericClasses.get("Item");
			blockClass = genericClasses.get("Block");
			itemStackClass = genericClasses.get("ItemStack");
			resourceLocationClass = genericClasses.get("ResourceLocation");
			FlameConfig.field.append("Block Class:" + blockClass + "\n");
			FlameConfig.field.append("Item Class:" + itemClass + "\n");
			FlameConfig.field.append("Item Stack Class:" + itemStackClass + "\n");
			FlameConfig.field.append("Resource Location: " + resourceLocationClass + "\n");
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		boolean success = false;
		ArrayList<Throwable> throwables = new ArrayList<>();
		try {
			FlameConfig.field.append(Registry.constructResourceLocation("FlameAPI:test").toString() + "\n");
			success = true;
		} catch (Throwable err) {
			throwables.add(err);
		}
		try {
			FlameConfig.field.append(Registry.constructResourceLocation("flame_api:test").toString() + "\n");
			success = true;
		} catch (Throwable err) {
			throwables.add(err);
		}
		try {
			FlameConfig.field.append(Registry.constructResourceLocation("flameapi:test").toString() + "\n");
			success = true;
		} catch (Throwable err) {
			throwables.add(err);
		}
		
		if (!success) {
			throwables.forEach(Logger::logErrFull);
			FlameConfig.field.append("Failed to construct a resource location.\n");
		}

//		try {
//			FlameASM.transformFieldAccess(ScanningUtils.toClassName(mainRegistry), "a", FlameASM.AccessType.PUBLIC_STATIC);
//		} catch (Throwable err) {
//			Logger.logErrFull(err);
//		}
		
		new ClientBrandRetriever();
	}
	
	@Override
	public void preinit(String[] args) {
	}
	
	//NYI on mod loader's side
	public static Main instance = null;
	private static Class<?> blockPropertiesClass = null;
	
	public static Class<?> getBlockPropertiesClass() {
		return blockPropertiesClass;
	}
	
	@Override
	public void init(String[] args) {
		try {
			for (Field f : Class.forName("net.minecraft.client.ClientBrandRetriever").getFields()) {
				try {
					FlameConfig.field.append("net.minecraft.client.ClientBrandRetriever%" + f.getName() + "=" + f.get(null) + "\n");
				} catch (Throwable ignored) {
				}
			}
		} catch (Throwable ignored) {
		}
		
		try {
			Logger.logLine("Block Class Constructors");
//			for (Constructor<?> c : Block.class.getConstructors()) {
//				String params = "";
//				for (Class<?> clazz : c.getParameterTypes()) {
//					params += clazz.getName() + ", ";
//				}
//				Logger.logLine(params.substring(0,params.length()-2));
//			}
			for (Constructor<?> c : Class.forName(ScanningUtils.toClassName(getBlockClass())).getConstructors()) {
				String params = "";
				int num = 0;
				for (Class<?> clazz : c.getParameterTypes()) {
					params += num + ": " + clazz.getName() + ", ";
					num++;
					if (clazz.getName().contains(ScanningUtils.toClassName(getBlockClass()))) {
						blockPropertiesClass = clazz;
					}
				}
				blockConstructors.add(c);
				Logger.logLine(params.substring(0, params.length() - 2));
			}
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		Logger.logLine(Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:stone")));
		Logger.logLine(Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:bedrock")));
		Logger.logLine(Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:ice")));
		
		try {
			BlockPropeteries properties = new BlockPropeteries(
					new Registry.ResourceLocation("flameapi:test"),
					Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:ice"))
			);
			Logger.logLine(properties);
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}

//		try {
//			Registry.registerBlock(new Registry.ResourceLocation("flame_api:test"), Registry.RegistryType.BLOCK);
//		} catch (Throwable err) {
//			Logger.logErrFull(err);
//		}

//		try {
//			for (Method m : Class.forName(mainRegistry.replace(".class", "")).getMethods()) {
//				try {
////					if (m.getName().equals("a")) {
//					FlameConfig.field.append("method name: " + m.getName() + "\n");
//					FlameConfig.field.append("main level: " + m.getModifiers() + "\n");
//					FlameConfig.field.append("protected static " + FlameASM.AccessType.PROTECTED_STATIC + "\n");
//					FlameConfig.field.append("public static " + FlameASM.AccessType.PUBLIC_STATIC + "\n");
////					}
//				} catch (Throwable ignored) {
//				}
//			}
//		} catch (Throwable ignored) {
//		}
	}
	
	@Override
	public void postinit(String[] args) {
		FlameConfig.field.append("PostInit Registries:" + registries.size() + "\n");
		Iterator<String> names = registries.keySet().iterator();
		Iterator<String> classes = registries.values().iterator();
		for (int i = 0; i < registries.size(); i++) {
			try {
				String name = names.next();
				String clazz = ScanningUtils.toClassName(classes.next());
				FlameConfig.field.append("Registry class: " + name + ": " + clazz + "\n");
				registryClassNames.put(name, clazz);
//				registryClasses.put(name,Class.forName(clazz));
			} catch (Throwable err) {
				Logger.logErrFull(err);
			}
		}
	}
}
