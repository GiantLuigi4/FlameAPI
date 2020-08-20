package com.tfc.hacky_class_stuff;

import com.tfc.API.flamemc.FlameASM;
import com.tfc.FlameAPIConfigs;
import com.tfc.flame.FlameConfig;
import com.tfc.utils.Bytecode;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

//ASM is cool...
//When it doesn't... magically generate classes... that extend byte..?
//Idk either.
//You must tell me how did you manage to get that class, it was hilarious :D
//No
//Ok now it has two constructors, both exactly the same, AND it still extends byte.
//Ok I'm just gonna... not do this rn...
//HEY! IT WORKS!
public class BlockClass {
	public static byte[] getBlock(String name, byte[] bytes) {
		if (name.equals("com.tfc.API.flamemc.blocks.Block")) {
			try {
				ClassReader reader = new ClassReader(bytes);
				ClassWriter blankWriter = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
				blankWriter.visit(52, FlameASM.AccessType.PUBLIC.level, reader.getClassName(), "none", ScanningUtils.toClassName(Main.getBlockClass()), null);
				blankWriter.visitEnd();
				byte[] bytes1 = blankWriter.toByteArray();
				Bytecode.writeBytes(name, "fabrication", bytes1);
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return bytes;
	}
}
