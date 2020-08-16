import com.tfc.Utils.ScanningUtils;
import com.tfc.flame.FlameConfig;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

public class GenericClassFinder {
	//I know this works on 1.15.2-1.16.2
	//I also know it does not work on 1.12
	private static final String[] checksBlocks = new String[]{
			"Ljava/util/Random;",
			"getAndMoveToFirst",
			"Ljava/lang/StringBuilder;",
			"Block{"
	};
	
	//1.15.2
	private static final String[] checksItems = new String[]{
			"Ljava/lang/String;",
			"java/util/Random",
			"Ljava/util/UUID;",
			"L%classname%",
			"item"
	};
	
	private static final String[] itemStackChecks = new String[]{
			"RepairCost",
			"Damage"
	};
	
	public static HashMap<String, String> findItemClasses(File versionDir) {
		try {
			AtomicReference<String> clazz_Item = new AtomicReference<>("null");
			AtomicReference<String> clazzStack = new AtomicReference<>("null");
			AtomicReference<String> clazzBlock = new AtomicReference<>("null");
			ScanningUtils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checks = new HashMap<>();
				HashMap<String, Boolean> checksBlock = new HashMap<>();
				ScanningUtils.forEachLine(sc, line -> {
					for (String s : checksItems) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checks, line);
					}
					for (String s : itemStackChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checks, line);
					}
					for (String s : checksBlocks) {
						ScanningUtils.checkLine(s, checksBlock, line);
					}
				});
				if (checks.size() == checksItems.length && !clazz_Item.get().equals(entry.getName())) {
					clazz_Item.set(entry.getName());
					FlameConfig.field.append("Potential item class: " + clazz_Item.get() + "\n");
				} else if (checks.size() == (checksItems.length + itemStackChecks.length) && !clazzStack.get().equals(entry.getName())) {
					clazzStack.set(entry.getName());
					FlameConfig.field.append("Potential item stack class: " + clazzStack.get() + "\n");
				} else if (checksBlock.size() == checksBlocks.length && !clazzBlock.get().equals(entry.getName())) {
					clazzBlock.set(entry.getName());
					FlameConfig.field.append("Potential item stack class: " + clazzBlock.get() + "\n");
				}
			}, name -> name.endsWith(".class") && !name.startsWith("com.tfc"));
			HashMap<String, String> classes = new HashMap<>();
			classes.put("Item", clazz_Item.get());
			classes.put("ItemStack", clazzStack.get());
			classes.put("Block", clazzBlock.get());
			return classes;
		} catch (Throwable ignored) {
		}
		return null;
	}
}
