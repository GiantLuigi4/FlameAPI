package com.tfc.API.flamemc.items;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flamemc.Registry;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class BlockItem {
	private static final ArrayList<Constructor<?>> blockItemConstructors = getBlockItemConstructors();
	
	public static void init() {
		Object o = blockItemConstructors;
	}
	
	private static ArrayList<Constructor<?>> getBlockItemConstructors() {
		ArrayList<Constructor<?>> blockItemConstructors = new ArrayList<>();
		try {
			Logger.logLine("Block Item Class Constructors");
			for (Constructor<?> c : Class.forName(ScanningUtils.toClassName(Main.getBlockItemClass())).getConstructors()) {
				String params = "";
				int num = 0;
				for (Class<?> clazz : c.getParameterTypes()) {
					params += num + ": " + clazz.getName() + ", ";
					num++;
					if (!clazz.getName().equals(ScanningUtils.toClassName(Main.getItemClass())) && clazz.getName().contains(ScanningUtils.toClassName(Main.getItemClass()))) {
						ItemProperties.itemProperties = clazz;
					}
				}
				blockItemConstructors.add(c);
				Logger.logLine(params.substring(0, params.length() - 2));
			}
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		return blockItemConstructors;
	}
	
	public static Object instance(Registry.RegistryObject<?> block, ItemProperties properties) {
		for (Constructor<?> constructor : blockItemConstructors) {
			Logger.logLine(constructor);
			Logger.logLine("arg1: " + block.get());
			Logger.logLine("arg2: " + properties.unwrap());
			if (constructor.getParameterTypes().length == 2) {
//				if (constructor.getParameterTypes()[0].equals(Main.getBlockPropertiesClass())) {
				try {
					constructor.setAccessible(true);
					Object o = constructor.newInstance(block.get(), properties.unwrap());
					Logger.logLine(o);
					return o;
				} catch (Throwable err) {
					Logger.logErrFull(err);
				}
//				}
			}
		}
		return null;
	}
}
