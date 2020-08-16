package com.tfc.hacky_class_stuff;

import com.tfc.utils.Bytecode;
import com.tfc.utils.ScanningUtils;
import com.tfc.flame.FlameConfig;
import entries.FlameAPI.Main;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

//ASM is cool...
//When it doesn't... magically generate classes... that extend byte..?
//Idk either.
//You must tell me how did you manage to get that class, it was hilarious :D
public class BlockClass {
	public static byte[] getBlock(String name, byte[] source) {
		if (name.equals("com.tfc.API.flamemc.Block")) {
			try {
				FlameConfig.field.append(name + "\n");
				ClassReader reader = new ClassReader(source);
				ClassWriter writer = new ClassWriter(reader, Opcodes.ASM7);
//				ClassNode node = new ClassNode();
//				node.superName = ScanningUtils.toClassName(Main.getResourceTypeClasses().get("Block"));
//				node.visitEnd();
				writer.visit(52, Opcodes.ACC_PUBLIC, name, "FlameAPI", ScanningUtils.toClassName(Main.getResourceTypeClasses().get("Block")), null);
//				node.accept(writer);
				writer.visitEnd();
				byte[] bytes = writer.toByteArray();
				Bytecode.writeBytes(name, "fabrication", bytes);
				return bytes;
			} catch (Throwable err) {
				FlameConfig.logError(err);
			}
		}
		return null;
	}
}
