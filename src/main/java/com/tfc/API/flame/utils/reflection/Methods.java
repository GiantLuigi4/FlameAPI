package com.tfc.API.flame.utils.reflection;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;
import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.utils.BiObject;
import com.tfc.utils.ScanningUtils;
import com.tfc.utils.TriObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

//TODO: javadocs
@Unmodifiable
public class Methods {
	public static <A> A invoke(Object object, String name, Object[] args, Class<A> expectedReturnVal) {
		try {
			return (A) forNameAndParams(object.getClass(), name, args).invoke(object, (Object) args);
		} catch (Throwable ignored) {
			return null;
		}
	}
	
	public static <A> A invokeStatic(Class<?> target, String name, Object[] args, Class<A> expectedReturnVal) {
		try {
			return (A) forNameAndParams(target, name, args).invoke(null, (Object) args);
		} catch (Throwable ignored) {
			return null;
		}
	}
	
	public static ArrayList<Method> getAllMethods(Class<?> clazz) {
		ArrayList<Method> allMethods = new ArrayList<>();
		Method[] methods = clazz.getMethods();
		if (methods != null) {
			allMethods.addAll(Arrays.asList(methods));
		}
		methods = clazz.getDeclaredMethods();
		if (methods != null) {
			allMethods.addAll(Arrays.asList(methods));
		}
		return allMethods;
	}
	
	public static ArrayList<Method> getAllMethodsWithParams(Class<?> clazz, Object[] params) {
		ArrayList<Method> allMethods = new ArrayList<>();
		Method[] methods = clazz.getMethods();
		ParamList params1 = new ParamList(params);
		if (methods != null) {
			for (Method m : methods) {
				if (new ParamList(m.getParameterTypes()).equals(params1)) {
					allMethods.add(m);
				}
			}
		}
		methods = clazz.getDeclaredMethods();
		if (methods != null) {
			for (Method m : methods) {
				if (new ParamList(m.getParameterTypes()).equals(params1)) {
					allMethods.add(m);
				}
			}
		}
		return allMethods;
	}
	
	public static Method forName(Class<?> clazz, String name) {
		for (Method method : getAllMethods(clazz)) {
			if (method.getName().equals(name)) {
				return method;
			}
		}
		return null;
	}
	
	public static Method forNameAndParams(Class<?> clazz, String name, Object[] params) {
		ParamList list = new ParamList(params);
		for (Method method : getAllMethods(clazz)) {
			if (method.getName().equals(name)) {
				if (list.equals(new ParamList(method.getParameterTypes()))) {
					return method;
				}
			}
		}
		return null;
	}
	
	public static void forEach(Class<?> target, Consumer<Method> function) {
		getAllMethods(target).forEach(function);
	}

	/**
	 * Searched a method in a class
	 * @param classToSearch the class inside the method is
	 * @param totalParameters the number of arguments the method has
	 * @param returnClass the return class of the metho ( int.class, void.class or for non primitives Class.forName(className) )
	 * @param classes an Array containing the class names of the parameters
	 * @return a {@link BiObject} containing the args and the method name and the method
	 */
	private static BiObject<String, Method> searchAndGettMethodInfos(String classToSearch, int totalParameters, Class<?> returnClass, String[] classes) {
		try {
			String methodToSearch;
			String argumentsToSearch;
			int argsMatched;
			for (Method possibleMethod : Methods.getAllMethods(Class.forName(ScanningUtils.toClassName(classToSearch)))) {
				argsMatched = 0;
				StringBuilder parameters = new StringBuilder();
				StringBuilder arguments = new StringBuilder();
				if (possibleMethod.getParameterCount() == totalParameters) {
					for (Class<?> param : possibleMethod.getParameterTypes()) {
						for (String s : classes) {
							if (param.getName().equals(ScanningUtils.toClassName(s))) {
								parameters.append(param.getName()).append(" var").append(argsMatched);
								arguments.append("var").append(argsMatched);
								if (argsMatched != totalParameters - 1) {
									parameters.append(", ");
									arguments.append(", ");
								}
								argsMatched++;
							}
						}
					}
					if (argsMatched == totalParameters && possibleMethod.getReturnType() == returnClass) {
						methodToSearch = possibleMethod.getName() + "(" + parameters + ")";
						argumentsToSearch = "new Object[]{" + arguments + "}";
						Logger.logLine(methodToSearch + "$/$" + argumentsToSearch);
						return new BiObject<>(methodToSearch + "$/$" + argumentsToSearch, possibleMethod);
					}
				}
			}
		} catch (Throwable ignored) {
		}
		return null;
	}

	/**
	 * Shortcut to {@link #searchAndGettMethodInfos(String, int, Class, String[]).getObject2()}
	 * @return the method
	 * @see #getMethodNameAndArgs(String, int, Class, String[])
	 */
	public static Method searchMethod(String classToSearch, int totalParameters, Class<?> returnClass, String[] classes) {
		return Objects.requireNonNull(searchAndGettMethodInfos(classToSearch, totalParameters, returnClass, classes)).getObject2();
	}

	/**
	 * Shortcut to {@link #searchAndGettMethodInfos(String, int, Class, String[]).getObject1()}
	 * @return the args + the params
	 * @see #getMethodNameAndArgs(String, int, Class, String[])
	 */
	public static String getMethodNameAndArgs(String classToSearch, int totalParameters, Class<?> returnClass, String[] classes) {
		return Objects.requireNonNull(searchAndGettMethodInfos(classToSearch, totalParameters, returnClass, classes)).getObject1();
	}

	private static class ParamList {
		private final Object[] params;
		
		public ParamList(Object[] params) {
			for (int i = 0; i < params.length; i++) {
				if (!params[i].getClass().equals(Class.class)) {
					params[i] = params[i].getClass();
				}
			}
			this.params = params;
		}
		
		@Override
		public boolean equals(Object obj) {
			return
					obj.getClass().equals(this.getClass()) &&
							Arrays.equals(((ParamList) obj).params, this.params);
		}
	}
}
