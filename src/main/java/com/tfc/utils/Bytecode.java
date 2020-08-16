package com.tfc.utils;

import entries.FlameAPI.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class Bytecode {
	
	public static void writeBytes(String clazz, String file, byte[] bytes) {
		try {
			File f1 = new File(Main.getGameDir() + "\\FlameASM\\" + file + "\\" + clazz.replace(".", "\\") + ".class");
			if (!f1.exists()) {
				f1.getParentFile().mkdirs();
				f1.createNewFile();
			}
			//https://www.geeksforgeeks.org/convert-byte-array-to-file-using-java/#:~:text=To%20convert%20byte%5B%5D%20to,and%20write%20in%20a%20file.
			OutputStream writer1 = new FileOutputStream(f1);
			writer1.write(bytes);
			writer1.close();
		} catch (Throwable ignored) {
		}
	}
}
