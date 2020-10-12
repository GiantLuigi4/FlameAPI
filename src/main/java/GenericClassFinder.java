import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.utils.ClassFindingUtils;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

import static com.tfc.utils.ScanningUtils.*;

public class GenericClassFinder {
	
	//I know this works on 1.15.2-1.16.2
	private static final String[] checksBlocks = new String[]{
			"Ljava/util/Random;",
			"getAndMoveToFirst",
			"Ljava/lang/StringBuilder;",
			"Block{"
	};
	//this works for 1.7.10 and 1.12.2, no need to thank me lol (Lorenzo)
	private static final String[] checksBlocks12 = new String[]{
			"doTileDrops",
			"air"
	};
	private static final String[] itemStackChecks = new String[]{
			"RepairCost",
			"Damage"
	};
	private static final String[] BlockItemChecks = new String[]{
			"%java/lang/invoke/MethodHandles$Lookup",
			"%baseclass%",
			"BlockState",
//			"Ljava/lang/Object;",
//			"Ljava/lang/Comparable;",
//			"Ljava/util/function/Function;",
			"&Lnet/minecraft/server/",
			"BlockEntityTag"
	};
	private static final String[] rlChecks = new String[]{
			"minecraft",
			":",
			"hashCode",
			"location"
	};
	private static final String[] fireChecks = new String[]{
			"doFireTick",
			"Ljava/util/Random;",
			"age",
			"north",
			"south"
	};
	private static final String[] blockPosChecks = new String[]{
			"L%classname%",
			"(DDD)L%classname%",
			"(III)L%classname%",
			"(JIIIIIII)V"
	};
	private static final String[] Vec3iChecks = new String[]{
			"Ljava/lang/Object;",
			"(Ljava/lang/Object;)Lcom/google/common/base/MoreObjects$ToStringHelper;",
			"()Ljava/lang/String;",
			"append",
			"RuntimeInvisibleAnnotations",
			"RuntimeVisibleAnnotations",
			"(Ljava/lang/String;I)Lcom/google/common/base/MoreObjects$ToStringHelper;",
			"(DDDZ)D",
			";D)Z",
			"(I)I"
	};
	private static final String[] blockStateChecks = new String[]{
			"Name",
			"Properties",
			"minecraft:air",
			"L%rl%;",
			"L%block%"
	};
	private static final String[] worldServerChecks = new String[]{
			"world border",
			"weather",
			"chunkSource",
			"tickPending",
			"raid",
			"blockEvents",
			"entities",
			"global"
	};
	public static final String[] IWorldChecks = new String[]{
			";DDDDDD)V",
			";I)V",
			";Ljava/util/Set;)Ljava/util/stream/Stream;",
			"(JF)F",
			"(F)F",
			"()I"
	};
	public static final String[] BBChecks = new String[]{
			"Needed to grow BufferBuilder buffer: Old size {} bytes, new size {} bytes.",
			"Already building!",
			"Not building!",
			"Not filled all elements of the vertex",
			"Bytes mismatch ",
			"BufferBuilder not started"
	};

	public static final String[] TessellatorChecks = new String[] {
		"L%class%;",
		"()L%class%",
		"L%bbclass%;",
		"com/mojang/blaze3d/systems/RenderSystem",
		"isOnGameThreadOrInit"
	};
	//Must be done like this, to avoid a literally identical Array for 1.12.2 to 1.7.10, but only location is removed
	//I disagree (GiantLuigi4)
	//lol
	//can we delete this convo? L o L
	
	
	public static HashMap<String, String> findRegistrableClasses(File versionDir) {
		ScanningUtils.checkVersion();
		if (isVersionGreaterThan12) {
			if (Main.getVersion().contains("forge")) {
				HashMap<String, String> map = new HashMap<>();
				map.put("Item", "net/minecraft/item/Item.class");
				map.put("ItemStack", "net/minecraft/item/ItemStack.class");
				map.put("Block", "net/minecraft/block/Block.class");
				map.put("ResourceLocation", "net/minecraft/util/ResourceLocation.class");
				return map;
			}
		}
		try {
			AtomicReference<String> clazzItem = new AtomicReference<>("null");
			AtomicReference<String> clazzStack = new AtomicReference<>("null");
			AtomicReference<String> clazzBlock = new AtomicReference<>("null");
			AtomicReference<String> clazzRL = new AtomicReference<>("null");
			AtomicReference<String> clazzBlockPos = new AtomicReference<>("null");
			AtomicReference<String> clazzVec3i = new AtomicReference<>("null");
			AtomicReference<String> clazzWorld = new AtomicReference<>("null");
			AtomicReference<String> clazzWorldServer = new AtomicReference<>("null");
			AtomicReference<String> clazzIWorld = new AtomicReference<>("null");
			AtomicReference<String> clazzBB = new AtomicReference<>("null");
			if (isVersionLessThan12)
				rlChecks[3] = "append";
			String[] version_checksBlocks = isVersionGreaterThan12 ? checksBlocks : checksBlocks12;
			ScanningUtils.forLittleFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checksRL = new HashMap<>();
				ScanningUtils.forEachLine(sc, line -> {
					for (String s : rlChecks)
						ScanningUtils.checkLine(s, checksRL, line);
				});
				ScanningUtils.checkGenericClass(checksRL.size(), rlChecks.length, clazzRL, "ResourceLocation", entry.getName());
			});
			ScanningUtils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checksWorld = new HashMap<>();
				HashMap<String, Boolean> checksWorldServer = new HashMap<>();
				HashMap<String, Boolean> checksItem = new HashMap<>();
				HashMap<String, Boolean> checksStack = new HashMap<>();
				HashMap<String, Boolean> checksBlock = new HashMap<>();
				HashMap<String, Boolean> checksBlockPos = new HashMap<>();
				HashMap<String, Boolean> checksVec3i = new HashMap<>();
				HashMap<String, Boolean> checksIWorld = new HashMap<>();
				HashMap<String, Boolean> checksBB = new HashMap<>();
				ScanningUtils.forEachLine(sc, line -> {
					//Looks like this UUID is always the same in the Item class, this is perfect
					ScanningUtils.checkLine("CB3F55D3-645C-4F38-A497-9C13A33DB5CF", checksItem, line);
					for (String s : itemStackChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checksStack, line);
					}
					for (String s : version_checksBlocks) {
						ScanningUtils.checkLine(s, checksBlock, line);
					}
					for (String s : blockPosChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checksBlockPos, line);
					}
					for (String s : Vec3iChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checksVec3i, line);
					}
					for (String s : IWorldChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checksIWorld, line);
					}
					//in 1.15.2, World class has this string, it's not present in any other classes
					ScanningUtils.checkLine("Should always be able to create a chunk!", checksWorld, line);
					if (isVersionGreaterThan12)
						for (String s : worldServerChecks) {
							ScanningUtils.checkLine(s, checksWorldServer, line);
						}

					for (String s : BBChecks) {
						ScanningUtils.checkLine(s, checksBB, line);
					}
				});
				String entryName = entry.getName();
				ScanningUtils.checkGenericClass(checksItem.size(), 1, clazzItem, "Item", entryName);
				ScanningUtils.checkGenericClass(checksBlock.size(), version_checksBlocks.length, clazzBlock, "Block", entryName);
				ScanningUtils.checkGenericClass(checksStack.size(), itemStackChecks.length, clazzStack, "Stack", entryName);
				ScanningUtils.checkGenericClass(checksBlockPos.size(), blockPosChecks.length, clazzBlockPos, "BlockPos", entryName);
				ScanningUtils.checkGenericClass(checksVec3i.size(), Vec3iChecks.length, clazzVec3i, "Vec3i", entryName);
				ScanningUtils.checkGenericClass(checksWorld.size(), 1, clazzWorld, "World", entryName);
				ScanningUtils.checkGenericClass(checksWorldServer.size(), worldServerChecks.length, clazzWorldServer, "WorldServer", entryName);
				ScanningUtils.checkGenericClass(checksIWorld.size(), IWorldChecks.length, clazzIWorld, "IWorld", entryName);
				ScanningUtils.checkGenericClass(checksBB.size(), BBChecks.length, clazzBB, "BufferBuilder", entryName);
			}, ClassFindingUtils::checkName);
			HashMap<String, String> classes = new HashMap<>();
			classes.put("Item", clazzItem.get());
			classes.put("ItemStack", clazzStack.get());
			classes.put("Block", clazzBlock.get());
			classes.put("ResourceLocation", clazzRL.get());
			classes.put("BlockPos", clazzBlockPos.get());
			classes.put("Vec3i", clazzVec3i.get());
			classes.put("World", clazzWorld.get());
			classes.put("WorldServer", clazzWorldServer.get());
			classes.put("IWorld", clazzIWorld.get());
			classes.put("BufferBuilder", clazzBB.get());
			if (Main.getVersion().equals("1.15.2-flame")) {
				classes.put("Entity", "akq.class");
			}
			return classes;
		} catch (Throwable ignored) {
		}
		return null;
	}
	
	public static HashMap<String, String> findExtensionClasses(File versionDir, HashMap<String, String> normal) {
		if (isVersionGreaterThan12) {
			if (Main.getVersion().contains("forge")) {
				normal.put("BlockItem", "net/minecraft/item/BlockItem.class");
				normal.put("BlockFire", "net/minecraft/block/FireBlock.class");
				return normal;
			}
		}
		AtomicReference<String> blockStateClass = new AtomicReference<>("null");
		AtomicReference<String> blockItemClass = new AtomicReference<>("null");
		AtomicReference<String> blockFireClass = new AtomicReference<>("null");
		AtomicReference<String> clazzTessellator = new AtomicReference<>("null");
		if (mcMajorVersion == 7) {
			fireChecks[2] = "largesmoke";
			fireChecks[3] = "_layer_0";
			fireChecks[4] = "fire.fire";
		}
		try {
			ScanningUtils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				String entryName = entry.getName();
				HashMap<String, Boolean> checksBlockItem = new HashMap<>();
				HashMap<String, Boolean> checksBlockState = new HashMap<>();
				HashMap<String, Boolean> checksFire = new HashMap<>();
				HashMap<String, Boolean> checksTS = new HashMap<>();
				ScanningUtils.forEachLine(sc, line -> {
					for (String s : BlockItemChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entryName)).replace("%baseclass%", ScanningUtils.toClassName(normal.get("Item"))), checksBlockItem, line);
					}
					for (String s : fireChecks) {
						ScanningUtils.checkLine(s.replace("%block%", ScanningUtils.toClassName(normal.get("Block"))), checksFire, line);
					}
					for (String s : blockStateChecks) {
						ScanningUtils.checkLine(s.replace("%block%", ScanningUtils.toClassName(normal.get("Block"))).replace("%rl%", ScanningUtils.toClassName(normal.get("ResourceLocation"))), checksBlockState, line);
					}
					for (String s : TessellatorChecks) {
						ScanningUtils.checkLine(s.replace("%class%", ScanningUtils.toClassName(entryName)).replace("%bbclass%", ScanningUtils.toClassName(normal.get("BufferBuilder"))), checksTS, line);
					}
				});
				if (!normal.containsValue(entryName)) {
					if (!Main.getBlockClass().equals(entryName))
						ScanningUtils.checkGenericClass(checksFire.size(), fireChecks.length, blockFireClass, "BlockFire", entryName);
					ScanningUtils.checkGenericClass(checksBlockItem.size(), BlockItemChecks.length, blockItemClass, "BlockItem", entryName);
					ScanningUtils.checkGenericClass(checksBlockState.size(), blockStateChecks.length, blockStateClass, "BlockState", entryName);
					ScanningUtils.checkGenericClass(checksTS.size(), TessellatorChecks.length, clazzTessellator, "Tessellator", entryName);
					if (checksTS.size() > TessellatorChecks.length - 1) {
						Logger.logLine(checksTS + ", " + entryName);
					}
				}
			}, ClassFindingUtils::checkName);
			normal.put("BlockItem", blockItemClass.get());
			normal.put("BlockFire", blockFireClass.get());
			normal.put("BlockState", blockStateClass.get());
			normal.put("Tessellator", clazzTessellator.get());
			return normal;
		} catch (Throwable ignored) {
		}
		return null;
	}
}
