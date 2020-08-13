package entries.FlameAPI;

import com.tfc.flame.FlameConfig;
import com.tfc.flame.IFlameMod;
import com.tfc.flamemc.FlameLauncher;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

public class Main implements IFlameMod {
	private static final HashMap<String, String> registryClassNames = new HashMap<>();
	private static final HashMap<String, Class<?>> registryClasses = new HashMap<>();
	private static String gameDir;
	private static String version;
	private static String execDir = System.getProperty("user.dir");
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
	
	@Override
	public void preinit(String[] args) {
		try {
			FlameLauncher.addClassReplacement("replacements.FlameAPI.net.minecraft.client.ClientBrandRetriever");
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		
		try {
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
				}
			}
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		
		try {
			registries = (HashMap<String, String>) Class.forName("RegistryHelper").getMethod("findRegistryClass", File.class).invoke(null, new File(execDir + "\\versions\\" + version + "\\" + version + ".jar"));
			FlameConfig.field.append("PreInit Registries:" + registries.size() + "\n");
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
