package com.tfc.API.flamemc;

import com.tfc.flame.FlameConfig;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class Registry {
	
	private static String getRegistryClass(String type) {
		return "minecraft:" + type.toLowerCase() + "s";
	}
	
	public static Object registerBlock(ResourceLocation resourceLocation, RegistryType type) {
		String rlClass = ScanningUtils.toClassName(Main.getResourceTypeClasses().get("ResourceLocation"));
		String mainRegistryClass = ScanningUtils.toClassName(Main.getMainRegistry());
		String typeClass = ScanningUtils.toClassName(Main.getResourceTypeClasses().get(type.name));
		String registry = ScanningUtils.toClassName(Main.getRegistries().get(getRegistryClass(type.name)));
		try {
			Class<?> toRegister = Class.forName(typeClass);
			Constructor<?> constructor = toRegister.getConstructors()[0];
			Class<?> properties = null;
			for (Class<?> clazz : toRegister.getClasses()) {
				if (clazz.equals(constructor.getParameterTypes()[0])) {
					properties = clazz;
				}
			}
			Method matInstance = null;
			Object material = null;
			try {
				for (Method method : properties.getDeclaredMethods()) {
					if (method.getParameterTypes().length == 1) {
						Class<?> materialClass = method.getParameterTypes()[0];
						//TODO:Get this to work on every version.
						if (Main.getVersion().equals("1.15.2-flame")) {
							materialClass = Class.forName("coo");
						}
						FlameConfig.field.append("Wants class: " + materialClass.getName() + "\n");
						for (Field test : materialClass.getDeclaredFields()) {
							try {
								test.setAccessible(true);
								material = test.get(null);
								break;
							} catch (Throwable err) {
//								FlameConfig.field.append("Accessing class: " + materialClass.getName()+".\n");
//								try {
//									FlameConfig.field.append("Field: " + test.getName()+".\n");
//								} catch (Throwable ignored) {}
							}
							if (material != null && material.getClass().equals(materialClass)) {
								matInstance = method;
								break;
							} else {
								if (material != null) {
									FlameConfig.field.append("Got class: " + material.getClass().getName() + "\n");
									FlameConfig.field.append("Wants class: " + materialClass.getName() + "\n");
								}
								material = null;
							}
						}
						if (material != null && material.getClass().equals(materialClass)) {
							FlameConfig.field.append("Got class: " + material.getClass().getName() + "\n");
							break;
						}
					}
				}
			} catch (Throwable err) {
				FlameConfig.logError(err);
				throw new RuntimeException(err);
			}
			ArrayList<Field> allFields = new ArrayList<>();
			allFields.addAll(Arrays.asList(Class.forName(mainRegistryClass).getDeclaredFields()));
//			allFields.addAll(Arrays.asList(Class.forName(mainRegistryClass).getFields()));
			for (Field registryField : allFields) {
				FlameConfig.field.append("registry field: " + registryField.getName() + "\n");
				if (!registryField.getName().equals("f")) {
					try {
						registryField.setAccessible(true);
//					if (registryField.isAccessible()) {
//						for (Field potentialRegister : registryField.get(null).getClass().getFields()) {
//							FlameConfig.field.append("potential register: " + potentialRegister.getName() + "\n");
//							try {
//								potentialRegister.setAccessible(true);
//								Object potentialRegisterObject = potentialRegister.get(null);
//								if (potentialRegisterObject.getClass().getName().equals(registry)) {
//									for (Method method : potentialRegisterObject.getClass().getMethods()) {
//										if (method.getName().equals("a")) {
//											FlameConfig.field.append("Registering block: "+resourceLocation.toString()+"\n");
//											return method.invoke(resourceLocation.toString(),matInstance.invoke(null,material));
//										}
//									}
//								}
//							} catch (Throwable err) {
//								FlameConfig.logError(err);
//							}
//						}
						ArrayList<Method> methods = new ArrayList<>();
						methods.addAll(Arrays.asList(registryField.get(null).getClass().getMethods()));
						methods.addAll(Arrays.asList(registryField.get(null).getClass().getDeclaredMethods()));
						methods.add(registryField.get(null).getClass().getEnclosingMethod());
						for (Method method : registryField.get(null).getClass().getMethods()) {
							try {
//							FlameConfig.field.append("method: " + method.getName() + "\n");
								method.setAccessible(true);
								if (method.getParameterTypes().length == 1) {
									try {
										Object o = method.getParameterTypes()[1];
									} catch (Throwable err) {
										if (method.getParameterTypes()[0].getName().equals(rlClass)) {
											Register blocks = new Register(method.invoke(registryField.get(null), new ResourceLocation("blocks").unWrap()));
											return blocks.register(resourceLocation, null);
										}
									}
								}
							} catch (Throwable ignored) {
							}
						}
//					}
					} catch (Throwable err) {
						FlameConfig.logError(err);
					}
				}
			}
		} catch (Throwable err) {
			FlameConfig.logError(err);
			throw new RuntimeException(err);
		}
		return null;
	}
	
	//If you want to use this with vanilla methods, you need to call ".unwrap()" on the ResourceLocation object this creates.
	public static ResourceLocation constructResourceLocation(String name) {
		return new ResourceLocation(name);
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
		public String toString() {
			return location.toString();
		}
		
		public Object unWrap() {
			return location;
		}
	}
}
