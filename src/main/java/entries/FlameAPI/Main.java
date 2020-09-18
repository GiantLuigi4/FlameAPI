package entries.FlameAPI;

import com.tfc.API.flame.FlameAPI;
import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flame.utils.reflection.Fields;
import com.tfc.API.flame.utils.reflection.Methods;
import com.tfc.API.flamemc.FlameASM;
import com.tfc.API.flamemc.Registry;
import com.tfc.API.flamemc.blocks.BlockProperties;
import com.tfc.API.flamemc.event.init_steps.RegistryStep;
import com.tfc.API.flamemc.items.BlockItem;
import com.tfc.API.flamemc.items.Item;
import com.tfc.flame.FlameConfig;
import com.tfc.flame.IFlameAPIMod;
import com.tfc.flamemc.FlameLauncher;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.ClassObject;
import com.tfc.utils.Fabricator;
import com.tfc.utils.ScanningUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
	private static String blockItemClass = "";
	private static String itemStackClass = "";
	private static String resourceLocationClass = "";
	private static String blockFireClass = "";
	private static String blockPosClass = "";
	private static String blockStateClass = "";
	private static String worldClass = "";
	private static String IWorldClass = "";
	private static String worldServerClass = "";
	
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
	
	public static String getBlockItemClass() {
		return blockItemClass;
	}
	
	public static String getItemStackClass() {
		return itemStackClass;
	}
	
	public static String getResourceLocationClass() {
		return resourceLocationClass;
	}
	
	public static String getBlockFireClass() {
		return blockFireClass;
	}
	
	public static String getBlockStateClass() {
		return blockStateClass;
	}
	
	public static String getBlockPosClass() {
		return blockPosClass;
	}
	
	public static String getWorldClass() {
		return worldClass;
	}
	
	public static String getWorldServerClass() {
		return worldServerClass;
	}
	
	public static String getIWorldClass() {
		return IWorldClass;
	}
	
	public static HashMap<String, String> getResourceTypeClasses() {
		HashMap<String, String> resourceTypes = new HashMap<>();
		resourceTypes.put("Block", blockClass);
		resourceTypes.put("Item", itemClass);
		resourceTypes.put("BlockItem", itemClass);
		resourceTypes.put("ItemStack", itemStackClass);
		resourceTypes.put("ResourceLocation", resourceLocationClass);
		resourceTypes.put("BlockFire", blockFireClass);
		return resourceTypes;
	}
	
	public static HashMap<String, String> getRegistries() {
		return (HashMap<String, String>) registries.clone();
	}
	
	/**
	 * downloads a maven artifact from the internet
	 *
	 * @param repo    the repo to download from
	 * @param path    the path
	 * @param name    artifact name
	 * @param version version
	 */
	public static void addDep(String repo, String path, String name, String version) {
		String url = repo + path.replace(".", "/") + "/" + name + "/" + version + "/" + name + "-" + version + ".jar";
		String name1 = path.replace(".", File.separatorChar + "") + File.separatorChar + name + File.separatorChar + version + File.separatorChar + name + "-" + version + ".jar";
		try {
			Method m = FlameLauncher.dependencyManager.getClass().getMethod("addFromURL", String.class);
			m.invoke(FlameLauncher.dependencyManager, ("libraries/" + name1 + "," + url));
		} catch (Throwable err) {
			FlameLauncher.downloadDep(name1, url);
		}
	}
	
	@Override
	public void setupAPI(String[] args) {
		try {
			downloadBytecodeUtils("4e402da");
			addDep("https://repo1.maven.org/maven2/", "org.javassist", "javassist", "3.27.0-GA");
//			addDep("https://repo1.maven.org/maven2/", "org.slf4j", "slf4j-api", "1.7.30");
//			addDep("https://repo1.maven.org/maven2/", "org.slf4j", "slf4j-simple", "1.7.30");
//			addDep("https://repo1.maven.org/maven2/", "org.apache.logging.log4j", "log4j-slf4j-impl", "2.13.3");
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
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
			Class.forName("java.util.concurrent.atomic.AtomicBoolean");
			Class.forName("org.objectweb.asm.tree.ClassNode");
			Class.forName("org.objectweb.asm.tree.MethodNode");
			Class.forName("org.objectweb.asm.tree.FieldNode");
			Class.forName("org.objectweb.asm.tree.AnnotationNode");
			Class.forName("javassist.NotFoundException");
			Class.forName("javassist.CannotCompileException");
			Class.forName("javassist.ClassPool");
			Class.forName("javassist.ClassPath");
			Class.forName("javassist.CtClass");
			Class.forName("javassist.CtField");
			Class.forName("javassist.CtMethod");
			Class.forName("javassist.CtConstructor");
			Class.forName("javassist.CtNewConstructor");
			Class.forName("javassist.CtNewMethod");
			Class.forName("javassist.compiler.ast.ASTList");
			Class.forName("com.tfc.API.flamemc.blocks.Block");
			FlameASM.AccessType type = FlameASM.AccessType.PUBLIC;
			Object obj1 = ClassObject.class;
		} catch (Throwable err) {
			Logger.logErrFull(err);
//			Runtime.getRuntime().exit(-1);
//			throw new RuntimeException(err);
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
//		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.Block", BlockClass::getBlock);

//		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.ASM", ASM::applyASM);
		
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
			HashMap<String, String> genericClasses = (HashMap<String, String>) Class.forName("GenericClassFinder").getMethod("findRegistrableClasses", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			genericClasses = (HashMap<String, String>) Class.forName("GenericClassFinder").getMethod("findExtensionClasses", File.class, HashMap.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"), genericClasses);
			itemClass = genericClasses.get("Item");
			blockItemClass = genericClasses.get("BlockItem");
			blockClass = genericClasses.get("Block");
			itemStackClass = genericClasses.get("ItemStack");
			resourceLocationClass = genericClasses.get("ResourceLocation");
			blockPosClass = genericClasses.get("BlockPos");
			blockFireClass = genericClasses.get("BlockFire");
			blockStateClass = genericClasses.get("BlockState");
			worldClass = genericClasses.get("World");
			IWorldClass = genericClasses.get("IWorld");
			worldServerClass = genericClasses.get("WorldServer");
			mainRegistry = (String) Class.forName("RegistryClassFinder").getMethod("findMainRegistry", HashMap.class, File.class).invoke(null, registries, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			FlameConfig.field.append("Block:" + blockClass + "\n");
			FlameConfig.field.append("Item:" + itemClass + "\n");
			FlameConfig.field.append("Item Stack:" + itemStackClass + "\n");
			FlameConfig.field.append("Resource Location: " + resourceLocationClass + "\n");
			FlameConfig.field.append("World: " + worldClass + "\n");
			FlameConfig.field.append("IWorld: " + IWorldClass + "\n");
			FlameConfig.field.append("WorldServer: " + worldServerClass + "\n");
			FlameConfig.field.append("Block Fire: " + blockFireClass + "\n");
			FlameConfig.field.append("BlockPos:" + blockPosClass + "\n");
			FlameConfig.field.append("BlockState: " + blockStateClass + "\n");
			FlameConfig.field.append("Main Registry:" + mainRegistry + "\n");
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
		
		try {
			Fabricator.compileAndLoad("client_brand_retriever.java", (code) -> code);
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}

//		try {
//			FlameASM.transformFieldAccess(ScanningUtils.toClassName(mainRegistry), "a", FlameASM.AccessType.PUBLIC_STATIC);
//		} catch (Throwable err) {
//			Logger.logErrFull(err);
//		}
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
		
		BlockItem.init();
		Item.init();
		
		Logger.logLine(Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:stone")));
		Logger.logLine(Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:bedrock")));
		Logger.logLine(Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:ice")));
		
		try {
			BlockProperties properties = new BlockProperties(
					new Registry.ResourceLocation("flameapi:test"),
					Registry.get(Registry.RegistryType.BLOCK, new Registry.ResourceLocation("minecraft:ice"))
			);
			Logger.logLine(properties);
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		String removedMethod = "a()";
		String placedMethod = "a()";
		String argsRemoved = "new Object[]{null}";
		String argsPlaced = "new Object[]{null}";
		
		try {
			for (Method m : Methods.getAllMethods(Class.forName(ScanningUtils.toClassName(getBlockClass())))) {
				int numMatched = 0;
				int num = 0;
				StringBuilder paramsR = new StringBuilder();
				StringBuilder argsR = new StringBuilder();
				for (Class<?> param : m.getParameterTypes()) {
					if (param.getName().equals(ScanningUtils.toClassName(IWorldClass)) && num == 0) {
						paramsR.append(param.getName()).append(" var0");
						argsR.append("var0");
						if (numMatched != 2) {
							paramsR.append(", ");
							argsR.append(", ");
						}
						numMatched++;
					} else if (param.getName().equals(ScanningUtils.toClassName(blockPosClass))) {
						paramsR.append(param.getName()).append(" var1");
						argsR.append("var1");
						if (numMatched != 2) {
							paramsR.append(", ");
							argsR.append(", ");
						}
						numMatched++;
					} else if (param.getName().equals(ScanningUtils.toClassName(blockStateClass))) {
						paramsR.append(param.getName()).append(" var2");
						argsR.append("var2");
						if (numMatched != 2) {
							paramsR.append(", ");
							argsR.append(", ");
						}
						numMatched++;
					} else {
						numMatched--;
					}
					num++;
				}
				if (numMatched == num && num == 3) {
					removedMethod = m.getName() + "(" + paramsR + ")";
					argsRemoved = "new Object[]{" + argsR + "}";
				}
				
				StringBuilder paramsA = new StringBuilder();
				StringBuilder argsA = new StringBuilder();
				numMatched = 0;
				num = 0;
				for (Class<?> param : m.getParameterTypes()) {
					if (param.getName().equals(ScanningUtils.toClassName(worldClass)) && num == 0) {
						paramsA.append(param.getName()).append(" var0");
						argsA.append("var0");
						if (numMatched != 4) {
							paramsA.append(", ");
							argsA.append(", ");
						}
						numMatched++;
					} else if (param.getName().equals(ScanningUtils.toClassName(blockPosClass))) {
						paramsA.append(param.getName()).append(" var1");
						argsA.append("var1");
						if (numMatched != 4) {
							paramsA.append(", ");
							argsA.append(", ");
						}
						numMatched++;
					} else if (param.getName().equals(ScanningUtils.toClassName(blockStateClass))) {
						paramsA.append(param.getName()).append(" var2");
						argsA.append("var2");
						if (numMatched != 4) {
							paramsA.append(", ");
							argsA.append(", ");
						}
						numMatched++;
					} else if (param.getName().equals(ScanningUtils.toClassName(itemStackClass))) {
						paramsA.append(param.getName()).append(" var5");
						argsA.append("var5");
						if (numMatched != 4) {
							paramsA.append(", ");
							argsA.append(", ");
						}
						numMatched++;
					} else if (num == 3) {
						paramsA.append(param.getName()).append(" var4");
						argsA.append("var4");
						if (numMatched != 4) {
							paramsA.append(", ");
							argsA.append(", ");
						}
						numMatched++;
					} else {
						numMatched--;
					}
					num++;
				}
				if (numMatched == num && num == 5) {
					placedMethod = m.getName() + "(" + paramsA + ")";
					argsPlaced = "new Object[]{" + argsA + "}";
				}
			}
		} catch (Throwable ignored) {
		}
		
		try {
			final String finalRemovedMethod = removedMethod;
			final String finalArgsRemoved = argsRemoved;
			final String finalPlacedMethod = placedMethod;
			final String finalArgsPlaced = argsPlaced;
			Fabricator.compileAndLoad("block_class.java", (code) -> code
					.replace("%block_class%", ScanningUtils.toClassName(blockClass))
					.replace("%callInfoGen_onRemoved%", "com.tfc.API.flamemc.abstraction.CallInfo info = null")
					.replace("%properties_class%", blockPropertiesClass.getName())
					.replace("%argsRemoved%", finalArgsRemoved)
					.replace("%removedMethod%", finalRemovedMethod)
					.replace("%placedMethod%", finalPlacedMethod)
					.replace("%argsPlaced%", finalArgsPlaced)
			);
			Field f0 = Fields.forName(Class.forName("Block"), "argsRemoved");
			Field f1 = Fields.forName(Class.forName("Block"), "argsPlaced");
			assert f0 != null;
			assert f1 != null;
			f0.setAccessible(true);
			f1.setAccessible(true);
			f0.set(null, new java.lang.String[]{"world", "pos", "state"});
			f1.set(null, new java.lang.String[]{"world", "pos", "state", "placer", "itemStack"});
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		FlameAPI.instance.bus.post(RegistryStep.class, new RegistryStep());
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
			} catch (Throwable err) {
				Logger.logErrFull(err);
			}
		}
	}
	
	private void downloadBytecodeUtils(String version) {
		addDep(
				"https://jitpack.io/",
				"com.github.GiantLuigi4",
				"Bytecode-Utils",
				version
		);
//		FlameLauncher.downloadDep("bytecode-utils-" + version + ".jar", "https://jitpack.io/com/github/GiantLuigi4/Bytecode-Utils/" + version + "/Bytecode-Utils-" + version + ".jar");
	}
}
