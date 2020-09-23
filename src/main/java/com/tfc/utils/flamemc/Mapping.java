package com.tfc.utils.flamemc;

import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

public class Mapping {
	public static String getUnmappedFor(String name) {
		if (name.startsWith("net.minecraft.init.")) {
			String s1 = name.replace("net.minecraft.init.", "");
			s1 = s1.toLowerCase();
			s1 = "minecraft:" + s1;
			return ScanningUtils.toClassName(Main.getRegistries().get(s1));
		} else {
			try {
				return Mojmap.getClassObsf(name).getSecondaryName();
			} catch (Throwable ignored) {
			}
			try {
				return Intermediary.getClassObsf(name).getSecondaryName();
			} catch (Throwable ignored) {
			}
		}
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
