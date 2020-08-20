package com.tfc.API.flamemc;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.flame.FlameConfig;
import com.tfc.utils.ScanningUtils;
import com.tfc.utils.TriHashMap;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class Registry {
	
	private static String getRegistryClass(String type) {
		return "minecraft:" + type.toLowerCase() + "s";
	}
	
	private static final TriHashMap<RegistryType, ResourceLocation, Object> registryHash = new TriHashMap<>();
	
	public static Object registerBlock(ResourceLocation resourceLocation, RegistryType type) {
		if (registryHash.contains(type, resourceLocation)) {
			throw new RuntimeException(new IllegalAccessException("Can not register two " + type.name + "s" + " to the same register."));
		}
		try {
			Class.forName(ScanningUtils.toClassName(Main.getMainRegistry()));
			Class<?> registry = Class.forName(ScanningUtils.toClassName(Main.getRegistries().get(getRegistryClass(type.name))));
			AtomicReference<RegistryObject<?>> returnVal = new AtomicReference<>(null);
			ArrayList<Method> allMethods = new ArrayList<>(Arrays.asList(registry.getDeclaredMethods()));
//			allMethods.addAll(Arrays.asList(registry.getMethods()));
			FlameConfig.field.append(allMethods.size() + "\n");
			allMethods.forEach(method -> {
//				if (returnVal.get()!=null) {
				try {
					FlameConfig.field.append("method: " + method.getName() + "\n");
					FlameConfig.field.append("args: " + Arrays.toString(method.getParameterTypes()) + "\n");
					Object o = registry.getDeclaredField("a").get(null);
					FlameConfig.field.append(o.toString() + "\n");
					method.setAccessible(true);
					returnVal.set(new RegistryObject<>(method.invoke(null, resourceLocation.toString(), o)));
					FlameConfig.field.append(returnVal.get() + "\n");
					FlameConfig.field.append(resourceLocation.unWrap().toString() + "\n");
				} catch (Throwable err) {
					Logger.logErrFull(err);
				}
//				}
			});
			registryHash.add(type, resourceLocation, returnVal.get());
			return returnVal.get();
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		return null;
	}
	
	public static Object get(RegistryType registry, ResourceLocation name) {
		if (registryHash.contains(registry, name))
			return registryHash.get(registry, name);
		try {
			Class.forName(ScanningUtils.toClassName(Main.getMainRegistry()));
			Class<?> registryClass = Class.forName(ScanningUtils.toClassName(Main.getRegistries().get(getRegistryClass(registry.name))));
			for (Field f : registryClass.getDeclaredFields()) {
//				Logger.logLine(f.getName());
				try {
					f.setAccessible(true);
					Object item = f.get(null);
					for (Field itemField : item.getClass().getDeclaredFields()) {
						try {
							itemField.setAccessible(true);
							Object check = itemField.get(item);
//							Logger.logLine("itemField: " + check);
							if (check.toString().equals(name.toString())) {
								registryHash.add(registry, name, item);
								return item;
							}
						} catch (Throwable ignored) {
						}
					}
					if (item.toString().contains(name.toString())) {
						registryHash.add(registry, name, item);
						return item;
					}
				} catch (Throwable ignored) {
				}
			}
		} catch (Throwable err) {
			FlameConfig.logError(err);
		}
		return null;
	}
	
	//If you want to use this with vanilla methods, you need to call ".unwrap()" on the ResourceLocation object this creates.
	public static ResourceLocation constructResourceLocation(String name) {
		return new ResourceLocation(name);
	}
	
	public static class RegistryObject<A> {
		private final A object;
		
		protected RegistryObject(A object) {
			this.object = object;
		}
		
		public A get() {
			return object;
		}
	}
	
	public static class Register {
		private final Object register;
		
		public Register(Object register) {
			this.register = register;
		}
		
		public Object register(ResourceLocation resourceLocation, Object object) throws IllegalAccessException, InvocationTargetException {
			for (Method method : register.getClass().getMethods()) {
				if (method.getName().equals("a")) {
					method.setAccessible(true);
					FlameConfig.field.append("Registering block: " + resourceLocation.toString() + "\n");
					return method.invoke(register, resourceLocation.toString(), object);
				}
			}
			return null;
		}
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
		public boolean equals(Object obj) {
			return obj.toString().equals(this.toString());
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
