package com.tfc.hacky_class_stuff.ASM.API;

import com.tfc.API.flamemc.FlameASM;

public class MethodAccess {
	public final String method;
	public FlameASM.AccessType type;
	
	public MethodAccess(FlameASM.AccessType type, String method) {
		this.type = type;
		this.method = method;
	}
	
	public void increase(FlameASM.AccessType type) {
		if (this.type.equals(FlameASM.AccessType.PRIVATE)) {
			if (type.equals(FlameASM.AccessType.PROTECTED) || type.equals(FlameASM.AccessType.PUBLIC)) {
				this.type = type;
			}
		} else if (this.type.equals(FlameASM.AccessType.PROTECTED)) {
			if (type.equals(FlameASM.AccessType.PUBLIC)) {
				this.type = type;
			}
		}
	}
}
