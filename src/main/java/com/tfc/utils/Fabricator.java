package com.tfc.utils;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.bytecode.Compiler;
import com.tfc.bytecode.EnumCompiler;
import com.tfc.bytecode.compilers.Janino_Compiler;
import com.tfc.bytecode.loading.ForceLoad;
import com.tfc.bytecode.utils.Formatter;
import entries.FlameAPI.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

public class Fabricator {
	private static final File f = new File(Main.getDataDir() + "\\FlameASM\\fabrication");
	
	private static final Janino_Compiler compiler = new Janino_Compiler();
	
	public static byte[] fabricate(EnumCompiler compilerEnum, String resource, Function<String, String> runtimeCodeReplacer) throws IOException {
		try {
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.mkdirs();
			}
			String text = readFromCL(resource);
			text = runtimeCodeReplacer.apply(text);
			byte[] output = Compiler.compile(compilerEnum, text);
			write(Formatter.formatForCompile(text), new File(f + "\\" + (resource)));
			write(text, new File(f + "\\" + (resource).replace(".java", "_source.java")));
			write(new String(output), new File(f + "\\" + (resource.replace(".java", ".class"))));
			return output;
		} catch (Throwable err) {
			Logger.logErrFull(err);
			return null;
		}
	}
	
	public static byte[] fabricateJanino(String resource, Function<String, String> runtimeCodeReplacer, String... otherClasses) throws IOException {
		try {
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.mkdirs();
			}
			String text = readFromCL(resource);
			text = runtimeCodeReplacer.apply(text);
			byte[] output = compiler.compile(text, "a", otherClasses);
			write(Formatter.formatForCompile(text), new File(f + "\\" + (resource)));
			write(text, new File(f + "\\" + (resource).replace(".java", "_source.java")));
			write(new String(output), new File(f + "\\" + (resource.replace(".java", ".class"))));
			return output;
		} catch (Throwable err) {
			Logger.logErrFull(err);
			return null;
		}
	}
	
	public static Class<?> compileAndLoad(String resource, Function<String, String> runtimeCodeReplacer) throws IOException, InvocationTargetException, IllegalAccessException {
		byte[] bytes = fabricate(EnumCompiler.JAVASSIST, resource, runtimeCodeReplacer);
		return ForceLoad.forceLoad(Fabricator.class.getClassLoader(), bytes);
	}
	
	public static Class<?> compileAndLoadJanino(String resource, Function<String, String> runtimeCodeReplacer, String... otherClasses) throws IOException, InvocationTargetException, IllegalAccessException {
		byte[] bytes = fabricateJanino(resource, runtimeCodeReplacer, otherClasses);
		return ForceLoad.forceLoad(Fabricator.class.getClassLoader(), bytes);
	}
	
	private static String readFromCL(String name) throws IOException {
		InputStream inStream = Main.class.getClassLoader().getResourceAsStream(name);
		byte[] bytes = new byte[inStream.available()];
		inStream.read(bytes);
		inStream.close();
		String text = new String(bytes);
		return text;
	}
	
	private static void write(String text, File file) throws IOException {
		FileOutputStream stream = new FileOutputStream(file);
		stream.write(text.getBytes());
		stream.close();
	}
}
