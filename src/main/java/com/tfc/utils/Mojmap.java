package com.tfc.utils;

import com.tfc.mappings.structure.Class;
import com.tfc.mappings.structure.MojmapHolder;
import com.tfc.mappings.types.Mojang;
import entries.FlameAPI.Main;

import java.util.HashMap;

public class Mojmap {
	private static final HashMap<String, MojmapHolder> mojmapHolderHashMap = new HashMap<>();
	
	public static void load(String version) {
		mojmapHolderHashMap.put(version, Mojang.generate(version));
	}
	
	public static Class getClassObsf(String version, String name) {
		if (!mojmapHolderHashMap.containsKey(version))
			load(version);
		return mojmapHolderHashMap.get(version).getFromPrimaryName(name);
	}
	
	/**
	 * Gets the obsfucated name of a mojmap class for the current version
	 *
	 * @param name the name of the class
	 * @return the obsfucated name
	 */
	public static Class getClassObsf(String name) {
		return getClassObsf(Main.getVersionMap(), name);
	}
	
	public static Class getClassMojmap(String version, String name) {
		if (!mojmapHolderHashMap.containsKey(version))
			load(version);
		return mojmapHolderHashMap.get(version).getFromSecondaryName(name);
	}
	
	public static Class getClassMojmap(String name) {
		return getClassMojmap(Main.getVersionMap(), name);
	}
}
