package com.tfc.hacky_class_stuff.ASM.API;

import com.tfc.asmlorenzo.MyASM;

//@Deprecated
/**
 * Deprecated
 * Not deleting it because it could be useful, we'll see it together
 * My version is better, both in file size (only one) and in code (eh eh daily flex)
 */
/**
 * Actually idk how to reproduce the increase method somewhere else lmao*/
public class MethodAccess {
	public final String method;
	public MyASM.AccessType type;
	
	public MethodAccess(MyASM.AccessType type, String method) {
		this.type = type;
		this.method = method;
	}
	
	//Better way found (Lorenzo)
	public void increase(MyASM.AccessType type) {
		switch (MyASM.AccessType.valueOf(this.type.name())) {
			case PUBLIC:
			case PUBLIC_STATIC:
				break;
			case PROTECTED:
				if (type.equals(MyASM.AccessType.PUBLIC)) this.type = type;
			case PRIVATE:
				if (type.equals(MyASM.AccessType.PUBLIC) || type.equals(MyASM.AccessType.PROTECTED))	this.type = type;
				break;
			case PRIVATE_STATIC:
				if (type.equals(MyASM.AccessType.PROTECTED_STATIC)) this.type = type;
				break;
			case PROTECTED_STATIC:
				if (type.equals(MyASM.AccessType.PRIVATE_STATIC)) this.type = type;
				break;
		}
	}
}
