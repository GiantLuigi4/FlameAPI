package com.tfc.API.flamemc.items;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flame.utils.reflection.Fields;
import com.tfc.API.flame.utils.reflection.Methods;
import com.tfc.API.flamemc.Registry;

import java.lang.reflect.Method;

public class ItemProperties {
	protected static String itemProperties = null;
	private Registry.ResourceLocation location;
	private Object properties;
	
	public ItemProperties(Registry.ResourceLocation location, Object propertiesSource) {
		this.location = location;
		try {
			for (Method m : Methods.getAllMethods(Class.forName(itemProperties))) {
				try {
					m.setAccessible(true);
					if (properties == null) {
						if (m.getParameterTypes().length == 1) {
							if (m.getReturnType().equals(Class.forName(itemProperties))) {
								this.properties = m.invoke(null, propertiesSource);
							}
						}
					}
				} catch (Throwable err) {
					Logger.logErrFull(err);
				}
			}
		} catch (Throwable ignored) {
		}
		rename(location);
	}
	
	public Registry.ResourceLocation getLocation() {
		return location;
	}
	
	public Object unwrap() {
		return this.properties;
	}
	
	public ItemProperties rename(Registry.ResourceLocation newLoc) {
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
