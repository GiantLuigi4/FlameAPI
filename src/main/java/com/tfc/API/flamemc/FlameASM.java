package com.tfc.API.flamemc;

import com.tfc.hacky_class_stuff.ASM.API.Access;
import com.tfc.hacky_class_stuff.ASM.API.FieldData;
import com.tfc.hacky_class_stuff.ASM.ASM;
import org.objectweb.asm.Opcodes;

public class FlameASM implements Opcodes {
	
	public static void addField(String clazz, String name, Object defaultVal, AccessType access) {
		ASM.addFieldNode(clazz, new FieldData(access.level, name, defaultVal));
	}
	
	public static void transformMethodAccess(String clazz, String method, AccessType access) {
		ASM.addMethodAT(new Access(access, method), clazz);
	}
	
	//No, I will not add final.
	//I hate final.
	//Why would you use asm to define a final value.
	//No.
	//Just no.
	public enum AccessType {
		PUBLIC(ACC_PUBLIC),
		PUBLIC_STATIC(ACC_PUBLIC + ACC_STATIC),
		PRIVATE(ACC_PRIVATE),
		//Grudgingly, I add this...
		//Please don't use it...
		//I hate private and protected...
		//Especially on static things.
		PRIVATE_STATIC(ACC_PRIVATE + ACC_STATIC),
		PROTECTED(ACC_PROTECTED),
		PROTECTED_STATIC(ACC_PROTECTED + ACC_STATIC);
		
		public final int level;
		
		AccessType(int level) {
			this.level = level;
		}
		
		
		public static AccessType forLevel(int level) {
			for (AccessType type : AccessType.values()) {
				if (type.level == level) {
					return type;
				}
			}
			int highestNormal = Math.max(PUBLIC.level, Math.max(PRIVATE.level, PROTECTED.level));
			if (highestNormal + ACC_STATIC < level) {
				return forLevel(highestNormal + ACC_STATIC);
			} else {
				return forLevel(highestNormal);
			}
		}
		
		@Override
		public String toString() {
			return "level: " + level;
		}
	}
}
