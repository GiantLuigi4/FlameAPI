package com.tfc.utils;

import mappings.structure.Class;
import mappings.structure.MojmapHolder;
import mappings.types.Mojang;

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
	
	public static Class getClassMojmap(String version, String name) {
		if (!mojmapHolderHashMap.containsKey(version))
			load(version);
		return mojmapHolderHashMap.get(version).getFromSecondaryName(name);
	}
}
