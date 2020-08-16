package com.tfc.hacky_class_stuff;

import com.tfc.flame.FlameConfig;

//ASM is cool...
//When it doesn't... magically generate classes... that extend byte..?
//Idk either.
//You must tell me how did you manage to get that class, it was hilarious :D
//No
//Ok now it has two constructors, both exactly the same, AND it still extends byte.
//Ok I'm just gonna... not do this rn...
public class BlockClass {
	public static byte[] getBlock(String name, byte[] bytes) {
		if (name.equals("com.tfc.API.flamemc.Block")) {
			try {
//				String superName = ScanningUtils.toClassName(Main.getResourceTypeClasses().get("Block")).replace(".", "/");
//				FlameConfig.field.append(name + "\n");
////				ClassReader reader = new ClassReader(new byte[]{0,0,0,0,0,0,0,0});
////				FlameConfig.field.append(reader.getSuperName() + "\n");
////				ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
//				ClassWriter writer = new ClassWriter(FlameAPIConfigs.ASM_Version);
////				reader.accept(new SupernameSetter(FlameAPIConfigs.ASM_Version, writer, superName,0);
//				writer.newPackage("com.tfc.API.flamemc");
//				writer.visit(52, FlameASM.AccessType.PUBLIC.level, "Block", "FlameAPI", superName, null);
//				writer.visitEnd();
//				byte[] bytes1 = writer.toByteArray();
//				ClassReader reader2 = new ClassReader(bytes1);
//				FlameConfig.field.append(reader2.getSuperName() + "\n");
//				Bytecode.writeBytes(name, "fabrication", bytes1);
//				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return null;
	}
}
