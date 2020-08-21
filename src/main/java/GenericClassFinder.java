import com.tfc.API.flame.utils.logging.Logger;
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
	//Must be done like this, to avoid a literally identical Array for 1.12.2 to 1.7.10, but only location is removed
	//I disagree (GiantLuigi4)
	//lol

	public static HashMap<String, String> findRegistrableClasses(File versionDir) {
		try {
			ScanningUtils.checkVersion();
			AtomicReference<String> clazzItem = new AtomicReference<>("null");
			AtomicReference<String> clazzStack = new AtomicReference<>("null");
			AtomicReference<String> clazzBlock = new AtomicReference<>("null");
			AtomicReference<String> clazzRL = new AtomicReference<>("null");
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
//				ScanningUtils.checkGenericClass(checksRL.size(), rlChecks.size(), clazzRL, "ResourceLocation", entry.getName());
			});
			ScanningUtils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checksItem = new HashMap<>();
				HashMap<String, Boolean> checksStack = new HashMap<>();
				HashMap<String, Boolean> checksBlock = new HashMap<>();
				ScanningUtils.forEachLine(sc, line -> {
					//Looks like this UUID is always the same in the Item class, this is perfect
					ScanningUtils.checkLine("CB3F55D3-645C-4F38-A497-9C13A33DB5CF", checksItem, line);
					for (String s : itemStackChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checksStack, line);
					}
					for (String s : version_checksBlocks) {
						ScanningUtils.checkLine(s, checksBlock, line);
					}
				});
				String entryName = entry.getName();
				ScanningUtils.checkGenericClass(checksItem.size(), 1, clazzItem, "Item", entryName);
				ScanningUtils.checkGenericClass(checksBlock.size(), version_checksBlocks.length, clazzBlock, "Block", entryName);
				ScanningUtils.checkGenericClass(checksStack.size(), itemStackChecks.length, clazzStack, "Stack", entryName);
			}, ClassFindingUtils::checkName);
			HashMap<String, String> classes = new HashMap<>();
			classes.put("Item", clazzItem.get());
			classes.put("ItemStack", clazzStack.get());
			classes.put("Block", clazzBlock.get());
			classes.put("ResourceLocation", clazzRL.get());
			return classes;
		} catch (Throwable ignored) {
		}
		return null;
	}
	
	public static HashMap<String, String> findExtensionClasses(File versionDir, HashMap<String, String> normal) {
		AtomicReference<String> blockItemClass = new AtomicReference<>("null");
		AtomicReference<String> blockFireClass = new AtomicReference<>("null");
		if (mcMajorVersion == 7) {
			fireChecks[2] = "largesmoke";
			fireChecks[3] = "_layer_0";
			fireChecks[4] = "fire.fire";
		}
		try {
			ScanningUtils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checksBlockItem = new HashMap<>();
				HashMap<String, Boolean> checksFire = new HashMap<>();
				ScanningUtils.forEachLine(sc, line -> {
					for (String s : BlockItemChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())).replace("%baseclass%", ScanningUtils.toClassName(normal.get("Item"))), checksBlockItem, line);
					}
					for (String s : fireChecks) {
						ScanningUtils.checkLine(s.replace("%block%", ScanningUtils.toClassName(normal.get("Block"))), checksFire, line);
					}
				});
				String entryName = entry.getName();
				if (!Main.getBlockClass().equals(entryName))
					ScanningUtils.checkGenericClass(checksFire.size(), fireChecks.length, blockFireClass, "BlockFire", entryName);
				ScanningUtils.checkGenericClass(checksBlockItem.size(), BlockItemChecks.length, blockItemClass, "BlockItem", entryName);
				if (checksFire.size() == 5) {
					Logger.logLine("\nFire: " + checksFire + ", " + entryName);
				}
			}, ClassFindingUtils::checkName);
			normal.put("BlockItem", blockItemClass.get());
			normal.put("BlockFire", blockFireClass.get());
			return normal;
		} catch (Throwable ignored) {
		}
		return null;
	}
}
