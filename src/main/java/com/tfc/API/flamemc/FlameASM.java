package com.tfc.API.flamemc;

import com.tfc.asmlorenzo.MyASM;
import com.tfc.hacky_class_stuff.ASM.API.MethodAccess;
import com.tfc.hacky_class_stuff.ASM.ASM;
import org.objectweb.asm.Opcodes;

@Deprecated
/**
 * Deprecated
 * Not deleting it because it could be useful, we'll see it together
 * My version is better, both in file size (only one) and in code (eh eh daily flex)
 */
public class FlameASM  implements Opcodes {
	public void transformMethodAccess(String clazz, String method, MyASM.AccessType access) {
		ASM.addMethodAT(new MethodAccess(access, clazz), method);
	}
}
