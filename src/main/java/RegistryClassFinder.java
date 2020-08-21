import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.flame.FlameConfig;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

import static com.tfc.utils.ScanningUtils.isVersionGreaterThan12;
import static com.tfc.utils.ScanningUtils.isVersionLessThan12;

public class RegistryClassFinder {
	//avi.class is struc reg in 1.7.10
	
	private static final String[] blocks = new String[]{
			"cobblestone",
			"air",
			"grass_block",
			"dirt"
	};
	private static final String[] items = new String[]{
			"bow",
			"arrow",
			"emerald",
			"diamond"
	};
	private static final String[] entities_11 = new String[]{ //1.7.10 has these things starting with uppercase
			"Item",
			"XPOrb",
			"LeashKnot",
			"Painting"
	};
	private static final String[] entities_12 = new String[]{
			"player",
			"lightning_bolt",
			"pig",
			"creeper"
	};
	private static final String[] tileEntities_11 = new String[]{ //1.7.10 has these things starting with uppercase
			"Furnace",
			"Chest",
			"EnderChest",
			"RecordPlayer"
	};
	private static final String[] tileEntities_12 = new String[]{
			"furnace",
			"chest",
			"jukebox",
			"dispenser"
	};
	private static final String[] enchantments_12 = new String[]{
			"protection",
			"thorns",
			"efficiency",
			"sharpness"
	};
	private static final String[] enchantments_11 = new String[]{  //these are the only strings in aft.class in 1.7.10. They are used for the lang file
			"enchantment.",
			"enchantment.level."
	};
	private static final String[] biomes_11 = new String[]{
			"Ocean",
			"Plains",
			"Desert",
			"MushroomIsland"
	};
	private static final String[] biomes_12 = new String[]{
			"plains",
			"desert",
			"mountains",
			"forest",
			"taiga"
	};
	//TODO Dimension in 1.7.10 -> https://github.com/MinecraftForge/MinecraftForge/blob/1.7.10/src/main/java/net/minecraftforge/common/DimensionManager.java
	private static final String[] dimensions = new String[]{
			"overworld",
			"the_nether",
			"the_end"
	};
	private static final String[] craftingRecipes = new String[]{
			"Invalid shapeless",
			"###"
	};
	private static final String[] furnace = new String[]{
			"Ljava/lang/Object;",
			"Ljava/util/Map;",
			"java/util/Iterator",
			"java/util/Map$Entry",
			"java/util/Set",
			"L%classname%",
			"getKey",
			"getValue",
			"hasNext",
			"333"
	};
	private static final String[] sounds = new String[] {
			"meta:missing_sound",
			"File {} does not exist, cannot add it to event {}"
	};
	private static final String[] bootstrap = new String[]{
			"<clinit>",
			"<init>",
			"%classname%",
			"(L%item%;",
			"Â",
			"9",
			""
	};
	
	public static HashMap<String, String> findRegistryClass(File versionDir) {
		try {
			ScanningUtils.checkVersion();
			JarFile file = new JarFile(versionDir);
			HashMap<String, String> registries = new HashMap<>();
			if (isVersionLessThan12)
				blocks[2] = "grass";
			String[] version_entities = isVersionLessThan12 ? entities_11 : entities_12;
			String[] version_tileEntities = isVersionLessThan12 ? tileEntities_11 : tileEntities_12;
			String[] version_enchantments = isVersionLessThan12 ? enchantments_11 : enchantments_12;
			String[] version_biomes = isVersionLessThan12 ? biomes_11 : biomes_12;
			ScanningUtils.forAllFiles(file, (sc, entry) -> {
				try {
					InputStream stream = file.getInputStream(entry);
					HashMap<String, Boolean> itemChecks = new HashMap<>();
					HashMap<String, Boolean> blockChecks = new HashMap<>();
					HashMap<String, Boolean> entityChecks = new HashMap<>();
					HashMap<String, Boolean> tileEntitiesChecks = new HashMap<>();
					HashMap<String, Boolean> enchantmentChecks = new HashMap<>();
					HashMap<String, Boolean> biomeChecks = new HashMap<>();
					HashMap<String, Boolean> dimensionChecks = new HashMap<>();
					HashMap<String, Boolean> recipeChecks = new HashMap<>();
					HashMap<String, Boolean> soundsChecks = new HashMap<>();
					HashMap<String, Boolean> furnaceChecks = new HashMap<>();
					ScanningUtils.forEachLine(sc, line -> {
						for (String s : items)
							ScanningUtils.checkLine(s, itemChecks, line);
						for (String s : blocks)
							ScanningUtils.checkLine(s, blockChecks, line);
						for (String s : version_entities)
							ScanningUtils.checkLine(s, entityChecks, line);
						for (String s : version_tileEntities)
							ScanningUtils.checkLine(s, tileEntitiesChecks, line);
						for (String s : version_enchantments)
							ScanningUtils.checkLine(s, enchantmentChecks, line);
						for (String s : version_biomes)
							ScanningUtils.checkLine(s, biomeChecks, line);
						for (String s : dimensions)
							ScanningUtils.checkLine(s, dimensionChecks, line);
						for (String s : craftingRecipes)
							ScanningUtils.checkLine(s, recipeChecks, line);
						for (String s : sounds)
							ScanningUtils.checkLine(s, soundsChecks, line);
						for (String s : furnace)
							ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), furnaceChecks, line);
					});
					String entryName = entry.getName();
					ScanningUtils.checkRegistry(blockChecks.size(), blocks.length, registries, "blocks", entryName);
					ScanningUtils.checkRegistry(itemChecks.size(), items.length, registries, "items", entryName);
					ScanningUtils.checkRegistry(tileEntitiesChecks.size(), version_tileEntities.length, registries, "tile_entities", entryName);
					ScanningUtils.checkRegistry(entityChecks.size(), version_tileEntities.length, registries, "entities", entryName);
					ScanningUtils.checkRegistry(enchantmentChecks.size(), version_enchantments.length, registries, "enchantments", entryName);
					ScanningUtils.checkRegistry(biomeChecks.size(), version_biomes.length, registries, "biome", entryName);
//					ScanningUtils.checkRegistry(dimensionChecks.size(), dimensions.length, registries, "dimensions", entryName);
//					ScanningUtils.checkRegistry(soundsChecks.size(), sounds.length, registries, "sounds", entryName);
					if (isVersionLessThan12) {
						ScanningUtils.checkRegistry(furnaceChecks.size(), furnace.length, registries, "furnaceRecipes", entryName);
						ScanningUtils.checkRegistry(recipeChecks.size(), craftingRecipes.length, registries, "recipes", entryName);
					}
//					FlameConfig.field.append("checksB:"+blockChecks.size()+"\n");
//					FlameConfig.field.append("goalB  :"+(blocks.length)+"\n");
//					FlameConfig.field.append("checksI:"+itemChecks.size()+"\n");
//					FlameConfig.field.append("goalI  :"+(items.length)+"\n");
					sc.close();
					stream.close();
				} catch (Throwable ignored) {
				}
			}, ClassFindingUtils::checkName);
			return registries;
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		return null;
	}
	
	public static String findMainRegistry(HashMap<String, String> registryTypes, File versionDir) {
		try {
			switch (Main.getVersion()) {
				case "1.15.2-flame":
				case "1.15-flame":
				case "1.15.1-flame":
					return "gb.class";
				case "1.16.2-flame":
					return "gm.class";
				case "1.16.1-flame":
				case "1.16-flame":
					return "gl.class";
				case "1.14-flame":
				case "1.14.1-flame":
				case "1.14.2-flame":
				case "1.14.3-flame":
				case "1.14.4-flame":
					return "fm.class";
				case "1.12-flame":
					return "ng.class";
				case "1.8-flame":
					return "od.class";
				case "1.8.8-flame":
				case "1.8.9-flame":
					return "kb.class";
			}
			JarFile file = new JarFile(versionDir);
			AtomicReference<String> registry = new AtomicReference<>(null);
			for (String typeClass : registryTypes.values()) {
				FlameConfig.field.append("Registry Type: " + typeClass + "\n");
			}
			StringBuilder builder = new StringBuilder("\n");
			if (isVersionGreaterThan12) {
				ScanningUtils.forAllFiles(file, (sc, entry) -> {
					HashMap<String, Boolean> types = new HashMap<>();
					ScanningUtils.forEachLine(sc, line -> {
						for (String typeClass : registryTypes.values()) {
							ScanningUtils.checkLine(ScanningUtils.toClassName(typeClass), types, line);
						}
						if (registryTypes.size() >= (types.size() - 1) && registryTypes.size() <= types.size()) {
							registry.set(entry.getName());
						}
					});
					if (registryTypes.size() > 1) {
						builder
								.append(registryTypes.size()).append(":")
								.append(entry.getName()).append(", ");
					}
				}, ClassFindingUtils::checkName);
			} else {
				ScanningUtils.forAllFiles(file, (sc, entry) -> {
					HashMap<String, Boolean> checks = new HashMap<>();
					ScanningUtils.forEachLine(sc, line -> {
						for (String s : bootstrap) {
							if (s.equals("%classname%"))
								s = s.replace("%classname%", ScanningUtils.toClassName(entry.getName()));
							else if (s.contains("%item%"))
								s = s.replace("%item%", ScanningUtils.toClassName(Main.getItemClass()));
							ScanningUtils.checkLine(s, checks, line);
						}
						if (bootstrap.length >= (checks.size() - 1) && bootstrap.length <= checks.size()) {
							registry.set(entry.getName());
						}
					});
					if (bootstrap.length == checks.size())
						Logger.logLine(checks + ", " + ScanningUtils.toClassName(entry.getName()));
				}, ClassFindingUtils::checkName);

			}
			Logger.logLine(builder.toString());
			return registry.get();
		} catch (Throwable ignored) {
		}
		return null;
	}
}
