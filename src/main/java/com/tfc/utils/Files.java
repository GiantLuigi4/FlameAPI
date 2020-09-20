package com.tfc.utils;

import entries.FlameAPI.Main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Files {
	public static final File gameDir = new File((Main.getGameDir() == null ? Main.getExecDir() : Main.getGameDir()));
	
	public static void write(String text, File file) throws IOException {
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		FileOutputStream stream = new FileOutputStream(file);
		stream.write(text.getBytes());
		stream.close();
	}
}
