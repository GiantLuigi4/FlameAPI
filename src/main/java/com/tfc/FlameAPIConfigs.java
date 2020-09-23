package com.tfc;

import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class FlameAPIConfigs {
	private static final String config =
			"" +
					"//Weird comments for anyone who wants to have syntax highlighting in np++*/\n\n" +
					"//*MinVersion = ASM4.\n" +
					"//Max will change.\n" +
					"//Currently, it's ASM8 though.*/\n" +
					"ASM_API:ASM8\n\n" +
					"//*Enable a bunch of things that slow down the game, but make debugging the API itself easier\n" +
					"//This will likely not be useful for the average modder, and only really be useful for the FlameAPI's devs\n" +
					"//It will also disable any crashes intentionally put in for when something goes fatally wrong*/\n" +
					"DevMode:false\n";
	public static final int ASM_Version = getVersion();
	public static final boolean devMode = isDevMode();
	
	private static int getVersion() {
		try {
			//I find that I like ".properties" files, so I'm gonna always use em for FlameMC if possible.
			File f = new File(Main.getDataDir() + "\\flame_config\\FlameAPI.properties");
			if (!f.exists()) {
				f.createNewFile();
				FileWriter writer = new FileWriter(f);
				writer.write(config);
				writer.close();
			}
			Scanner sc = new Scanner(f);
			AtomicReference<String> API = new AtomicReference<>();
			ScanningUtils.forEachLine(sc, line -> {
				if (line.startsWith("ASM_API:")) {
					API.set(line.replace("ASM_API:", ""));
				}
			});
			sc.close();
			Field field = Opcodes.class.getField(API.get().toUpperCase());
			return (int) field.get(null);
		} catch (Throwable ignored) {
		}
		return Opcodes.ASM8;
	}
	
	private static boolean isDevMode() {
		try {
			//I find that I like ".properties" files, so I'm gonna always use em for FlameMC if possible.
			File f = new File(Main.getDataDir() + "\\flame_config\\FlameAPI.properties");
			if (!f.exists()) {
				f.createNewFile();
				FileWriter writer = new FileWriter(f);
				writer.write(config);
				writer.close();
			}
			Scanner sc = new Scanner(f);
			AtomicReference<String> isDev = new AtomicReference<>();
			ScanningUtils.forEachLine(sc, line -> {
				if (line.startsWith("DevMode:")) {
					isDev.set(line.replace("DevMode:", ""));
				}
			});
			return Boolean.parseBoolean(isDev.get());
		} catch (Throwable ignored) {
		}
		return false;
	}
}
