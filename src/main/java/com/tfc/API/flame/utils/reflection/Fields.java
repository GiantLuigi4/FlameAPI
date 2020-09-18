package com.tfc.API.flame.utils.reflection;

import com.tfc.API.flame.annotations.ASM.Unmodifiable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;

@Unmodifiable
public class Fields {
	/**
	 * @param object      the object you want to access the field from
	 * @param fieldName   the name of the field
	 * @param returnClass the class in which the field is (if you don't have the class of the field, use {@link Object#getClass()})
	 * @param <A>         the class of the return value
	 * @return the value of the field
	 */
	public static <A> A get(Object object, String fieldName, Class<A> returnClass) {
		try {
			return (A) forName(object.getClass(), fieldName).get(object);
		} catch (Throwable err) {
			return null;
		}
	}
	
	/**
	 * @param target      the class you want to access the field from
	 * @param fieldName   the name of the field
	 * @param returnClass the class in which the field is (if you don't have the class of the field, use {@link Object#getClass()})
	 * @param <A>         the class of the return value
	 * @return the value of the field
	 */
	public static <A> A getStatic(Class<?> target, String fieldName, Class<A> returnClass) {
		try {
			return (A) forName(target, fieldName).get(null);
		} catch (Throwable err) {
			return null;
		}
	}
	
	/**
	 * @param object     the object you want to access the field from
	 * @param fieldName  the name of the field
	 * @param defaultVal the value to return if the field is not present (if you don't have the class of the field or your default's class is different from the field's class, use {@link Object#getClass()})
	 * @param <A>        the class of the return value
	 * @return the value of the field if present, else wise it returns the default value.
	 */
	public static <A> A getOrDefault(Object object, String fieldName, A defaultVal) {
		try {
			return (A) forName(object.getClass(), fieldName).get(object);
		} catch (Throwable err) {
			return defaultVal;
		}
	}
	
	/**
	 * @param clazz the class in which you want all fields of
	 * @return an array list containing all fields held by the class
	 */
	public static ArrayList<Field> getAllFields(Class<?> clazz) {
		ArrayList<Field> allFields = new ArrayList<>();
		Field[] fields = clazz.getFields();
		//From past experiences, I disagree that "fields" is always !=null, intelliJ
		if (fields != null)
			allFields.addAll(Arrays.asList(fields));
		fields = clazz.getDeclaredFields();
		if (fields != null)
			allFields.addAll(Arrays.asList(fields));
		return allFields;
	}
	
	/**
	 * @param clazz the class that you want to get a field from
	 * @param name  the name of the field you want
	 * @return any field with that name, weather it be static, private, final, protected, public, or a combination
	 */
	public static Field forName(Class<?> clazz, String name) {
		try {
			return clazz.getField(name);
		} catch (Throwable err) {
			try {
				return clazz.getDeclaredField(name);
			} catch (Throwable err2) {
				return null;
			}
		}
	}
	
	/**
	 * @param clazz    the class that you want to want to do a function for all fields of
	 * @param function a consumer (or lambda statement) which will be called for every field of the class
	 */
	public static void forEach(Class<?> clazz, Consumer<Field> function) {
		getAllFields(clazz).forEach(function);
	}
}
