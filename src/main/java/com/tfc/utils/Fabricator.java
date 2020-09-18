package com.tfc.utils;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.bytecode.Compiler;
import com.tfc.bytecode.compilers.Javassist_Compiler;
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
	private static final File f = new File(Main.getExecDir() + "\\FlameASM\\fabrication");
	
	private static final Javassist_Compiler compiler = new Javassist_Compiler();
	
	public static byte[] fabricate(String resource, Function<String, String> runtimeCodeReplacer) throws IOException {
		try {
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.mkdirs();
			}
			FileOutputStream stream = new FileOutputStream(new File(f + "\\" + (resource.replace(".java", ".class"))));
			FileOutputStream stream1 = new FileOutputStream(new File(f + "\\" + (resource)));
			InputStream inStream = Main.class.getClassLoader().getResourceAsStream(resource);
			byte[] bytes = new byte[inStream.available()];
			inStream.read(bytes);
			inStream.close();
			String text = new String(bytes);
			text = runtimeCodeReplacer.apply(text);
			stream1.write(Formatter.formatForCompile(text).getBytes());
			stream1.close();
//			byte[] output = compiler.compile(Parser.parse(text));
			byte[] output = Compiler.compile(text);
			stream.write(output);
			stream.close();
			return output;
		} catch (Throwable err) {
			Logger.logErrFull(err);
			return null;
		}
	}
	
	public static Class<?> compileAndLoad(String resource, Function<String, String> runtimeCodeReplacer) throws IOException, InvocationTargetException, IllegalAccessException {
		byte[] bytes = fabricate(resource, runtimeCodeReplacer);
		return ForceLoad.forceLoad(Fabricator.class.getClassLoader(), bytes);
	}
}
