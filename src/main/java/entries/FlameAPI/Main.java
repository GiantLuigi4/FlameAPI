package entries.FlameAPI;

import com.github.lorenzopapi.asmutils.ConstructorUtils;
import com.tfc.API.flame.FlameAPI;
import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flame.utils.reflection.Fields;
import com.tfc.API.flame.utils.reflection.Methods;
import com.tfc.API.flamemc.FlameASM;
import com.tfc.API.flamemc.Registry;
import com.tfc.API.flamemc.blocks.BlockProperties;
import com.tfc.API.flamemc.data.NBTObject;
import com.tfc.API.flamemc.entities.EntityClassGenerator;
import com.tfc.API.flamemc.event.init_steps.PreFlameInit;
import com.tfc.API.flamemc.event.init_steps.RegistryStep;
import com.tfc.API.flamemc.items.BlockItem;
import com.tfc.API.flamemc.items.Item;
import com.tfc.API.flamemc.world.BlockPos;
import com.tfc.FlameAPIConfigs;
import com.tfc.bytecode.Compiler;
import com.tfc.bytecode.compilers.Javassist_Compiler;
import com.tfc.bytecode.loading.ForceLoad;
import com.tfc.bytecode.utils.Formatter;
import com.tfc.bytecode.utils.Parser;
import com.tfc.bytecode.utils.class_structure.ClassNode;
import com.tfc.flame.IFlameAPIMod;
import com.tfc.flamemc.FlameLauncher;
import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.Applier.Applicator;
import com.tfc.utils.BiObject;
import com.tfc.utils.ClassFindingUtils;
import com.tfc.utils.Fabricator;
import com.tfc.utils.ScanningUtils;
import com.tfc.utils.flamemc.Intermediary;
import com.tfc.utils.flamemc.Mojmap;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

public class Main implements IFlameAPIMod {
	private static final HashMap<String, String> registryClassNames = new HashMap<>();
	
	private static final String bytecodeUtilsVersion = "509c9e4";
	
	public static final ArrayList<Constructor<?>> blockConstructors = new ArrayList<>();
	
	//private static final HashMap<String, Class<?>> registryClasses = new HashMap<>();
	static File dataDirectory = null;
	private static String gameDir;
	private static String version;
	private static String assetVersion; //for snapshots
	private static final String execDir = System.getProperty("user.dir");
	
	private static String mainRegistry = "";
	private static String blockClass = "";
	private static String entityClass = "";
	private static String livingEntityClass = "";
	private static String NBTObjectClass = "";
	private static String entitySpawnPacketClass = "";
	private static String equipmentSlotClass = "";
	private static String packetClass = "";
	private static String itemClass = "";
	private static String blockItemClass = "";
	private static String itemStackClass = "";
	private static String resourceLocationClass = "";
	private static String blockFireClass = "";
	private static String blockPosClass = "";
	private static String armClass = "";
	private static String Vec3iClass = "";
	private static String blockStateClass = "";
	private static String worldClass = "";
	private static String IWorldClass = "";
	private static String worldServerClass = "";
	private static String bbClass = "";
	private static String tessellatorClass = "";
	
	private static Method block$onRemoved = null;
	private static Method block$onPlaced = null;
	private static Method block$onNeighborChanged = null;
	
	private static Method world$setBlockState = null;
	private static Method world$getBlockState = null;
	
	private static Method entity$tick = null;
	private static Method entity$getX = null;
	private static Method entity$getY = null;
	private static Method entity$getZ = null;
	private static Method entity$move = null;
	//I have never heard of this method until literally the day that I started making the entity base class
	private static Method entity$defineSynchedData = null;
	private static Method entity$getArmorItems = null;
	private static Method entity$equipStack = null;
	private static Method entity$getEquippedStack = null;
	private static Method entity$getMainArm = null;
	private static Method entity$readAdditionalSaveData = null;
	private static Method entity$writeAdditionalSaveData = null;
	private static Method entity$getAddEntityPacket = null;
	
	private static Method blocks$register = null;
	
	private static String getMainArm = "";

	private static BiObject<String, Method>[] nbtMethods = null;
	
	private static String versionMap = "";
	private static boolean isMappedVersion = false;
	
	
	public static String getEquipmentSlotClass() {
		return equipmentSlotClass;
	}
	
	public static Method getEntity$tick() {
		return entity$tick;
	}
	
	public static String getNBTObjectClass() {
		return NBTObjectClass;
	}
	
	public static String getGetMainArm() {
		return getMainArm;
	}
	
	public static Method getEntity$getEquippedStack() {
		return entity$getEquippedStack;
	}
	
	public static Method getEntity$getMainArm() {
		return entity$getMainArm;
	}
	
	public static Method getBlocks$register() {
		return blocks$register;
	}
	
	public static Method getEntity$getArmorItems() {
		return entity$getArmorItems;
	}
	
	public static String getArmClass() {
		return armClass;
	}
	
	public static String getEntitySpawnPacketClass() {
		return entitySpawnPacketClass;
	}
	
	public static Method getEntity$equipStack() {
		return entity$equipStack;
	}
	
	public static String getPacketClass() {
		return packetClass;
	}
	
	public static Method getEntity$defineSynchedData() {
		return entity$defineSynchedData;
	}
	
	public static Method getEntity$readAdditionalSaveData() {
		return entity$readAdditionalSaveData;
	}
	
	public static Method getEntity$writeAdditionalSaveData() {
		return entity$writeAdditionalSaveData;
	}
	
	public static Method getEntity$getAddEntityPacket() {
		return entity$getAddEntityPacket;
	}
	
	public static Method getEntity$getX() {
		return entity$getX;
	}
	
	public static Method getEntity$getY() {
		return entity$getY;
	}
	
	public static Method getEntity$getZ() {
		return entity$getZ;
	}
	
	public static Method getEntity$move() {
		return entity$move;
	}
	
	public static boolean isIsMappedVersion() {
		return isMappedVersion;
	}
	
	public static String getVec3iClass() {
		return Vec3iClass;
	}
	
	public static Method getBlock$onRemoved() {
		return block$onRemoved;
	}
	
	public static Method getBlock$onPlaced() {
		return block$onPlaced;
	}
	
	public static String getMainRegistry() {
		return mainRegistry;
	}
	
	private static HashMap<String, String> registries = new HashMap<>();
	
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
	
	public static String getVersionMap() {
		return versionMap;
	}
	
	public static String getIWorldClass() {
		return IWorldClass;
	}
	
	public static String getBBClass() {
		return bbClass;
	}
	
	public static String getTessellatorClass() {
		return tessellatorClass;
	}
	
	public static Method getWorld$setBlockState() {
		return world$setBlockState;
	}
	
	public static Method getWorld$getBlockState() {
		return world$getBlockState;
	}
	
	public static HashMap<String, String> getResourceTypeClasses() {
		HashMap<String, String> resourceTypes = new HashMap<>();
		resourceTypes.put("Block", blockClass);
		resourceTypes.put("Entity", entityClass);
		resourceTypes.put("Item", itemClass);
		resourceTypes.put("BlockItem", itemClass);
		resourceTypes.put("ItemStack", itemStackClass);
		resourceTypes.put("ResourceLocation", resourceLocationClass);
		resourceTypes.put("BlockFire", blockFireClass);
		return resourceTypes;
	}
	
	public static String getEntityClass() {
		return entityClass;
	}
	
	public static String getLivingEntityClass() {
		return livingEntityClass;
	}
	
	public static Method getBlock$onNeighborChanged() {
		return block$onNeighborChanged;
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
			m.invoke(FlameLauncher.dependencyManager, ("\\libraries\\" + name1 + "," + url));
		} catch (Throwable err) {
			FlameLauncher.downloadDep(name1, url);
		}
	}
	
	public static String getDataDir() {
		return dataDirectory.getAbsolutePath();
	}
	
	public static void quitIfNotDev() {
		//Force quit the game, as something must have gone fatally wrong,
		//weather it be moj-err-microsoft changing mojmap,
		//microsoft abandoning mojmap,
		//or one of the methods being changed or deleted
		if (!FlameAPIConfigs.devMode) {
			Runtime.getRuntime().exit(-1);
		}
	}
	
	/**
	 * note to self: com/tfc/API/flamemc/EmptyClass
	 */

	private static InsnList staticInitRegisterInjection(String registry, String methodThatRegisterThingsInMCClass, String objectToRegisterClass) {
		InsnList list = new InsnList();
		list.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/tfc/API/flamemc/EmptyClassMK2", "registryObjects", "Ljava/util/ArrayList;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/ArrayList", "iterator", "()Ljava/util/Iterator;"));
		list.add(new VarInsnNode(Opcodes.ASTORE, 0));
		LabelNode l2 = new LabelNode();
		list.add(new FrameNode(Opcodes.F_FULL, 1, new Object[]{Iterator.class}, 0, new Object[0]));
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z"));
		addIfConditionWithoutElse(list, Opcodes.IFEQ, () -> {
			list.add(new FrameNode(Opcodes.F_FULL, 0, new Object[0], 0, new Object[0]));
			list.add(new InsnNode(Opcodes.RETURN));
		});
		list.add(new VarInsnNode(Opcodes.ALOAD, 0));
		list.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;"));
		list.add(new TypeInsnNode(Opcodes.CHECKCAST, "com/tfc/API/flamemc/Registry$RegistryObject"));
		list.add(new VarInsnNode(Opcodes.ASTORE, 1));
		list.add(l2);
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/tfc/API/flamemc/Registry$RegistryObject", "getName", "()Lcom/tfc/API/flamemc/Registry$ResourceLocation;"));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toString", "()Ljava/lang/String;"));
		list.add(new VarInsnNode(Opcodes.ALOAD, 1));
		list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/tfc/API/flamemc/Registry$RegistryObject", "get", "()Ljava/lang/Object;"));
		list.add(new TypeInsnNode(Opcodes.CHECKCAST, objectToRegisterClass));
		list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, registry, methodThatRegisterThingsInMCClass, "(Ljava/lang/String;L" + registry + ";)L" + registry + ";"));
		list.add(new InsnNode(Opcodes.POP));
		list.add(new JumpInsnNode(Opcodes.GOTO, l2));
		return list;
	}

	private static void addIfConditionWithoutElse(InsnList list, int opcode, Runnable ifTrue) {
		LabelNode then = new LabelNode();
		list.add(new JumpInsnNode(opcode, then));
		ifTrue.run();
		list.add(then);
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
		FlameAPI.instance.bus.post(PreFlameInit.class, new PreFlameInit());
		try {
			for (Field f : Class.forName("net.minecraft.client.ClientBrandRetriever").getFields()) {
				try {
					Logger.log("net.minecraft.client.ClientBrandRetriever%" + f.getName() + "=" + f.get(null) + "\n");
				} catch (Throwable ignored) {
				}
			}
		} catch (Throwable ignored) {
		}
		
		try {
			Logger.logLine(new BlockPos(0, 0, 0));
			Logger.logLine(new BlockPos(3, 0, 1).offset(1, 2, 3));
			Logger.logLine(Class.forName("BlockPos"));
//			Logger.logLine(LinkieImplementation.unmap("net.minecraft.entity.mob.PhantomEntity","yarn","1.15.2"));
			Logger.logLine("Phantom for " + versionMap + ": " + Mojmap.getClassObsf(versionMap, "net/minecraft/world/entity/monster/Phantom").getSecondaryName());
			
			NBTObject nbt = new NBTObject();
			nbt.putString("hello", "hi");
			nbt.putBoolean("boolean", false);
			Logger.logLine(nbt.toString());
		} catch (Throwable err) {
			Logger.logErrFull(err);
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
		String argsPlaced = "new Object[]{null}";
		
		String neighborChanged = "voideee(int var0)/new Object[]{Integer.valueOf(var0)}";
		
		try {
			for (Method m : Methods.getAllMethods(Class.forName(ScanningUtils.toClassName(getBlockClass())))) {
				int numMatched = 0;
				int num = 0;
				
				StringBuilder paramsA = new StringBuilder();
				StringBuilder argsA = new StringBuilder();
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
					} else if (num == 3) {                      //TODO UNDERSTAND WHAT LUIGI MEANS HERE
						//LUIGI TELL ME
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
					block$onPlaced = m;
				}
			}
			
			BiObject<String, Method> onRemovedB = Methods.searchAndGetMethodInfosPrecise(getBlockClass(), 3, void.class, ClassFindingUtils.createBiObjectArray(
					getIWorldClass(), getBlockPosClass(), getBlockStateClass()
			));
			if (onRemovedB != null) {
				block$onRemoved = onRemovedB.getObject2();
				removedMethod = onRemovedB.getObject1();
			}
			
			BiObject<String, Method> neighborChangedB = Methods.searchAndGetMethodInfosPrecise(getBlockClass(), 6, null, ClassFindingUtils.createBiObjectArray(
					getBlockStateClass(), getWorldClass(),
					getBlockPosClass(), getBlockClass(),
					getBlockPosClass(), boolean.class.getName() + ".class"
			));
			if (neighborChangedB != null) {
				block$onNeighborChanged = neighborChangedB.getObject2();
				neighborChanged = neighborChangedB.getObject1();
			}
			
			Logger.logLine("Scanning world:");
			Logger.logLine("Method setBlockState:");
			world$setBlockState = Methods.searchMethod(getWorldClass(), 2, boolean.class, ClassFindingUtils.createBiObjectArray(
					getBlockPosClass(), getBlockStateClass()
			));
			Logger.logLine("Method getBlockState:");
			if (ScanningUtils.mcMajorVersion == 15)
				world$getBlockState = ScanningUtils.classFor(getWorldClass()).getMethod("d_", ScanningUtils.classFor(getBlockPosClass()));
			else
				world$getBlockState = Methods.searchMethod(getWorldClass(), 1, Class.forName(getBlockStateClass()), ClassFindingUtils.createBiObjectArray(
						getBlockPosClass()
				));
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		try {
			try {
				Logger.logLine(world$getBlockState.toString());
			} catch (Throwable ignored) {
			}
			
			try {
				Logger.logLine(world$setBlockState.toString());
			} catch (Throwable ignored) {
			}
			
			if (isMappedVersion) {
				//Gets the onUpdated method for block class
				BiObject<String, Method> method = Mojmap.getMethod(
						Class.forName(blockClass), Mojmap.getClassMojmap(blockClass),
						"neighborChanged",
						"(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V",
						Mojmap.toStringBiObjectArray(
								"Lnet/minecraft/world/level/block/state/BlockState;", blockStateClass + ",",
								"Lnet/minecraft/world/level/Level;", worldClass + ",",
								"Lnet/minecraft/core/BlockPos;", blockPosClass + ",",
								"Lnet/minecraft/world/level/block/Block;", blockClass + ",",
								"Z)V", "boolean,"
						)
				);
				block$onNeighborChanged = method.getObject2();
				neighborChanged = method.getObject1();
				Logger.logLine(block$onNeighborChanged.toString());
				Logger.logLine(neighborChanged);
			}
			
			final String finalRemovedMethod = removedMethod.split("/")[0];
			final String finalArgsRemoved = removedMethod.split("/")[1];
			final String finalPlacedMethod = placedMethod;
			final String finalArgsPlaced = argsPlaced;
			final String finalUpdatedMethod = neighborChanged.split("/")[0];
			final String finalArgsUpdate = neighborChanged.split("/")[1];
			Fabricator.compileAndLoad("block_class.java", (code) -> code
					.replace("%block_class%", ScanningUtils.toClassName(blockClass))
					.replace("%callInfoGen_onRemoved%", "com.tfc.API.flamemc.abstraction.CallInfo info = null")
					.replace("%properties_class%", blockPropertiesClass.getName())
					.replace("%argsRemoved%", finalArgsRemoved)
					.replace("%removedMethod%", finalRemovedMethod)
					.replace("%placedMethod%", finalPlacedMethod)
					.replace("%argsPlaced%", finalArgsPlaced)
					.replace("%updatedMethod%", finalUpdatedMethod)
					.replace("%argsUpdated%", finalArgsUpdate)
			);
			Field f0 = Fields.forName(Class.forName("Block"), "argsRemoved");
			Field f1 = Fields.forName(Class.forName("Block"), "argsPlaced");
			Field f2 = Fields.forName(Class.forName("Block"), "argsUpdated");
			assert f0 != null;
			assert f1 != null;
			assert f2 != null;
			f0.setAccessible(true);
			f1.setAccessible(true);
			f2.setAccessible(true);
			f0.set(null, new java.lang.String[]{"world", "pos", "state"});
			f1.set(null, new java.lang.String[]{"world", "pos", "state", "placer", "itemStack"});
			f2.set(null, new java.lang.String[]{"sate", "world", "pos", "updater_block", "updater_pos", "moved"});
			
			Fabricator.compileAndLoad("world_class.java", (code) -> code
					.replace("%get_block_state%", world$getBlockState.getName())
					.replace("%set_block_state%", world$setBlockState.getName())
					.replace("%block_state_class%", ScanningUtils.toClassName(blockStateClass))
					.replace("%world_class%", ScanningUtils.toClassName(worldClass))
					.replace("%block_pos_class%", ScanningUtils.toClassName(blockPosClass))
			);

//			Logger.logLine("Phantom Class");
//			Logger.logLine(ClassGenerator.getEntityClass("minecraft:phantom"));
//			Logger.logLine("Zombie Class");
//			Logger.logLine(ClassGenerator.getEntityClass("minecraft:zombie"));
//
//			ClassGenerator.generate("com.tfc.TestEntity", "minecraft:sheep", "" +
//					"public void tick(CallInfo args) {" +
//					"	super.tick();" +
//					"}" +
//					"");
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		if (isMappedVersion) {
			try {
				blocks$register =
						Mojmap.getMethod(
								Class.forName(Mojmap.getClassMojmap("net/minecraft/class_2246").getSecondaryName()),
								Mojmap.getClassMojmap("net/minecraft/class_2246"),
								"register", "(Ljava/lang/String;Lnet/minecraft/world/level/block/Block;)Lnet/minecraft/world/level/block/Block;",
								Mojmap.toStringBiObjectArray(
										"Ljava/lang/String;", "String",
										"net/minecraft/world/level/block/Block", blockClass
								)
						).getObject2();
			} catch (Throwable err) {
				try {
					blocks$register = Class.forName(Mojmap.getClassMojmap("net/minecraft/class_2246").getSecondaryName()).getDeclaredMethod(
							"a", String.class, Class.forName(blockClass)
					);
				} catch (Throwable err2) {
					try {
						try {
							for (Method m : Class.forName(Mojmap.getClassMojmap("net/minecraft/class_2246").getSecondaryName()).getDeclaredMethods())
								if (m.getName().equals("a")) blocks$register = m;
						} catch (Throwable ignored) {
						}
						if (blocks$register == null) {
							blocks$register = Class.forName("bpi").getDeclaredMethod("a", String.class, Class.forName(blockClass));
							if (blocks$register == null) {
								throw new RuntimeException("a");
							}
						}
					} catch (Throwable err3) {
						Logger.logErrFull(err);
						Logger.logErrFull(err2);
						Logger.logErrFull(err3);
					}
				}
			}
		}
		
		FlameAPI.instance.bus.post(RegistryStep.class, new RegistryStep());
	}
	
	@Override
	public void postinit(String[] args) {
		Logger.log("PostInit Registries:" + registries.size() + "\n");
		Iterator<String> names = registries.keySet().iterator();
		Iterator<String> classes = registries.values().iterator();
		for (int i = 0; i < registries.size(); i++) {
			try {
				String name = names.next();
				String clazz = ScanningUtils.toClassName(classes.next());
				Logger.log("Registry class: " + name + ": " + clazz + "\n");
				registryClassNames.put(name, clazz);
			} catch (Throwable err) {
				Logger.logErrFull(err);
			}
		}
	}
	
	private void downloadBytecodeUtils() {
		addDep(
				"https://jitpack.io/",
				"com.github.GiantLuigi4",
				"Bytecode-Utils",
				bytecodeUtilsVersion
		);
	}
	
	@Override
	public void setupAPI(String[] args) {
		try {
			if (!FlameLauncher.isDev)
				dataDirectory = new File((Main.getGameDir() == null ? Main.getExecDir() : Main.getGameDir()));
			else
				dataDirectory = new File(Main.getExecDir() + "\\run");
			//Bytecode-Utils
			downloadBytecodeUtils();
			//Compilers
			addDep("https://repo1.maven.org/maven2/", "org.javassist", "javassist", "3.27.0-GA");
			addDep("https://repo1.maven.org/maven2/", "org.codehaus.janino", "janino", "3.1.2");
			addDep("https://repo1.maven.org/maven2/", "org.codehaus.janino", "commons-compiler", "3.1.2");
			addDep("https://repo1.maven.org/maven2/", "org.codehaus.janino", "commons-compiler-jdk", "3.1.2");
			//Mappings Helper
			addDep("https://jitpack.io/", "com.github.GiantLuigi4", "MCMappingsHelper", "f2ec2b7");
//			//Kotlin
//			addDep("https://repo1.maven.org/maven2/", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8", "1.4.0");
//			addDep("https://repo1.maven.org/maven2/", "org.jetbrains.kotlin", "kotlin-stdlib", "1.4.0");
//			addDep("https://repo1.maven.org/maven2/", "org.jetbrains.kotlin", "kotlin-reflect", "1.4.0");
//			addDep("https://repo1.maven.org/maven2/", "org.jetbrains.kotlin", "kotlin-stdlib-common", "1.4.0");
//			addDep("https://repo1.maven.org/maven2/", "org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.9");
//			addDep("https://repo1.maven.org/maven2/", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-common", "1.3.8");
//			addDep("https://repo1.maven.org/maven2/", "org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.0.0-RC");
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		try {
			Class.forName("org.objectweb.asm.ClassVisitor");
			Class.forName("org.objectweb.asm.ClassReader");
			Class.forName("org.objectweb.asm.ClassWriter");
			Class.forName("com.tfc.hacky_class_stuff.ASM.Applier.Applicator");
			Class.forName("com.tfc.API.flame.utils.logging.Logger");
			Class.forName("com.tfc.utils.Files");
			Class.forName("java.io.FileOutputStream");
			Class.forName("java.lang.RuntimeException");
			Class.forName("java.lang.Throwable");
			Class.forName("java.lang.StackTraceElement");
			Class.forName("com.tfc.hacky_class_stuff.ASM.API.Field");
			Class.forName("com.tfc.hacky_class_stuff.ASM.API.Access");
			Class.forName("com.tfc.bytecode.asm.ASM.ASM");
			Class.forName("com.tfc.bytecode.asm.ASM.FieldVisitor");
			Class.forName("com.tfc.utils.Bytecode");
			Class.forName("com.tfc.utils.ScanningUtils");
			Class.forName("RegistryClassFinder");
			Class.forName("GenericClassFinder");
			Class.forName("com.tfc.API.flamemc.FlameASM");
			Class.forName("com.tfc.API.flame.annotations.ASM.Hookin");
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
					versionMap = version.replace("-flame", "");
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
					isMappedVersion = Integer.parseInt(assetVersion.replace("1.", "")) >= 14;
					isAssetIndex = false;
				}
			}
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		if (versionMap.startsWith("1.14"))
			isMappedVersion = isMappedVersion && versionMap.replace("1.14", "").equals(".4");
//		FlameASM.addInstructions(registries.get(Mapping.getUnmappedFor(Mapping.getMappedClassForRegistry("minecraft:blocks"))),"static","<clinit>","()V",,false);
		ScanningUtils.checkVersion();
		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.ASM", Applicator::apply);
		
		Access access = new Access(FlameASM.AccessType.PRIVATE, "hello");
		Logger.log(access.type.name() + "\n");
		Logger.log(access.type.level + "\n");
		
		access.increase(FlameASM.AccessType.PROTECTED);
		Logger.log(access.type.name() + "\n");
		Logger.log(access.type.level + "\n");
		
		access.increase(FlameASM.AccessType.PUBLIC);
		Logger.log(access.type.name() + "\n");
		Logger.log(access.type.level + "\n");
		
		access.increase(FlameASM.AccessType.PUBLIC_STATIC);
		Logger.log(access.type.name() + "\n");
		Logger.log(access.type.level + "\n");

		try {
			Logger.log("PreInit Registries:" + registries.size() + "\n");
			if (isMappedVersion) {
				registries.put("minecraft:blocks", Mojmap.getClassObsf("net/minecraft/world/level/block/Blocks").getSecondaryName());
				registries.put("minecraft:items", Mojmap.getClassObsf("net/minecraft/world/item/Items").getSecondaryName());
				registries.put("minecraft:entities", Mojmap.getClassObsf("net/minecraft/world/entity/EntityType").getSecondaryName());
				registries.put("minecraft:tile_entities", Mojmap.getClassObsf("net/minecraft/world/level/block/entity/BlockEntityType").getSecondaryName());
				registries.put("minecraft:enchantments", Mojmap.getClassObsf("net/minecraft/world/item/enchantment/Enchantments").getSecondaryName());
				registries.put("minecraft:biome", Mojmap.getClassObsf("net/minecraft/world/level/biome/Biomes").getSecondaryName());
				
				mainRegistry = Mojmap.getClassObsf("net/minecraft/core/Registry").getSecondaryName();
				itemClass = Mojmap.getClassObsf("net/minecraft/world/item/Item").getSecondaryName();
				/*
					ConstructorUtils itemClassChanger = new ConstructorUtils(getClassBytes(itemClass));
					itemClassChanger.addInstructionsToStartOrEnd(staticInitRegisterInjection(itemClass, "", ""), "()V", true, true);
				*/
				blockItemClass = Mojmap.getClassObsf("net/minecraft/world/item/BlockItem").getSecondaryName();
				blockClass = Mojmap.getClassObsf("net/minecraft/world/level/block/Block").getSecondaryName();
				worldClass = Mojmap.getClassObsf("net/minecraft/world/level/Level").getSecondaryName();
				IWorldClass = Mojmap.getClassObsf("net/minecraft/world/level/LevelAccessor").getSecondaryName();
				worldServerClass = Mojmap.getClassObsf("net/minecraft/server/level/ServerLevel").getSecondaryName();
				IWorldClass = Mojmap.getClassObsf("net/minecraft/world/level/LevelAccessor").getSecondaryName();
				Vec3iClass = Mojmap.getClassObsf("net/minecraft/core/Vec3i").getSecondaryName();
				blockPosClass = Mojmap.getClassObsf("net/minecraft/core/BlockPos").getSecondaryName();
				try {
					armClass = Mojmap.getClassObsf("net/minecraft/util/Arm").getSecondaryName();
				} catch (Throwable ignored) {
					armClass = Intermediary.getClassObsf("net/minecraft/class_1306").getSecondaryName();
				}
				bbClass = Mojmap.getClassObsf("com/mojang/blaze3d/vertex/BufferBuilder").getSecondaryName();
				tessellatorClass = Mojmap.getClassObsf("com/mojang/blaze3d/vertex/Tesselator").getSecondaryName();
				entityClass = Mojmap.getClassObsf("net/minecraft/world/entity/Entity").getSecondaryName();
				livingEntityClass = Mojmap.getClassObsf("net/minecraft/world/entity/LivingEntity").getSecondaryName();
				NBTObjectClass = Mojmap.getClassObsf("net/minecraft/nbt/CompoundTag").getSecondaryName();
				entitySpawnPacketClass = Mojmap.getClassObsf("net/minecraft/network/protocol/game/ClientboundAddMobPacket").getSecondaryName();
				equipmentSlotClass = Mojmap.getClassObsf("net/minecraft/world/entity/EquipmentSlot").getSecondaryName();
				packetClass = Mojmap.getClassObsf("net/minecraft/network/protocol/Packet").getSecondaryName();
				itemStackClass = Mojmap.getClassObsf("net/minecraft/world/item/ItemStack").getSecondaryName();
				resourceLocationClass = Mojmap.getClassObsf("net/minecraft/resources/ResourceLocation").getSecondaryName();
				blockStateClass = Mojmap.getClassObsf("net/minecraft/world/level/block/state/BlockState").getSecondaryName();
				blockFireClass = Mojmap.getClassObsf("net/minecraft/world/level/block/FireBlock").getSecondaryName();
				
				try {
					String[] nbtArr = new String[]{
							"Int", "Float", "Byte", "Long", "Short", "Double", "String", "Boolean", "UUID", "Id"
					};
					String[] nbtArr2 = new String[]{
							"Int", "Float", "Byte", "Long", "Short", "Double", "String", "Boolean", "UUID"
					};
					BiObject<String, Method>[] nbtGets = ClassFindingUtils.getMethodsForClass(NBTObjectClass, ClassFindingUtils.createTriObjArr(
							ClassFindingUtils.createNBTSearchArray("get", false, nbtArr),
							ClassFindingUtils.createNBTSearchArray("get", true, nbtArr),
							ClassFindingUtils.createArrayOfEmptyArrays(nbtArr.length)
					));
					BiObject<String, Method>[] nbtPuts = ClassFindingUtils.getMethodsForClass(NBTObjectClass, ClassFindingUtils.createTriObjArr(
							ClassFindingUtils.createNBTSearchArray("put", false, nbtArr2),
							ClassFindingUtils.createNBTSearchArray("put", true, nbtArr2),
							ClassFindingUtils.createArrayOfEmptyArrays(nbtArr2.length)
					));
					
					nbtMethods = ClassFindingUtils.mergeBiObjectArrays(nbtGets, nbtPuts);
					//Gets the tick method for entity class
					BiObject<String, Method> method = Mojmap.getMethod(
							Class.forName(entityClass), Mojmap.getClassMojmap(entityClass),
							"tick",
							"()V",
							new ArrayList<>()
					);
					entity$tick = method.getObject2();
					//Gets the getX method for entity class
					method = Mojmap.getMethod(
							Class.forName(entityClass), Mojmap.getClassMojmap(entityClass),
							"getX",
							"()D",
							new ArrayList<>()
					);
					entity$getX = method.getObject2();
					//Gets the getY method for entity class
					method = Mojmap.getMethod(
							Class.forName(entityClass), Mojmap.getClassMojmap(entityClass),
							"getY",
							"()D",
							new ArrayList<>()
					);
					entity$getY = method.getObject2();
					//Gets the getZ method for entity class
					method = Mojmap.getMethod(
							Class.forName(entityClass), Mojmap.getClassMojmap(entityClass),
							"getZ",
							"()D",
							new ArrayList<>()
					);
					entity$getZ = method.getObject2();
					//Gets the setPos method for entity class
					method = Mojmap.getMethod(
							Class.forName(entityClass), Mojmap.getClassMojmap(entityClass),
							"setPos",
							"(DDD)V",
							Mojmap.toStringBiObjectArray(
									"D", "double,"
							)
					);
					entity$move = method.getObject2();
					try {
						//Gets the defineSynchedData method for entity class
						com.tfc.mappings.structure.Class classInter = Intermediary.getClassInter(entityClass);
						com.tfc.mappings.structure.Class classInterL = Intermediary.getClassInter(livingEntityClass);
						String defineSynchedData = "";
						String readAdditionalSaveData = "";
						String writeAdditionalSaveData = "";
						String getAddEntityPacket = "";
						String getArmorItems = "";
						String equipStack = "";
						String getEquippedStack = "";
//						String getMainArm = "";
						ArrayList<com.tfc.mappings.structure.Method> allMethods = new ArrayList<>();
						allMethods.addAll(classInter.getMethods());
						allMethods.addAll(classInterL.getMethods());
						for (com.tfc.mappings.structure.Method methodTest : allMethods) {
							switch (methodTest.getPrimary()) {
								case "method_5693":
									defineSynchedData = methodTest.getSecondary();
									break;
								case "method_5749":
									readAdditionalSaveData = methodTest.getSecondary();
									break;
								case "method_5652":
									writeAdditionalSaveData = methodTest.getSecondary();
									break;
								case "method_18002":
									getAddEntityPacket = methodTest.getSecondary();
									break;
								case "method_5661":
									getArmorItems = methodTest.getSecondary();
									break;
								case "method_5673":
									equipStack = methodTest.getSecondary();
									break;
								case "method_6118":
									getEquippedStack = methodTest.getSecondary();
									break;
								case "method_6068":
									getMainArm = methodTest.getSecondary();
									break;
							}
						}
						Logger.logLine(classInter.toString());
						for (Method m : Methods.getAllMethods(Class.forName(entityClass))) {
							if (
									m.getParameterTypes().length == 0 &&
											defineSynchedData.equals(m.getName())
							) {
								entity$defineSynchedData = m;
							} else if (
									m.getParameterTypes().length == 1 &&
											m.getParameterTypes()[0].getName().equals(NBTObjectClass) &&
											readAdditionalSaveData.equals(m.getName())
							) {
								entity$readAdditionalSaveData = m;
							} else if (
									m.getParameterTypes().length == 1 &&
											m.getParameterTypes()[0].getName().equals(NBTObjectClass) &&
											writeAdditionalSaveData.equals(m.getName())
							) {
								entity$writeAdditionalSaveData = m;
							} else if (
									m.getParameterTypes().length == 0 &&
											getAddEntityPacket.equals(m.getName())
							) {
								entity$getAddEntityPacket = m;
							} else if (
									m.getParameterTypes().length == 0 &&
											getArmorItems.equals(m.getName())
							) {
								entity$getArmorItems = m;
							} else if (
									m.getParameterTypes().length == 2 &&
											equipStack.equals(m.getName())
							) {
								entity$equipStack = m;
							} else if (
									m.getParameterTypes().length == 1 &&
											getEquippedStack.equals(m.getName())
							) {
								entity$getEquippedStack = m;
							} else if (
									m.getReturnType().getName().equals(armClass)
//											getMainArm.equals(m.getName())
							) {
								entity$getMainArm = m;
							}
						}
					} catch (Throwable ignored) {
					}
					
					Logger.logLine(entity$tick.toString());
					Logger.logLine(entity$getX.toString());
					Logger.logLine(entity$getY.toString());
					Logger.logLine(entity$getZ.toString());
					Logger.logLine(entity$move.toString());
					Logger.logLine(entity$defineSynchedData.toString());
					Logger.logLine(entity$readAdditionalSaveData.toString());
					Logger.logLine(entity$writeAdditionalSaveData.toString());
					Logger.logLine(entity$getAddEntityPacket.toString());
					
					try {
						InputStream sourceStream = Main.class.getClassLoader().getResourceAsStream("entity_base_class.java");
						byte[] sourceBytes = new byte[sourceStream.available()];
						sourceStream.read(sourceBytes);
						sourceStream.close();
						String source = new String(sourceBytes).replace("\t", "").replace("\n", "").replace("//TODO", "");
						String entityBaseClass = EntityClassGenerator.generate(
								"com.tfc.API.flamemc.entities.Entity",
								Mojmap.getClassObsf("net/minecraft/world/entity/LivingEntity").getSecondaryName(),
								"import java.util.ArrayList;import java.lang.Iterable;", source
						);
						File f = new File(dataDirectory + "\\FlameASM\\fabrication\\EntityBase.class");
						File f1 = new File(dataDirectory + "\\FlameASM\\fabrication\\EntityBase_source.java");
						if (!f.exists()) {
							f.getParentFile().mkdirs();
							f.createNewFile();
						}
						if (!f1.exists()) {
							f1.createNewFile();
						}
						Logger.logLine(entityBaseClass);
						FileOutputStream stream = new FileOutputStream(f);
						FileOutputStream stream1 = new FileOutputStream(f1);
						stream1.write(Formatter.formatForCompile(entityBaseClass).getBytes());
						stream1.close();
						byte[] entityClassBytes = EntityClassGenerator.compile(entityBaseClass);
						stream.write(entityClassBytes);
						stream.close();
						ForceLoad.forceLoad(Main.class.getClassLoader(), entityClassBytes);
					} catch (ReflectiveOperationException ignored) {
					} catch (Throwable err) {
						Logger.logErrFull(err);
						quitIfNotDev();
					}
					try {
						InputStream sourceStream = Main.class.getClassLoader().getResourceAsStream("nbt_class.java");
						byte[] sourceBytes = new byte[sourceStream.available()];
						sourceStream.read(sourceBytes);
						sourceStream.close();

//						AtomicReference<String> source = new AtomicReference<>(new String(sourceBytes).replace("\t", "").replace("\n", "").replace("//TODO", ""));
						AtomicReference<String> source = new AtomicReference<>(new String("import java.util.UUID;public class NBTObject {%nbtClass% thisNBT;public NBTObject(%nbtClass% thisNBT) {this.thisNBT = thisNBT;}public NBTObject() {this.thisNBT = new %nbtClass%();}public int getInt(String tag) {return thisNBT.%getInt%(tag);}public float getFloat(String tag) {return thisNBT.%getFloat%(tag);}public byte getByte(String tag) {return thisNBT.%getByte%(tag);}public long getLong(String tag) {return thisNBT.%getLong%(tag);}public short getShort(String tag) {return thisNBT.%getShort%(tag);}public double getDouble(String tag) {return thisNBT.%getDouble%(tag);}public String getString(String tag) {return thisNBT.%getString%(tag);}public boolean getBoolean(String tag) {return thisNBT.%getBoolean%(tag);}public java.util.UUID getUUID(String tag) {return thisNBT.%getUUID%(tag);}public byte getId() {return thisNBT.%getId%();}public void putInt(String tag, int value) {thisNBT.%putInt%(tag, value);}public void putFloat(String tag, float value) {thisNBT.%putFloat%(tag, value);}public void putByte(String tag, byte value) {thisNBT.%putByte%(tag, value);}public void putLong(String tag, long value) {thisNBT.%putLong%(tag, value);}public void putShort(String tag, short value) {thisNBT.%putShort%(tag, value);}public void putDouble(String tag, double value) {thisNBT.%putDouble%(tag, value);}public void putString(String tag, String value) {thisNBT.%putString%(tag, value);}public void putBoolean(String tag, boolean value) {thisNBT.%putBoolean%(tag, value);}public void putUUID(String tag, java.util.UUID value) {thisNBT.%putUUID%(tag, value);}public Object unwrap() {return this.thisNBT;}public String toString() {return this.thisNBT.toString();}}").replace("\t", "").replace("\n", "").replace("//TODO", ""));
						source.set(source.get().replace("%nbtClass%", NBTObjectClass));
						
						com.tfc.mappings.structure.Method[] methodstoFindArr = new com.tfc.mappings.structure.Method[nbtMethods.length];
						int counter = 0;
						for (com.tfc.mappings.structure.Method nbtMet : Mojmap.getClassMojmap(NBTObjectClass).getMethods()) {
							String match = "%" + nbtMet.getPrimary() + "%";
							if (source.get().contains(match)) {
								methodstoFindArr[counter] = nbtMet;
								counter++;
							}
						}
						
						for (BiObject<String, Method> biObject : nbtMethods) {
							for (com.tfc.mappings.structure.Method value : methodstoFindArr) {
								if (value.getSecondary().equals(biObject.getObject2().getName())) {
									source.set(source.get().replace("%" + value.getPrimary() + "%", biObject.getObject2().getName()));
								}
							}
						}
						
						File f = new File(dataDirectory + "\\FlameASM\\fabrication\\NBTObject.class");
						File f1 = new File(dataDirectory + "\\FlameASM\\fabrication\\NBTObject_source.java");
						if (!f.exists()) {
							f.getParentFile().mkdirs();
							f.createNewFile();
						}
						if (!f1.exists()) {
							f1.createNewFile();
						}
						
						FileOutputStream stream = new FileOutputStream(f);
						FileOutputStream stream1 = new FileOutputStream(f1);
						
						stream1.write(Formatter.formatForCompile(source.get()).getBytes());
						stream1.close();
						
						Field compiler = Compiler.class.getDeclaredField("javassist");
						compiler.setAccessible(true);
						Javassist_Compiler compiler1 = (Javassist_Compiler) compiler.get(null);
						ClassNode node = Parser.parse(Formatter.formatForCompile(source.get()));
						node.name = "com.tfc.API.flamemc.data.NBTObject";
						byte[] compiledBytes = compiler1.compile(node);
						ForceLoad.forceLoad(Main.class.getClassLoader(), compiledBytes);
//						byte[] compiledBytes = Compiler.compile(EnumCompiler.JAVASSIST, source.get());
						stream.write(compiledBytes);
						stream.close();
					} catch (Throwable err) {
						Logger.logErrFull(err);
						quitIfNotDev();
					}
					try {
						String testClass = EntityClassGenerator.generate(
								"com.tfc.test.test", Mojmap.getClassObsf("net/minecraft/world/entity/monster/Phantom").getSecondaryName(),
								"", "public test(%entity_type_class% type, %world_class% world) {super(type,world);} public void %tick%() {	double x = %getX%;	double y = %getY%;	double z = %getZ%;	super.%tick%();	%move%(x,y,z);}"
						);
						File f = new File(dataDirectory + "\\FlameASM\\fabrication\\testEntity.class");
						if (!f.exists()) {
							f.getParentFile().mkdirs();
							f.createNewFile();
						}
						FileOutputStream stream = new FileOutputStream(f);
						byte[] testClassBytes = EntityClassGenerator.compile(testClass);
						stream.write(testClassBytes);
						stream.close();
					} catch (Throwable err) {
						Logger.logErrFull(err);
					}
				} catch (Throwable err) {
					Logger.logErrFull(err);
					quitIfNotDev();
				}
			} else {
				registries = (HashMap<String, String>) Class.forName("RegistryClassFinder").getMethod("findRegistryClass", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
				
				HashMap<String, String> genericClasses = (HashMap<String, String>) Class.forName("GenericClassFinder").getMethod("findRegistrableClasses", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
				genericClasses = (HashMap<String, String>) Class.forName("GenericClassFinder").getMethod("findExtensionClasses", File.class, HashMap.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"), genericClasses);
				itemClass = genericClasses.get("Item");
				blockItemClass = genericClasses.get("BlockItem");
				blockClass = genericClasses.get("Block");
				entityClass = genericClasses.get("Entity");
				itemStackClass = genericClasses.get("ItemStack");
				resourceLocationClass = genericClasses.get("ResourceLocation");
				blockPosClass = genericClasses.get("BlockPos");
				Vec3iClass = genericClasses.get("Vec3i");
				blockFireClass = genericClasses.get("BlockFire");
				blockStateClass = genericClasses.get("BlockState");
				worldClass = genericClasses.get("World");
				IWorldClass = genericClasses.get("IWorld");
				worldServerClass = genericClasses.get("WorldServer");
				bbClass = genericClasses.get("BufferBuilder");
				tessellatorClass = genericClasses.get("Tessellator");
				
				mainRegistry = (String) Class.forName("RegistryClassFinder").getMethod("findMainRegistry", HashMap.class, File.class).invoke(null, registries, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			}
			Logger.log("Block: " + blockClass + "\n");
			Logger.log("Item: " + itemClass + "\n");
			Logger.log("ItemStack: " + itemStackClass + "\n");
			Logger.log("ResourceLocation: " + resourceLocationClass + "\n");
			Logger.log("World: " + worldClass + "\n");
			Logger.log("IWorld: " + IWorldClass + "\n");
			Logger.log("WorldServer: " + worldServerClass + "\n");
			Logger.log("BufferBuilder: " + bbClass + "\n");
			Logger.log("Tessellator: " + tessellatorClass + "\n");
			Logger.log("BlockFire: " + blockFireClass + "\n");
			Logger.log("BlockPos: " + blockPosClass + "\n");
			Logger.log("BlockState: " + blockStateClass + "\n");
			Logger.log("MainRegistry: " + mainRegistry + "\n");
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		
		boolean success = false;
		ArrayList<Throwable> throwables = new ArrayList<>();
		
		try {
			Logger.log(Registry.constructResourceLocation("FlameAPI:test").toString() + "\n");
			success = true;
		} catch (Throwable err) {
			throwables.add(err);
		}
		
		try {
			Logger.log(Registry.constructResourceLocation("flame_api:test").toString() + "\n");
			success = true;
		} catch (Throwable err) {
			throwables.add(err);
		}
		
		try {
			Logger.log(Registry.constructResourceLocation("flameapi:test").toString() + "\n");
			success = true;
		} catch (Throwable err) {
			throwables.add(err);
		}
		
		if (!success) {
			throwables.forEach(Logger::logErrFull);
			Logger.log("Failed to construct a resource location.\n");
		}
		
		try {
			Fabricator.compileAndLoadJanino("client_brand_retriever.java", (code) -> code);
//			String className = "net.minecraft.client.ClientBrandRetriever";
//			FlameASM.addField(className,"public static", "brand", "Ljava/lang/String;", "\"flamemc\"");
//			InsnList list = new InsnList();
//			//https://stackoverflow.com/questions/30186103/creating-a-getter-for-a-static-field-in-java-using-objecweb-asm
//			list.add(new FieldInsnNode(Opcodes.GETSTATIC,"net/minecraft/client/ClientBrandRetriever","brand","java/lang/String"));
//			list.add(new InsnNode(Opcodes.IRETURN));
//			FlameASM.addMethod(className,"public static","getClientModName","()Ljava/lang/String;",list);
			Fabricator.compileAndLoad("block_pos.java", (code) -> code.replace("%block_pos_class%", ScanningUtils.toClassName(blockPosClass)));
		} catch (Throwable err) {
			Logger.logErrFull(err);

//			try {
//				Thread.sleep(5000);
//			} catch (Throwable ignored) {
//			}
		}
	}

	private static byte[] getClassBytes(String className) throws IOException {
		InputStream inputStream = ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class");
		if (inputStream == null)
			throw new IOException("Class not found");
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			byte[] data = new byte[4096];
			int bytesRead;
			while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
				outputStream.write(data, 0, bytesRead);
			}
			outputStream.flush();
			return outputStream.toByteArray();
		} finally {
			inputStream.close();
		}
	}
}
