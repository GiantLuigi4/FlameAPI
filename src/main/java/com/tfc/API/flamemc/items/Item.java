package com.tfc.API.flamemc.items;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Item {
	private static final ArrayList<Constructor<?>> itemConstructors = getItemConstructors();
	
	public static void init() {
		Object o = itemConstructors;
	}
	
	private static ArrayList<Constructor<?>> getItemConstructors() {
		ArrayList<Constructor<?>> itemConstructors = new ArrayList<>();
		try {
			Logger.logLine("Block Item Class Constructors");
			for (Constructor<?> c : Class.forName(ScanningUtils.toClassName(Main.getItemClass())).getConstructors()) {
				String params = "";
				int num = 0;
				for (Class<?> clazz : c.getParameterTypes()) {
					params += num + ": " + clazz.getName() + ", ";
					num++;
					if (!clazz.getName().equals(ScanningUtils.toClassName(Main.getItemClass())) && clazz.getName().contains(ScanningUtils.toClassName(Main.getItemClass()))) {
						ItemProperties.itemProperties = clazz;
					}
				}
				itemConstructors.add(c);
				Logger.logLine(params.substring(0, params.length() - 2));
			}
		} catch (Throwable err) {
			Logger.logErrFull(err);
		}
		return itemConstructors;
	}
	
	public static Object instance(ItemProperties properties) {
		for (Constructor<?> constructor : itemConstructors) {
			Logger.logLine(constructor);
			Logger.logLine("arg1: " + properties.unwrap());
			if (constructor.getParameterTypes().length == 2) {
//				if (constructor.getParameterTypes()[0].equals(Main.getBlockPropertiesClass())) {
				try {
					constructor.setAccessible(true);
					Object o = constructor.newInstance(properties.unwrap());
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
