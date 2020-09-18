package com.tfc.API.flame.utils.reflection;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
