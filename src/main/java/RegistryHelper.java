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
	private static final String[] entities = new String[]{
			"player",
			"lightning_bolt",
			"fishing_bobber",
			"pig",
			"creeper"
	};
	private static final String[] tileEntities = new String[]{
			"furnace",
			"chest",
			"jukebox",
			"dispenser"
	};
	private static final String[] enchantments = new String[]{
			"protection",
			"thorns",
			"efficiency",
			"sharpness"
	};
	
	public static HashMap<String, String> findRegistryClass(File versionDir) {
		try {
			JarFile file = new JarFile(versionDir);
			HashMap<String, String> registries = new HashMap<>();
			String mcVersion = Main.getVersion().substring(0, Main.getVersion().indexOf("-"));
			String mcMiddleVersion = mcVersion.substring(2);
			mcMiddleVersion = mcMiddleVersion.substring(0, mcMiddleVersion.indexOf("."));
			FlameConfig.field.append(mcMiddleVersion + "\n");
			String[] version_blocks = Integer.parseInt(mcMiddleVersion) > 12 ? blocks_13 : blocks_12;
			try {
				for (Iterator<JarEntry> it = file.stream().iterator(); it.hasNext(); ) {
					JarEntry entry = it.next();
					if (entry.getName().endsWith(".class")) {
						try {
							InputStream stream = file.getInputStream(entry);
							Scanner sc = new Scanner(stream);
							boolean containsUnmodifiableIterator = false;
							HashMap<String, Boolean> blockChecks = new HashMap<>();
							HashMap<String, Boolean> itemChecks = new HashMap<>();
							HashMap<String, Boolean> tileEntitiesChecks = new HashMap<>();
							HashMap<String, Boolean> entityChecks = new HashMap<>();
							HashMap<String, Boolean> enchantmentChecks = new HashMap<>();
							while (sc.hasNext()) {
								String s1 = sc.next();
								if (s1.contains("UnmodifiableIterator")) containsUnmodifiableIterator = true;
								for (String s : items)
									if (!itemChecks.containsKey(s) && s1.contains(s))
										itemChecks.put(s, true);
								for (String s : version_blocks)
									if (!blockChecks.containsKey(s) && s1.contains(s))
										blockChecks.put(s, true);
								for (String s : entities)
									if (!entityChecks.containsKey(s) && s1.contains(s))
										entityChecks.put(s, true);
								for (String s : tileEntities)
									if (!tileEntitiesChecks.containsKey(s) && s1.contains(s))
										tileEntitiesChecks.put(s, true);
								for (String s : enchantments)
									if (!enchantmentChecks.containsKey(s) && s1.contains(s))
										enchantmentChecks.put(s, true);
							}
							if (Integer.parseInt(mcMiddleVersion) > 12) {
								if (containsUnmodifiableIterator) {
									if (blockChecks.size() == (version_blocks.length)) {
										registries.put("minecraft:blocks", entry.getName());
										FlameConfig.field.append("block registry class:" + entry.getName() + "\n");
									}
								} else if (itemChecks.size() == (items.length)) {
									registries.put("minecraft:items", entry.getName());
									FlameConfig.field.append("item registry class:" + entry.getName() + "\n");
								} else if (tileEntitiesChecks.size() == (tileEntities.length)) {
									registries.put("minecraft:tile_entities", entry.getName());
									FlameConfig.field.append("tile entities registry class:" + entry.getName() + "\n");
								} else if (entityChecks.size() == (entities.length)) {
									registries.put("minecraft:entities", entry.getName());
									FlameConfig.field.append("entity registry class:" + entry.getName() + "\n");
								} else if (enchantmentChecks.size() == (enchantments.length)) {
									registries.put("minecraft:enchantments", entry.getName());
									FlameConfig.field.append("enchantments registry class:" + entry.getName() + "\n");
								}
							} else {
								int invalidator = 0;
								if (blockChecks.size() == (version_blocks.length)) {
									registries.put("minecraft:blocks", entry.getName());
									FlameConfig.field.append("block registry class:" + entry.getName() + "\n");
									invalidator++;
								} else if (itemChecks.size() == (items.length)) {
									registries.put("minecraft:items", entry.getName());
									FlameConfig.field.append("item registry class:" + entry.getName() + "\n");
									invalidator++;
								} else if (invalidator == 0 && tileEntitiesChecks.size() == (tileEntities.length)) {
									registries.put("minecraft:tile_entities", entry.getName());
									FlameConfig.field.append("tile entities registry class:" + entry.getName() + "\n");
								} else if (entityChecks.size() == (entities.length)) {
									registries.put("minecraft:entities", entry.getName());
									FlameConfig.field.append("entity registry class:" + entry.getName() + "\n");
								} else if (enchantmentChecks.size() == (enchantments.length)) {
									registries.put("minecraft:enchantments", entry.getName());
									FlameConfig.field.append("enchantments registry class:" + entry.getName() + "\n");
								}
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
