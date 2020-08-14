package com.tfc.API.flamemc;

import entries.FlameAPI.Main;

public class Mapping {
	public static String getUnmappedFor(String name) {
		if (name.startsWith("net.minecraft.init.")) {
			String s1 = name.replace("net.minecraft.init.", "");
			s1 = s1.toLowerCase();
			return Main.getRegistries().get(s1).replace(".class", "").replace("/", ".");
		}
//		if (name.equals("net.minecraft.init.Items")) {
//			return Main.getRegistries().get("minecraft:items");
//		} else if (name.equals("net.minecraft.init.Blocks")) {
//			return Main.getRegistries().get("minecraft:blocks");
//		} else if (name.equals("net.minecraft.init.TileEntities")) {
//			return Main.getRegistries().get("minecraft:tile_entities");
//		} else if (name.equals("net.minecraft.init.Entities")) {
//			return Main.getRegistries().get("minecraft:entities");
//		} else if (name.equals("net.minecraft.init.Enchantments")) {
//			return Main.getRegistries().get("minecraft:enchantments");
//		}
		return name;
	}
	
	public static String getMappedClassForRegistry(String name) {
		if (name.startsWith("minecraft:")) {
			String s1 = name.replace("minecraft:", "");
			s1 = s1.toUpperCase().replace(s1.substring(1).toUpperCase(), s1.substring(1));
			return "net.minecraft.init." + s1;
		}
		return name;
	}
}
