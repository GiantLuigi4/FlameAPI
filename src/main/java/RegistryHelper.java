import com.tfc.flame.FlameConfig;
import entries.FlameAPI.Main;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RegistryHelper {
	private static final String[] blocks_13 = new String[]{
			"cobblestone",
			"air",
			"grass_block",
			"dirt"
	};
	private static final String[] blocks_12 = new String[]{
			"cobblestone",
			"air",
			"grass",
			"dirt"
	};
	private static final String[] items = new String[]{
			"bow",
			"arrow",
			"emerald",
			"diamond"
	};
	private static final String[] entities_11 = new String[] {
			"Item",
			"XPOrb",
			"LeashKnot",
			"Painting"
	};
	private static final String[] entities_12 = new String[]{
			"player",
			"lightning_bolt",
			"fishing_bobber",
			"pig",
			"creeper"
	};
	private static final String[] tileEntities_11 = new String[]{
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
	private static final int[] enchantments_11 = new int[] {
			0,1,2,3,4,5,6,7,20
	};
	
	public static HashMap<String, String> findRegistryClass(File versionDir) {
		try {
			JarFile file = new JarFile(versionDir);
			HashMap<String, String> registries = new HashMap<>();
			String mcAssetVer = Main.getAssetVersion();
			String mcMajorVer = mcAssetVer.substring(mcAssetVer.indexOf(".") + 1);
			if (mcMajorVer.contains(".")) {
				mcMajorVer = mcMajorVer.substring(0, mcMajorVer.indexOf("."));
			}
			FlameConfig.field.append(mcMajorVer + "\n");
			boolean flagGreaterThan12 = Integer.parseInt(mcMajorVer) > 12;
			boolean flagLessThan11 = Integer.parseInt(mcMajorVer) < 11; // 11 is just a placeholder, still gotta check
			String[] version_blocks = flagGreaterThan12 ? blocks_13 : blocks_12;
			String[] version_entities = flagLessThan11 ? entities_11 : entities_12;
			String[] version_tileEntities = flagLessThan11 ? tileEntities_11 : tileEntities_12;

			try {
				for (Iterator<JarEntry> it = file.stream().iterator(); it.hasNext(); ) {
					JarEntry entry = it.next();
					if (entry.getName().endsWith(".class")) {
						try {
							InputStream stream = file.getInputStream(entry);
							Scanner sc = new Scanner(stream);
							HashMap<String, Boolean> blockChecks = new HashMap<>();
							HashMap<String, Boolean> itemChecks = new HashMap<>();
							HashMap<String, Boolean> tileEntitiesChecks = new HashMap<>();
							HashMap<String, Boolean> entityChecks = new HashMap<>();
							HashMap<String, Boolean> enchantmentChecks12 = new HashMap<>();
							HashMap<Integer, Boolean> enchantmentChecks11 = new HashMap<>();
							while (sc.hasNext()) {
								String s1 = sc.next();
								for (String s : items)
									if (!itemChecks.containsKey(s) && s1.contains(s))
										itemChecks.put(s, true);
								for (String s : version_blocks)
									if (!blockChecks.containsKey(s) && s1.contains(s))
										blockChecks.put(s, true);
								for (String s : version_entities)
									if (!entityChecks.containsKey(s) && s1.contains(s))
										entityChecks.put(s, true);
								for (String s : version_tileEntities)
									if (!tileEntitiesChecks.containsKey(s) && s1.contains(s))
										tileEntitiesChecks.put(s, true);
								if (flagLessThan11)
									for (Integer i : enchantments_11)
										if (!enchantmentChecks11.containsKey(i) && s1.contains(i.toString()))
											enchantmentChecks11.put(i, true);
								else
									for (String s : enchantments_12)
										if (!enchantmentChecks12.containsKey(s) && s1.contains(s))
											enchantmentChecks12.put(s, true);
							}
							if (!enchantmentChecks11.isEmpty()) {
								FlameConfig.field.append("enchant checks: " + enchantmentChecks11 + "\n");
							}
							if (blockChecks.size() == (version_blocks.length)) {
								registries.put("minecraft:blocks", entry.getName());
								FlameConfig.field.append("block registry class:" + entry.getName() + "\n");
							} else if (itemChecks.size() == (items.length)) {
								registries.put("minecraft:items", entry.getName());
								FlameConfig.field.append("item registry class:" + entry.getName() + "\n");
							} else if (tileEntitiesChecks.size() == (version_tileEntities.length)) {
								registries.put("minecraft:tile_entities", entry.getName());
								FlameConfig.field.append("tile entities registry class:" + entry.getName() + "\n");
							} else if (entityChecks.size() == (version_entities.length)) {
								registries.put("minecraft:entities", entry.getName());
								FlameConfig.field.append("entity registry class:" + entry.getName() + "\n");
							} else if (flagLessThan11 && enchantmentChecks11.size() == (enchantments_11.length)) {
								registries.put("minecraft:enchantments", entry.getName());
								FlameConfig.field.append("enchantments registry class:" + entry.getName() + "\n");
							} else if (enchantmentChecks12.size() == (enchantments_12.length)) {
								registries.put("minecraft:enchantments", entry.getName());
								FlameConfig.field.append("enchantments registry class:" + entry.getName() + "\n");
							}
//							FlameConfig.field.append("checksB:"+blockChecks.size()+"\n");
//							FlameConfig.field.append("goalB  :"+(blocks.length)+"\n");
//							FlameConfig.field.append("checksI:"+itemChecks.size()+"\n");
//							FlameConfig.field.append("goalI  :"+(items.length)+"\n");
							sc.close();
							stream.close();
						} catch (Throwable ignored) {
						}
					}
				}
			} catch (Throwable ignored) {
			}
			return registries;
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		return null;
	}
	
	public static String findMainRegistry(HashMap<String, String> registryTypes, File versionDir) {
		try {
			JarFile file = new JarFile(versionDir);
			AtomicReference<String> registry = new AtomicReference<>(null);
			for (String typeClass : registryTypes.values()) {
				FlameConfig.field.append(typeClass + "\n");
			}
			forAllFiles(file, (sc, entry) -> {
				HashMap<String, Boolean> types = new HashMap<>();
				while (sc.hasNextLine()) {
					String s = sc.nextLine();
					for (String typeClass : registryTypes.values()) {
						if (s.contains(typeClass.replace(".class", "").replace("/", ".")) && !types.containsKey(typeClass))
							types.put(typeClass, true);
					}
					if (registryTypes.size() == types.size()) {
						registry.set(entry.getName());
					}
				}
			}, name -> name.endsWith(".class"));
			return registry.get();
		} catch (Throwable ignored) {
		}
		return null;
	}
	
	public static void forAllFiles(JarFile file, BiConsumer<Scanner, JarEntry> textConsumer, Function<String, Boolean> fileValidator) {
		file.stream().forEach(f -> {
			if (fileValidator.apply(f.getName())) {
				try {
					InputStream stream = file.getInputStream(f);
					Scanner sc = new Scanner(stream);
					textConsumer.accept(sc, f);
					sc.close();
					stream.close();
				} catch (Throwable err) {
				}
			}
		});
	}
}
