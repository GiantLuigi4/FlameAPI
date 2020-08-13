import com.tfc.flame.FlameConfig;
import entries.FlameAPI.Main;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
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
							while (sc.hasNext()) {
								String s1 = sc.next();
								if (s1.contains("UnmodifiableIterator")) containsUnmodifiableIterator = true;
								for (String s : items)
									if (!itemChecks.containsKey(s) && s1.contains(s))
										itemChecks.put(s, true);
								for (String s : version_blocks)
									if (!blockChecks.containsKey(s) && s1.contains(s))
										blockChecks.put(s, true);
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
								}
							} else {
								if (blockChecks.size() == (version_blocks.length)) {
									registries.put("minecraft:blocks", entry.getName());
									FlameConfig.field.append("block registry class:" + entry.getName() + "\n");
								}
								if (itemChecks.size() == (items.length)) {
									registries.put("minecraft:items", entry.getName());
									FlameConfig.field.append("item registry class:" + entry.getName() + "\n");
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
}
