package com.tfc.API.flamemc.blocks;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flame.utils.reflection.Fields;
import com.tfc.API.flame.utils.reflection.Methods;
import com.tfc.API.flamemc.Registry;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BlockPropeteries {
	private Registry.ResourceLocation location;
	private Object properties;
	
	public BlockPropeteries(Registry.ResourceLocation location, Object propertiesSource) {
		this.location = location;
		try {
			for (Field f : propertiesSource.getClass().getFields()) {
				try {
					if (f.getType().equals(Main.getBlockPropertiesClass())) {
						properties = f.get(propertiesSource);
					}
				} catch (Throwable err) {
					Logger.logErrFull(err);
				}
			}
		} catch (Throwable ignored) {
		}
		if (properties == null) {
			Object material = null;
			Object materialColor = null;
			Class<?> matClass = null;
			Class<?> matColorClass = null;
			try {
				for (Constructor constructor : Main.getBlockPropertiesClass().getConstructors()) {
					matClass = constructor.getParameterTypes()[0];
					matColorClass = constructor.getParameterTypes()[1];
				}
			} catch (Throwable ignored) {
			}
			try {
				for (Field f : propertiesSource.getClass().getFields()) {
					try {
						if (f.getType().equals(matClass)) {
							f.setAccessible(true);
							material = f.get(propertiesSource);
						} else if (f.getType().equals(matColorClass)) {
							f.setAccessible(true);
							materialColor = f.get(propertiesSource);
						}
					} catch (Throwable ignored) {
					}
				}
				properties = Main.getBlockPropertiesClass().getConstructors()[0].newInstance(material, materialColor);
			} catch (Throwable ignored) {
			}
			if (properties == null) {
				boolean hasReachedJustBlock = false;
				for (Method m : Methods.getAllMethods(Main.getBlockPropertiesClass())) {
					if (properties == null) {
						for (Field field1 : Fields.getAllFields(propertiesSource.getClass())) {
							if (properties == null) {
								for (Field field2 : Fields.getAllFields(propertiesSource.getClass())) {
									if (properties == null) {
										try {
											if (m.getParameterTypes().length == 2) {
												field1.setAccessible(true);
												if (m.getParameterTypes()[0].isInstance(field1.get(propertiesSource))) {
													field2.setAccessible(true);
													if (m.getParameterTypes()[0].isInstance(field2.get(propertiesSource))) {
														properties = m.invoke(null, field1.get(propertiesSource), field2.get(propertiesSource));
													}
												}
											} else if (!hasReachedJustBlock && m.getParameterTypes().length == 1) {
												if (m.getParameterTypes()[0].isInstance(propertiesSource)) {
													hasReachedJustBlock = true;
													properties = m.invoke(null, propertiesSource);
												}
											}
										} catch (Throwable ignored) {
//											Logger.logLine(m.getName() + Arrays.asList(m.getParameterTypes()));
//											Logger.logLine(propertiesSource);
										}
									} else {
										break;
									}
								}
							} else {
//								Logger.logLine(properties);
								break;
							}
						}
					} else {
						break;
					}
				}
				//It should never get to this point, but it's here just incase.
				if (properties == null) {
					Object prop = null;
					try {
						for (Method m : Main.getBlockPropertiesClass().getMethods()) {
							try {
								if (prop == null) {
									m.setAccessible(true);
									Object test = m.invoke(null, propertiesSource);
									if (test.getClass().getName().equals(Main.getBlockPropertiesClass().getName())) {
										prop = test;
									}
								}
							} catch (Throwable ignored) {
							}
						}
					} catch (Throwable ignored) {
					}
					this.properties = prop;
					if (properties == null) {
						try {
							for (Method m : Methods.getAllMethods(Main.getBlockPropertiesClass())) {
								try {
									m.setAccessible(true);
									if (properties == null) {
										if (m.getParameterTypes().length >= 1) {
											if (m.getParameterTypes()[0].isInstance(propertiesSource)) {
												if (m.getReturnType().equals(Main.getBlockPropertiesClass())) {
													this.properties = m.invoke(null, propertiesSource);
												}
											}
										}
									}
								} catch (Throwable ignored) {
								}
							}
						} catch (Throwable ignored) {
						}
					}
				}
			}
		}
		rename(location);
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
