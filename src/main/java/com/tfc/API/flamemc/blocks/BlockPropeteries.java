package com.tfc.API.flamemc.blocks;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flame.utils.reflection.Fields;
import com.tfc.API.flame.utils.reflection.Methods;
import com.tfc.API.flamemc.Registry;
import entries.FlameAPI.Main;

import java.lang.reflect.Method;

public class BlockPropeteries {
	private Registry.ResourceLocation location;
	private Object properties;
	
	public BlockPropeteries(Registry.ResourceLocation location, Object propertiesSource) {
		this.location = location;
//		for (Method m : Methods.getAllMethodsWithParams(
//				Main.getBlockPropertiesClass(),
//				new Object[]{propertiesSource.getClass()})
//		) {
		for (Method m : Methods.getAllMethods(Main.getBlockPropertiesClass())) {
			try {
				m.setAccessible(true);
				if (properties == null) {
					if (m.getParameterTypes().length == 1) {
						if (m.getReturnType().equals(Main.getBlockPropertiesClass())) {
							this.properties = m.invoke(null, propertiesSource);
						}
					}
				}
			} catch (Throwable err) {
				Logger.logErrFull(err);
			}
		}
		if (properties == null) {
			try {
				properties = Main.getBlockPropertiesClass().newInstance();
			} catch (Throwable ignored) {
			}
		}
		rename(location);
//		Object properties = null;
//		for (Method m : Main.getBlockPropertiesClass().getMethods()) {
//			try {
//				if (properties == null) {
//					m.setAccessible(true);
//					Object test = m.invoke(null, propertiesSource);
//					if (test.getClass().getName().equals(Main.getBlockPropertiesClass().getName())) {
//						properties = test;
//					}
//				}
//			} catch (Throwable ignored) {
//			}
//		}
//		this.properties = properties;
	}
	
	public Registry.ResourceLocation getLocation() {
		return location;
	}
	
	public Object unwrap() {
		return this.properties;
	}
	
	public BlockPropeteries rename(Registry.ResourceLocation newLoc) {
		if (!newLoc.equals(location)) {
			Fields.forEach(properties.getClass(), field -> {
				try {
					field.setAccessible(true);
					if (field.getType().equals(newLoc.unWrap().getClass())) {
						field.set(properties, newLoc.unWrap());
					}
				} catch (Throwable ignored) {
				}
			});
		}
		location = newLoc;
		return this;
	}
	
	@Override
	public String toString() {
		return "BlockPropeteries{" +
				"location=" + location +
				", properties=" + properties +
				'}';
	}
}
