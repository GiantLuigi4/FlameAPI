package entries.FlameAPI;

import com.tfc.API.flamemc.FlameASM;
import com.tfc.flame.FlameConfig;
import com.tfc.flame.IFlameMod;
import com.tfc.flamemc.FlameLauncher;
import com.tfc.hacky_class_stuff.ASM.ASM;
import com.tfc.hacky_class_stuff.BlockClass;
import net.minecraft.client.ClientBrandRetriever;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

public class Main implements IFlameMod {
	private static final HashMap<String, String> registryClassNames = new HashMap<>();
	private static final HashMap<String, Class<?>> registryClasses = new HashMap<>();
	private static String gameDir;
	private static String version;
	private static String assetVersion; //for snapshots
	private static final String execDir = System.getProperty("user.dir");

	private static String mainRegistry = "";

	private static HashMap<String, String> registries = null;

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
	
	
	public static HashMap<String, String> getRegistries() {
		return (HashMap<String, String>) registries.clone();
	}
	
	@Override
	public void preinit(String[] args) {
		FlameLauncher.getLoader().getReplacementGetters().put("com.tfc.FlameAPI.Block", BlockClass::getBlock);
		FlameLauncher.getLoader().getAsmAppliers().put("com.tfc.FlameAPI.ASM", ASM::apply);

//		try {
//			FlameLauncher.addClassReplacement("replacements.FlameAPI.net.minecraft.client.ClientBrandRetriever");
//		} catch (Throwable err) {
//			FlameConfig.logError(err);
//		}
		
		try {
			FlameASM.addField("net.minecraft.client.ClientBrandRetriever", "brand", "flamemc", FlameASM.AccessType.PUBLIC);
			FlameConfig.field.append(ClientBrandRetriever.class.getFields().length + "\n");
		} catch (Throwable err) {
			FlameConfig.logError(err);
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
					isAssetIndex = false;
				}
			}
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		
		try {
			registries = (HashMap<String, String>) Class.forName("RegistryHelper").getMethod("findRegistryClass", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			FlameConfig.field.append("PreInit Registries:" + registries.size() + "\n");
			mainRegistry = (String) Class.forName("RegistryHelper").getMethod("findMainRegistry", HashMap.class, File.class).invoke(null, registries, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			FlameConfig.field.append("Main Registry Class:" + mainRegistry + "\n");
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
	}
	
	@Override
	public void init(String[] args) {
	}
	
	@Override
	public void postinit(String[] args) {
		FlameConfig.field.append("PostInit Registries:" + registries.size() + "\n");
		Iterator<String> names = registries.keySet().iterator();
		Iterator<String> classes = registries.values().iterator();
		for (int i = 0; i < registries.size(); i++) {
			try {
				String name = names.next();
				String clazz = classes.next();
				clazz = (clazz.replace(".class", "").replace("/", "."));
				FlameConfig.field.append("Registry class:   " + name + ":" + clazz + "\n");
				registryClassNames.put(name, clazz);
//				registryClasses.put(name,Class.forName(clazz));
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
	}
}
