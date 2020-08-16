import com.tfc.flame.FlameConfig;
import com.tfc.utils.ScanningUtils;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

import static com.tfc.utils.ScanningUtils.isVersionGreaterThan12;

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
	
	public static HashMap<String, String> findItemClasses(File versionDir) {
		try {
			ScanningUtils.checkVersion();
			AtomicReference<String> clazzItem = new AtomicReference<>("null");
			AtomicReference<String> clazzStack = new AtomicReference<>("null");
			AtomicReference<String> clazzBlock = new AtomicReference<>("null");
			String[] version_checksBlocks = isVersionGreaterThan12 ? checksBlocks : checksBlocks12;
			ScanningUtils.forAllFiles(new JarFile(versionDir), (sc, entry) -> {
				HashMap<String, Boolean> checksItem = new HashMap<>();
				HashMap<String, Boolean> checksStack = new HashMap<>();
				HashMap<String, Boolean> checksBlock = new HashMap<>();
				ScanningUtils.forEachLine(sc, line -> {
					//Looks like this UUID is always the same in the Item class, this is perfect
					ScanningUtils.checkLine("CB3F55D3-645C-4F38-A497-9C13A33DB5CF", checksItem, line);
					/*for (String s : checksItems) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checksStack, line);
					}*/
					for (String s : itemStackChecks) {
						ScanningUtils.checkLine(s.replace("%classname%", ScanningUtils.toClassName(entry.getName())), checksStack, line);
					}
					for (String s : version_checksBlocks) {
						ScanningUtils.checkLine(s, checksBlock, line);
					}
				});
				if (!checksItem.isEmpty() && !clazzItem.get().equals(entry.getName())) {
					clazzItem.set(entry.getName());
					FlameConfig.field.append("Potential item class: " + clazzItem.get() + "\n");
				} else if (checksStack.size() == itemStackChecks.length && !clazzStack.get().equals(entry.getName())) {
					clazzStack.set(entry.getName());
					FlameConfig.field.append("Potential item stack class: " + clazzStack.get() + "\n");
				} else if (checksBlock.size() == version_checksBlocks.length && !clazzBlock.get().equals(entry.getName())) {
					clazzBlock.set(entry.getName());
					FlameConfig.field.append("Potential block class: " + clazzBlock.get() + "\n");
				}
			}, name -> name.endsWith(".class") && !name.startsWith("com.tfc"));
			HashMap<String, String> classes = new HashMap<>();
			classes.put("Item", clazzItem.get());
			classes.put("ItemStack", clazzStack.get());
			classes.put("Block", clazzBlock.get());
			return classes;
		} catch (Throwable ignored) {
		}
		return null;
	}
}
