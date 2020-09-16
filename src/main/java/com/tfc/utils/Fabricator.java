package com.tfc.utils;

import com.tfc.bytecode.Compiler;
import com.tfc.bytecode.loading.ForceLoad;
import entries.FlameAPI.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Fabricator {
	private static final File f = new File(Main.getExecDir() + "\\FlameASM\\fabrication");
	
	public static byte[] fabricate(String resource, Function<String, String> runtimeCodeReplacer) throws IOException {
		FileOutputStream stream = new FileOutputStream(new File(f + "\\" + (resource.replace(".java", ".class"))));
		InputStream inStream = Main.class.getClassLoader().getResourceAsStream(resource);
		byte[] bytes = new byte[inStream.available()];
		inStream.read(bytes);
		inStream.close();
		String text = new String(bytes);
		text = runtimeCodeReplacer.apply(text);
		byte[] output = Compiler.compile(text);
		stream.write(output);
		stream.close();
		return output;
	}
	
	public static Class<?> compileAndLoad(String resource, Function<String, String> runtimeCodeReplacer) throws IOException, InvocationTargetException, IllegalAccessException {
		byte[] bytes = fabricate(resource, runtimeCodeReplacer);
		return ForceLoad.forceLoad(Fabricator.class.getClassLoader(), bytes);
	}
}
