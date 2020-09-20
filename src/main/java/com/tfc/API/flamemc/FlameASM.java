package com.tfc.API.flamemc;

import com.tfc.hacky_class_stuff.ASM.API.Field;
import com.tfc.hacky_class_stuff.ASM.API.Method;
import com.tfc.hacky_class_stuff.ASM.Applier.Applicator;
import com.tfc.utils.BiObject;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;

import java.util.ArrayList;

public class FlameASM implements Opcodes {
	public static void addField(String className, String access, String fieldName, String descriptor, Object defaultVal) {
		if (!Applicator.fields.containsKey(className)) Applicator.fields.put(className, new ArrayList<>());
		Applicator.fields.get(className).add(new Field(fieldName, descriptor, access, defaultVal));
	}
	
	public static void addMethod(String className, String access, String methodName, String descriptor, InsnList instructions) {
		if (!Applicator.methods.containsKey(className)) Applicator.methods.put(className, new ArrayList<>());
		Applicator.methods.get(className).add(new Method(access, methodName, descriptor, instructions));
	}
	
	public static void addInstructions(String className, String access, String methodName, String descriptor, InsnList instructions, boolean atStart) {
		if (!Applicator.insnAdds.containsKey(className)) Applicator.insnAdds.put(className, new ArrayList<>());
		Applicator.insnAdds.get(className).add(new BiObject<>(new Method(access, methodName, descriptor, instructions), atStart));
	}
	
	public AccessType forString(String type) {
		if (type.contains("public"))
			if (type.contains("static")) return AccessType.PUBLIC_STATIC;
			else return AccessType.PUBLIC;
		else if (type.contains("private"))
			if (type.contains("static")) return AccessType.PRIVATE_STATIC;
			else return AccessType.PRIVATE;
		else if (type.contains("protected"))
			if (type.contains("static")) return AccessType.PROTECTED_STATIC;
			else return AccessType.PROTECTED;
		return AccessType.PUBLIC;
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
