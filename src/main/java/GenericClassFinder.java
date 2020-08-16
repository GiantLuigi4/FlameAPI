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
	
	//TODO:merge into findBlockClass to reduce load time
	public static HashMap<String, String> findItemClasses(File versionDir) {
		try {
			AtomicReference<String> clazz = new AtomicReference<>("null");
			AtomicReference<String> clazzStack = new AtomicReference<>("null");
			Utils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checks = new HashMap<>();
				Utils.forEachLine(sc, line -> {
					for (String s : checksItems) {
						Utils.checkLine(s.replace("%classname%", Utils.toClassName(entry.getName())), checks, line);
					}
					for (String s : itemStackChecks) {
						Utils.checkLine(s.replace("%classname%", Utils.toClassName(entry.getName())), checks, line);
					}
				});
				if (checks.size() == checksItems.length && !clazz.get().equals(entry.getName())) {
					clazz.set(entry.getName());
					FlameConfig.field.append("Potential item class: " + clazz.get() + "\n");
				} else if (checks.size() == (checksItems.length + itemStackChecks.length) && !clazzStack.get().equals(entry.getName())) {
					clazzStack.set(entry.getName());
					FlameConfig.field.append("Potential item stack class: " + clazzStack.get() + "\n");
				}
			}, name -> name.endsWith(".class") && !name.startsWith("com.tfc"));
			HashMap<String, String> classes = new HashMap<>();
			classes.put("Item", clazz.get());
			classes.put("ItemStack", clazzStack.get());
			return classes;
		} catch (Throwable ignored) {
		}
		return null;
	}
	
	public static String findBlockClass(File versionDir) {
		try {
			AtomicReference<String> clazz = new AtomicReference<>("null");
			Utils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checks = new HashMap<>();
				Utils.forEachLine(sc, line -> {
					for (String s : checksBlocks) {
						Utils.checkLine(s, checks, line);
					}
					if (checks.size() == checksBlocks.length) {
						if (!clazz.get().equals(entry.getName())) {
							clazz.set(entry.getName());
							FlameConfig.field.append("Potential block class: " + clazz.get() + "\n");
						}
					}
				});
			}, name -> name.endsWith(".class") && !name.startsWith("com.tfc"));
			return clazz.get();
		} catch (Throwable ignored) {
		}
		return null;
	}
}
