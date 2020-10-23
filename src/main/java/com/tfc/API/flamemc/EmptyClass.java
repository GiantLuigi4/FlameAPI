package com.tfc.API.flamemc;

public class EmptyClass {
	private static final String test;

	static {
		test = "hello";
		for (Registry.RegistryObject object : EmptyClassMK2.registryObjects) {
			a(object.getName().toString(), (String) object.get());
		}
	}

	public static Object a(String name, String o) {
		return o;
	}
}
