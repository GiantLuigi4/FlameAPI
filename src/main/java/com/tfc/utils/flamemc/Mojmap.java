package com.tfc.utils.flamemc;

import com.tfc.mappings.structure.Class;
import com.tfc.mappings.structure.Method;
import com.tfc.mappings.structure.MojmapHolder;
import com.tfc.mappings.types.Mojang;
import com.tfc.utils.BiObject;
import entries.FlameAPI.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Mojmap {
	private static final HashMap<String, MojmapHolder> mojmapHolderHashMap = new HashMap<>();
	
	public static void load(String version) {
		mojmapHolderHashMap.put(version, Mojang.generate(version));
	}
	
	public static Class getClassObsf(String version, String name) {
		if (!mojmapHolderHashMap.containsKey(version))
			load(version);
		return mojmapHolderHashMap.get(version).getFromPrimaryName(name);
	}
	
	/**
	 * Gets the obsfucated name of a mojmap class for the current version
	 *
	 * @param name the name of the class
	 * @return the obsfucated name
	 */
	public static Class getClassObsf(String name) {
		return getClassObsf(Main.getVersionMap(), name);
	}
	
	public static Class getClassMojmap(String version, String name) {
		if (!mojmapHolderHashMap.containsKey(version))
			load(version);
		return mojmapHolderHashMap.get(version).getFromSecondaryName(name);
	}
	
	public static Class getClassMojmap(String name) {
		return getClassMojmap(Main.getVersionMap(), name);
	}
	
	public static BiObject<String, java.lang.reflect.Method> getMethod(java.lang.Class<?> clazz, Class mappingsClass, String name, String descriptor, ArrayList<BiObject<String, String>> replacements) {
		java.lang.reflect.Method returnVal = null;
		String info = "";
		AtomicReference<Method> mA = new AtomicReference<>();
		mappingsClass.getMethods().forEach((method) -> {
			if (method.getDesc().startsWith(descriptor)) {
				if (method.getPrimary().equals(name)) {
					mA.set(method);
				}
			}
		});
		com.tfc.mappings.structure.Method m = mA.get();
//		Logger.logLine("descriptor " + m.getDesc());
//		Logger.logLine("prime name " + m.getPrimary());
//		Logger.logLine("second name " + m.getSecondary());
		String desc = m.getDesc();
		for (BiObject<String, String> biObject : replacements) {
//			Logger.logLine(biObject.getObject1() + "," + biObject.getObject2());
			desc = desc.replace(biObject.getObject1(), biObject.getObject2());
		}
		desc = (desc + ")").replace(",)", ")")
				.replace(")V)", ")")
				.replace(")D)", ")")
				.replace(")I)", ")")
				.replace(")J)", ")")
		;
//		Logger.logLine("desc:" + desc);
		for (java.lang.reflect.Method method : clazz.getMethods()) {
			if (method.toString().contains(desc) && method.getName().equals(m.getSecondary())) {
//				Logger.logLine("success");
//				Logger.logLine("name:" + method.toString());
//				Logger.logLine("desc:" + desc);
				returnVal = method;
				String unexpandedMethod = method.getName() + desc;
				int argCount = 0;
				StringBuilder expandedMethod = new StringBuilder();
				for (int i = 0; i < unexpandedMethod.length(); i++) {
					if (unexpandedMethod.charAt(i) == ',') {
						expandedMethod.append(" var").append(argCount++).append(", ");
					} else {
						expandedMethod.append(unexpandedMethod.charAt(i));
					}
				}
				String finishedExpandedMethod = expandedMethod.toString().replace(")", " var" + (argCount) + ")");
				info = finishedExpandedMethod + "/new Object[]{" + "var0,var1,var2,var3,var4,Boolean.valueOf(var5)" + "}";
//				Logger.logLine(method.toString());
//				Logger.logLine(info);
				return new BiObject<>(info, returnVal);
			}
		}
		throw new RuntimeException(new NullPointerException("The requested method: " + name + descriptor + " does not exist." + desc));
	}
	
	public static ArrayList<BiObject<String, String>> toStringBiObjectArray(String... list) {
		ArrayList<BiObject<String, String>> arrayList = new ArrayList<>();
		String temp = null;
		for (String s : list) {
			if (temp == null) temp = s;
			else {
				arrayList.add(new BiObject<>(temp, s));
				temp = null;
			}
		}
		return arrayList;
	}
}
