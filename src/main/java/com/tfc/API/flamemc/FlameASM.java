package com.tfc.API.flamemc;

import com.tfc.hacky_class_stuff.ASM.API.FieldNode;
import com.tfc.hacky_class_stuff.ASM.ASM;
import org.objectweb.asm.Opcodes;

public class FlameASM {
	public static void addField(String clazz, String name, Object defaultVal, AccessType access) {
		ASM.addFieldNode(clazz, new FieldNode(access.level, name, defaultVal));
	}
	
	public static enum AccessType {
		PUBLIC(org.objectweb.asm.Opcodes.ACC_PUBLIC),
		PRIVATE(Opcodes.ACC_PRIVATE),
		PROTECTED(Opcodes.ACC_PROTECTED);
		
		final int level;
		
		AccessType(int level) {
			this.level = level;
		}
	}
}
