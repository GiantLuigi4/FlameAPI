package com.tfc.hacky_class_stuff.ASM.API;

import com.tfc.API.flamemc.FlameASM;

public class MethodAccess {
	public final String method;
	public FlameASM.AccessType type;
	
	public MethodAccess(FlameASM.AccessType type, String method) {
		this.type = type;
		this.method = method;
	}
	
	//There has to be a better way to do this
	//TODO:Find a better way to do this, lol
	public void increase(FlameASM.AccessType type) {
		if (!this.type.equals(FlameASM.AccessType.PUBLIC_STATIC) && !this.type.equals(FlameASM.AccessType.PUBLIC)) {
			if (this.type.equals(FlameASM.AccessType.PRIVATE)) {
				if (type.equals(FlameASM.AccessType.PROTECTED) || type.equals(FlameASM.AccessType.PUBLIC)) {
					this.type = type;
				}
			} else if (this.type.equals(FlameASM.AccessType.PROTECTED)) {
				if (type.equals(FlameASM.AccessType.PUBLIC)) {
					this.type = type;
				}
			} else if (this.type.equals(FlameASM.AccessType.PRIVATE_STATIC)) {
				if (type.equals(FlameASM.AccessType.PROTECTED_STATIC) || type.equals(FlameASM.AccessType.PUBLIC_STATIC)) {
					this.type = type;
				}
			} else if (this.type.equals(FlameASM.AccessType.PROTECTED_STATIC)) {
				if (type.equals(FlameASM.AccessType.PUBLIC_STATIC)) {
					this.type = type;
				}
			}
		}
	}
}
