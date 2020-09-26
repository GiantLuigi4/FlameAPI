package com.tfc.utils;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.utils.flamemc.Mojmap;

import java.lang.reflect.Method;
import java.util.ArrayList;

//this class was untouched for toooooooooooooooooooooooooooo long
public class ClassFindingUtils {
	public static boolean checkName(String name) {
		return
			name.endsWith(".class") &&
			!name.startsWith("com/tfc") &&
			!name.startsWith("com/github");
	}

	public static BiObject<String, Method>[] getMethodsForClass(String className, TriObject<String, String, ArrayList<BiObject<String, String>>>[] methodInfos) throws ClassNotFoundException {
		BiObject<String, Method>[] methods = new BiObject[methodInfos.length];
		int counter = 0;
		for (TriObject<String, String, ArrayList<BiObject<String, String>>> map : methodInfos) {
			BiObject<String, Method> localMethod = Mojmap.getMethodBetter(
					Class.forName(className),
					Mojmap.getClassMojmap(className),
					map.getObj1(),
					map.getObj2(),
					map.getObj3()
			);
			methods[counter] = localMethod;
			counter++;
		}
		return methods;
	}

	public static TriObject<String, String, ArrayList<BiObject<String, String>>>[] createTriObjArr(String[] obj1, String[] obj2, ArrayList<BiObject<String, String>>[] obj3) {
		TriObject<String, String, ArrayList<BiObject<String, String>>>[] array = new TriObject[obj1.length];
		for (int i = 0; i < obj1.length; i++)
			array[i] = new TriObject<>(obj1[i], obj2[i], obj3[i]);
		return array;
	}

	public static BiObject<String, String>[] createBiObjectArray(String... obj1Array) {
		BiObject<String, String>[] newArray = new BiObject[obj1Array.length];
		for (int i = 0; i < obj1Array.length; i++)
			newArray[i] = new BiObject<>(obj1Array[i], "var" + i);
		return newArray;
	}

	public static String[] createNBTSearchArray(String prefix, boolean descriptors, String... strings) {
		String[] newArray = new String[strings.length];
		if (!descriptors)
			for (int i = 0; i < strings.length; i++)
				newArray[i] = prefix + strings[i];
		else
			for (int i = 0; i < strings.length; i++) {
				String localString = strings[i];

				if (localString.equals("UUID"))
					localString = "java.util.UUID";
				else if (strings[i].equals("String"))
					localString = "java.lang.String";

				if (prefix.equals("get")) {
					if (!localString.equals("Id"))
						newArray[i] = "(Ljava/lang/String;)" + parse(localString);
					else
						newArray[i] = "()B";
				} else {
					newArray[i] = "(Ljava/lang/String;" + parse(localString) + ")V";
				}
			}
		return newArray;
	}

	private static String parse(String desc) {
		String lowerDesc = desc.toLowerCase();
		StringBuilder parsedArg = new StringBuilder();
		switch (lowerDesc) {
			case "int":
				parsedArg.append("I");
				break;
			case "long":
				parsedArg.append("J");
				break;
			case "byte":
				parsedArg.append("B");
				break;
			case "float":
				parsedArg.append("F");
				break;
			case "double":
				parsedArg.append("D");
				break;
			case "boolean":
				parsedArg.append("Z");
				break;
			case "char":
				parsedArg.append("C");
				break;
			case "short":
				parsedArg.append("S");
				break;
			default:
				parsedArg
						.append("L")
						.append(desc.replace(".", "/"))
						.append(";");
				break;
			}
		if (desc.startsWith("["))
			parsedArg.append("[]");
		return parsedArg.toString();
	}

	public static <S, M> BiObject<S, M>[] mergeBiObjectArrays(BiObject<S, M>[] firstArr, BiObject<S, M>[] secondArr) {
		BiObject<S, M>[] array = new BiObject[firstArr.length + secondArr.length];
		System.arraycopy(firstArr, 0, array, 0, firstArr.length);
		System.arraycopy(secondArr, 0, array, firstArr.length, secondArr.length);
		return array;
	}

	public static <S> ArrayList<S>[] createArrayOfEmptyArrays(int numberOfArr) {
		ArrayList<S>[] arrayLists = new ArrayList[numberOfArr];
		for (int i = 0; i < numberOfArr; i++)
			arrayLists[i] = new ArrayList<>();
		return arrayLists;
	}
}
