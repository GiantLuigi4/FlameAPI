package com.tfc.hacky_class_stuff.ASM.API;

import com.tfc.API.flamemc.FlameASM;

public class Access {
	public final String method;
	public FlameASM.AccessType type;
	
	public Access(FlameASM.AccessType type, String method) {
		this.type = type;
		this.method = method;
	}
	
	//Better way found (Lorenzo)
	//TODO:Find a better way to do this, lol
	public void increase(FlameASM.AccessType type) {
		switch (FlameASM.AccessType.valueOf(this.type.name())) {
			case PUBLIC:
			case PUBLIC_STATIC:
				break;
			case PROTECTED:
				if (type.equals(FlameASM.AccessType.PUBLIC)) this.type = type;
			case PRIVATE:
				if (type.equals(FlameASM.AccessType.PUBLIC) || type.equals(FlameASM.AccessType.PROTECTED))	this.type = type;
				break;
			case PRIVATE_STATIC:
				if (type.equals(FlameASM.AccessType.PROTECTED_STATIC)) this.type = type;
				break;
			case PROTECTED_STATIC:
				if (type.equals(FlameASM.AccessType.PRIVATE_STATIC)) this.type = type;
				break;
		}
	}
}
