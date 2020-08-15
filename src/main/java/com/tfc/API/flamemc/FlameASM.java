package com.tfc.API.flamemc;

import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import com.tfc.hacky_class_stuff.ASM.API.MethodAccess;
import com.tfc.hacky_class_stuff.ASM.ASM;
import org.objectweb.asm.Opcodes;

public class FlameASM {
	public static void addField(String clazz, String name, Object defaultVal, AccessType access) {
		ASM.addFieldNode(clazz, new FieldData(access.level, name, defaultVal));
	}
	
	public static enum AccessType {
		PUBLIC(org.objectweb.asm.Opcodes.ACC_PUBLIC),
		PRIVATE(Opcodes.ACC_PRIVATE),
		PROTECTED(Opcodes.ACC_PROTECTED);
		
		public final int level;
		
		AccessType(int level) {
			this.level = level;
		}
	}
	
	public void transformMethodAccess(String clazz, String method, AccessType access) {
		ASM.addMethodAT(new MethodAccess(access, clazz), method);
	}
}
