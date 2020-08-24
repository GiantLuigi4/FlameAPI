package com.tfc.hacky_class_stuff.ASM.API;

import com.tfc.API.flamemc.FlameASM;

public class Access {
	public final String name;
	public FlameASM.AccessType type;
	
	public Access(FlameASM.AccessType type, String method) {
		this.type = type;
		this.name = method;
	}
	
	//Better way found (Lorenzo)
	//Even better way found (GiantLuigi4)
	//NVM (GiantLuigi4)
	public void increase(FlameASM.AccessType type) {
//		if (
//				(type.level <= FlameASM.AccessType.PRIVATE.level && this.type.level <= FlameASM.AccessType.PRIVATE.level) ||
//						(type.level >= FlameASM.AccessType.PRIVATE_STATIC.level && this.type.level >= FlameASM.AccessType.PRIVATE_STATIC.level)
//		)
//			this.type = FlameASM.AccessType.forLevel(Math.min(this.type.level,type.level));
		
		switch (FlameASM.AccessType.valueOf(this.type.name())) {
			case PUBLIC:
			case PUBLIC_STATIC:
				break;
			case PROTECTED:
				if (type.equals(FlameASM.AccessType.PUBLIC)) this.type = type;
                                break;
			case PRIVATE:
				if (type.equals(FlameASM.AccessType.PUBLIC) || type.equals(FlameASM.AccessType.PROTECTED))
					this.type = type;
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
