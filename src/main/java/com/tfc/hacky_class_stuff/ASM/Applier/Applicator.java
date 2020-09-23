package com.tfc.hacky_class_stuff.ASM.Applier;

import com.tfc.API.flame.utils.logging.Logger;
import com.tfc.bytecode.asm.ASM.ASM;
import com.tfc.hacky_class_stuff.ASM.API.Field;
import com.tfc.hacky_class_stuff.ASM.API.Method;
import com.tfc.utils.BiObject;
import com.tfc.utils.Bytecode;

import java.util.ArrayList;
import java.util.HashMap;

public class Applicator {
	public static final HashMap<String, ArrayList<Field>> fields = new HashMap<>();
	public static final HashMap<String, ArrayList<Method>> methods = new HashMap<>();
	public static final HashMap<String, ArrayList<BiObject<Method, Boolean>>> insnAdds = new HashMap<>();
	
	public static byte[] apply(String name, byte[] source) {
		ASM asm = new ASM(source);
		boolean transformed = false;
		if (fields.containsKey(name)) {
			transformed = true;
			for (Field f : fields.get(name))
				asm.addField(f.getName(), f.getAccess(), f.getDescriptor(), f.getDefaultValue());
		}
		if (methods.containsKey(name)) {
			transformed = true;
			for (Method m : methods.get(name))
				asm.addMethod(m.getAccess(), m.getName(), m.getDescriptor(), null, null, m.getInstructions());
		}
		if (insnAdds.containsKey(name)) {
			transformed = true;
			for (BiObject<Method, Boolean> add : insnAdds.get(name)) {
				Method m = add.getObject1();
				asm.transformMethod(m.getName(), m.getDescriptor(), m.getInstructions(), add.getObject2());
			}
		}
		byte[] bytes = asm.toBytes();
		if (transformed) {
			try {
				Bytecode.writeBytes(name, "post_asm", bytes);
			} catch (Throwable err) {
				Logger.logErrFull(err);
				throw new RuntimeException(err);
			}
		}
		return bytes;
	}
}
