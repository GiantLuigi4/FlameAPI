package com.tfc.API.flamemc.blocks;

import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;

public class BlockInstancer {
	public static Object instanceBlock(BlockProperties properties) {
		for (Constructor<?> constructor : Main.blockConstructors) {
			if (constructor.getParameterTypes().length == 1) {
				if (constructor.getParameterTypes()[0].equals(Main.getBlockPropertiesClass())) {
					try {
						constructor.setAccessible(true);
						return constructor.newInstance(properties.unwrap());
					} catch (Throwable ignored) {
					}
				}
			}
		}
		return null;
	}
}
