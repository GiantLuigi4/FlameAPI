package com.tfc.utils;

import com.tfc.flame.FlameConfig;
import entries.FlameAPI.Main;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * just to reduce code redundancy, lol
 * also, it can make files vastly smaller if done correctly
 */
public class ScanningUtils {

	public static boolean isVersionGreaterThan12 = false;
	public static boolean isVersionLessThan12 = false;

	public static void checkVersion() {
		String mcAssetVer = Main.getAssetVersion();                                //like 1.16, 1.15 or for 1.7.10 and before, the same version number
		String mcMajorVer = mcAssetVer.substring(mcAssetVer.indexOf(".") + 1);    //I get everything after 1. (aka 16, 15 or 7.10)
		if (mcMajorVer.contains(".")) {
			mcMajorVer = mcMajorVer.substring(0, mcMajorVer.indexOf("."));        //if there is still a dot, make another substring, so it actually get 7 in case of 7.10
		}
		isVersionGreaterThan12 = Integer.parseInt(mcMajorVer) > 12;
		isVersionLessThan12 = !isVersionGreaterThan12;            // 11 is just a placeholder, still gotta check
	}

	public static void forAllFiles(JarFile file, BiConsumer<Scanner, JarEntry> textConsumer, Function<String, Boolean> fileValidator) {
		file.stream().forEach(f -> {
			if (fileValidator.apply(f.getName())) {
				try {
					InputStream stream = file.getInputStream(f);
					Scanner sc = new Scanner(stream);
					textConsumer.accept(sc, f);
					sc.close();
					stream.close();
				} catch (Throwable ignored) {
				}
			}
		});
	}
	
	public static void forEachLine(Scanner sc, Consumer<String> textConsumer) {
		while (sc.hasNextLine()) {
			textConsumer.accept(sc.nextLine());
		}
	}
	
	public static boolean checkLine(String query, HashMap<String, Boolean> checks, String line) {
		if (!checks.containsKey(query)) {
			if (line.contains(query)) {
				checks.put(query, true);
				return true;
			}
		}
		return false;
	}

	public static void checkRegistry(int checksLength, int arrayLength, HashMap<String, String> registries, String registryName, String entryName) {
		if (checksLength == arrayLength) {
			registries.put("minecraft:" + registryName, entryName);
			//FlameConfig.field.append(registryName.toUpperCase() + " registry class:" + entryName + "\n");
		}
	}

	public static void checkGenericClass(int checksLength, int arrayLength, AtomicReference<String> clazz, String genericName, String entryName) {
		if (checksLength == arrayLength && !clazz.get().equals(entryName)) {
			clazz.set(entryName);
			FlameConfig.field.append("Potential " + genericName + "class: " + clazz.get() + "\n");
		}
	}

	public static String toClassName(String s) {
		return s.replace(".class", "").replace("/", ".");
	}
}
