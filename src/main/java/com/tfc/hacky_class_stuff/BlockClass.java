package com.tfc.hacky_class_stuff;

import com.tfc.FlameAPIConfigs;
import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.SupernameSetter;
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
public class BlockClass {
	public static byte[] getBlock(String name, byte[] bytes) {
		if (name.equals("com.tfc.API.flamemc.Block")) {
			try {
				FlameConfig.field.append(name + "\n");
				ClassReader reader = new ClassReader(bytes);
				FlameConfig.field.append(reader.getSuperName() + "\n");
//				writer.visit(52, FlameASM.AccessType.PUBLIC.level, name.replace(".","/")+".class",null, ScanningUtils.toClassName(Main.getResourceTypeClasses().get("Block")), null);
//				reader.accept(new FieldAdder(FlameAPIConfigs.ASM_Version, writer, "blankField", "", "java/lang/String", FlameASM.AccessType.PRIVATE_STATIC.level), 0);
				ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
				writer.newPackage("");
				reader.accept(new SupernameSetter(FlameAPIConfigs.ASM_Version, writer, ScanningUtils.toClassName(Main.getResourceTypeClasses().get("Block")).replace(".", "/")), 0);
				writer.visitEnd();
				byte[] bytes1 = writer.toByteArray();
				ClassReader reader2 = new ClassReader(bytes1);
				FlameConfig.field.append(reader2.getSuperName() + "\n");
				Bytecode.writeBytes(name, "fabrication", bytes1);
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return null;
	}
}
