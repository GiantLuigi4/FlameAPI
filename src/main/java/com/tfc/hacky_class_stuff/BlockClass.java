package com.tfc.hacky_class_stuff;

import com.tfc.API.flamemc.FlameASM;
import com.tfc.FlameAPIConfigs;
import com.tfc.flame.FlameConfig;
import com.tfc.hacky_class_stuff.ASM.FieldAdder;
import com.tfc.utils.Bytecode;
import com.tfc.utils.ScanningUtils;
import entries.FlameAPI.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

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
				ClassNode node = new ClassNode();
				reader.accept(node, 0);
				ClassWriter writer = new ClassWriter(reader, FlameAPIConfigs.ASM_Version);
//				For some reason I need to asm in an empty field... idk?
				writer.visit(52, FlameASM.AccessType.PUBLIC.level, name, "FlameAPI", node.superName, null);
				reader.accept(new FieldAdder(FlameAPIConfigs.ASM_Version, writer, "blankField", "", "java/lang/String", FlameASM.AccessType.PRIVATE_STATIC.level), 0);
				node.superName = ScanningUtils.toClassName(Main.getResourceTypeClasses().get("Block"));
				node.visitEnd();
				try {
					node.accept(writer);
				} catch (Throwable ignored) {
				}
				writer.visitEnd();
				byte[] bytes1 = writer.toByteArray();
				Bytecode.writeBytes(name, "fabrication", bytes1);
				return bytes1;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return null;
	}
}
