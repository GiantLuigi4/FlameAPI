package entries.FlameAPI;

import com.tfc.API.flamemc.Block;
import com.tfc.API.flamemc.FlameASM;
import com.tfc.flame.FlameConfig;
import com.tfc.flame.IFlameAPIMod;
import com.tfc.flamemc.FlameLauncher;
import com.tfc.hacky_class_stuff.ASM.ASM;
import com.tfc.utils.ScanningUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;

public class Main implements IFlameAPIMod {
	private static final HashMap<String, String> registryClassNames = new HashMap<>();
	private static final HashMap<String, Class<?>> registryClasses = new HashMap<>();
	private static String gameDir;
	private static String version;
	private static String assetVersion; //for snapshots
	private static final String execDir = System.getProperty("user.dir");
	
	private static String mainRegistry = "";
	private static String blockClass = "";
	private static String itemClass = "";
	private static String itemStackClass = "";
	private static String resourceLocationClass = "";
	
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
	
	
	public static HashMap<String, String> getResourceTypeClasses() {
		HashMap<String, String> resourceTypes = new HashMap<>();
		resourceTypes.put("Block", blockClass);
		resourceTypes.put("Item", itemClass);
		resourceTypes.put("ItemStack", itemStackClass);
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
			Class.forName("com.tfc.hacky_class_stuff.ASM.FieldAdder");
			Class.forName("com.tfc.hacky_class_stuff.ASM.Writer");
			Class.forName("com.tfc.hacky_class_stuff.ASM.API.FieldData");
			Class.forName("com.tfc.utils.Bytecode");
			Class.forName("com.tfc.utils.ScanningUtils");
			Class.forName("com.tfc.API.flamemc.EmptyClass");
			Class.forName("RegistryClassFinder");
			Class.forName("GenericClassFinder");
			Class.forName("com.tfc.API.flamemc.FlameASM");
			FlameASM.AccessType type = FlameASM.AccessType.PUBLIC;
		} catch (Throwable err) {
			FlameConfig.logError(err);
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
			FlameConfig.logError(err);
		}

//		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.Block", BlockClass::getBlock);
		
		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.ASM.addField", ASM::applyFields);
		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.ASM.atMethod", ASM::applyMethodTransformers);

//		try {
//			FlameLauncher.addClassReplacement("replacements.FlameAPI.net.minecraft.client.ClientBrandRetriever");
//			FlameLauncher.addClassReplacement("replacements.FlameAPI.net.client.ClientBrandRetriever");
//		} catch (Throwable err) {
//			FlameConfig.logError(err);
//		}
		
		try {
			new Block();
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		
		try {
			registries = (HashMap<String, String>) Class.forName("RegistryClassFinder").getMethod("findRegistryClass", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			FlameConfig.field.append("PreInit Registries:" + registries.size() + "\n");
			mainRegistry = (String) Class.forName("RegistryClassFinder").getMethod("findMainRegistry", HashMap.class, File.class).invoke(null, registries, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			FlameConfig.field.append("Main Registry Class:" + mainRegistry + "\n");
			HashMap<String, String> genericClasses = (HashMap<String, String>) Class.forName("GenericClassFinder").getMethod("findItemClasses", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			itemClass = genericClasses.get("Item");
			blockClass = genericClasses.get("Block");
			itemStackClass = genericClasses.get("ItemStack");
			resourceLocationClass = genericClasses.get("ResourceLocation");
			FlameConfig.field.append("Block Class:" + blockClass + "\n");
			FlameConfig.field.append("Item Class:" + itemClass + "\n");
			FlameConfig.field.append("Item Stack Class:" + itemStackClass + "\n");
			FlameConfig.field.append("Resource Location: " + resourceLocationClass + "\n");
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		
		try {
			FlameASM.addField("net.minecraft.client.ClientBrandRetriever", "brand", "flamemc", FlameASM.AccessType.PUBLIC_STATIC);
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
	}
	
	@Override
	public void preinit(String[] args) {
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
				FlameConfig.logError(err);
			}
		}
	}
}
