package com.tfc.API.flamemc;

import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;

public class Registry {
	public static Object register(ResourceLocation resourceLocation, RegistryType type) {
		String rlClass = ScanningUtils.toClassName(Main.getResourceTypeClasses().get("ResourceLocation"));
		String typeClass = ScanningUtils.toClassName(Main.getResourceTypeClasses().get(type.name));
//		try {
//			for (Field f : Class.forName(ScanningUtils.toClassName(Main.getMainRegistry())).getFields()) {
//				FlameConfig.field.append(f.getName()+":"+f.get(null).getClass().getFields().length+" fields\n");
//				for (Field f2 : f.get(null).getClass().getDeclaredFields()) {
//					try {
//						if (f2.get(null).getClass().getName().equals(ScanningUtils.toClassName(Main.getResourceTypeClasses().get(type.name)))) {
//							Object registry = f.get(null);
//							for (Method m : registry.getClass().getMethods()) {
//								int params = 0;
//								for (Class<?> c : m.getParameterTypes()) {
//									if (c.getName().equals(rlClass)) {
//										params++;
//									} else if (c.getName().equals(typeClass)) {
//										params++;
//									} else if (c.getName().equals(Main.getMainRegistry())) {
//										params++;
//									} else {
//										params--;
//									}
//								}
//								if (params == 3) {
//									Constructor<?>[] constructors = Class.forName(rlClass).getConstructors();
//									for (Constructor<?> constructor:constructors) {
//										Object[] objects = new Object[constructor.getParameterTypes().length];
//										int i=0;
//										for (Class<?> c2 : constructor.getParameterTypes()) {
//											for (Field f3 : c2.getFields()) {
//												if (c2.getClass().equals(f3.get(null).getClass())) {
//													objects[i] = f3.get(null);
//												}
//											}
//											i++;
//											try {
//												return m.invoke(null,f.get(null),resourceLocation,constructors,objects);
//											} catch (Throwable ignored) {
//												FlameConfig.field.append();
//											}
//										}
//									}
//								}
//							}
//						}
//					} catch (Throwable ignored) {
//					}
//				}
//			}
//		} catch (Throwable err) {
//			throw new RuntimeException(err);
//		}
		return null;
	}
	
	//If you want to use this with vanilla methods, you need to call ".unwrap()" on the ResourceLocation object this creates.
	public static ResourceLocation constructResourceLocation(String name) {
		return new ResourceLocation(name);
	}
	
	public enum RegistryType {
		BLOCK("Block"),
		ITEM("Item");
		
		final String name;
		
		RegistryType(String name) {
			this.name = name;
		}
	}
	
	public static class ResourceLocation {
		private final Object location;
		
		public ResourceLocation(String namespace, String location) {
			Object value = null;
			try {
				Constructor<?> constructor = Class.forName(ScanningUtils.toClassName(Main.getResourceTypeClasses().get("ResourceLocation"))).getConstructor(String.class, String.class);
				value = constructor.newInstance(namespace, location);
			} catch (Throwable err) {
				throw new RuntimeException(err);
			}
			this.location = value;
		}
		
		public ResourceLocation(String name) {
			Object value = null;
			try {
				Constructor<?> constructor = Class.forName(ScanningUtils.toClassName(Main.getResourceTypeClasses().get("ResourceLocation"))).getConstructor(String.class);
				value = constructor.newInstance(name);
			} catch (Throwable err) {
				throw new RuntimeException(err);
			}
			this.location = value;
		}
		
		public String getNamespace() {
			return location.toString().split(":", 2)[0].replace(":", "");
		}
		
		public String getPath() {
			return location.toString().split(":", 2)[1].replace(":", "");
		}
		
		@Override
		public String toString() {
			return location.toString();
		}
		
		public Object unWrap() {
			return location;
		}
	}
}
