package com.tfc.API.flamemc;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.API.flame.utils.reflection.Methods;
import com.tfc.API.flamemc.items.BlockItem;
import com.tfc.API.flamemc.items.ItemProperties;
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
	
	public static RegistryObject<?> register(ResourceLocation resourceLocation, RegistryType type, Object toRegister) {
		if (toRegister == null) {
			throw new RuntimeException(new NullPointerException("The object being registered is null."));
		}
		if (registryHash.contains(type, resourceLocation)) {
			throw new RuntimeException(new IllegalAccessException("Can not register two " + type.name + "s" + " to the same register."));
		}
		if (type == RegistryType.BLOCK) {
			System.out.println(Main.getBlocks$register());
			if (Main.getBlocks$register() != null) {
				try {
					Method m = Main.getBlocks$register();
					m.setAccessible(true);
					Object returnVal = m.invoke(null, resourceLocation.toString(), toRegister);
					if (returnVal != null) {
						registryHash.add(type, resourceLocation, returnVal);
						return new RegistryObject<>(returnVal);
					} else
						throw new RuntimeException(new NullPointerException("Cannot register a null object... idk how you bypassed the first null check though."));
				} catch (Throwable err) {
					Logger.logErrFull(err);
					throw new RuntimeException(err);
				}
			}
		}
		try {
			Class.forName(ScanningUtils.toClassName(Main.getMainRegistry()));
			Class<?> registry = Class.forName(ScanningUtils.toClassName(Main.getRegistries().get(getRegistryClass(type.name))));
			AtomicReference<RegistryObject<?>> returnVal = new AtomicReference<>(null);
			ArrayList<Method> allMethods = Methods.getAllMethods(registry);
			FlameConfig.field.append(allMethods.size() + "\n");
			ArrayList<Throwable> errs = new ArrayList<>();
			for (Method method : allMethods) {
				if (method.getParameterTypes().length == 2) {
					if (returnVal.get() == null) {
						try {
							FlameConfig.field.append("method: " + method.getName() + "\n");
							FlameConfig.field.append("args: " + Arrays.toString(method.getParameterTypes()) + "\n");
							method.setAccessible(true);
							returnVal.set(new RegistryObject<>(method.invoke(null, resourceLocation.toString(), toRegister)));
							if (returnVal.get() != null) {
								registryHash.add(type, resourceLocation, returnVal.get());
								return returnVal.get();
							}
						} catch (Throwable err) {
							errs.add(err);
//							Logger.logErrFull(err);
						}
					}
				}
			}
			if (returnVal.get() == null)
				errs.forEach(Logger::logErrFull);
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
//			if (ScanningUtils.isVersionGreaterThan12) {
			try {
				if (ScanningUtils.isVersionLessThan12) {
					Methods.forEach(
							Class.forName(ScanningUtils.toClassName(Main.getMainRegistry())),
							method -> {
								try {
									method.setAccessible(true);
									method.invoke(null);
								} catch (Throwable ignored) {
								}
							}
					);
				} else {
					Class.forName(ScanningUtils.toClassName(Main.getMainRegistry()));
				}
			} catch (Throwable err) {
				Logger.logLine("Failed to find Main Registry class.");
				Logger.logLine("The game will probably crash.");
				try {
					Thread.sleep(1000);
				} catch (Throwable ignored) {
				}
			}
//			}
			Logger.logLine(ScanningUtils.toClassName(Main.getRegistries().get(getRegistryClass(registry.name))));
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
			Logger.logErrFull(err);
		}
		return null;
	}
	
	public static RegistryObject<?> registerBlockItem(ResourceLocation location, RegistryObject<?> block) {
		Logger.logLine(block.get());
		Object returnVal = null;
		if (registryHash.contains(RegistryType.ITEM, location)) {
			throw new RuntimeException(new IllegalAccessException("Can not register two " + RegistryType.BLOCK.name + "s" + " to the same register."));
		}
		try {
			returnVal = register(location, RegistryType.ITEM, BlockItem.instance(block, new ItemProperties(location, block.get())));
		} catch (Throwable ignored) {
		}
		if (returnVal == null) {
			try {
				Class.forName(ScanningUtils.toClassName(Main.getMainRegistry()));
				Class<?> registry = Class.forName(ScanningUtils.toClassName(Main.getRegistries().get(getRegistryClass(RegistryType.BLOCK.name))));
				ArrayList<Method> allMethods = Methods.getAllMethods(registry);
				FlameConfig.field.append(allMethods.size() + "\n");
				for (Method method : allMethods) {
					if (method.getParameterTypes().length == 1) {
						if (method.getParameterTypes()[0].equals(block.get().getClass())) {
							if (returnVal == null) {
								try {
									FlameConfig.field.append("method: " + method.getName() + "\n");
									FlameConfig.field.append("args: " + Arrays.toString(method.getParameterTypes()) + "\n");
									method.setAccessible(true);
									returnVal = new RegistryObject<>(method.invoke(null, block));
									registryHash.add(RegistryType.ITEM, location, returnVal);
								} catch (Throwable err) {
									Logger.logErrFull(err);
								}
							}
						}
					}
				}
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return new RegistryObject<>(returnVal);
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
		
		@Override
		public String toString() {
			return "RegistryObject:{" + object.toString() + "}";
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
